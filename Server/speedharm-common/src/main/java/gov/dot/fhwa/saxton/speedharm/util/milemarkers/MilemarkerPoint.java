package gov.dot.fhwa.saxton.speedharm.util.milemarkers;

/**
 * Representation of a lat/long/milemarker triad
 */
public class MilemarkerPoint {
    private double latitude;
    private double longitude;
    private double milemarker;

    public MilemarkerPoint(double latitude, double longitude, double milemarker) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.milemarker = milemarker;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getMilemarker() {
        return milemarker;
    }

    public void setMilemarker(double milemarker) {
        this.milemarker = milemarker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MilemarkerPoint that = (MilemarkerPoint) o;

        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;
        return Double.compare(that.milemarker, milemarker) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(milemarker);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "MilemarkerPoint{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", milemarker=" + milemarker +
                '}';
    }
}
