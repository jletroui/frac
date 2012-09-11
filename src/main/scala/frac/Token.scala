package frac

import java.awt.Color

/** Represents one token in a definition rule */
trait Token

/** A primitive token. Primitives are token that have a rule to be executed */
case class Primitive(value: String) extends Token {
  def this(c: Char) = this(c.toString)
  override val toString = value
}
