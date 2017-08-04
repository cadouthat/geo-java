package com.github.cadouthat.geojava;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

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
    public void testShortArc() {
        assertArcLength(40.7623756, -73.9961386, 40.7618880, -73.9953661, 84.7);
    }

    @Test
    public void testMaxLength() {
        assertArcLength(0, 0, 0, 180, EARTH_CIRCUM_METRES / 2);
    }

    @Test
    public void testPoleToPole() {
        assertArcLength(90, 0, -90, 0, EARTH_CIRCUM_METRES / 2);
    }

    @Test
    public void testAcrossPrimeMeridian() {
        assertArcLength(27.2204411, -81.3867188, 58.8468166, 29.6269083, 8551560);
    }

    @Test
    public void testAcross180thMeridian() {
        assertArcLength(45.837895, 126.492845, 16.333099, -96.541917, 11861180);
    }

    @Test
    public void testAcrossNorthPole() {
        assertArcLength(40, 120, 80, -60, 6671370);
    }

    @Test
    public void testAcrossSouthPole() {
        assertArcLength(-40, 120, -80, -60, 6671370);
    }

    @Test
    public void testAcrossEquator() {
        assertArcLength(40, 120, -20, 140, 6986360);
    }
}
