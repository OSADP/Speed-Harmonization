package gov.dot.fhwa.saxton.speedharm.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Database persistence class for RTMSStatusUpdates,
 *
 * Primarily should be used in a read-only fashion, since we don't want to be tampering
 * with the actual RTMS measurements.
 */
@Entity
@Table(name = "dbo.tblRtmsHistory")
public class RTMSStatusUpdateEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "RTMS_NETWORK_ID")
    private Integer rtmsNetworkId;

    @Column(name = "RTMS_NAME")
    private String rtmsName;

    @Column(name = "Zone")
    private Integer zone;

    @Column(name = "Speed")
    private Integer speed;

    @Column(name = "FWDLK_Speed")
    private Integer fwdlkSpeed;

    @Column(name = "Volume")
    private Integer volume;

    @Column(name = "Vol_Mid")
    private Integer volMid;

    @Column(name = "Vol_Long")
    private Integer volLong;

    @Column(name = "Vol_Extra_Long")
    private Integer volExtraLong;

    @Column(name = "Occupancy")
    private Double occupancy;

    @Column(name = "MsgNumber")
    private Integer msgNumber;

    @Column(name = "DateTimeStamp")
    private LocalDateTime timestamp;

    @Column(name = "SensorErrRate")
    private Double sensorErrorRate;

    @Column(name = "HealthByte")
    private Integer health;

    @Column(name = "SpeedUnits")
    private Integer speedUnits;

    @Column(name = "Vol_Mid2")
    private Integer volMid2;

    @Column(name = "Vol_Long2")
    private Integer volLong2;

    public Integer getRtmsNetworkId() {
        return rtmsNetworkId;
    }

    public void setRtmsNetworkId(Integer rtmsNetworkId) {
        this.rtmsNetworkId = rtmsNetworkId;
    }

    public String getRtmsName() {
        return rtmsName;
    }

    public void setRtmsName(String rtmsName) {
        this.rtmsName = rtmsName;
    }

    public Integer getZone() {
        return zone;
    }

    public void setZone(Integer zone) {
        this.zone = zone;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getFwdlkSpeed() {
        return fwdlkSpeed;
    }

    public void setFwdlkSpeed(Integer fwdlkSpeed) {
        this.fwdlkSpeed = fwdlkSpeed;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Integer getVolMid() {
        return volMid;
    }

    public void setVolMid(Integer volMid) {
        this.volMid = volMid;
    }

    public Integer getVolLong() {
        return volLong;
    }

    public void setVolLong(Integer volLong) {
        this.volLong = volLong;
    }

    public Integer getVolExtraLong() {
        return volExtraLong;
    }

    public void setVolExtraLong(Integer volExtraLong) {
        this.volExtraLong = volExtraLong;
    }

    public Double getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(Double occupancy) {
        this.occupancy = occupancy;
    }

    public Integer getMsgNumber() {
        return msgNumber;
    }

    public void setMsgNumber(Integer msgNumber) {
        this.msgNumber = msgNumber;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setDateTimeStamp(LocalDateTime dateTimeStamp) {
        this.timestamp = dateTimeStamp;
    }

    public Double getSensorErrorRate() {
        return sensorErrorRate;
    }

    public void setSensorErrorRate(Double sensorErrorRate) {
        this.sensorErrorRate = sensorErrorRate;
    }

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public Integer getSpeedUnits() {
        return speedUnits;
    }

    public void setSpeedUnits(Integer speedUnits) {
        this.speedUnits = speedUnits;
    }

    public Integer getVolMid2() {
        return volMid2;
    }

    public void setVolMid2(Integer volMid2) {
        this.volMid2 = volMid2;
    }

    public Integer getVolLong2() {
        return volLong2;
    }

    public void setVolLong2(Integer volLong2) {
        this.volLong2 = volLong2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RTMSStatusUpdateEntity that = (RTMSStatusUpdateEntity) o;

        if (rtmsNetworkId != null ? !rtmsNetworkId.equals(that.rtmsNetworkId) : that.rtmsNetworkId != null)
            return false;
        if (rtmsName != null ? !rtmsName.equals(that.rtmsName) : that.rtmsName != null) return false;
        if (zone != null ? !zone.equals(that.zone) : that.zone != null) return false;
        if (speed != null ? !speed.equals(that.speed) : that.speed != null) return false;
        if (fwdlkSpeed != null ? !fwdlkSpeed.equals(that.fwdlkSpeed) : that.fwdlkSpeed != null) return false;
        if (volume != null ? !volume.equals(that.volume) : that.volume != null) return false;
        if (volMid != null ? !volMid.equals(that.volMid) : that.volMid != null) return false;
        if (volLong != null ? !volLong.equals(that.volLong) : that.volLong != null) return false;
        if (volExtraLong != null ? !volExtraLong.equals(that.volExtraLong) : that.volExtraLong != null) return false;
        if (occupancy != null ? !occupancy.equals(that.occupancy) : that.occupancy != null) return false;
        if (msgNumber != null ? !msgNumber.equals(that.msgNumber) : that.msgNumber != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null)
            return false;
        if (sensorErrorRate != null ? !sensorErrorRate.equals(that.sensorErrorRate) : that.sensorErrorRate != null)
            return false;
        if (health != null ? !health.equals(that.health) : that.health != null) return false;
        if (speedUnits != null ? !speedUnits.equals(that.speedUnits) : that.speedUnits != null) return false;
        if (volMid2 != null ? !volMid2.equals(that.volMid2) : that.volMid2 != null) return false;
        return !(volLong2 != null ? !volLong2.equals(that.volLong2) : that.volLong2 != null);

    }

    @Override
    public int hashCode() {
        int result = rtmsNetworkId != null ? rtmsNetworkId.hashCode() : 0;
        result = 31 * result + (rtmsName != null ? rtmsName.hashCode() : 0);
        result = 31 * result + (zone != null ? zone.hashCode() : 0);
        result = 31 * result + (speed != null ? speed.hashCode() : 0);
        result = 31 * result + (fwdlkSpeed != null ? fwdlkSpeed.hashCode() : 0);
        result = 31 * result + (volume != null ? volume.hashCode() : 0);
        result = 31 * result + (volMid != null ? volMid.hashCode() : 0);
        result = 31 * result + (volLong != null ? volLong.hashCode() : 0);
        result = 31 * result + (volExtraLong != null ? volExtraLong.hashCode() : 0);
        result = 31 * result + (occupancy != null ? occupancy.hashCode() : 0);
        result = 31 * result + (msgNumber != null ? msgNumber.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (sensorErrorRate != null ? sensorErrorRate.hashCode() : 0);
        result = 31 * result + (health != null ? health.hashCode() : 0);
        result = 31 * result + (speedUnits != null ? speedUnits.hashCode() : 0);
        result = 31 * result + (volMid2 != null ? volMid2.hashCode() : 0);
        result = 31 * result + (volLong2 != null ? volLong2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RTMSStatusUpdateEntity{" +
                "rtmsNetworkId=" + rtmsNetworkId +
                ", rtmsName='" + rtmsName + '\'' +
                ", zone=" + zone +
                ", speed=" + speed +
                ", fwdlkSpeed=" + fwdlkSpeed +
                ", volume=" + volume +
                ", volMid=" + volMid +
                ", volLong=" + volLong +
                ", volExtraLong=" + volExtraLong +
                ", occupancy=" + occupancy +
                ", msgNumber=" + msgNumber +
                ", dateTimeStamp=" + timestamp +
                ", sensorErrorRate=" + sensorErrorRate +
                ", health=" + health +
                ", speedUnits=" + speedUnits +
                ", volMid2=" + volMid2 +
                ", volLong2=" + volLong2 +
                '}';
    }
}
