package com.github.cadouthat.geojava;

/**
 * Represents the shortest path between two points on a great circle on Earth's surface
 */
public class GeoArc {

    public static final double EARTH_RADIUS_METRES = 6371000;

    /**
     * First endpoint of the arc
     */
    GeoPoint pointA;
    /**
     * Second endpoint of the arc
     */
    GeoPoint pointB;

    public GeoArc(GeoPoint pointA, GeoPoint pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
    }

    /**
     * Calculates the length of the arc along the Earth's surface using a formula from
     * https://en.wikipedia.org/wiki/Great-circle_distance
     *
     * @return distance in metres
     */
    public double length() {
        double dLon = Math.abs(pointA.lon - pointB.lon);
        double sinPart = Math.sin(pointA.lat) * Math.sin(pointB.lat);
        double cosPart = Math.cos(pointA.lat) * Math.cos(pointB.lat) * Math.cos(dLon);
        double spanAngle = Math.acos(sinPart + cosPart);
        return EARTH_RADIUS_METRES * spanAngle;
    }
}
