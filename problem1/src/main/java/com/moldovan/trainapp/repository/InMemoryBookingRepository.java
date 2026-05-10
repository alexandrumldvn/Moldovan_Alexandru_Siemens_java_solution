package com.moldovan.trainapp.repository;

import com.moldovan.trainapp.domain.Booking;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryBookingRepository implements BookingRepository {

    private final Map<String, Booking> store = new HashMap<>();

    @Override
    public void save(Booking booking) { store.put(booking.getId(), booking); }

    @Override
    public Optional<Booking> findById(String id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public List<Booking> findAll() { return new ArrayList<>(store.values()); }

    @Override
    public List<Booking> findByTrainId(String trainId) {
        return store.values().stream()
                .filter(b -> b.getTrainId().equals(trainId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) { store.remove(id); }
}
