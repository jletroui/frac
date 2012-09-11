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

import org.specs2.mutable._
import math._

class FractalDefinitionParserSpec extends Specification {
  val sut = new FractalDefinitionParser

  "The rule parser" should {
    "parse empty string rule" in {
      val src = "seed = "

      sut.parseFractalDefinition(src).result.get must beEqualTo(
        FractalDefinition(
          Nil,
          src) )
    }
    "parse angle assignment" in {
      val src = "angle = 60\nseed = "

      sut.parseFractalDefinition(src).result.get must beEqualTo(
        FractalDefinition(
          Nil,
          src,
          turnAngle = (Pi / 3) ) )
    }
    "parse scale ratio assignment" in {
      val src = "ratio = 0.2\nseed = "

      sut.parseFractalDefinition(src).result.get must beEqualTo(
        FractalDefinition(
          Nil,
          src,
          scaleRatio = 0.2 ) )
    }
    "parse start point assignment" in {
      val src = "start = bottom\nseed = "

      sut.parseFractalDefinition(src).result.get must beEqualTo(
        FractalDefinition(
          Nil,
          src,
          startingPoint = StartingPoint.Bottom ) )
    }
    "parse title assignment" in {
      val src = "title = Koch Flake\nseed = "

      sut.parseFractalDefinition(src).result.get must beEqualTo(
        FractalDefinition(
          Nil,
          src,
          title = "Koch Flake" ) )
    }
    "parse single primitive rule" in {
      val src = "seed = F"

      sut.parseFractalDefinition(src).result.get must beEqualTo(
        FractalDefinition(
          List(RuleReference('F')),
          src) )
    }
    "parse multiple primitives rule" in {
      val src = "seed = F+A"

      sut.parseFractalDefinition(src).result.get must beEqualTo(
        FractalDefinition(
          List(RuleReference('F'), RuleReference('+'), RuleReference('A')),
          src) )
    }
    "parse a color statement" in {
      val src = "seed = {12,250,0}"

      sut.parseFractalDefinition(src).result.get must beEqualTo(
        FractalDefinition(
          List(ConstantColorOperation(12, 250, 0)),
          src) )
    }
    "parse a predeined color statement" in {
      val src = "seed = {red}"

      sut.parseFractalDefinition(src).result.get must beEqualTo(
        FractalDefinition(
          List(ConstantColorOperation("red")),
          src) )
    }
    "parse a color increment statement" in {
      val src = "seed = {+10,+0,-10}"

      sut.parseFractalDefinition(src).result.get must beEqualTo(
        FractalDefinition(
          List(IncrementColorOperation(10, 0, -10)),
          src) )
    }
    "parse multiple rules" in {
      val src = "seed = F\nF = A\nA = +"

      sut.parseFractalDefinition(src).result.get must beEqualTo(
        FractalDefinition(
          List(RuleReference('F')),
          src,
          rules = List(
            Rule('F', List(RuleReference('A'))),
            Rule('A', List(RuleReference('+')))
          )) )
    }
    "parse a complex rule made of all possible elements" in {
      val src = "seed = F{1,2,3}+"

      sut.parseFractalDefinition(src).result.get must beEqualTo(
        FractalDefinition(
          List(RuleReference('F'), ConstantColorOperation(1, 2, 3), RuleReference('+')),
          src) )
    }
    "ignore spaces and empty lines" in {
      val src = "angle = \t60\n \t\nseed\t = \n\n"

      sut.parseFractalDefinition(src).result.get must beEqualTo(
        FractalDefinition(
          Nil,
          sourceText = src,
          turnAngle = (Pi / 3) ) )
    }
    "parse multiple definitions" in {
      val src = "angle = 60\nseed = \nangle = 30\nseed = "

      sut.parseFractalDefinitionList(src).result.get must beEqualTo( List(
        FractalDefinition(
          Nil,
          sourceText = "angle = 60\nseed = \n",
          turnAngle = (Pi / 3) ),
        FractalDefinition(
          Nil,
          sourceText = "angle = 30\nseed = ",
          turnAngle = (Pi / 6) )
      ) )
    }
  }
}
