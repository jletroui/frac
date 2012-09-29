/*
 * Copyright (C) 2012 Julien Letrouit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package frac

import java.io._
import java.lang.StringBuffer
import java.nio.file.{Paths, Files}
import java.nio.charset.Charset

/** Reads the examples in definition.frac file */
object ExampleRepository
{
  private[this] val stream = Thread.currentThread.getContextClassLoader.getResourceAsStream("definitions.frac")
  private[this] val defintionText = new String(stream.toByteArray, Charset.defaultCharset)
  val examples = new FractalDefinitionParser().parseFractalDefinitionList(defintionText).result.get
}