## Slopes of edgeScans on log-log axes

| Algorithm | sparse (m = 4n) | dense (m = 20n) |
|---|---|---|
| Kruskal | 1.055 | 1.119 |
| Prim | 1.000 | 1.000 |
| Kosaraju | 1.000 | 1.000 |
| Kahn | 1.000 | 1.000 |
| DagShortestPath | 1.000 | 1.000 |

## Counter divided by formula

| Algorithm | Ratio | Density | n=1000 | n=3162 | n=10000 | n=31623 | n=100000 |
|---|---|---|---|---|---|---|---|
| Kruskal | dsuOps / edgeScans | sparse | 2.972 | 2.848 | 2.776 | 2.757 | 2.751 |
| Kruskal | dsuOps / edgeScans | dense | 2.697 | 2.657 | 2.723 | 2.497 | 2.403 |
| Kruskal | dsuOps / E | sparse | 2.290 | 2.519 | 2.682 | 2.732 | 2.748 |
| Kruskal | dsuOps / E | dense | 0.580 | 0.606 | 0.565 | 0.754 | 0.894 |
| Kruskal | comparisons / (E log2 E) | sparse | 0.891 | 0.907 | 0.869 | 0.809 | 0.745 |
| Kruskal | comparisons / (E log2 E) | dense | 0.903 | 0.846 | 0.784 | 0.720 | 0.666 |
| Prim | comparisons / (E log2 V) | sparse | 2.196 | 2.186 | 2.180 | 2.178 | 2.167 |
| Prim | comparisons / (E log2 V) | dense | 2.616 | 2.533 | 2.473 | 2.421 | 2.381 |
| Prim | edgeScans / (2E) | sparse | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 |
| Prim | edgeScans / (2E) | dense | 1.000 | 1.000 | 1.000 | 1.000 | 1.000 |
| Kosaraju | edgeScans / (V + E) | sparse | 1.600 | 1.600 | 1.600 | 1.600 | 1.600 |
| Kosaraju | edgeScans / (V + E) | dense | 1.905 | 1.905 | 1.905 | 1.905 | 1.905 |
| Kahn | edgeScans / (V + E) | sparse | 0.800 | 0.800 | 0.800 | 0.800 | 0.800 |
| Kahn | edgeScans / (V + E) | dense | 0.952 | 0.952 | 0.952 | 0.952 | 0.952 |
| DagShortestPath | edgeScans / (V + E) | sparse | 0.800 | 0.800 | 0.800 | 0.800 | 0.800 |
| DagShortestPath | edgeScans / (V + E) | dense | 0.952 | 0.952 | 0.952 | 0.952 | 0.952 |

## Share of edges Kruskal examines (edgeScans / m)

| Density | n=1000 | n=3162 | n=10000 | n=31623 | n=100000 |
|---|---|---|---|---|---|
| sparse | 0.770 | 0.885 | 0.966 | 0.991 | 0.999 |
| dense | 0.215 | 0.228 | 0.207 | 0.302 | 0.372 |

## Median wall-clock time, ms

| Algorithm | Density | n=1000 | n=3162 | n=10000 | n=31623 | n=100000 |
|---|---|---|---|---|---|---|
| Kruskal | sparse | 6.5 | 6.1 | 20.3 | 31.6 | 102.0 |
| Kruskal | dense | 20.8 | 31.3 | 65.3 | 133.0 | 494.0 |
| Prim | sparse | 5.0 | 7.9 | 19.0 | 58.0 | 294.5 |
| Prim | dense | 15.4 | 43.3 | 132.5 | 516.6 | 2134.6 |
| Kosaraju | sparse | 3.7 | 4.7 | 9.8 | 29.2 | 173.3 |
| Kosaraju | dense | 14.0 | 9.9 | 40.3 | 201.4 | 871.2 |
| Kahn | sparse | 1.5 | 0.5 | 0.8 | 6.4 | 24.8 |
| Kahn | dense | 1.3 | 1.5 | 6.8 | 20.3 | 134.1 |
| DagShortestPath | sparse | 3.8 | 1.7 | 4.4 | 11.5 | 66.5 |
| DagShortestPath | dense | 4.8 | 2.8 | 7.8 | 57.7 | 253.6 |

## Maximum depth of the explicit stack (Kosaraju)

| Density | n=1000 | n=3162 | n=10000 | n=31623 | n=100000 |
|---|---|---|---|---|---|
| sparse | 626 | 1871 | 6002 | 18822 | 59668 |
| dense | 925 | 2937 | 9217 | 29073 | 91920 |
