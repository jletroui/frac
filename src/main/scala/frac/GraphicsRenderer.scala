package frac

import java.awt.Graphics
import collection.immutable.Stack
import java.util.Date

case class Point(x: Double, y: Double)
case class RendererStats(segments: Int, tokens: Int, time: Long)

class GraphicsRenderer(g: Graphics) extends Renderer[RendererStats]
{
    private val MARGIN = 20
    private var position = Point(0, 0)
    private var angle = 0.0
    private var turnAngle = math.Pi / 2
    private var (minPoint, maxPoint) = (Point(0, 0), Point(0, 0))
    private var travelLength = 10.0
    private var stateStack = Stack.empty[TurtleState]
    private var (segmentCounter, tokenCounter) = (0, 0)

    private case class TurtleState(position: Point, angle: Double, moveLength: Double)

    def render(definition: Definition, depth: Int) : RendererStats =
    {
        val start = new Date().getTime
        // Dry run to compute size
        init(Point(0, 0) -> 10.0, definition)
        definition.run(depth, callback(false, definition.scaleRatio))

        // Center, scale, and draw
        init(computeTransformation, definition)
        definition.run(depth, callback(true, definition.scaleRatio))

        RendererStats(segmentCounter, tokenCounter, new Date().getTime - start)
    }

    private def init(transformation: (Point, Double), definition: Definition)
    {
        position = transformation._1
        angle = definition.startingPoint match {
            case StartingPoint.Left => 0.0
            case StartingPoint.Bottom => -math.Pi / 2
        }
        travelLength = transformation._2
        this.turnAngle = definition.turnAngle
        minPoint = Point(0, 0)
        maxPoint = Point(0, 0)
        stateStack = Stack.empty[TurtleState]
        segmentCounter = 0
        tokenCounter = 0
    }

    private def computeTransformation =
    {
        val (boundsWidth, boundsHeight) = (g.getClipBounds.width.toDouble - 2 * MARGIN, g.getClipBounds.height.toDouble - 2 * MARGIN)
        val (width, height) = (maxPoint.x - minPoint.x, maxPoint.y - minPoint.y)
        val (boundsRatio, ratio) = (boundsWidth / boundsHeight, width / height)

        val scale = if (ratio > boundsRatio) boundsWidth / width else boundsHeight / height
        val (scaledWidth, scaledHeight) = (width * scale, height * scale)

        (Point(MARGIN - minPoint.x * scale + (boundsWidth - scaledWidth) / 2, MARGIN - minPoint.y * scale + (boundsHeight - scaledHeight) / 2), scale * 10.0)
    }

    private def callback(draw: Boolean, scaleRatio: Double)(c: Char)
    {
        c match {
            case '+' => angle -= turnAngle
            case '-' => angle += turnAngle
            case 'F' =>
                move(draw, 1.0)
                segmentCounter += 1
            case 'f' =>
                move(false, 1.0)
                segmentCounter += 1
            case 'B' =>
                move(draw, -1.0)
                segmentCounter += 1
            case 'b' =>
                move(false, -1.0)
                segmentCounter += 1
            case '['=> stateStack = stateStack.push(TurtleState(position, angle, travelLength))
            case ']'=>
                val (state, newStack) = stateStack.pop2
                angle = state.angle
                travelLength = state.moveLength
                position = state.position
                stateStack = newStack
            case '>' => travelLength *= scaleRatio
            case '<' => travelLength /= scaleRatio
            case _ => ()
        }

        tokenCounter += 1
    }

    private def move(draw: Boolean, scale: Double)
    {
        val newPoint = Point(
            position.x + scale * travelLength * math.cos(angle),
            position.y + scale * travelLength * math.sin(angle))

        if (newPoint.x < minPoint.x) minPoint = minPoint.copy(x = newPoint.x)
        if (newPoint.y < minPoint.y) minPoint = minPoint.copy(y = newPoint.y)
        if (newPoint.x > maxPoint.x) maxPoint = maxPoint.copy(x = newPoint.x)
        if (newPoint.y > maxPoint.y) maxPoint = maxPoint.copy(y = newPoint.y)

        if (draw) g.drawLine(position.x.toInt, position.y.toInt, newPoint.x.toInt, newPoint.y.toInt)

        position = newPoint
    }
}