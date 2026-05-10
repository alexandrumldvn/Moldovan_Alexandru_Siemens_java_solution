package com.moldovan.trainapp.domain;

import java.util.ArrayList;
import java.util.List;

public class Route {

    private final String id;
    private String name;
    private final List<String> stationIds;

    public Route(String id, String name, List<String> stationIds) {
        this.id = id;
        this.name = name;
        this.stationIds = new ArrayList<>(stationIds);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getStationIds() { return stationIds; }

    public int indexOf(String stationId) {
        return stationIds.indexOf(stationId);
    }

    public boolean covers(String fromStationId, String toStationId) {
        int a = indexOf(fromStationId);
        int b = indexOf(toStationId);
        return a >= 0 && b >= 0 && a < b;
    }

    @Override
    public String toString() { return name + " (" + id + ")"; }
}
