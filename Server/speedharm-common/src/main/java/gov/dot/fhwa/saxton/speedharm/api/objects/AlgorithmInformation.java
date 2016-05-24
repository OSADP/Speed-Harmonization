package gov.dot.fhwa.saxton.speedharm.api.objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Domain object representing data about an Algorithm or Algorithm instance in the SpeedHarm API
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlgorithmInformation {

    private Long id;
    private String className;
    private String versionString;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the fully-qualified class name of the associated algorithm implementation.
     * @return A String containing the fully qualified class name of algorithm class.
     */
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Get the String that uniquely identifies this version of the underlying algorithm implementation.
     * @return A String that uniquely identifies this version of the underlying algorithm implementation.
     */
    public String getVersionString() {
        return versionString;
    }

    public void setVersionString(String versionString) {
        this.versionString = versionString;
    }

    /**
     * Get the timestamp at which the algorithm described by this AlgorithmInstance started running.
     * @return The millisecond-resolution timestamp of execution for the algorithm.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Get the timestamp at which the algorithm described by this AlgorithmInformation ended execution.
     * @return The millisecond-resolution timestamp of termination for the algorithm, if it has stopped,
     *         Null, o.w.
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlgorithmInformation algorithm = (AlgorithmInformation) o;

        if (id != null ? !id.equals(algorithm.id) : algorithm.id != null) return false;
        if (className != null ? !className.equals(algorithm.className) : algorithm.className != null) return false;
        if (versionString != null ? !versionString.equals(algorithm.versionString) : algorithm.versionString != null)
            return false;
        if (startTime != null ? !startTime.equals(algorithm.startTime) : algorithm.startTime != null) return false;
        return !(endTime != null ? !endTime.equals(algorithm.endTime) : algorithm.endTime != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + (versionString != null ? versionString.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Algorithm{" +
                "id=" + id +
                ", className='" + className + '\'' +
                ", versionString='" + versionString + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
