# geo-java
Utilities for geospatial calculations written in Java

## Uses
* Normalize lat/lon coordinates
* Measure arc length between points
* Determine intersection point between arcs
* Test whether a point lies within a polygon (even-odd rule)

## Assumptions
* Models the earth as a sphere, with a radius of 6,371km
* Arcs (and edges of polygons) cannot span more than half the globe
* Double floating point precision