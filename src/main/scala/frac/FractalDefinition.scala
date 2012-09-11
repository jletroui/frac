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

import frac._

case class FractalDefinition(
                              seed: List[Symbol],
                              turnAngle: Double = 90.toRad,
                              scaleRatio: Double = 0.5,
                              startingPoint: StartingPoint.Value = StartingPoint.Left,
                              rules: List[Rule] = Nil)

case class Rule(name: Char, expression: List[Symbol])

/** Represents one symbol in a rule */
trait Symbol

/** A primitive token. Primitives are token that have a rule to be executed */
case class RuleReference(value: Char) extends Symbol {
  override lazy val toString = new String(Array(value))
}