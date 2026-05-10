package com.moldovan.trainapp.repository;

import com.moldovan.trainapp.domain.Train;
import java.util.List;
import java.util.Optional;

public interface TrainRepository {
    void save(Train train);
    Optional<Train> findById(String id);
    List<Train> findAll();
    List<Train> findByRouteId(String routeId);
    void deleteById(String id);
}
