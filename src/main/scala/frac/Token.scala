package frac

import java.awt.Color

/** Represents one token in a definition rule */
trait Token

/** A primitive token. Primitives are token that have a rule to be executed */
case class Primitive(value: String) extends Token {
  def this(c: Char) = this(c.toString)
  override val toString = value
}

/** A color statement instructs for a change of color. It does not contain a rule */
trait ColorStatement extends Token {
  def changeColor(previousColor: Color): Color
}

object ConstantColorStatement {
  val predefined = classOf[Color]
    .getFields
    .filter(_.getType == classOf[Color])
    .map(f => f.getName -> f.get(null).asInstanceOf[Color])
    .toMap

  def apply(r: Int, g: Int, b: Int): ConstantColorStatement = ConstantColorStatement(new Color(r, g, b))
  def apply(name: String): ConstantColorStatement = ConstantColorStatement(predefined(name))

  def findPredifinedName(c: Color) = predefined
      .find(_._2 == c)
      .map(_._1)
}
case class ConstantColorStatement(color: Color) extends ColorStatement {
  def changeColor(previousColor: Color) = color
  override lazy val toString = ConstantColorStatement.findPredifinedName(color) match {
    case Some(name) => "{%s}".format(name)
    case _ => "{%d,%d,%d}".format(color.getRed, color.getGreen, color.getBlue)
  }
}
case class IncrementColorStatement(redIncrement: Int, greenIncrement: Int, blueIncrement: Int) extends ColorStatement {
  def changeColor(previousColor: Color) = new Color(
    previousColor.getRed + redIncrement,
    previousColor.getGreen + greenIncrement,
    previousColor.getBlue + blueIncrement)
  override lazy val toString = "{%+d,%+d,%+d}".format(redIncrement, greenIncrement, blueIncrement)
}