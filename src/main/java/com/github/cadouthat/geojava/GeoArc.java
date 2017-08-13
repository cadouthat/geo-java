package com.github.cadouthat.geojava;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Represents the shortest path between two points on a great circle on Earth's surface
 */
public class GeoArc {

    static final double TOLERANCE = 0.01;

    public static final double EARTH_RADIUS_METRES = 6371000;

    /**
     * Starting point of the arc
     */
    GeoPoint pointA;
    /**
     * Ending point of the arc
     */
    GeoPoint pointB;

    /**
     * Construct directly from start and end point
     */
    public GeoArc(GeoPoint pointA, GeoPoint pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
    }

    /**
     * Calculates the length of the arc along the Earth's surface using the haversine formula
     *
     * @return distance in metres
     */
    public double length() {
        double dLat = pointB.lat - pointA.lat;
        double dLon = pointB.lon - pointA.lon;

        double sinHaLat = Math.sin(dLat / 2);
        double sinHaLon = Math.sin(dLon / 2);

        double h = sinHaLat * sinHaLat +
                Math.cos(pointA.lat) * Math.cos(pointB.lat) * sinHaLon * sinHaLon;

        // Clamp rounding errors to real range
        if (h > 1) h = 1;
        if (h < 0) h = 0;

        double theta = Math.asin(Math.sqrt(h));
        return EARTH_RADIUS_METRES * 2 * theta;
    }

    /**
     * Given a point on the same great circle, determine whether it lies within this arc
     */
    public boolean contains(GeoPoint p) {
        // Get the arc length between the point and each endpoint
        double lenFromA = new GeoArc(pointA, p).length();
        double lenFromB = new GeoArc(pointB, p).length();
        // If the point is on the arc, these lengths should sum to the total length
        double sum = this.length() - lenFromA - lenFromB;
        return Math.abs(sum) < TOLERANCE;
    }

    /**
     * Determine point of intersection with another arc
     *
     * @return point of intersection, or null if there is no unique intersection
     */
    public GeoPoint intersect(GeoArc arcB) {
        GeoArc arcA = this;

        // If either arc is zero-length, no solution exists
        if (arcA.length() < TOLERANCE) return null;
        if (arcB.length() < TOLERANCE) return null;

        // Convert points to cartesian
        Vector3D p1 = arcA.pointA.toCartesian();
        Vector3D p2 = arcA.pointB.toCartesian();
        Vector3D p3 = arcB.pointA.toCartesian();
        Vector3D p4 = arcB.pointB.toCartesian();

        // Determine planes on which arcs lie
        Vector3D vA = Vector3D.crossProduct(p1, p2);
        Vector3D vB = Vector3D.crossProduct(p3, p4);

        // Zero vector indicates antipodal points, which have no solution
        if (vA.getNormSq() <= 0) return null;
        if (vB.getNormSq() <= 0) return null;

        // Determine line where planes intersect (which lies between points of circle intersection)
        Vector3D v = Vector3D.crossProduct(vA.normalize(), vB.normalize());
        double vLenSq = v.getNormSq();

        // Zero vector indicates arcs on the same plane, which would have infinite solutions
        if (vLenSq <= 0) return null;

        // Normalize to unit length, which will result in a point on the sphere surface
        v = v.scalarMultiply(1 / Math.sqrt(vLenSq));

        // Convert the intersection points from cartesian to GeoPoint
        GeoPoint s1 = new GeoPoint(v);
        GeoPoint s2 = new GeoPoint(v.negate());

        // If one of the points lies on both arcs, it is the solution
        if (arcA.contains(s1) && arcB.contains(s1)) {
            return s1;
        }
        if (arcA.contains(s2) && arcB.contains(s2)) {
            return s2;
        }

        return null;
    }
}
