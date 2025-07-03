package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Clouds {
    @JsonProperty("all")
    private int cloudCover;

    public int getCloudCover() { return cloudCover; }
    public void setCloudCover(int cloudCover) { this.cloudCover = cloudCover; }
}
