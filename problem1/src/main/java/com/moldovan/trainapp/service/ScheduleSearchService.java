package com.moldovan.trainapp.service;

import com.moldovan.trainapp.domain.*;
import com.moldovan.trainapp.exception.NotFoundException;
import com.moldovan.trainapp.repository.RouteRepository;
import com.moldovan.trainapp.repository.TrainRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleSearchService {

    private static final int MIN_CONNECT_MIN = 5;

    private final TrainRepository trainRepo;
    private final RouteRepository routeRepo;

    public ScheduleSearchService(TrainRepository trainRepo, RouteRepository routeRepo) {
        this.trainRepo = trainRepo;
        this.routeRepo = routeRepo;
    }

    public List<Journey> search(String fromStationId, String toStationId) {
        if (fromStationId.equals(toStationId)) {
            throw new IllegalArgumentException("from and to must differ");
        }
        List<Journey> all = new ArrayList<>();
        all.addAll(findDirect(fromStationId, toStationId));
        all.addAll(findOneChangeover(fromStationId, toStationId));

        if (all.isEmpty()) {
            throw new NotFoundException(
                    "no link between " + fromStationId + " and " + toStationId);
        }
        all.sort(Comparator.comparing(Journey::getArrival).thenComparing(Journey::getDeparture));
        return all;
    }

    private List<Journey> findDirect(String from, String to) {
        return trainRepo.findAll().stream()
                .filter(t -> routeCovers(t, from, to))
                .map(t -> new Journey(List.of(buildLeg(t, from, to))))
                .collect(Collectors.toList());
    }

    private List<Journey> findOneChangeover(String from, String to) {
        List<Journey> out = new ArrayList<>();

        List<Train> trains = trainRepo.findAll();
        for (Train t1 : trains) {
            Route r1 = routeRepo.findById(t1.getRouteId()).orElse(null);
            if (r1 == null || r1.indexOf(from) < 0) continue;

            int fromIdx = r1.indexOf(from);
            for (int i = fromIdx + 1; i < r1.getStationIds().size(); i++) {
                String mid = r1.getStationIds().get(i);
                if (mid.equals(to)) continue; // direct case, handled elsewhere

                LocalTime arrAtMid = t1.arrivalAt(mid).orElse(null);
                if (arrAtMid == null) continue;

                for (Train t2 : trains) {
                    if (t2.getId().equals(t1.getId())) continue;
                    if (!routeCovers(t2, mid, to)) continue;
                    LocalTime depFromMid = t2.departureFrom(mid).orElse(null);
                    if (depFromMid == null) continue;
                    if (depFromMid.isBefore(arrAtMid.plusMinutes(MIN_CONNECT_MIN))) continue;

                    JourneyLeg leg1 = buildLeg(t1, from, mid);
                    JourneyLeg leg2 = buildLeg(t2, mid, to);
                    out.add(new Journey(List.of(leg1, leg2)));
                }
            }
        }
        return out;
    }

    private boolean routeCovers(Train t, String from, String to) {
        Route r = routeRepo.findById(t.getRouteId()).orElse(null);
        return r != null && r.covers(from, to);
    }

    private JourneyLeg buildLeg(Train t, String from, String to) {
        LocalTime dep = t.departureFrom(from).orElseThrow();
        LocalTime arr = t.arrivalAt(to).orElseThrow();
        return new JourneyLeg(t.getId(), t.getName(), from, to, dep, arr);
    }
}
