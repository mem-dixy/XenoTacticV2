package com.xenotactic.gamelogic.utils

import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.radians
import com.xenotactic.gamelogic.globals.ALLOWABLE_DIRECTION_DIFF
import com.xenotactic.gamelogic.model.GRectInt
import com.xenotactic.gamelogic.model.GameUnitPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.pathing.HorizontalDirection
import com.xenotactic.gamelogic.pathing.VerticalDirection

import kotlin.math.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

const val LINE_INTERSECTION_DIFF_THRESHOLD = 0.01

infix fun Int.to(that: Int): GameUnitPoint = GameUnitPoint(this, that)

fun abs(f1: Float): Float {
    return if (f1 < 0) -f1 else f1
}


fun intersectRectangles(
    x1: Double,
    y1: Double,
    w1: Double,
    h1: Double,
    x2: Double,
    y2: Double,
    w2: Double,
    h2: Double,
): Boolean {
    return x1 < (x2 + w2) &&
            y1 < (y2 + h2) &&
            (x1 + w1) > x2 &&
            (y1 + h1) > y2
}

fun isFullyCoveredBy(
    x1: Double,
    y1: Double,
    w1: Double,
    h1: Double,
    x2: Double,
    y2: Double,
    w2: Double,
    h2: Double,
): Boolean {
    if (w1 > w2) return false
    if (h1 > h2) return false

    return x1 >= x2 && (x1 + w1) <= (x2 + w2) &&
            y1 >= y2 && (y1 + h1) <= (y2 + h2)
}

fun Point.verticalDirectionTo(v: Point, allowableDifference: Double = ALLOWABLE_DIRECTION_DIFF):
        VerticalDirection {
    if (abs(this.y - v.y) < allowableDifference) {
        return VerticalDirection.NONE
    }
    if (this.y > v.y) {
        return VerticalDirection.DOWN
    } else {
        return VerticalDirection.UP
    }
}

fun Point.horizontalDirectionTo(v: Point, allowableDifference: Double = ALLOWABLE_DIRECTION_DIFF):
        HorizontalDirection {
    if (abs(this.x - v.x) < allowableDifference) {
        return HorizontalDirection.NONE
    }
    if (this.x > v.x) {
        return HorizontalDirection.LEFT
    } else {
        return HorizontalDirection.RIGHT
    }
}

@OptIn(ExperimentalTime::class)
inline fun <T> measureTime(
    message: String? = null,
    printMessage: Boolean = true,
    block: () -> T
):
        Pair<Long, T> {
    val ret = measureTimedValue {
        block()
    }
    if (printMessage) {
        if (message == null) {
            println("Took ${ret.duration}")
        } else {
            println("$message: Took ${ret.duration}")
        }
    }
    return Pair(ret.duration.inWholeNanoseconds, ret.value)
}


fun toWorldCoordinates(gridSize: Double, entity: MapEntity, gameWidth: GameUnit, gameHeight: GameUnit) =
    toWorldCoordinates(
        gridSize,
        entity.gameUnitPoint, gameHeight, entityHeight = entity.height
    )

fun toWorldCoordinates(
    gridSize: Double, gameUnitPoint: GameUnitPoint, gameHeight: GameUnit, entityHeight: GameUnit = GameUnit(0)
): Pair<WorldUnit, WorldUnit> =
    toWorldCoordinates(
        gridSize,
        gameUnitPoint.x.value.toDouble(), gameUnitPoint.y.value.toDouble(), gameHeight, entityHeight
    )

fun toWorldCoordinates(
    gridSize: Double, point: Point, gameHeight: GameUnit, entityHeight: GameUnit = GameUnit(0)
): Pair<WorldUnit, WorldUnit> =
    toWorldCoordinates(
        gridSize,
        point.x, point.y, gameHeight, entityHeight
    )

fun toWorldCoordinates(
    gridSize: Double, x: Double, y: Double, gameHeight: GameUnit, entityHeight: GameUnit = GameUnit(0)
) = Pair(toWorldUnit(gridSize, x), toWorldUnit(gridSize, (gameHeight.value - y - entityHeight.value)))

fun toWorldUnit(gridSize: Double, value: Double) = value.toGameUnit().toWorldUnit(gridSize)

fun toWorldDimensions(width: GameUnit, height: GameUnit, gridSize: Double) =
    Pair(WorldUnit(width.value * gridSize), WorldUnit(height.value * gridSize))

fun toWorldDimensions(entity: MapEntity, gridSize: Double) =
    toWorldDimensions(entity.width, entity.height, gridSize)


fun angleRadians(v1: Point, v2: Point): Angle {
    return atan2(v2.y - v1.y, v2.x - v1.x).radians
}

//fun <E> Iterable<E>.sumOf(selector: (E) -> Float): Float {
//    var sum = 0f
//    for (element in this) {
//        sum += selector(element)
//    }
//    return sum
//}

fun Point.lerp(target: Point, alpha: Double): Point {
    val invAlpha = (1.0f - alpha).toFloat()
    return Point(
        this.x * invAlpha + target.x * alpha,
        this.y * invAlpha + target.y * alpha
    )
}

fun getIntersectionPointsOfLineSegmentAndRectangle(
    p1: Point,
    p2: Point,
    rectBottomLeft: Point,
    width: Float,
    height: Float
): Set<Point> {
    val lowerRectX = rectBottomLeft.x
    val upperRectX = rectBottomLeft.x + width
    val lowerRectY = rectBottomLeft.y
    val upperRectY = rectBottomLeft.y + height

    val lowerPointX = min(p1.x, p2.x)
    val upperPointX = max(p1.x, p2.x)

    val lowerPointY = min(p1.y, p2.y)
    val upperPointY = max(p1.y, p2.y)

    val intersectionPoints = mutableSetOf<Point>()

    if (p1.x == p2.x) {
        if (p1.x !in lowerRectX..upperRectX) return emptySet()
        if (lowerRectY in lowerPointY..upperPointY) intersectionPoints.add(
            Point(
                p1.x,
                lowerRectY
            )
        )
        if (upperRectY in lowerPointY..upperPointY) intersectionPoints.add(
            Point(
                p1.x,
                upperRectY
            )
        )
        return intersectionPoints.toSet()
    }

    if (p1.y == p2.y) {
        if (p1.y !in lowerRectY..upperRectY) return emptySet()
        if (lowerRectX in lowerPointX..upperPointX) intersectionPoints.add(
            Point(
                lowerRectX,
                p1.y
            )
        )
        if (upperRectX in lowerPointX..upperPointX) intersectionPoints.add(
            Point(
                upperRectX,
                p1.y
            )
        )
        return intersectionPoints.toSet()
    }

    val yLineM = (p2.y - p1.y) / (p2.x - p1.x)
    val yLineB = p1.y - yLineM * p1.x

    val yFunc = { x: Double -> yLineM * x + yLineB }
    val xFunc = { y: Double -> (y - yLineB) / yLineM }

    val rectTopY = rectBottomLeft.y + height
    val rectBotY = rectBottomLeft.y

    val rectLeftX = rectBottomLeft.x
    val rectRightX = rectBottomLeft.x + width

    // Bottom side
    if (rectBotY in lowerPointY..upperPointY) {
        val intersectRectX = xFunc(rectBotY)
        if (intersectRectX in lowerRectX..upperRectX) {
            intersectionPoints.add(Point(intersectRectX, rectBotY))
        }
    }

    // Top side
    if (rectTopY in lowerPointY..upperPointY) {
        val intersectRectX = xFunc(rectTopY)
        if (intersectRectX in lowerRectX..upperRectX) {
            intersectionPoints.add(Point(intersectRectX, rectTopY))
        }
    }

    // Left side
    if (rectLeftX in lowerPointX..upperPointX) {
        val rectY = yFunc(rectLeftX)
        if (rectY in lowerRectY..upperRectY) {
            intersectionPoints.add(Point(rectLeftX, rectY))
        }
    }

    // Right side
    if (rectRightX in lowerPointX..upperPointX) {
        val rectY = yFunc(rectRightX)
        if (rectY in lowerRectY..upperRectY) {
            intersectionPoints.add(Point(rectRightX, rectY))
        }
    }


    return intersectionPoints.toSet()
}

fun getIntersectionPointsOfLineSegmentAndCircle(
    p1: Point,
    p2: Point,
    circleCenter: Point,
    radius: Double
): Set<Point> {
    val lowerX = min(p1.x, p2.x)
    val upperX = max(p1.x, p2.x)
    val lowerY = min(p1.y, p2.y)
    val upperY = max(p1.y, p2.y)
    val intersectionPoints =
        getIntersectionPointsOfLineEquationFromPointsAndCircle(p1, p2, circleCenter, radius)
    println("lowerX: $lowerX, upperX: $upperX, lowerY: $lowerY, upperY: $upperY")
    println(intersectionPoints)
    return intersectionPoints.filter {
        it.x in lowerX..upperX && it.y in lowerY..upperY
    }.toSet()
}

fun getIntersectionPointsOfVerticalLineAndCircle(
    a: Double, circleCenter: Point,
    radius: Double
): Set<Point> {
    // Vertical line: x = a for all y
    // The circle equation: (x - u)^2 + (y - v)^2 = r^2
    // becomes: (a - u)^2 + (y - v)^2 = r^2
    //
    // Solving for y yields:
    // (positive case) y = sqrt(r^2 - (a - u)^2) + v
    // (negative case) y = -sqrt(r^2 - (a - u)^2) + v
    //
    // The intersection points would be (a, (positive case) y), (a, (negative case) y)
    val circleU = circleCenter.x
    val circleV = circleCenter.y
    val sqrtTerm = radius * radius - (a - circleU).pow(2)
    if (sqrtTerm < 0) return setOf()
    if (sqrtTerm == 0.0) return setOf(Point(a, circleV))
    val sqrt = sqrt(sqrtTerm)
    return setOf(Point(a, circleV + sqrt), Point(a, circleV - sqrt))
}

fun getIntersectionPointsOfHorizontalLineAndCircle(
    b: Double, circleCenter: Point,
    radius: Double
): Set<Point> {
    // Horizontal line: y = b for all x
    // The circle equation: (x - u)^2 + (y - v)^2 = r^2
    // becomes: (x - u)^2 + (b - v)^2 = r^2
    //
    // Solving for x yields:
    // (positive case) x = sqrt(r^2 - (b - v)^2) + u
    // (negative case) x = -sqrt(r^2 - (b - v)^2) + u
    //
    // The intersection points would be ((postive case) x, b), ((negative case) x, b)
    val circleU = circleCenter.x
    val circleV = circleCenter.y
    val sqrtTerm = radius * radius - (b - circleV).pow(2)
    if (sqrtTerm < 0) return setOf()
    if (sqrtTerm == 0.0) return setOf(Point(circleU, b))
    val sqrt = sqrt(sqrtTerm)
    return setOf(Point(circleU + sqrt, b), Point(circleU - sqrt, b))
}

fun getIntersectionPointsOfLineEquationFromPointsAndCircle(
    p1: Point,
    p2: Point,
    circleCenter: Point,
    radius: Double
): Set<Point> {
    // Equation for a circle is: (x - u)^2 + (y - v)^2 = r^2
    // Equation for a line is: y = mx + b
    // where m = (y2 - y1) / (x2 - x1)
    // and   b = y1 - m * x1

    if (p1.x == p2.x && p1.y == p2.y) {
        return setOf()
    }

    val xDiff = abs(p2.x - p1.x)
    println("xDiff: $xDiff, meets threshold: ${xDiff < LINE_INTERSECTION_DIFF_THRESHOLD}")
    if (xDiff <= LINE_INTERSECTION_DIFF_THRESHOLD) {
        println("Vertical line")
        return getIntersectionPointsOfVerticalLineAndCircle(p1.x, circleCenter, radius)
    }

    if (p1.y == p2.y) {
        println("Horizontal line")
        return getIntersectionPointsOfHorizontalLineAndCircle(p1.y, circleCenter, radius)
    }

    val circleU = circleCenter.x
    val circleV = circleCenter.y

    println("normal line")
    println("p2.x - p1.x: ${p2.x - p1.x}")
    val yLineM = (p2.y - p1.y) / (p2.x - p1.x)
    val yLineB = p1.y - yLineM * p1.x

    val k = yLineB - circleV

    // ax^2 + bx + c
    val a = 1 + yLineM * yLineM
    val b = 2 * yLineM * k - 2 * circleU
    val c = circleU * circleU + k * k - radius * radius

    // Solving for a quadratic:
    // x = (-b + sqrt(b^2 - 4ac)) / 2a
    // x = (-b - sqrt(b^2 - 4ac)) / 2a
    println("a: $a, b: $b, c: $c")

    // b^2 - 4ac
    val sqrtTerm = b * b - 4 * a * c
    println("sqrtTerm: $sqrtTerm")
    if (sqrtTerm < 0) return setOf<Point>()

    val xFromPositive = (-b + sqrt(sqrtTerm)) / (2 * a)
    println("xFromPositive: $xFromPositive, yFromPositive: ${yLineM * xFromPositive + yLineB}")
    // There's only 1 term
    if (sqrtTerm == 0.0) {
        return setOf(Point(xFromPositive, yLineM * xFromPositive + yLineB))
    }
    val xFromNegative = (-b - sqrt(sqrtTerm)) / (2 * a)
    println("xFromNegative: $xFromNegative, yFromNegative: ${yLineM * xFromNegative + yLineB}")
    return setOf(
        Point(xFromPositive, yLineM * xFromPositive + yLineB),
        Point(xFromNegative, yLineM * xFromNegative + yLineB)
    )
}

fun String.removeAllIndents(): String {
    val lines = lines()
    return lines.joinToString(separator = "\n") {
        it.trimIndent()
    }
}


fun <T> sequenceOfNullable(element: T?) =
    if (element == null) emptySequence<T>() else sequenceOf(element)

fun rectangleIntersects(a: GRectInt, b: GRectInt): Boolean {
    return a.left < b.right && a.right > b.left &&
            a.bottom < b.top && a.top > b.bottom
}


fun main() {
    //     println(getIntersectionPoints(_root_ide_package_.com.soywiz.korma.geom.Point(0f, 0f), _root_ide_package_.com.soywiz.korma.geom.Point(8f, 8f), _root_ide_package_.com.soywiz.korma.geom.Point(3f, 3f), 3f))
    //    println(sqrt(2f) + 3)
    //    println(-sqrt(2f) + 3)

    val num = 12.65745

    val rounded = (num * 100).toInt() / 100.0

    println(rounded)

}