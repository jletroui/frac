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

import java.io.{InputStreamReader, Reader, BufferedInputStream, BufferedReader}
import java.lang.StringBuffer

/** Reads the definition.frac file and parses the definitions */
class DefaultDefinitionRepository extends DefinitionRepository with Using
{
  private var sources = List.empty[DefinitionSource]
  using(Thread.currentThread.getContextClassLoader.getResourceAsStream("definitions.frac")) { is =>
    using(new InputStreamReader(is)) { isr =>
      using(new BufferedReader(isr)) { br =>
        var line = br.readLine
        while (line != null) {
          val name = line
          val buf = new StringBuffer()
          line = br.readLine
          while (line != null && line.length > 0) {
            buf.append(line).append("\n")
            line = br.readLine
          }
          sources = sources :+ DefinitionSource(name, buf.toString)
          while (line != null && line.length == 0) line = br.readLine
        }
      }
    }
  }

  def getDefinitions = sources
}