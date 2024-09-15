# Voroni Tessellation

Given a canvas of `width x height` and a number of `seed` points (random by default), use the
Voroni tessellation algorithm to create the cells based on different metrics.

## Various Metrics

* [Euclidean Metric](images/voroni_euclidean.png)
* [Manhattan Metric](images/voroni_manhattan.png)
* [Maximum Metric](images/voroni_maximum.png)
* [Minimum Metric: Euclidean](images/voroni_minimum(euclidean).png)
* [Minkowski, p=1](images/voroni_minkowski(1.0).png) - equivalent to Manhattan
* [Minkowski, p=2](images/voroni_minkowski(2.0).png) - equivalent to Euclidean
* [Minkowski, p=3](images/voroni_minkowski(3.0).png)
* [Minkowski, p=4](images/voroni_minkowski(4.0).png)


## Metrics with Celestial Coordinates and Applications to Telescopes

From a discussion with GPT-4o. These may be useful for automosaics.

* [Celestial Coordintes](gpt/gpt1.png)
* [Applications to Astronomy](gpt/gpt2.png)