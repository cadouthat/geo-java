package com.github.cadouthat.geojava;

/**
 * Represents the shortest path between two points on a great circle on Earth's surface
 */
public class GeoArc {

    public static final double EARTH_RADIUS_METRES = 6371000;

    /**
     * Starting point of the arc
     */
    GeoPoint pointA;
    /**
     * Ending point of the arc
     */
    GeoPoint pointB;

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
        if (h > 1) h = 1;
        if (h < 0) h = 0;
        double theta = Math.asin(Math.sqrt(h));
        return EARTH_RADIUS_METRES * 2 * theta;
    }

    /**
     * Calculate the initial bearing from pointA toward pointB using a formula from
     * http://mathforum.org/library/drmath/view/55417.html
     *
     * @return bearing in radians
     */
    public double initialBearing() {
        double dLon = pointB.lon - pointA.lon;
        double y = Math.sin(dLon) * Math.cos(pointB.lat);
        double x = Math.cos(pointA.lat) * Math.sin(pointB.lat) -
                Math.sin(pointA.lat) * Math.cos(pointB.lat) * Math.cos(dLon);
        return Math.atan2(y, x);
    }

    /**
     * Converts the result of {@link #initialBearing} to degrees
     *
     * @return bearing in degrees
     */
    public double initialBearingDegrees() {
        return this.initialBearing() / Math.PI * 180;
    }

    /**
     * Determine intersection with another arc using formulas from
     * http://www.edwilliams.org/avform.htm#Intersection
     *
     * @return the point of intersection, or null if the arcs do not intersect
     */
    public GeoPoint intersect(GeoArc arcB) {
        GeoArc arcA = this;

        GeoPoint startA = arcA.pointA;
        GeoPoint startB = arcB.pointA;

        double bearingA = arcA.initialBearing();
        double bearingB = arcB.initialBearing();
        double dLat = startB.lat - startA.lat;
        double dLon = startB.lon - startA.lon;

        double dLatSin = Math.sin(dLat / 2);
        double dLonSin = Math.sin(dLon / 2);
        double deltaSinSq = dLatSin * dLatSin +
                Math.cos(startA.lat) * Math.cos(startB.lat) * dLonSin * dLonSin;
        double delta = 2 * Math.asin(Math.sqrt(deltaSinSq));
        if (delta == 0) return null;

        double thetaACosDenom = Math.sin(delta) * Math.cos(startA.lat);
        if (thetaACosDenom == 0) return null;
        double thetaACos = (Math.sin(startB.lat) - Math.sin(startA.lat) * Math.cos(delta)) / thetaACosDenom;
        if (Math.abs(thetaACos) > 1) return null;
        double thetaA = Math.acos(thetaACos);

        double thetaBCosDenom = Math.sin(delta) * Math.cos(startB.lat);
        if (thetaBCosDenom == 0) return null;
        double thetaBCos = (Math.sin(startA.lat) - Math.sin(startB.lat) * Math.cos(delta)) / thetaBCosDenom;
        if (Math.abs(thetaBCos) > 1) return null;
        double thetaB = Math.acos(thetaBCos);

        double thetaAB = thetaA;
        double thetaBA = thetaB;
        if (Math.sin(startB.lon - startA.lon) <= 0) {
            thetaAB = 2 * Math.PI - thetaAB;
        }
        else {
            thetaBA = 2 * Math.PI - thetaBA;
        }

        double alphaA = (bearingA - thetaAB + Math.PI) % (2 * Math.PI) - Math.PI;
        double alphaB = (thetaBA - bearingB + Math.PI) % (2 * Math.PI) - Math.PI;

        if (Math.sin(alphaA) == 0 && Math.sin(alphaB) == 0) return null;
        if (Math.sin(alphaA) * Math.sin(alphaB) < 0) return null;

        double alphaC = Math.acos(-Math.cos(alphaA) * Math.cos(alphaB) +
                Math.sin(alphaA) * Math.sin(alphaB) * Math.cos(delta));
        double deltaC = Math.atan2(Math.sin(delta) * Math.sin(alphaA) * Math.sin(alphaB),
                Math.cos(alphaB) + Math.cos(alphaA) * Math.cos(alphaC));
        double latC = Math.asin(Math.sin(startA.lat) * Math.cos(deltaC) +
                Math.cos(startA.lat) * Math.sin(deltaC) * Math.cos(bearingA));
        double dLonC = Math.atan2(Math.sin(bearingA) * Math.sin(deltaC) * Math.cos(startA.lat),
                Math.cos(deltaC) - Math.sin(startA.lat) * Math.sin(latC));
        double lonC = startA.lon + dLonC;

        //TODO test arc boundaries

        return new GeoPoint(latC / Math.PI * 180, lonC / Math.PI * 180);
    }
}
