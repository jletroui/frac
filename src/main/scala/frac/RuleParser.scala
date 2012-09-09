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
  def ColorStatement = rule { "{" ~ RGBComponent ~ "," ~ RGBComponent ~ "," ~ RGBComponent ~ "}" ~~> (ConstantColorStatement(_: Int, _: Int, _: Int)) }
  def RGBComponent = rule { oneOrMore(Digit) ~> (_.toInt) }
  def Digit = rule { "0" - "9" }
  def Primitive = rule { noneOf("{") ~> (frac.Primitive(_)) }

  def parseRule(input: String) = ReportingParseRunner(Rule).run(input)
}
