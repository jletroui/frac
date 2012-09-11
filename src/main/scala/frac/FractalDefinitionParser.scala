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

import org.parboiled.scala._
import org.parboiled.errors.{ErrorUtils, ParsingException}
import frac._

class FractalDefinitionParser extends Parser {
  private type DefinitionTansformer = FractalDefinition => FractalDefinition
  private def setAngle(angle: Int)(fd: FractalDefinition) = fd.copy(turnAngle = angle.toRad)
  private def setRatio(ratio: Double)(fd: FractalDefinition) = fd.copy(scaleRatio = ratio)
  private def setStart(startingPoint: StartingPoint.Value)(fd: FractalDefinition) = fd.copy(startingPoint = startingPoint)
  private def build(transformers: List[DefinitionTansformer],
                    seed: List[Symbol],
                    rules: List[frac.Rule]) = {
    var definition = frac.FractalDefinition(seed, rules = rules)
    transformers.foreach(transform => definition = transform(definition))
    definition
  }

  def FractalDefinition = rule { zeroOrMore(ConstantAssignment) ~ SeedAssignment ~ zeroOrMore(Rule) ~ EOI ~~> (build(_, _, _)) }
  def ConstantAssignment = rule { AngleAssignment | RatioAssignment | StartAssignment }
  def AngleAssignment: Rule1[DefinitionTansformer] = rule { "angle" ~ "=" ~ PositiveInteger ~~> (setAngle(_)) }
  def RatioAssignment: Rule1[DefinitionTansformer] = rule { "ratio" ~ "=" ~ RealNumber ~~> (setRatio(_)) }
  def StartAssignment: Rule1[DefinitionTansformer] = rule { "start" ~ "=" ~ ( LeftStartingPoint | BottomStartingPoint ) ~~> (setStart(_)) }
  def SeedAssignment = rule { "seed" ~ "=" ~ Symbols }
  def Rule = rule { RuleName ~ "=" ~ Symbols ~~> (frac.Rule(_, _)) }
  def RuleName = rule { noneOf("{") ~:> (name => name) }

  def Symbols = rule { zeroOrMore(Symbol) }
  def Symbol = rule { ColorOperation | RuleReference }
  def RuleReference: Rule1[Symbol] = rule { noneOf("{") ~:> (frac.RuleReference(_)) }
  def ColorOperation = rule { "{" ~ ( ConstantColorOperation | PredefinedColorOperation | IncrementColorOperation )  ~ "}" }
  def ConstantColorOperation: Rule1[Symbol] = rule { PositiveInteger ~ "," ~ PositiveInteger ~ "," ~ PositiveInteger ~~> (ConstantColorOperation(_, _, _)) }
  def PredefinedColorOperation: Rule1[Symbol] = rule { Letters ~> (ConstantColorOperation(_)) }
  def IncrementColorOperation: Rule1[Symbol] = rule { Increment ~ "," ~ Increment ~ "," ~ Increment ~~> (IncrementColorOperation(_, _, _)) }

  def LeftStartingPoint = rule { "left" ~ push(StartingPoint.Left) }
  def BottomStartingPoint = rule { "bottom" ~ push(StartingPoint.Bottom) }
  def RealNumber = rule { oneOrMore(Digit) ~ optional( "." ~ oneOrMore(Digit) ) ~> (_.toDouble) }
  def PositiveInteger = rule { oneOrMore(Digit) ~> (_.toInt) }
  def Increment = rule { Sign ~ PositiveInteger ~~> ( (_: Int) * (_: Int) ) }
  def Sign = rule { ( "+" ~ push(1) ) | ( "-" ~ push(-1) ) }
  def Digit = rule { "0" - "9" }
  def Letters = rule { oneOrMore(Letter) }
  def Letter = rule { "a" - "z" | "A" - "Z" }

  def parseFractalDefinition(input: String) = ReportingParseRunner(FractalDefinition).run(input)
}
