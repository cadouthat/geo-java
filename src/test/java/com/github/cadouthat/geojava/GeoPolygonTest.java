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

    @Test
    public void testSeattle() {
        GeoPolygon seattle = new GeoPolygon(
                new GeoPoint(47.736389, -122.377089),
                new GeoPoint(47.735466, -122.285765),
                new GeoPoint(47.682331, -122.245253),
                new GeoPoint(47.647186, -122.274779),
                new GeoPoint(47.496164, -122.244567),
                new GeoPoint(47.525847, -122.304991),
                new GeoPoint(47.494772, -122.372969),
                new GeoPoint(47.577752, -122.423781),
                new GeoPoint(47.599055, -122.341384),
                new GeoPoint(47.661987, -122.437514)
        );

        GeoPoint[] inner = new GeoPoint[]{
                new GeoPoint(47.681869, -122.295378),
                new GeoPoint(47.649961, -122.370909),
                new GeoPoint(47.613406, -122.306365),
                new GeoPoint(47.552733, -122.365416),
                new GeoPoint(47.641635, -122.334517)
        };
        GeoPoint[] outer = new GeoPoint[]{
                new GeoPoint(47.615258, -122.415541),
                new GeoPoint(47.745625, -122.337950),
                new GeoPoint(47.601833, -122.264479),
                new GeoPoint(47.485029, -122.311858),
                new GeoPoint(47.747814, -122.036922)
        };

        assertInnerOuter(seattle, inner, outer);
    }
}
