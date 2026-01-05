package miniclust.manager


import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import better.files.*
import org.apache.commons.codec.digest.DigestUtils

/*
 * Copyright (C) 2025 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
 
 object Tool:
   given ExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
   def now = System.currentTimeMillis()

   def withTmpDirectory[R]()(f: File => R) =
     val dir = File.newTemporaryDirectory()
     try f(dir)
     finally dir.delete(true)

   def hash(v: String, salt: String) =
     val shaHex: String = DigestUtils.sha256Hex(salt + v)
     s"sha256:$shaHex"

   def randomUUID = java.util.UUID.randomUUID().toString



