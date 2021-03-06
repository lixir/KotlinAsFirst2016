@file:Suppress("UNUSED_PARAMETER")

package lesson6.task1

import lesson1.task1.sqr

/**
 * Точка на плоскости
 */
data class Point(val x: Double, val y: Double) {
    /**
     * Пример
     *
     * Рассчитать (по известной формуле) расстояние между двумя точками
     */
    fun distance(other: Point): Double = Math.sqrt(sqr(x - other.x) + sqr(y - other.y))
}

/**
 * Треугольник, заданный тремя точками
 */
data class Triangle(val a: Point, val b: Point, val c: Point) {
    /**
     * Пример: полупериметр
     */
    fun halfPerimeter() = (a.distance(b) + b.distance(c) + c.distance(a)) / 2.0

    /**
     * Пример: площадь
     */
    fun area(): Double {
        val p = halfPerimeter()
        return Math.sqrt(p * (p - a.distance(b)) * (p - b.distance(c)) * (p - c.distance(a)))
    }

    /**
     * Пример: треугольник содержит точку
     */
    fun contains(p: Point): Boolean {
        val abp = Triangle(a, b, p)
        val bcp = Triangle(b, c, p)
        val cap = Triangle(c, a, p)
        return abp.area() + bcp.area() + cap.area() <= area()
    }
}

/**
 * Окружность с заданным центром и радиусом
 */
data class Circle(val center: Point, val radius: Double) {
    /**
     * Простая
     *
     * Рассчитать расстояние между двумя окружностями.
     * Расстояние между непересекающимися окружностями рассчитывается как
     * расстояние между их центрами минус сумма их радиусов.
     * Расстояние между пересекающимися окружностями считать равным 0.0.
     */
    fun distance(other: Circle): Double {
        val result = (Math.sqrt(sqr(center.x - other.center.x) + sqr(center.y - other.center.y))) - radius - other.radius
        if (result < 0) return 0.0 else return result
    }

    /**
     * Тривиальная
     *
     * Вернуть true, если и только если окружность содержит данную точку НА себе или ВНУТРИ себя
     */
    fun contains(p: Point): Boolean = p.distance(center) <= radius
}

/**
 * Отрезок между двумя точками
 */
data class Segment(val begin: Point, val end: Point)

/**
 * Средняя
 *
 * Дано множество точек. Вернуть отрезок, соединяющий две наиболее удалённые из них.
 * Если в множестве менее двух точек, бросить IllegalArgumentException
 */
fun diameter(vararg points: Point): Segment {
    if (points.size < 2) throw IllegalArgumentException()
    var max = points[0].distance(points[0])
    var max1 = points[0]
    var max2 = points[1]
    for (i in 0..points.size - 1) {
        for (j in i..points.size - 1) {
            if (points[i].distance(points[j]) > max) {
                max = points[i].distance(points[j])
                max1 = points[i]
                max2 = points[j]
            }
        }
    }
    return Segment(max1, max2)
}

/**
 * Простая
 *
 * Построить окружность по её диаметру, заданному двумя точками
 * Центр её должен находиться посередине между точками, а радиус составлять половину расстояния между ними
 */
fun circleByDiameter(diameter: Segment): Circle {
    val minX = Math.min(diameter.begin.x, diameter.end.x)
    val minY = Math.min(diameter.begin.y, diameter.end.y)
    val center = Point(minX + (Math.abs((diameter.begin.x - diameter.end.x) / 2)),
            minY + (Math.abs(diameter.begin.y - diameter.end.y) / 2))
    return Circle(center, diameter.begin.distance(diameter.end) / 2)
}

/**
 * Прямая, заданная точкой и углом наклона (в радианах) по отношению к оси X.
 * Уравнение прямой: (y - point.y) * cos(angle) = (x - point.x) * sin(angle)
 */
data class Line(val point: Point, val angle: Double) {
    /**
     * Средняя
     *
     * Найти точку пересечения с другой линией.
     * Для этого необходимо составить и решить систему из двух уравнений (каждое для своей прямой)
     */
    fun crossPoint(other: Line): Point {
        var x = 0.0
        var y = 0.0
        if (Math.abs(Math.cos(angle)) <= 0.0000000000000001) {
            x = point.x
            y = (x - other.point.x) * Math.tan(other.angle) + other.point.y
        } else {
            if (Math.abs(Math.cos(other.angle)) <= 0.0000000000000001) x = other.point.x
            else x = (point.x * Math.tan(angle) - other.point.x * Math.tan(other.angle) - point.y + other.point.y) /
                    (Math.tan(angle) - Math.tan(other.angle))
            y = (x - point.x) * Math.tan(angle) + point.y
        }
        return Point(x, y)
    }
}

/**
 * Средняя
 *
 * Построить прямую по отрезку
 */
fun lineBySegment(s: Segment): Line {
    val x0 = (s.end.x - s.begin.x)
    val y0 = (s.end.y - s.begin.y)
    var angle = 0.0
    if (x0 * y0 < 0.0) angle = Math.PI - Math.abs(Math.atan(y0 / x0))
    else  angle = Math.atan(y0 / x0)
    return Line(s.begin, angle)
}

/**
 * Средняя
 *
 * Построить прямую по двум точкам
 */
fun lineByPoints(a: Point, b: Point): Line = lineBySegment(Segment(a, b))

/**
 * Сложная
 *
 * Построить серединный перпендикуляр по отрезку или по двум точкам
 */
fun bisectorByPoints(a: Point, b: Point): Line {
    val x0 = (a.x - b.x)
    val y0 = (a.y - b.y)
    var angle = 0.0
    if (x0 * y0 < 0.0) angle = Math.PI - Math.abs(Math.atan(y0 / x0)) else if (x0 == 0.0) angle = -Math.PI / 2
    else if (y0 == 0.0) angle = 0.0 else angle = Math.atan(y0 / x0)
    val p = Point((Math.min(a.x, b.x) + Math.abs(x0) / 2), Math.min(a.y, b.y) + Math.abs(y0) / 2)
    if (angle > (Math.PI / 2)) angle -= Math.PI
    return Line(p, angle + (Math.PI / 2))
}

/**
 * Средняя
 *
 * Задан список из n окружностей на плоскости. Найти пару наименее удалённых из них.
 * Если в списке менее двух окружностей, бросить IllegalArgumentException
 */
fun findNearestCirclePair(vararg circles: Circle): Pair<Circle, Circle> {
    if (circles.size < 2) throw IllegalArgumentException()
    var circle1 = circles[0]
    var circle2 = circles[1]
    var minDistance = circles[0].distance(circles[1])
    for (i in 0..circles.size - 2) {
        for (j in i + 1..circles.size - 1) {
            if (circles[i].distance(circles[j]) < minDistance) {
                circle1 = circles[i]
                circle2 = circles[j]
                minDistance = circles[i].distance(circles[j])
            }
        }
    }
    return Pair(circle1, circle2)
}

/**
 * Очень сложная
 *
 * Дано три различные точки. Построить окружность, проходящую через них
 * (все три точки должны лежать НА, а не ВНУТРИ, окружности).
 * Описание алгоритмов см. в Интернете
 * (построить окружность по трём точкам, или
 * построить окружность, описанную вокруг треугольника - эквивалентная задача).
 */
fun circleByThreePoints(a: Point, b: Point, c: Point): Circle {
    val result = bisectorByPoints(a, b).crossPoint(bisectorByPoints(b, c))
    return Circle(result, result.distance(c))
}

/**
 * Очень сложная
 *
 * Дано множество точек на плоскости. Найти круг минимального радиуса,
 * содержащий все эти точки. Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит одну точку, вернуть круг нулевого радиуса с центром в данной точке.
 *
 * Примечание: в зависимости от ситуации, такая окружность может либо проходить через какие-либо
 * три точки данного множества, либо иметь своим диаметром отрезок,
 * соединяющий две самые удалённые точки в данном множестве.
 */
fun minContainingCircle(vararg points: Point): Circle {
    if (points.size == 0) throw IllegalArgumentException()
    if (points.size == 1) return Circle(points[0], 0.0)
    if (points.size == 2) return circleByDiameter(Segment(points[0], points[1]))
    val segment = diameter(*points)
    val max1 = segment.begin
    val max2 = segment.end
    val p = Point((Math.min(max1.x, max2.x) + Math.abs(max1.x - max2.x) / 2), Math.min(max1.y, max2.y)
            + Math.abs(max1.y - max2.y) / 2)
    val radius = max1.distance(p)
    var nmax = radius
    var elementMax = max1
    for (point in points) {
        if (p.distance(point) > nmax) {
            nmax = p.distance(point)
            elementMax = point
        }
    }
    if (Math.abs(elementMax.distance(p) - radius) <= 1e-12) return Circle(p, nmax)
    else return circleByThreePoints(max1, max2, elementMax)
}
