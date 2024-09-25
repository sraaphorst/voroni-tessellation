# Voronoi Tessellation

Given a canvas of `width x height` and a number of `seed` points (random by default), use the
Voronoi tessellation algorithm to create the cells based on different metrics.

## Various Metrics

* [Euclidean Metric](images/voronoi_euclidean.png)
* [Manhattan Metric](images/voronoi_manhattan.png)
* [Minimum Metric: Euclidean](images/voronoi_minimum(euclidean).png)
* [Minkowski, p=1](images/voronoi_minkowski(1.0).png) - equivalent to Manhattan
* [Minkowski, p=1.5](images/voronoi_minkowski(1.5).png)
* [Minkowski, p=2](images/voronoi_minkowski(2.0).png) - equivalent to Euclidean
* [Minkowski, p=2.5](images/voronoi_minkowski(2.5).png)
* [Minkowski, p=3](images/voronoi_minkowski(3.0).png)
* [Minkowski, p=4](images/voronoi_minkowski(4.0).png)
* [Maximum Metric](images/voronoi_maximum.png)
* [Cenberra](images/voronoi_canberra.png)
* [Mahalanobis](images/voronoi_mahalanobis.png)
* [Hamming](images/voronoi_hamming.png)


## Metrics with Celestial Coordinates and Applications to Telescopes

From a discussion with GPT-4o. These may be useful for automosaics.

* [Celestial Coordintes](gpt/gpt1.png)
* [Applications to Astronomy](gpt/gpt2.png)