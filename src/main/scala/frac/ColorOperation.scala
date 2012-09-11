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

import java.awt.Color

/** A color operation instructs for a change of color. */
trait ColorOperation extends Symbol {
  def changeColor(previousColor: Color): Color
}

object ColorHelper {
  def colorComponent(unclean: Int) = if (unclean < 0) 256 + (unclean % 256) else unclean % 256
}

object ConstantColorOperation {
  import ColorHelper._

  val predefined = classOf[Color]
    .getFields
    .filter(_.getType == classOf[Color])
    .map(f => f.getName -> f.get(null).asInstanceOf[Color])
    .toMap

  def apply(r: Int, g: Int, b: Int): ConstantColorOperation = ConstantColorOperation(new Color(colorComponent(r), colorComponent(g), colorComponent(b)))
  def apply(name: String): ConstantColorOperation = ConstantColorOperation(predefined(name))

  def findPredifinedName(c: Color) = predefined
    .find(_._2 == c)
    .map(_._1)
}
case class ConstantColorOperation(color: Color) extends ColorOperation {
  require(color != null, "color must not be null")

  def changeColor(previousColor: Color) = color

  override lazy val toString = ConstantColorOperation.findPredifinedName(color) match {
    case Some(name) => "{%s}".format(name)
    case _ => "{%d,%d,%d}".format(color.getRed, color.getGreen, color.getBlue)
  }
}

case class IncrementColorOperation(redIncrement: Int, greenIncrement: Int, blueIncrement: Int) extends ColorOperation {
  import ColorHelper._

  def changeColor(previousColor: Color) = new Color(
    colorComponent(previousColor.getRed + redIncrement),
    colorComponent(previousColor.getGreen + greenIncrement),
    colorComponent(previousColor.getBlue + blueIncrement))

  override lazy val toString = "{%+d,%+d,%+d}".format(redIncrement, greenIncrement, blueIncrement)
}