package com.github.cadouthat.geojava;

import org.junit.Test;

import static org.junit.Assert.*;

public class GeoArcTest {

    static final double TOLERANCE_RATIO = 0.001;

    static final double EARTH_CIRCUM_METRES = GeoArc.EARTH_RADIUS_METRES * Math.PI * 2;

    private void assertArcLength(double lat1, double lon1, double lat2, double lon2, double expectedLength) {
        GeoArc arc = new GeoArc(new GeoPoint(lat1, lon1), new GeoPoint(lat2, lon2));
        assertEquals("arc length error", expectedLength, arc.length(), TOLERANCE_RATIO * Math.abs(expectedLength));
    }

    @Test
    public void testZeroLength() {
        assertArcLength(-1.878327, -65.584714, -1.878327, -65.584714, 0);
    }

    @Test
    public void testShortLength() {
        assertArcLength(40.7623756, -73.9961386, 40.7618880, -73.9953661, 84.7);
    }

    @Test
    public void testMaxLength() {
        assertArcLength(0, 0, 0, 180, EARTH_CIRCUM_METRES / 2);
    }

    @Test
    public void testPoleToPoleLength() {
        assertArcLength(90, 0, -90, 0, EARTH_CIRCUM_METRES / 2);
    }

    @Test
    public void testAcrossPrimeMeridianLength() {
        assertArcLength(27.2204411, -81.3867188, 58.8468166, 29.6269083, 8551560);
    }

    @Test
    public void testAcross180thMeridianLength() {
        assertArcLength(45.837895, 126.492845, 16.333099, -96.541917, 11861180);
    }

    @Test
    public void testAcrossNorthPoleLength() {
        assertArcLength(40, 120, 80, -60, 6671370);
    }

    @Test
    public void testAcrossSouthPoleLength() {
        assertArcLength(-40, 120, -80, -60, 6671370);
    }

    @Test
    public void testAcrossEquatorLength() {
        assertArcLength(40, 120, -20, 140, 6986360);
    }

    @Test
    public void testContain() {
        GeoPoint a = new GeoPoint(34, 120);
        GeoPoint b = new GeoPoint(-20, 120);
        GeoPoint c = new GeoPoint(-15, 120);
        GeoArc arc = new GeoArc(a, b);
        assertTrue("should contain endpoints", arc.contains(a) && arc.contains(b));
        assertTrue("should contain inner point", arc.contains(c));
    }

    @Test
    public void testNonContain() {
        GeoPoint a = new GeoPoint(34, 120);
        GeoPoint b = new GeoPoint(-20, 120);
        GeoPoint c = new GeoPoint(35, 120);
        GeoPoint d = new GeoPoint(-25, 120);
        GeoPoint e = new GeoPoint(20, -60);
        GeoArc arc = new GeoArc(a, b);
        assertFalse("should not contain upper point", arc.contains(c));
        assertFalse("should not contain lower point", arc.contains(d));
        assertFalse("should not contain back point", arc.contains(e));
    }

    @Test
    public void testIntersectNearCoplanar() {
        GeoArc arcA = new GeoArc(new GeoPoint(16, 0), new GeoPoint(14, 50));
        GeoArc arcB = new GeoArc(new GeoPoint(15, 0), new GeoPoint(15, 50));
        assertNotNull("should intersect", arcA.intersect(arcB));
    }

    @Test
    public void testIntersectLocation() {
        GeoArc arcA = new GeoArc(new GeoPoint(1, -50), new GeoPoint(3, 50));
        GeoArc arcB = new GeoArc(new GeoPoint(2, -50), new GeoPoint(2, 50));
        GeoPoint expectedPoint = new GeoPoint(3.10965, -0.042);
        GeoPoint point = arcA.intersect(arcB);
        assertNotNull("should intersect", point);
        assertEquals("should be at expected lat", expectedPoint.getLatDegrees(), point.getLatDegrees(), TOLERANCE_RATIO);
        assertEquals("should be at expected lon", expectedPoint.getLonDegrees(), point.getLonDegrees(), TOLERANCE_RATIO);
    }

}
