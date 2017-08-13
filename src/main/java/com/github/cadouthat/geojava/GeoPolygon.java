package com.github.cadouthat.geojava;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an enclosed area on Earth's surface, defined by a sequence of points connected by arcs
 */
public class GeoPolygon {

    /**
     * The minimum distance between intersection points for them to be considered unique, in metres
     */
    static final double UNIQUE_INTERSECTION_DIST = 0.1;

    /**
     * Any point known to be outside the polygon, used as the destination for even-odd intersection tests,
     * defaults to the North Pole
     */
    GeoPoint externalReferenceA = new GeoPoint(90, 0);

    /**
     * Another point known to be outside the polygon, used as an alternative reference point whenever
     * it is closer to the point being tested, defaults to the South Pole
     */
    GeoPoint externalReferenceB = new GeoPoint(-90, 0);

    /**
     * Vertices of the polygon in sequence around the perimeter, the last will be connected to the first to
     * form a complete loop. Must contain at least 3 vertices to define a valid polygon.
     */
    List<GeoPoint> vertices;

    public GeoPolygon(List<GeoPoint> vertices) {
        this.vertices = new ArrayList<>(vertices);
    }

    public GeoPolygon(GeoPoint... vertices) {
        this(Arrays.asList(vertices));
    }

    public void setExternalReferenceA(GeoPoint externalReferenceA) {
        this.externalReferenceA = externalReferenceA;
    }

    public void setExternalReferenceB(GeoPoint externalReferenceB) {
        this.externalReferenceB = externalReferenceB;
    }

    /**
     * @return true if the given point lies inside the polygon, based on the even-odd rule
     */
    public boolean contains(GeoPoint point) {
        // Fewer than 3 vertices do not define a polygon and cannot contain anything
        if (vertices.size() < 3) return false;

        // The shortest arc between the point and external references will be used for testing
        GeoArc pointArcA = new GeoArc(point, externalReferenceA);
        GeoArc pointArcB = new GeoArc(point, externalReferenceB);
        GeoArc pointArc = (pointArcA.length() < pointArcB.length()) ? pointArcA : pointArcB;

        GeoPoint[] intersectionPoints = new GeoPoint[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            // Check each arc between adjacent vertices
            GeoPoint vertex1 = vertices.get(i);
            GeoPoint vertex2 = (i > 0) ?
                    vertices.get(i - 1) :
                    vertices.get(vertices.size() - 1);
            GeoArc vertexArc = new GeoArc(vertex1, vertex2);

            // Store the intersection point on this edge (if any)
            intersectionPoints[i] = pointArc.intersect(vertexArc);
        }

        // Count the number of unique intersections
        int intersections = 0;
        for (int i = 0; i < intersectionPoints.length; i++) {
            GeoPoint iPoint = intersectionPoints[i];
            if (iPoint == null) continue;

            // If there was an intersection on the previous edge, determine if iPoint is a duplicate
            GeoPoint prevPoint = (i > 0) ?
                    intersectionPoints[i - 1] :
                    intersectionPoints[intersectionPoints.length - 1];
            if (prevPoint != null) {
                GeoArc comparisonArc = new GeoArc(prevPoint, iPoint);
                if (comparisonArc.length() <= UNIQUE_INTERSECTION_DIST) {
                    // iPoint will not be counted, but prevPoint has or will be
                    iPoint = null;
                }
            }

            if (iPoint != null) intersections++;
        }

        // The point is considered inside the polygon if the number of intersections is odd
        return (intersections % 2) > 0;
    }
}
