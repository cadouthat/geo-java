package com.github.cadouthat.geojava;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GeoPointTest {

    final double TOLERANCE = 0.0000001;

    void assertPoint(String description, GeoPoint point, double lat, double lon) {
        assertEquals(String.format("latitude error%s", description), lat, point.getLatDegrees(), TOLERANCE);

        // Longitude has no effect if latitude is -90 or 90
        if (Math.abs(90 - Math.abs(lat)) > TOLERANCE) {
            assertEquals(String.format("longitude error%s", description), lon, point.getLonDegrees(), TOLERANCE);
        }

        assertTrue(String.format("latitude out of bounds%s", description),
                point.getLatDegrees() >= -90 && point.getLatDegrees() <= 90);

        assertTrue(String.format("longitude out of bounds%s", description),
                point.getLonDegrees() > -180 && point.getLonDegrees() <= 180);
    }

    void assertPoint(GeoPoint point, double lat, double lon) {
        assertPoint("", point, lat, lon);
    }

    void assertPoint(double inLat, double inLon, double expectedLat, double expectedLon) {
        String description = String.format(" on input (%f, %f)", inLat, inLon);
        assertPoint(description, new GeoPoint(inLat, inLon), expectedLat, expectedLon);
    }

    @Test
    public void testGoodStringInit() {
        GeoPoint point = new GeoPoint("1.12345", "-120");
        assertPoint(point, 1.12345, -120);
    }

    @Test
    public void testBadStringInit() {
        try {
            new GeoPoint("1.1", "1..3");
        }
        catch (NumberFormatException e) {
            return;
        }
        fail();
    }

    @Test
    public void testDoubleInit() {
        GeoPoint point = new GeoPoint(1.12345, -120);
        assertPoint(point, 1.12345, -120);
    }

    @Test
    public void testNormalization() {
        double[] basePoints = new double[] {
                45.54, 80.08,
                45.54, -80.08,
                -45.54, 80.08,
                -45.54, -80.08,
                0, 0,
                -0, -0,
                45, 180,
                -45, 180,
                89.9999, 179.9999,
                -89.9999, 179.9999,
                89.9999, -179.9999,
                -89.9999, -179.9999,
                90, 180,
                -90, 180,
                90, -180,
                -90, -180
        };
        for (int i = 0; i < basePoints.length; i += 2) {
            double lat = basePoints[i];
            double lon = basePoints[i + 1];
            double[] points = new double[]{
                    lat, lon,
                    lat + 360, lon + 360,
                    lat - 360, lon - 360,
                    lat + 720, lon + 720,
                    lat - 720, lon - 720,
                    180 - lat, lon + 180,
                    180 - lat, lon - 180,
                    180 - lat + 360, lon + 180 + 360,
                    180 - lat - 360, lon - 180 - 360
            };
            for (int j = 0; j < points.length; j += 2) {
                assertPoint(points[j], points[j + 1], lat, lon);
            }
        }
    }
}
