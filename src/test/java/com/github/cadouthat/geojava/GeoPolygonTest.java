package com.github.cadouthat.geojava;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GeoPolygonTest {

    void assertInnerOuter(GeoPolygon polygon, GeoPoint[] inner, GeoPoint[] outer) {

        for (GeoPoint point : inner) {
            assertTrue("should contain " + point, polygon.contains(point));
        }

        for (GeoPoint point : outer) {
            assertFalse("should not contain " + point, polygon.contains(point));
        }
    }

    @Test
    public void testIncompleteContain() {
        String message = "incomplete polygons cannot contain points";
        GeoPoint origin = new GeoPoint(0, 0);
        GeoPoint northPole = new GeoPoint(90, 0);
        GeoPoint southPole = new GeoPoint(-90, 0);

        GeoPolygon zeroPoints = new GeoPolygon();
        assertFalse(message, zeroPoints.contains(northPole));
        assertFalse(message, zeroPoints.contains(southPole));
        assertFalse(message, zeroPoints.contains(origin));

        GeoPolygon onePoint = new GeoPolygon(northPole);
        assertFalse(message, onePoint.contains(northPole));
        assertFalse(message, onePoint.contains(southPole));
        assertFalse(message, onePoint.contains(origin));

        GeoPolygon twoPoints = new GeoPolygon(
                new GeoPoint(10, -10),
                new GeoPoint(10, 10)
        );
        assertFalse(message, twoPoints.contains(northPole));
        assertFalse(message, twoPoints.contains(southPole));
        assertFalse(message, twoPoints.contains(origin));
    }

    @Test
    public void testTriangle() {
        GeoPolygon triangle = new GeoPolygon(
                new GeoPoint(0, -10),
                new GeoPoint(0, 10),
                new GeoPoint(10, 0)
        );

        GeoPoint[] inner = new GeoPoint[]{
                new GeoPoint(5, 1),
                new GeoPoint(1, -1),
                new GeoPoint(9, 0)
        };
        GeoPoint[] outer = new GeoPoint[]{
                new GeoPoint(9, -9),
                new GeoPoint(10, 10),
                new GeoPoint(11, 0),
                new GeoPoint(-1, 0)
        };

        assertInnerOuter(triangle, inner, outer);
    }

    @Test
    public void testQuad() {
        GeoPolygon quad = new GeoPolygon(
                new GeoPoint(0, -10),
                new GeoPoint(0, 10),
                new GeoPoint(10, 10),
                new GeoPoint(10, -10)
        );

        GeoPoint[] inner = new GeoPoint[]{
                new GeoPoint(5, 1),
                new GeoPoint(1, -1),
                new GeoPoint(9, 0),
                new GeoPoint(9, 9),
                new GeoPoint(1, 1)
        };
        GeoPoint[] outer = new GeoPoint[]{
                new GeoPoint(5, -11),
                new GeoPoint(5, 11),
                new GeoPoint(11, 11),
                new GeoPoint(1, -11),
                new GeoPoint(-1, 0)
        };

        assertInnerOuter(quad, inner, outer);
    }
}
