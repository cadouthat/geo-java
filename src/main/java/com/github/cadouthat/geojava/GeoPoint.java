package com.github.cadouthat.geojava;

/**
 * Represents a point on the Earth's surface, defined by latitude and longitude
 */
public class GeoPoint {

    final double PI = Math.PI;
    final double TWO_PI = Math.PI * 2;
    final double HALF_PI = Math.PI / 2;

    /**
     * Latitude in radians
     */
    double lat;
    /**
     * Longitude in radians
     */
    double lon;

    /**
     * Without changing the final position, bring the lat/lon angles into [-PI/2, PI/2]/(-PI, PI] ranges
     */
    void normalize() {

        // Bring latitude into (-TWO_PI, TWO_PI)
        lat = lat % TWO_PI;
        // Bring latitude into [-PI, PI]
        if (lat > PI) lat -= TWO_PI;
        if (lat < -PI) lat += TWO_PI;

        // Bring latitude into [-HALF_PI, HALF_PI], flipping longitude as needed
        if (Math.abs(lat) > HALF_PI) {
            lon += PI;
            if (lat > 0) lat = PI - lat;
            else lat = -PI - lat;
        }

        // Bring longitude into (-TWO_PI, TWO_PI)
        lon = lon % TWO_PI;
        // Bring longitude into (-PI, PI]
        if (lon > PI) lon -= TWO_PI;
        if (lon <= -PI) lon += TWO_PI;
    }

    public GeoPoint(double latDegrees, double lonDegrees) {
        this.lat = latDegrees / 180 * Math.PI;
        this.lon = lonDegrees / 180 * Math.PI;
        normalize();
    }
    public GeoPoint(String latDegrees, String lonDegrees) {
        this.lat = Double.parseDouble(latDegrees) / 180 * Math.PI;
        this.lon = Double.parseDouble(lonDegrees) / 180 * Math.PI;
        normalize();
    }

    public double getLatDegrees() {
        return lat / Math.PI * 180;
    }

    public double getLonDegrees() {
        return lon / Math.PI * 180;
    }

    /**
     * @return latitude/longitude string in decimal degrees
     */
    public String toString() {
        return String.format("%f, %f", this.getLatDegrees(), this.getLonDegrees());
    }
}
