package gov.dot.fhwa.saxton.speedharm.util.milemarkers;

import java.util.ArrayList;
import java.util.List;

/**
 * MilemarkerConverter converts from latitude/longitude pairs to closest milemarker in a given trajectory
 */
public class MilemarkerConverter {

    private static final double EARTH_RADIUS_MI = 3690.00;

    private List<MilemarkerPoint> milemarkers = new ArrayList<>();

    public MilemarkerConverter(List<MilemarkerPoint> milemarkers) {
        this.milemarkers = milemarkers;
    }

    private double deg2rad(double deg) {
        return deg / 180.0 * Math.PI;
    }

    /**
     * Compute the haversine distance formula between two lat long pairs.
     *
     * @param lat1 The latitude of the first point
     * @param lon1 The longitude of the first point
     * @param lat2 The latitude of the second point
     * @param lon2 The longitude of the second point
     * @return The distance between the two points in miles
     */
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double delta_lat = lat2 - lat1;
        double delta_lon = lon2 - lon1;

        double alpha = deg2rad(delta_lat / 2.0);
        double beta = deg2rad(delta_lon / 2.0);

        double a = Math.pow(Math.sin(alpha), 2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                 * Math.pow(Math.sin(beta), 2);

        double c = Math.asin(Math.min(1, Math.sqrt(a)));

        return 2 * EARTH_RADIUS_MI * c;
    }

    /**
     * Convert from the (lat, lon) pair to the nearest milemarker in the defined trajectory.
     * @param lat The latitude of the input point
     * @param lon The longitude of the input point
     * @return The milemarker associated with the nearest point in the input set or -1, if no closest milemarker exists.
     */
    public double convert(double lat, double lon) {
        double minDistance = Double.POSITIVE_INFINITY;
        double closestMilemarker = -1;

        for (MilemarkerPoint m : milemarkers) {
            double dist = haversine(lat, lon, m.getLatitude(), m.getLongitude());
            if (dist <= minDistance) {
                minDistance = dist;
                closestMilemarker = m.getMilemarker();
            }
        }

        return closestMilemarker;
    }
}
