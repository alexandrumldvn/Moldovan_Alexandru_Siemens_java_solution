package com.moldovan.trainapp.repository;

import com.moldovan.trainapp.domain.Route;
import java.util.*;

public class InMemoryRouteRepository implements RouteRepository {

    private final Map<String, Route> store = new HashMap<>();

    @Override
    public void save(Route route) { store.put(route.getId(), route); }

    @Override
    public Optional<Route> findById(String id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public List<Route> findAll() { return new ArrayList<>(store.values()); }

    @Override
    public void deleteById(String id) { store.remove(id); }
}
