package com.moldovan.trainapp.repository;

import com.moldovan.trainapp.domain.Route;
import java.util.List;
import java.util.Optional;

public interface RouteRepository {
    void save(Route route);
    Optional<Route> findById(String id);
    List<Route> findAll();
    void deleteById(String id);
}
