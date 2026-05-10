package com.moldovan.trainapp.domain;

import java.time.LocalTime;

public class StopTime {

    private final String stationId;
    private final LocalTime arrival;
    private final LocalTime departure;

    public StopTime(String stationId, LocalTime arrival, LocalTime departure) {
        this.stationId = stationId;
        this.arrival = arrival;
        this.departure = departure;
    }

    public String getStationId() { return stationId; }
    public LocalTime getArrival() { return arrival; }
    public LocalTime getDeparture() { return departure; }

    @Override
    public String toString() {
        return stationId + " arr=" + arrival + " dep=" + departure;
    }
}
