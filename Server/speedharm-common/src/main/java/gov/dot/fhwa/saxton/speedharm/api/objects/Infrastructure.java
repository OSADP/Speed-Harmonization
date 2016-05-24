package gov.dot.fhwa.saxton.speedharm.api.objects;

/**
 * Class for implementation of an Infrastructure device
 *
 * Infrastructure devices are to be the primary source of road condition
 * input to the algorithms in use by the server.
 */
public class Infrastructure {
    private String name; // Name of this Infrastructure entity
    private Double lat; // Latitude of this infrastructure
    private Double lon; // Longitude of this infrastructure
    private InfrastructureDataSource dataSource; // Data Source Type for this infrastructure

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public InfrastructureDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(InfrastructureDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Infrastructure that = (Infrastructure) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (lat != null ? !lat.equals(that.lat) : that.lat != null) return false;
        if (lon != null ? !lon.equals(that.lon) : that.lon != null) return false;
        return dataSource == that.dataSource;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (lat != null ? lat.hashCode() : 0);
        result = 31 * result + (lon != null ? lon.hashCode() : 0);
        result = 31 * result + (dataSource != null ? dataSource.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Infrastructure{" +
                "name='" + name + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", dataSource=" + dataSource +
                '}';
    }
}
