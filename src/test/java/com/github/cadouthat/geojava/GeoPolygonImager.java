package com.github.cadouthat.geojava;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GeoPolygonImager {

    static final int IMAGE_WIDTH = 2048;
    static final int IMAGE_HEIGHT= 1024;

    static final int RED_ARGB = 0xFFFF0000;
    static final int WHITE_ARGB = 0xFFFFFFFF;

    /**
     * Map lat/lon point to image coords
     */
    static Point project(GeoPoint geoPoint, BufferedImage image,
                         GeoPoint minBound, GeoPoint maxBound) {
        double latSpan = maxBound.getLatDegrees() - minBound.getLatDegrees();
        double lonSpan = maxBound.getLonDegrees() - minBound.getLonDegrees();

        double normX = (geoPoint.getLonDegrees() - minBound.getLonDegrees()) / lonSpan;
        int x = (int)Math.round(normX * image.getWidth());
        if (x < 0) x = 0;
        if (x >= image.getWidth()) x = image.getWidth() - 1;

        double normY = (geoPoint.getLatDegrees() - minBound.getLatDegrees()) / latSpan;
        int y = (int)Math.round((1 - normY) * image.getHeight());
        if (y < 0) y = 0;
        if (y >= image.getHeight()) y = image.getHeight() - 1;

        return new Point(x, y);
    }

    /**
     * Map image coords to lat/lon point
     */
    static GeoPoint unProject(Point imagePoint, BufferedImage image,
                              GeoPoint minBound, GeoPoint maxBound) {
        double latSpan = maxBound.getLatDegrees() - minBound.getLatDegrees();
        double lonSpan = maxBound.getLonDegrees() - minBound.getLonDegrees();

        double normX = imagePoint.x / (double)image.getWidth();
        double lon = normX * lonSpan + minBound.getLonDegrees();

        double normY = imagePoint.y / (double)image.getHeight();
        double lat = (1 - normY) * latSpan + minBound.getLatDegrees();

        return new GeoPoint(lat, lon);
    }

    /**
     * Test polygon containment of each pixel point and color accordingly
     */
    static void drawPixels(GeoPolygon polygon, BufferedImage image,
                           GeoPoint minBound, GeoPoint maxBound) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                GeoPoint point = unProject(new Point(x, y), image, minBound, maxBound);
                boolean contains = polygon.contains(point);

                image.setRGB(x, y, contains ? RED_ARGB : WHITE_ARGB);
            }
        }
    }

    /**
     * Draw the border of the polygon on the image
     */
    static void drawPolygon(GeoPolygon polygon, BufferedImage image,
                            GeoPoint minBound, GeoPoint maxBound) {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1));

        for (int i = 0; i < polygon.vertices.size(); i++) {
            GeoPoint point = polygon.vertices.get(i);
            GeoPoint prev = (i > 0) ?
                    polygon.vertices.get(i - 1) :
                    polygon.vertices.get(polygon.vertices.size() - 1);

            Point imagePoint = project(point, image, minBound, maxBound);
            Point imagePrev = project(prev, image, minBound, maxBound);

            g.drawLine(imagePoint.x, imagePoint.y, imagePrev.x, imagePrev.y);
        }
    }

    /**
     * Create image and draw polygon test data
     */
    static BufferedImage buildImage(GeoPolygon polygon,
                                    int width, int height,
                                    GeoPoint minBound, GeoPoint maxBound) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        drawPixels(polygon, image, minBound, maxBound);
        drawPolygon(polygon, image, minBound, maxBound);

        return image;
    }

    static void writeImagePNG(GeoPolygon polygon,
                              GeoPoint minBound, GeoPoint maxBound,
                              String filename) {
        BufferedImage image = buildImage(polygon,
                IMAGE_WIDTH, IMAGE_HEIGHT,
                minBound, maxBound);

        try {
            File imageFile = new File(filename);
            ImageIO.write(image, "png", imageFile);
        }
        catch (IOException e) {
            System.err.println("Failed to write " + filename);
        }
    }

    public static void main(String[] args) {
        GeoPolygon triangle = new GeoPolygon(
                new GeoPoint(30, 0),
                new GeoPoint(0, 30),
                new GeoPoint(0, -30)
        );
        writeImagePNG(triangle,
                new GeoPoint(-90, -179.9), new GeoPoint(90, 180),
                "triangle.png");

        GeoPolygon quad = new GeoPolygon(
                new GeoPoint(30, -30),
                new GeoPoint(30, 30),
                new GeoPoint(0, 30),
                new GeoPoint(0, -30)
        );
        writeImagePNG(quad,
                new GeoPoint(-90, -179.9), new GeoPoint(90, 180),
                "quad.png");

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
        writeImagePNG(seattle,
                new GeoPoint(47, -123), new GeoPoint(48, -121),
                "seattle.png");
    }
}
