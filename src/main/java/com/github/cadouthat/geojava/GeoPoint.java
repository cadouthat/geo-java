package com.github.cadouthat.geojava;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

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

    /**
     * Construct from lat/lon in degrees
     */
    public GeoPoint(double latDegrees, double lonDegrees) {
        this.lat = latDegrees / 180 * Math.PI;
        this.lon = lonDegrees / 180 * Math.PI;
        normalize();
    }

    /**
     * Construct from lat/lon strings in degrees
     */
    public GeoPoint(String latDegrees, String lonDegrees) {
        this.lat = Double.parseDouble(latDegrees) / 180 * Math.PI;
        this.lon = Double.parseDouble(lonDegrees) / 180 * Math.PI;
        normalize();
    }

    /**
     * Construct from cartesian coordinates
     */
    public GeoPoint(Vector3D vec) {
        this.lat = vec.getDelta();
        this.lon = vec.getAlpha();
        normalize();
    }

    public double getLatDegrees() {
        return lat / Math.PI * 180;
    }

    public double getLonDegrees() {
        return lon / Math.PI * 180;
    }

    /**
     * Get the location of the point in cartesian coordinates, with the center of the sphere as the origin
     *
     * @return a unit-length vector
     */
    public Vector3D toCartesian() {
        return new Vector3D(lon, lat);
    }

    /**
     * @return latitude/longitude string in decimal degrees
     */
    public String toString() {
        return String.format("%f, %f", this.getLatDegrees(), this.getLonDegrees());
    }
}
