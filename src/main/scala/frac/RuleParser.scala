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
import java.awt.Color

class RuleParser extends Parser {
  def Rule = rule { zeroOrMore(Token) ~ EOI }
  def Token: Rule1[frac.Token] = rule { ColorStatement | Primitive }

  def Primitive = rule { noneOf("{") ~> (frac.Primitive(_)) }

  def ColorStatement = rule { "{" ~ ( RGBConstant | PredefinedColor | ColorIncrement )  ~ "}" }
  def RGBConstant: Rule1[Token] = rule { Digits ~ "," ~ Digits ~ "," ~ Digits ~~> (ConstantColorStatement(_, _, _)) }
  def PredefinedColor: Rule1[Token] = rule { Letters ~> (ConstantColorStatement(_)) }
  def ColorIncrement: Rule1[Token] = rule { Increment ~ "," ~ Increment ~ "," ~ Increment ~~> (IncrementColorStatement(_, _, _)) }

  def Digits = rule { oneOrMore(Digit) ~> (_.toInt) }
  def Increment = rule { Sign ~ Digits ~~> ( (_: Int) * (_: Int) ) }
  def Sign = rule { ("+" ~> (_ => 1) ) | ("-" ~> (_ => -1) ) }
  def Digit = rule { "0" - "9" }
  def Letters = rule { oneOrMore(Letter) }
  def Letter = rule { "a" - "z" | "A" - "Z" }

  def parseRule(input: String) = ReportingParseRunner(Rule).run(input)
}
