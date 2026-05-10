package com.moldovan.trainapp.domain;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Train {

    private final String id;
    private String name;
    private String routeId;
    private int capacity;
    private final List<StopTime> schedule;
    private int delayMin;

    public Train(String id, String name, String routeId, int capacity, List<StopTime> schedule) {
        this.id = id;
        this.name = name;
        this.routeId = routeId;
        this.capacity = capacity;
        this.schedule = new ArrayList<>(schedule);
        this.delayMin = 0;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public List<StopTime> getSchedule() { return schedule; }

    public int getDelayMin() { return delayMin; }
    public void setDelayMin(int delayMin) { this.delayMin = delayMin; }

    public Optional<StopTime> stopAt(String stationId) {
        return schedule.stream()
                .filter(st -> st.getStationId().equals(stationId))
                .findFirst();
    }

    public Optional<LocalTime> departureFrom(String stationId) {
        return stopAt(stationId).map(st -> st.getDeparture().plusMinutes(delayMin));
    }

    public Optional<LocalTime> arrivalAt(String stationId) {
        return stopAt(stationId).map(st -> st.getArrival().plusMinutes(delayMin));
    }

    @Override
    public String toString() { return name + " (" + id + ")"; }
}
