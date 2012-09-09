package frac

import java.awt.{Color, Graphics}
import collection.immutable.Stack
import java.util.Date
import math._

case class Point(x: Double, y: Double)
case class RendererStats(turtleMoves: Int, turtleTurns: Int, sequenceLength: Int, duration: Long)

/** Renders the given definition on an AWT graphics */
class GraphicsRenderer(g: Graphics) extends Renderer[RendererStats] {
  private[this] val DIGIT = """([1-9])""".r
  private[this] val MARGIN = 20
  private[this] var position = Point(0, 0)
  private[this] var heading = 0.0
  private[this] var turnAngle = Pi / 2
  private[this] var (minPoint, maxPoint) = (Point(0, 0), Point(0, 0))
  private[this] var travelLength = 10.0
  private[this] var stateStack = Stack.empty[TurtleState]
  private[this] var (turtleMovesCounter, turtleTurnsCounter, sequenceCounter) = (0, 0, 0)
  private[this] var repetitionCount = 1
  private[this] var strokeColor = Color.black

  private case class TurtleState(position: Point, heading: Double, moveLength: Double)

  def render(definition: Definition, depth: Int) : RendererStats = {
    val start = new Date().getTime
    // Dry run to compute size
    init(Point(0, 0) -> 10.0, definition)
    definition.run(depth, callback(false, definition.scaleRatio))

    // Center, scale, and draw
    init(computeTransformation, definition)
    definition.run(depth, callback(true, definition.scaleRatio))

    RendererStats(turtleMovesCounter, turtleTurnsCounter, sequenceCounter, new Date().getTime - start)
  }

  private def init(transformation: (Point, Double), definition: Definition) {
    position = transformation._1
    heading = definition.startingPoint match {
      case StartingPoint.Left => 0.0
      case StartingPoint.Bottom => -Pi / 2
    }
    travelLength = transformation._2
    this.turnAngle = definition.turnAngle
    minPoint = Point(0, 0)
    maxPoint = Point(0, 0)
    stateStack = Stack.empty[TurtleState]
    turtleMovesCounter = 0
    turtleTurnsCounter = 0
    sequenceCounter = 0
  }

  /** Look what is the scaling and translation that must be applied to fill the drawing space */
  private def computeTransformation = {
    val (boundsWidth, boundsHeight) = (g.getClipBounds.width.toDouble - 2 * MARGIN, g.getClipBounds.height.toDouble - 2 * MARGIN)
    val (width, height) = (maxPoint.x - minPoint.x, maxPoint.y - minPoint.y)
    val (boundsRatio, ratio) = (boundsWidth / boundsHeight, width / height)

    val scale = if (ratio > boundsRatio) boundsWidth / width else boundsHeight / height
    val (scaledWidth, scaledHeight) = (width * scale, height * scale)

    (Point(MARGIN - minPoint.x * scale + (boundsWidth - scaledWidth) / 2, MARGIN - minPoint.y * scale + (boundsHeight - scaledHeight) / 2), scale * 10.0)
  }


  /** Interpret the given character and update state accordingly */
  private def callback(draw: Boolean, scaleRatio: Double)(c: Token) {
    c match {
      case Primitive("+") =>
        heading -= turnAngle * repetitionCount
        turtleTurnsCounter += repetitionCount
        repetitionCount = 1
      case Primitive("-") =>
        heading += turnAngle * repetitionCount
        turtleTurnsCounter += repetitionCount
        repetitionCount = 1
      case Primitive("F") =>
        move(draw)
        turtleMovesCounter += repetitionCount
        repetitionCount = 1
      case Primitive("f") =>
        move(false)
        turtleMovesCounter += repetitionCount
        repetitionCount = 1
      case Primitive("[") =>
        stateStack = stateStack.push(TurtleState(position, heading, travelLength))
      case Primitive("]") =>
        val (state, newStack) = stateStack.pop2
        heading = state.heading
        travelLength = state.moveLength
        position = state.position
        stateStack = newStack
      case Primitive(">") =>
        travelLength *= scaleRatio
        repetitionCount = 1
      case Primitive("<") =>
        travelLength /= scaleRatio
        repetitionCount = 1
      case Primitive(DIGIT(number)) =>
        repetitionCount = number.toString.toInt
      case colorStatement : ColorStatement =>
        strokeColor = colorStatement.changeColor(strokeColor)
        g.setColor(strokeColor)
      case _ =>
        () // Ignores all other characters
    }

    sequenceCounter += 1
  }

  /** Move the turtle, and optionally draws the movement */
  private def move(draw: Boolean) {
    val newPoint = Point(
        position.x + travelLength * repetitionCount * cos(heading),
        position.y + travelLength * repetitionCount * sin(heading))

    if (newPoint.x < minPoint.x) minPoint = minPoint.copy(x = newPoint.x)
    if (newPoint.y < minPoint.y) minPoint = minPoint.copy(y = newPoint.y)
    if (newPoint.x > maxPoint.x) maxPoint = maxPoint.copy(x = newPoint.x)
    if (newPoint.y > maxPoint.y) maxPoint = maxPoint.copy(y = newPoint.y)

    if (draw) g.drawLine(position.x.toInt, position.y.toInt, newPoint.x.toInt, newPoint.y.toInt)

    position = newPoint
  }
}