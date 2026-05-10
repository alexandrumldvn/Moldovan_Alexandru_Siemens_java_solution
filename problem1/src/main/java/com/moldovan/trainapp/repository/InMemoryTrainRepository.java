package com.moldovan.trainapp.repository;

import com.moldovan.trainapp.domain.Train;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTrainRepository implements TrainRepository {

    private final Map<String, Train> store = new HashMap<>();

    @Override
    public void save(Train train) { store.put(train.getId(), train); }

    @Override
    public Optional<Train> findById(String id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public List<Train> findAll() { return new ArrayList<>(store.values()); }

    @Override
    public List<Train> findByRouteId(String routeId) {
        return store.values().stream()
                .filter(t -> t.getRouteId().equals(routeId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) { store.remove(id); }
}
