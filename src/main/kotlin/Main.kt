// By Sebastian Raaphorst, 2024.

package org.vorpal

import kotlinx.coroutines.*

import java.awt.Color
import java.awt.Graphics2D

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.random.Random
import kotlin.system.measureTimeMillis

/**
 * A point: used to represent the seeds, and to perform any distance calculations.
 */
data class Point(val x: Int, val y: Int) {
    companion object {
        val COLORS = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.MAGENTA,
            Color.CYAN, Color.PINK, Color.BLACK, Color.GRAY, Color.LIGHT_GRAY, Color.DARK_GRAY,
            Color.ORANGE)
    }
}

/**
 * A coroutine job to calculate the chunk of the specified size: for the given area
 * [startX, endX) x [startY, endY) and the collection of seeds, determine for each pixel
 * in the given area what seed it is closest to and color it.
 */
suspend fun calculateVoronoiChunk(startX: Int, endX: Int,
                                 startY: Int, endY: Int,
                                 seeds: Collection<Point>,
                                 metric: Metric,
                                 colors: List<Color>,
                                 img: BufferedImage) = withContext(Dispatchers.Default) {
    (startX until endX).forEach { x ->
        (startY until endY).forEach { y ->
            val point = Point(x, y)
            val closestSeed = seeds.minBy { metric.distance(it, point) }
            val colorIndex = seeds.indexOf(closestSeed)
            img.setRGB(x, y, colors[colorIndex].rgb)
    } }
}

/**
 * The coroutine entry point that generates the actual Voronoi diagram, returning it as a
 * BufferedImage.
 */
suspend fun generateVoronoi(width: Int, height: Int,
                           seeds: Collection<Point>,
                           metric: Metric,
                           colors: List<Color>): BufferedImage {
    val img = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val chunkSize = height / Runtime.getRuntime().availableProcessors()

    coroutineScope {
        // For each chunk, create a coroutine.
        val jobs = mutableListOf<Job>()
        (0 until height step chunkSize).forEach { i ->
            // If we cannot divide evenly, make the last chunk smaller.
            val endY = (i + chunkSize).coerceAtMost(height)

            // Create and launch the job.
            jobs.add(launch {
                calculateVoronoiChunk(0, width, i, endY, seeds, metric, colors, img)
            })
        }

        jobs.joinAll()
    }

    return img
}

/**
 * A function to render the seeds in a visible fashion as black circles in the image.
 */
fun renderSeeds(seeds: Collection<Point>, img: BufferedImage, radius: Int = 3) {
    val g2d: Graphics2D = img.createGraphics()
    g2d.color = Color.BLACK
    seeds.forEach { (x, y) -> g2d.fillOval(x - radius, y - radius, 2 * radius, 2 * radius) }
    g2d.dispose() // Dispose of the graphics context to free resources
}

/**
 * Generate a collection of random points in the grid defined by width x height.
 */
fun generateRandomSeeds(width: Int, height: Int, numSeeds: Int): Collection<Point> =
    List(numSeeds) { Point(Random.nextInt(width), Random.nextInt(height)) }

fun main() = runBlocking {
    val random = Random.Default
    val width = 2048
    val height = 1025
    val numSeeds = 45
    val seedRadius = 4

    // Generate the seeds for each of the Voronoi cells.
    val seeds = generateRandomSeeds(width, height, numSeeds)

    // Create enough colours to colour each of the Voronoi cells. If not specified, we use default
    // colours defined in Color, provided there are enough: otherwise, we create random ones.
    val colors = if (Point.COLORS.size >= numSeeds) Point.COLORS else {
        List(numSeeds) { Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)) }
    }

    val directory = File("images")
    if (!directory.exists()) directory.mkdir()
    if (!directory.exists() || directory.isFile) throw RuntimeException("Cannot create image directory.")

    // Comment this out to avoid generating Voronoi diagrams for all metrics.
    listOf(
        EuclideanMetric,
        ManhattanMetric,
        MaximumMetric,
        MinimumMetric(EuclideanMetric),
        MinkowskiDistanceMetric(1.0),
        MinkowskiDistanceMetric(2.0),
        MinkowskiDistanceMetric(3.0),
        MinkowskiDistanceMetric(4.0)
    ).forEach { metric ->
        val timeTaken = measureTimeMillis {
            val img = generateVoronoi(width, height, seeds, metric, colors)
            renderSeeds(seeds, img, seedRadius)
            ImageIO.write(img, "png", File(directory, "voronoi_$metric.png"))
        }

        // Statistics
        println("Time taken to generate the Voronoi tessellation for $metric: $timeTaken ms.")
    }
}
