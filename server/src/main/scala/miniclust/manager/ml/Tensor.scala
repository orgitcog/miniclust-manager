package miniclust.manager.ml

import io.circe.{Codec, Decoder, Encoder}
import io.circe.generic.semiauto.*

/**
 * GGML-inspired tensor implementation for the Civic Angel ML operations
 * 
 * Provides memory-efficient tensor operations with quantization support
 * inspired by GGML (GPT-Generated Model Library)
 */

/**
 * Tensor data types supporting various quantization levels
 */
enum TensorDType:
  case FP32   // 32-bit floating point
  case FP16   // 16-bit floating point
  case INT8   // 8-bit integer
  case INT4   // 4-bit integer (packed)

object TensorDType:
  given Codec[TensorDType] = deriveCodec[TensorDType]

/**
 * Shape of a tensor
 */
case class TensorShape(dimensions: List[Int]) derives Codec:
  def rank: Int = dimensions.length
  def size: Int = dimensions.product
  
  def isScalar: Boolean = dimensions.isEmpty || dimensions == List(1)
  def isVector: Boolean = dimensions.length == 1
  def isMatrix: Boolean = dimensions.length == 2
  
  override def toString: String = dimensions.mkString("[", " × ", "]")

object TensorShape:
  def scalar: TensorShape = TensorShape(List(1))
  def vector(n: Int): TensorShape = TensorShape(List(n))
  def matrix(rows: Int, cols: Int): TensorShape = TensorShape(List(rows, cols))

/**
 * Tensor - Core data structure for ML operations
 * 
 * Uses FP32 by default but supports quantization for memory efficiency
 */
case class Tensor(
  shape: TensorShape,
  data: Array[Float],
  dtype: TensorDType = TensorDType.FP32,
  requiresGrad: Boolean = false
):
  require(data.length == shape.size, s"Data length ${data.length} doesn't match shape ${shape.size}")
  
  /**
   * Element-wise addition
   */
  def +(other: Tensor): Tensor =
    require(shape == other.shape, s"Shape mismatch: $shape vs ${other.shape}")
    Tensor(
      shape,
      data.zip(other.data).map { case (a, b) => a + b },
      dtype
    )
  
  /**
   * Element-wise multiplication
   */
  def *(other: Tensor): Tensor =
    require(shape == other.shape, s"Shape mismatch: $shape vs ${other.shape}")
    Tensor(
      shape,
      data.zip(other.data).map { case (a, b) => a * b },
      dtype
    )
  
  /**
   * Scalar multiplication
   */
  def *(scalar: Float): Tensor =
    Tensor(shape, data.map(_ * scalar), dtype)
  
  /**
   * Matrix multiplication
   */
  def matmul(other: Tensor): Tensor =
    require(shape.isMatrix && other.shape.isMatrix, "Both tensors must be matrices")
    require(shape.dimensions(1) == other.shape.dimensions(0), 
      s"Incompatible dimensions for matmul: $shape × ${other.shape}")
    
    val m = shape.dimensions(0)
    val n = shape.dimensions(1)
    val p = other.shape.dimensions(1)
    
    val result = Array.fill(m * p)(0.0f)
    
    for
      i <- 0 until m
      j <- 0 until p
      k <- 0 until n
    do
      result(i * p + j) += data(i * n + k) * other.data(k * p + j)
    
    Tensor(TensorShape.matrix(m, p), result, dtype)
  
  /**
   * Transpose (for 2D tensors)
   */
  def transpose: Tensor =
    require(shape.isMatrix, "Transpose only defined for matrices")
    val rows = shape.dimensions(0)
    val cols = shape.dimensions(1)
    
    val result = Array.fill(cols * rows)(0.0f)
    for
      i <- 0 until rows
      j <- 0 until cols
    do
      result(j * rows + i) = data(i * cols + j)
    
    Tensor(TensorShape.matrix(cols, rows), result, dtype)
  
  /**
   * Apply ReLU activation
   */
  def relu: Tensor =
    Tensor(shape, data.map(x => math.max(0, x)), dtype)
  
  /**
   * Apply sigmoid activation
   */
  def sigmoid: Tensor =
    Tensor(shape, data.map(x => 1.0f / (1.0f + math.exp(-x).toFloat)), dtype)
  
  /**
   * Apply tanh activation
   */
  def tanh: Tensor =
    Tensor(shape, data.map(x => math.tanh(x).toFloat), dtype)
  
  /**
   * Sum all elements
   */
  def sum: Float = data.sum
  
  /**
   * Mean of all elements
   */
  def mean: Float = data.sum / data.length
  
  /**
   * Quantize tensor to lower precision
   */
  def quantize(targetDtype: TensorDType): Tensor =
    targetDtype match
      case TensorDType.FP32 => this
      case TensorDType.FP16 => 
        // Simple FP16 simulation (not true half-precision)
        Tensor(shape, data, TensorDType.FP16)
      case TensorDType.INT8 =>
        // Quantize to INT8: scale to [-128, 127]
        val maxAbs = data.map(math.abs).max
        val scale = 127.0f / maxAbs
        val quantized = data.map(x => (x * scale).round.toFloat / scale)
        Tensor(shape, quantized, TensorDType.INT8)
      case TensorDType.INT4 =>
        // Quantize to INT4: scale to [-8, 7]
        val maxAbs = data.map(math.abs).max
        val scale = 7.0f / maxAbs
        val quantized = data.map(x => (x * scale).round.toFloat / scale)
        Tensor(shape, quantized, TensorDType.INT4)
  
  override def toString: String =
    s"Tensor(shape=$shape, dtype=$dtype, data=[${data.take(5).mkString(", ")}...])"

object Tensor:
  /**
   * Create tensor from nested lists
   */
  def apply(data: List[List[Float]]): Tensor =
    val rows = data.length
    val cols = data.headOption.map(_.length).getOrElse(0)
    val flat = data.flatten.toArray
    Tensor(TensorShape.matrix(rows, cols), flat)
  
  /**
   * Create tensor filled with zeros
   */
  def zeros(shape: TensorShape): Tensor =
    Tensor(shape, Array.fill(shape.size)(0.0f))
  
  /**
   * Create tensor filled with ones
   */
  def ones(shape: TensorShape): Tensor =
    Tensor(shape, Array.fill(shape.size)(1.0f))
  
  /**
   * Create tensor with random values
   */
  def randn(shape: TensorShape): Tensor =
    val rng = new scala.util.Random()
    Tensor(shape, Array.fill(shape.size)(rng.nextGaussian().toFloat))
  
  /**
   * Create identity matrix
   */
  def eye(n: Int): Tensor =
    val data = Array.fill(n * n)(0.0f)
    for i <- 0 until n do
      data(i * n + i) = 1.0f
    Tensor(TensorShape.matrix(n, n), data)

/**
 * Tensor operations for building computation graphs
 */
object TensorOps:
  /**
   * Softmax activation
   */
  def softmax(tensor: Tensor): Tensor =
    val expData = tensor.data.map(x => math.exp(x).toFloat)
    val sumExp = expData.sum
    Tensor(tensor.shape, expData.map(_ / sumExp), tensor.dtype)
  
  /**
   * Batch normalization (simplified)
   */
  def batchNorm(tensor: Tensor, epsilon: Float = 1e-5f): Tensor =
    val mean = tensor.mean
    val variance = tensor.data.map(x => (x - mean) * (x - mean)).sum / tensor.data.length
    val normalized = tensor.data.map(x => (x - mean) / math.sqrt(variance + epsilon).toFloat)
    Tensor(tensor.shape, normalized, tensor.dtype)
  
  /**
   * Dropout (for training)
   */
  def dropout(tensor: Tensor, p: Float = 0.5f): Tensor =
    val rng = new scala.util.Random()
    val mask = Array.fill(tensor.data.length)(if rng.nextFloat() > p then 1.0f / (1.0f - p) else 0.0f)
    Tensor(tensor.shape, tensor.data.zip(mask).map { case (x, m) => x * m }, tensor.dtype)
