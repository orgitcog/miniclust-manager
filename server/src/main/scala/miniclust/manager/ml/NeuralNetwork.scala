package miniclust.manager.ml

import io.circe.{Codec, Decoder, Encoder}
import io.circe.generic.semiauto.*

/**
 * Torch7-inspired neural network builder for the Civic Angel system
 * 
 * Provides a modular approach to building neural networks with
 * familiar layer types and sequential composition
 */

/**
 * Base trait for all neural network modules
 */
trait NNModule:
  def forward(input: Tensor): Tensor
  def parameters: List[Tensor]
  def name: String

/**
 * Linear (fully connected) layer
 */
case class Linear(inputSize: Int, outputSize: Int, useBias: Boolean = true) extends NNModule:
  private val weights = Tensor.randn(TensorShape.matrix(inputSize, outputSize)) * 0.01f
  private val bias = if useBias then Tensor.zeros(TensorShape.vector(outputSize)) else null
  
  override def forward(input: Tensor): Tensor =
    val output = input.matmul(weights)
    if useBias then output + bias else output
  
  override def parameters: List[Tensor] =
    if useBias then List(weights, bias) else List(weights)
  
  override def name: String = s"Linear($inputSize → $outputSize)"

/**
 * ReLU activation layer
 */
case class ReLU() extends NNModule:
  override def forward(input: Tensor): Tensor = input.relu
  override def parameters: List[Tensor] = List.empty
  override def name: String = "ReLU"

/**
 * Sigmoid activation layer
 */
case class Sigmoid() extends NNModule:
  override def forward(input: Tensor): Tensor = input.sigmoid
  override def parameters: List[Tensor] = List.empty
  override def name: String = "Sigmoid"

/**
 * Tanh activation layer
 */
case class Tanh() extends NNModule:
  override def forward(input: Tensor): Tensor = input.tanh
  override def parameters: List[Tensor] = List.empty
  override def name: String = "Tanh"

/**
 * Softmax activation layer
 */
case class Softmax() extends NNModule:
  override def forward(input: Tensor): Tensor = TensorOps.softmax(input)
  override def parameters: List[Tensor] = List.empty
  override def name: String = "Softmax"

/**
 * Dropout layer
 */
case class Dropout(p: Float = 0.5f) extends NNModule:
  override def forward(input: Tensor): Tensor = TensorOps.dropout(input, p)
  override def parameters: List[Tensor] = List.empty
  override def name: String = s"Dropout($p)"

/**
 * Batch normalization layer
 */
case class BatchNorm() extends NNModule:
  override def forward(input: Tensor): Tensor = TensorOps.batchNorm(input)
  override def parameters: List[Tensor] = List.empty
  override def name: String = "BatchNorm"

/**
 * Sequential container for composing layers
 */
case class Sequential(modules: List[NNModule]) extends NNModule:
  override def forward(input: Tensor): Tensor =
    modules.foldLeft(input) { (tensor, module) =>
      module.forward(tensor)
    }
  
  override def parameters: List[Tensor] =
    modules.flatMap(_.parameters)
  
  override def name: String =
    s"Sequential[\n  ${modules.map(_.name).mkString(",\n  ")}\n]"

object Sequential:
  def apply(modules: NNModule*): Sequential = Sequential(modules.toList)

/**
 * Neural network model metadata
 */
case class ModelMetadata(
  name: String,
  version: String,
  architecture: String,
  parameterCount: Int,
  created: Long = System.currentTimeMillis()
) derives Codec

/**
 * Complete neural network model
 */
case class NeuralNetworkModel(
  metadata: ModelMetadata,
  network: Sequential
):
  def forward(input: Tensor): Tensor = network.forward(input)
  
  def predict(input: Tensor): Tensor = forward(input)
  
  def parameterCount: Int = network.parameters.map(_.shape.size).sum
  
  override def toString: String =
    s"Model: ${metadata.name}\n${network.name}\nParameters: $parameterCount"

/**
 * Neural network builder for easy model construction
 */
object NNBuilder:
  /**
   * Build a simple feedforward network
   */
  def buildFeedForward(
    inputSize: Int,
    hiddenSizes: List[Int],
    outputSize: Int,
    activation: String = "relu"
  ): NeuralNetworkModel =
    val layers = scala.collection.mutable.ListBuffer[NNModule]()
    
    // Input to first hidden
    layers += Linear(inputSize, hiddenSizes.head)
    layers += activationLayer(activation)
    
    // Hidden to hidden
    for (i <- 0 until hiddenSizes.length - 1) do
      layers += Linear(hiddenSizes(i), hiddenSizes(i + 1))
      layers += activationLayer(activation)
    
    // Last hidden to output
    layers += Linear(hiddenSizes.last, outputSize)
    
    val network = Sequential(layers.toList)
    val metadata = ModelMetadata(
      name = "FeedForward",
      version = "1.0",
      architecture = s"$inputSize → ${hiddenSizes.mkString(" → ")} → $outputSize",
      parameterCount = network.parameters.map(_.shape.size).sum
    )
    
    NeuralNetworkModel(metadata, network)
  
  /**
   * Build a classifier with softmax output
   */
  def buildClassifier(
    inputSize: Int,
    hiddenSizes: List[Int],
    numClasses: Int
  ): NeuralNetworkModel =
    val layers = scala.collection.mutable.ListBuffer[NNModule]()
    
    // Input to first hidden
    layers += Linear(inputSize, hiddenSizes.head)
    layers += ReLU()
    
    // Hidden layers
    for (i <- 0 until hiddenSizes.length - 1) do
      layers += Linear(hiddenSizes(i), hiddenSizes(i + 1))
      layers += ReLU()
      layers += Dropout(0.3f)
    
    // Output layer with softmax
    layers += Linear(hiddenSizes.last, numClasses)
    layers += Softmax()
    
    val network = Sequential(layers.toList)
    val metadata = ModelMetadata(
      name = "Classifier",
      version = "1.0",
      architecture = s"$inputSize → ${hiddenSizes.mkString(" → ")} → $numClasses (softmax)",
      parameterCount = network.parameters.map(_.shape.size).sum
    )
    
    NeuralNetworkModel(metadata, network)
  
  private def activationLayer(activation: String): NNModule =
    activation.toLowerCase match
      case "relu" => ReLU()
      case "sigmoid" => Sigmoid()
      case "tanh" => Tanh()
      case _ => ReLU() // default

/**
 * Example usage and pre-built architectures
 */
object NNArchitectures:
  /**
   * Simple perceptron for binary classification
   */
  def perceptron(inputSize: Int): NeuralNetworkModel =
    NNBuilder.buildFeedForward(inputSize, List.empty, 1, "sigmoid")
  
  /**
   * Multi-layer perceptron for general tasks
   */
  def mlp(inputSize: Int, outputSize: Int): NeuralNetworkModel =
    NNBuilder.buildFeedForward(inputSize, List(64, 32), outputSize, "relu")
  
  /**
   * Deep network for complex pattern recognition
   */
  def deepNet(inputSize: Int, outputSize: Int): NeuralNetworkModel =
    NNBuilder.buildFeedForward(inputSize, List(128, 64, 32, 16), outputSize, "relu")
