package com.moldovan.trainapp.repository;

import com.moldovan.trainapp.domain.Station;
import java.util.List;
import java.util.Optional;

public interface StationRepository {
    void save(Station station);
    Optional<Station> findById(String id);
    List<Station> findAll();
    void deleteById(String id);
}
