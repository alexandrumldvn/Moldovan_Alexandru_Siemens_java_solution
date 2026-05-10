package com.moldovan.trainapp.domain;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class Journey {

    private final List<JourneyLeg> legs;

    public Journey(List<JourneyLeg> legs) {
        this.legs = List.copyOf(legs);
    }

    public List<JourneyLeg> getLegs() { return Collections.unmodifiableList(legs); }

    public LocalTime getDeparture() { return legs.get(0).getDeparture(); }
    public LocalTime getArrival() { return legs.get(legs.size() - 1).getArrival(); }

    public boolean isDirect() { return legs.size() == 1; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Journey dep=").append(getDeparture())
          .append(" arr=").append(getArrival())
          .append(" legs=").append(legs.size())
          .append("\n");
        for (JourneyLeg leg : legs) {
            sb.append("  ").append(leg).append("\n");
        }
        return sb.toString();
    }
}
