// By Sebastian Raaohorst, 2024.

package org.vorpal

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.pow

sealed class Metric {
    abstract fun distance(point1: Point, point2: Point): Double
}

data object EuclideanMetric : Metric() {
    /**
     * Note that while this is not the actual distance between point1 and point2, it is the
     * square of the distance. There is no reason to calculate the square root - an expensive
     * operation - and calculate the actual distance instead. The behaviour is the same.
     */
    override fun distance(point1: Point, point2: Point): Double =
        (point1.x - point2.x).toDouble().pow(2) + (point1.y - point2.y).toDouble().pow(2)

    override fun toString(): String =
        "euclidean"
}

data object ManhattanMetric : Metric() {
    override fun distance(point1: Point, point2: Point): Double =
        (point1.x - point2.x).toDouble().absoluteValue + (point1.y - point2.y).toDouble().absoluteValue

    override fun toString(): String =
        "manhattan"
}

data object MaximumMetric: Metric() {
    /**
     * Also known as the Chebyshev Distance: the maximum distance of the absolute values of the coordinates.
     */
    override fun distance(point1: Point, point2: Point): Double =
        max((point1.x - point2.x).toDouble().absoluteValue, (point1.y - point2.y).toDouble().absoluteValue)

    override fun toString(): String =
        "maximum"
}

data class MinimumMetric(private val otherMetric: Metric): Metric() {
    /**
     * Just for fun: this should be incredibly weird. Points that are nearer according to the specified
     * are actually judged to be further away. This will look very messy.
     */
    override fun distance(point1: Point, point2: Point): Double {
        val metric = otherMetric.distance(point1, point2)
        if (metric == 0.0) return 0.0
        else return 1.0/metric
    }

    override fun toString(): String =
        "minimum($otherMetric)"
}

/**
 * A generalization of the Euclidean and Manhattan metrics.
 * For p=1, the result is the Manhattan metric.
 * For p=2, the result if the Euclidean metric: however, taking the sqrt in the Euclidean metric
 *     it unnecessary, so we still have a separate implementation of it.
 */
data class MinkowskiDistanceMetric(private val p: Double): Metric() {
    override fun distance(point1: Point, point2: Point): Double =
        ((point1.x - point2.x).toDouble().absoluteValue.pow(p) + (point1.y - point2.y).toDouble().absoluteValue.pow(p)).pow(1.0/p)

    override fun toString(): String =
        "minkowski($p)"
}
