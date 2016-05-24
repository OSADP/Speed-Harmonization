package gov.dot.fhwa.saxton.speedharm.util;

import gov.dot.fhwa.saxton.speedharm.util.milemarkers.MilemarkerConverter;
import gov.dot.fhwa.saxton.speedharm.util.milemarkers.MilemarkerPoint;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for MilemarkerConverter class functionality
 */
public class MilemarkerConverterTest {

    private static final double EPSILON = 0.0001;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testConvertOnePoint() throws Exception {
        List<MilemarkerPoint> points = new ArrayList<>();
        points.add(new MilemarkerPoint(0, 0, 0));

        MilemarkerConverter converter = new MilemarkerConverter(points);

        assertTrue(doubleEq(converter.convert(1, 1), 0, EPSILON));
    }

    @Test
    public void testConvertTwoPoints() throws Exception {
        List<MilemarkerPoint> points = new ArrayList<>();
        points.add(new MilemarkerPoint(0, 0, 0));
        points.add(new MilemarkerPoint(1, 1, 1));

        MilemarkerConverter converter = new MilemarkerConverter(points);

        assertTrue(doubleEq(converter.convert(0.4, 0.4), 0, EPSILON));
    }

    @Test
    public void testConvertFourPoints() throws Exception {
        List<MilemarkerPoint> points = new ArrayList<>();
        points.add(new MilemarkerPoint(0, 0, 0));
        points.add(new MilemarkerPoint(0, 1, 1));
        points.add(new MilemarkerPoint(1, 0, 2));
        points.add(new MilemarkerPoint(1, 1, 3));


        MilemarkerConverter converter = new MilemarkerConverter(points);

        assertTrue(doubleEq(converter.convert(0.5, 0.5), 3, EPSILON));
    }

    @Test
    public void testConvertTwoPoints2() throws Exception {
        List<MilemarkerPoint> points = new ArrayList<>();
        points.add(new MilemarkerPoint(0, 0, 0));
        points.add(new MilemarkerPoint(1, 1, 1));

        MilemarkerConverter converter = new MilemarkerConverter(points);

        assertTrue(doubleEq(converter.convert(0.6, 0.6), 1, EPSILON));
    }

    private boolean doubleEq(double a, double b, double eps) {
        return Math.abs(a - b) <= eps;
    }

    @Test
    public void testConvertEmptyList() throws Exception {
        List<MilemarkerPoint> points = new ArrayList<>();

        MilemarkerConverter converter = new MilemarkerConverter(points);

        assertTrue(doubleEq(converter.convert(0, 0), -1, EPSILON));
    }
}