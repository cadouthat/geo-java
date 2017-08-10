package com.github.cadouthat.geojava;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an enclosed area on Earth's surface, defined by a sequence of points connected by arcs
 */
public class GeoPolygon {

    /**
     * Any point known to be outside the polygon, used as the destination for even-odd intersection tests,
     * defaults to the North Pole
     */
    GeoPoint externalReference = new GeoPoint(90, 0);

    /**
     * Vertices of the polygon in sequence around the perimeter, the last will be connected to the first to
     * form a complete loop. Must contain at least 3 vertices to define a valid polygon.
     */
    List<GeoPoint> vertices;

    public GeoPolygon() {
        this.vertices = new ArrayList<>();
    }

    public GeoPolygon(List<GeoPoint> vertices) {
        this.vertices = new ArrayList<>(vertices);
    }

    public GeoPolygon(GeoPoint... vertices) {
        this(Arrays.asList(vertices));
    }

    public void setExternalReference(GeoPoint externalReference) {
        this.externalReference = externalReference;
    }

    /**
     * @return true if the given point lies inside the polygon
     */
    public boolean contains(GeoPoint point) {
        // Fewer than 3 vertices do not define a polygon and cannot contain anything
        if (vertices.size() < 0) return false;

        // The arc between the point and external reference will be used for testing
        GeoArc pointArc = new GeoArc(point, externalReference);

        // Count the number of intersections with edges
        int intersections = 0;

        for (int i = 0; i < vertices.size(); i++) {
            // Check each arc between adjacent vertices
            GeoPoint vertex1 = vertices.get(i);
            GeoPoint vertex2 = (i > 0) ? vertices.get(i - 1) : vertices.get(vertices.size() - 1);
            GeoArc vertexArc = new GeoArc(vertex1, vertex2);

            // Location of intersection doesn't matter, only the presence of intersection
            if (pointArc.intersect(vertexArc) != null) {
                intersections++;
            }
        }

        // The point is considered inside the polygon if the number of intersections is odd
        return (intersections % 2) > 0;
    }
}
