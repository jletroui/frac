/*
 * Copyright (C) 2011 Julien Letrouit
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

import org.specs2.mutable._

class StringRendererSpec extends SpecificationWithJUnit
{
    val definition = new Definition {
        def run(depth: Int, callback: (Char) => Unit)
        {
            Range(0, depth).foreach(i => callback((48 + i).toChar))
        }
    }
    val sut = new StringRenderer

    "a string renderer" should {
        "render all characters" in {
            sut.render(definition, 4).length() must beEqualTo(4)
        }

        "render the right characters" in {
            sut.render(definition, 4) must beEqualTo("0123")
        }


    }
}