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

class DefaultDefinitionRepositorySpec extends Specification
{
    val sut = new DefaultDefinitionRepository
    val kochSrc = "angle = 60seed = FF = F+F--F+F"

    "definition repository" should {
        "have definitions" in {
            sut.getDefinitions.size must be_>(0)
        }

        "have the cross as the first definition" in {
            sut.getDefinitions(0).source.replace("\r", "").replace("\n", "") must beEqualTo(kochSrc)
        }
    }

}