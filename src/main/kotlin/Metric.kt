// By Sebastian Raaohorst, 2024.

package org.vorpal

import kotlin.math.*

sealed class Metric {
    abstract fun distance(point1: Point, point2: Point): Double
}

object EuclideanMetric: Metric() {
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

object ManhattanMetric: Metric() {
    override fun distance(point1: Point, point2: Point): Double =
        (point1.x - point2.x).toDouble().absoluteValue + (point1.y - point2.y).toDouble().absoluteValue

    override fun toString(): String =
        "manhattan"
}

object MaximumMetric: Metric() {
    /**
     * Also known as the Chebyshev Distance: the maximum distance of the absolute values of the coordinates.
     */
    override fun distance(point1: Point, point2: Point): Double =
        max((point1.x - point2.x).toDouble().absoluteValue, (point1.y - point2.y).toDouble().absoluteValue)

    override fun toString(): String =
        "maximum"
}

class MahalanobisMetric(seeds: Collection<Point>) : Metric() {
    // Calculate the inverse of the covariance metric.
    private val s_inv: List<List<Double>> = run {
            // Mean values for x and y.
            val x_mean: Double = seeds.sumOf { it.x }.toDouble() / seeds.size
            val y_mean: Double = seeds.sumOf { it.y }.toDouble() / seeds.size

            // Variance and covariance.
            val sigma_x2 = seeds.sumOf { (it.x - x_mean).pow(2) } / seeds.size
            val sigma_y2 = seeds.sumOf { (it.y - y_mean).pow(2) } / seeds.size
            val covar_xy = seeds.map { (it.x - x_mean) * (it.y - y_mean) }.sum() / seeds.size

            // Invert the matrix. If the determinant is 0, then we throw as otherwise this will be infinity.
            val determinant = sigma_x2 * sigma_y2 - covar_xy.pow(2)
            if (determinant == 0.0) throw ArithmeticException("The covariance matrix is not invertible.")

            // Invert the matrix. If the determinant is 0, then we throw as otherwise this will be infinity.
            listOf(
                listOf(sigma_y2, -covar_xy).map { it / determinant },
                listOf(-covar_xy, sigma_x2).map { it / determinant }
            )
        }

    // B^T S^{-1} * B
    // [a1 = (p1.x - p2.x), a2 = (p1.y - p2.y)][s[0][0] s[0][1]] = [b1, b2]
    //                                         [s[1][0] s[1][1]]
    // Then:
    // [b1, b2][a1] = b1a1 + b2a2
    //         [a2]
    override fun distance(point1: Point, point2: Point): Double {
        val a1 = point1.x - point2.x
        val a2 = point1.y - point2.y

        val b1 = a1 * s_inv[0][0] + a2 * s_inv[0][1]
        val b2 = a1 * s_inv[1][0] + a2 * s_inv[1][1]
        return sqrt(b1 * a1 + b2 * a2)
    }

    override fun toString(): String =
        "mahalanobis"
}

class MinimumMetric(private val otherMetric: Metric): Metric() {
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
class MinkowskiMetric(private val p: Double): Metric() {
    override fun distance(point1: Point, point2: Point): Double =
        ((point1.x - point2.x).toDouble().absoluteValue.pow(p) + (point1.y - point2.y).toDouble().absoluteValue.pow(p)).pow(1.0/p)

    override fun toString(): String =
        "minkowski($p)"
}

/**
 * Hamming distance, which counts the number of coordinates that differ between two points.
 * This looks very odd.
 */
object HammingMetric: Metric() {
    override fun distance(point1: Point, point2: Point): Double {
        val xDiff = if (point1.x != point2.x) 1 else 0
        val yDiff = if (point1.y != point2.y) 1 else 0
        return (xDiff + yDiff).toDouble()
    }
    override fun toString(): String = "hamming"
}

object CanberraMetric : Metric() {
    override fun distance(point1: Point, point2: Point): Double {
        val xNumerator = (point1.x - point2.x).absoluteValue.toDouble()
        val xDenominator = (point1.x.absoluteValue + point2.x.absoluteValue).toDouble()
        val xTerm = if (xDenominator == 0.0) 0.0 else xNumerator / xDenominator
        val yNumerator = (point1.y - point2.y).absoluteValue.toDouble()
        val yDenominator = (point1.y.absoluteValue + point2.y.absoluteValue).toDouble()
        val yTerm = if (yDenominator == 0.0) 0.0 else yNumerator / yDenominator
        return xTerm + yTerm
    }
    override fun toString(): String = "canberra"
}
