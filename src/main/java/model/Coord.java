package model;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
@author Sambhav D Sethia
 */
public class Coord {
    @JsonProperty("lon")
    private double lon;
    @JsonProperty("lat")
    private double lat;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
