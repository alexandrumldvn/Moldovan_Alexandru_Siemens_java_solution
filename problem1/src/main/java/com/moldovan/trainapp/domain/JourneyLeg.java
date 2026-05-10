package com.moldovan.trainapp.domain;

import java.time.LocalTime;

public class JourneyLeg {

    private final String trainId;
    private final String trainName;
    private final String fromStationId;
    private final String toStationId;
    private final LocalTime departure;
    private final LocalTime arrival;

    public JourneyLeg(String trainId, String trainName,
                      String fromStationId, String toStationId,
                      LocalTime departure, LocalTime arrival) {
        this.trainId = trainId;
        this.trainName = trainName;
        this.fromStationId = fromStationId;
        this.toStationId = toStationId;
        this.departure = departure;
        this.arrival = arrival;
    }

    public String getTrainId() { return trainId; }
    public String getTrainName() { return trainName; }
    public String getFromStationId() { return fromStationId; }
    public String getToStationId() { return toStationId; }
    public LocalTime getDeparture() { return departure; }
    public LocalTime getArrival() { return arrival; }

    @Override
    public String toString() {
        return trainName + ": " + fromStationId + " " + departure
                + " -> " + toStationId + " " + arrival;
    }
}
