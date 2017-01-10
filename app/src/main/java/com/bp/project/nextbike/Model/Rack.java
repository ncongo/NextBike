package com.bp.project.nextbike.Model;

/**
 * Created by Congo on 27.12.2016.
 */
import java.util.HashMap;
import java.util.Map;

public class Rack {

    private Integer id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Integer maxBikes;
    private Integer usedBikes;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getMaxBikes() {
        return maxBikes;
    }

    public void setMaxBikes(Integer maxBikes) {
        this.maxBikes = maxBikes;
    }

    public Integer getUsedBikes() {
        return usedBikes;
    }

    public void setUsedBikes(Integer usedBikes) {
        this.usedBikes = usedBikes;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
