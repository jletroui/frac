package frac

import java.awt.Graphics

case class Point(x: Double, y: Double)

class GraphicsRenderer(g: Graphics, moveLength: Double, turnAngle: Double, startAngle: Double = 0.0) extends Renderer[Unit]
{
    private var currentPoint = Point(0, 0)
    private var currentAngle = startAngle
    private var (minPoint, maxPoint) = (Point(0, 0), Point(0, 0))
    private var isPenDown = true

    def render(definition: Definition, depth: Int)
    {
        currentPoint = Point(0, 0)
        minPoint = Point(0, 0)
        maxPoint = Point(0, 0)
        currentAngle = startAngle
        isPenDown = true

        definition.run(depth, callback(false))
    }

    private def callback(draw: Boolean)(c: Char)
    {
        c match {
            case '+' => currentAngle += turnAngle
            case '-' => currentAngle += turnAngle
            case '0' => isPenDown = false
            case '1' => isPenDown = true
            case 'F' | 'f' => move(draw, 1.0)
            case 'B' | 'b' => move(draw, -1.0)
        }
    }

    private def move(draw: Boolean, scale: Double)
    {
        val newPoint = Point(
            currentPoint.x + scale * moveLength * math.cos(currentAngle),
            currentPoint.y + scale * moveLength * math.sin(currentAngle))

        if (newPoint.x < minPoint.x) minPoint = minPoint.copy(x = newPoint.x)
        if (newPoint.y < minPoint.y) minPoint = minPoint.copy(y = newPoint.y)
        if (newPoint.x > maxPoint.x) maxPoint = maxPoint.copy(x = newPoint.x)
        if (newPoint.y > maxPoint.y) maxPoint = maxPoint.copy(y = newPoint.y)

        if (draw && isPenDown) g.drawLine(currentPoint.x, currentPoint.y, newPoint.x, newPoint.y)

        currentPoint = newPoint
    }
}