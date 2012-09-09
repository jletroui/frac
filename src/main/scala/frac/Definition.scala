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

import java.awt.Color

trait Definition {
  def turnAngle: Double
  def scaleRatio: Double
  def startingPoint: StartingPoint.Value
  def run(depth: Int, callback: Token => Unit)
}

trait Token

case class Primitive(value: String) extends Token {
  def this(c: Char) = this(c.toString)
  override val toString = value
}
trait ColorStatement extends Token {
  def changeColor(previousColor: Color): Color
}
object ConstantColorStatement {
  def apply(r: Int, g: Int, b: Int): ConstantColorStatement = ConstantColorStatement(new Color(r, g, b))
}
case class ConstantColorStatement(color: Color) extends ColorStatement {
  def changeColor(previousColor: Color) = color
  override lazy val toString = "{%d,%d,%d}".format(color.getRed, color.getGreen, color.getBlue)
}

object StartingPoint extends Enumeration {
  val Left, Bottom = Value
}