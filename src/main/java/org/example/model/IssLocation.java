package org.example.model;

import java.time.LocalDateTime;

public class IssLocation {

    private LocalDateTime localDateTime;
    private double lat;
    private double lon;

    public IssLocation(final LocalDateTime localDateTime, final double lat, final double lon) {
        this.localDateTime = localDateTime;
        this.lat = lat;
        this.lon = lon;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return "IssLocation{" +
                "localDateTime=" + localDateTime +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
