package frac

import java.awt.Graphics

case class Point(x: Double, y: Double)

class GraphicsRenderer(g: Graphics, turnAngle: Double, startAngle: Double = 0.0) extends Renderer[Unit]
{
    private var currentPoint = Point(0, 0)
    private var currentAngle = startAngle
    private var (minPoint, maxPoint) = (Point(0, 0), Point(0, 0))
    private var isPenDown = true
    private var moveLength = 10.0

    def render(definition: Definition, depth: Int)
    {
        // Dry run to compute size
        init(Point(0, 0) -> 10.0)
        definition.run(depth, callback(false))

        // Center and draw
        init(computeTransformation)
        definition.run(depth, callback(true))
    }

    private def init(transformation: (Point, Double))
    {
        currentPoint = transformation._1
        moveLength = transformation._2
        minPoint = Point(0, 0)
        maxPoint = Point(0, 0)
        currentAngle = startAngle
        isPenDown = true
    }

    private def computeTransformation =
    {
        val (boundsWidth, boundsHeight) = (g.getClipBounds.width.toDouble, g.getClipBounds.height.toDouble)
        val (width, height) = (maxPoint.x - minPoint.x, maxPoint.y - minPoint.y)
        val (boundsRatio, ratio) = (boundsWidth / boundsHeight, width / height)

        val scale = if (ratio > boundsRatio) boundsWidth / width else boundsHeight / height
        val (scaledWidth, scaledHeight) = (width * scale, height * scale)

//        println("bounds(%d, %d, %d, %d) size(%d, %d)".format(bounds.x, bounds.y, bounds.width, bounds.height, width.toInt, height.toInt))

        (Point(-minPoint.x * scale + (boundsWidth - scaledWidth) / 2, -minPoint.y * scale + (boundsHeight - scaledHeight) / 2), scale * 10.0)
    }

    private def callback(draw: Boolean)(c: Char)
    {
        c match {
            case '+' => currentAngle -= turnAngle
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

        if (draw && isPenDown) g.drawLine(currentPoint.x.toInt, currentPoint.y.toInt, newPoint.x.toInt, newPoint.y.toInt)

        currentPoint = newPoint
    }
}