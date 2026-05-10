package com.moldovan.trainapp.repository;

import com.moldovan.trainapp.domain.Station;
import java.util.*;

public class InMemoryStationRepository implements StationRepository {

    private final Map<String, Station> store = new HashMap<>();

    @Override
    public void save(Station station) { store.put(station.getId(), station); }

    @Override
    public Optional<Station> findById(String id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public List<Station> findAll() { return new ArrayList<>(store.values()); }

    @Override
    public void deleteById(String id) { store.remove(id); }
}
