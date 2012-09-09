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

class RuleParserSpec extends Specification {
  val sut = new RuleParser

  "The rule parser" should {
    "parse empty string rule" in {
      sut.parseRule("").result.get must beEqualTo(Nil)
    }
    "parse single primitive rule" in {
      sut.parseRule("F").result.get must beEqualTo(List(Primitive("F")))
    }
    "parse multiple primitives rule" in {
      sut.parseRule("F+A").result.get must beEqualTo(List(Primitive("F"), Primitive("+"), Primitive("A")))
    }
    "parse a color statement" in {
      sut.parseRule("{12,250,0}").result.get must beEqualTo(List(ConstantColorStatement(12, 250, 0)))
    }
    "parse a complex rule made of all possible elements" in {
      sut.parseRule("F{1,2,3}+").result.get must beEqualTo(List(Primitive("F"), ConstantColorStatement(1, 2, 3), Primitive("+")))
    }
  }
}
