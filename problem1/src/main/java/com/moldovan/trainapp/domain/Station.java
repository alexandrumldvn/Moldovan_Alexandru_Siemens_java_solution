package com.moldovan.trainapp.domain;

import java.util.Objects;

public class Station {

    private final String id;
    private String name;

    public Station(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station)) return false;
        Station s = (Station) o;
        return id.equals(s.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return name + " (" + id + ")"; }
}
