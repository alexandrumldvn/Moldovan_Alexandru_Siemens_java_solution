package com.moldovan.trainapp.service;

import com.moldovan.trainapp.domain.*;
import com.moldovan.trainapp.exception.NotFoundException;
import com.moldovan.trainapp.notification.EmailService;
import com.moldovan.trainapp.repository.*;

import java.util.List;

public class AdminService {

    private final StationRepository stationRepo;
    private final RouteRepository routeRepo;
    private final TrainRepository trainRepo;
    private final BookingRepository bookingRepo;
    private final EmailService emailSvc;

    public AdminService(StationRepository stationRepo,
                        RouteRepository routeRepo,
                        TrainRepository trainRepo,
                        BookingRepository bookingRepo,
                        EmailService emailSvc) {
        this.stationRepo = stationRepo;
        this.routeRepo = routeRepo;
        this.trainRepo = trainRepo;
        this.bookingRepo = bookingRepo;
        this.emailSvc = emailSvc;
    }


    public Station addStation(String id, String name) {
        Station s = new Station(id, name);
        stationRepo.save(s);
        return s;
    }

    public void removeStation(String id) { stationRepo.deleteById(id); }

    public void renameStation(String id, String newName) {
        Station s = stationRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("station " + id));
        s.setName(newName);
        stationRepo.save(s);
    }


    public Route addRoute(String id, String name, List<String> stationIds) {
        Route r = new Route(id, name, stationIds);
        routeRepo.save(r);
        return r;
    }

    public void removeRoute(String id) { routeRepo.deleteById(id); }

    public void renameRoute(String id, String newName) {
        Route r = routeRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("route " + id));
        r.setName(newName);
        routeRepo.save(r);
    }

    public void updateRouteStations(String id, List<String> newStationIds) {
        Route r = routeRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("route " + id));
        r.getStationIds().clear();
        r.getStationIds().addAll(newStationIds);
        routeRepo.save(r);
    }


    public Train addTrain(String id, String name, String routeId, int capacity, List<StopTime> schedule) {
        if (routeRepo.findById(routeId).isEmpty()) {
            throw new NotFoundException("route " + routeId);
        }
        Train t = new Train(id, name, routeId, capacity, schedule);
        trainRepo.save(t);
        return t;
    }

    public void removeTrain(String id) { trainRepo.deleteById(id); }

    public void renameTrain(String id, String newName) {
        Train t = trainRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("train " + id));
        t.setName(newName);
        trainRepo.save(t);
    }

    public void changeTrainCapacity(String id, int newCapacity) {
        Train t = trainRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("train " + id));
        t.setCapacity(newCapacity);
        trainRepo.save(t);
    }


    public List<Booking> bookingsForTrain(String trainId) {
        return bookingRepo.findByTrainId(trainId);
    }

    public void markDelayed(String trainId, int delayMin) {
        if (delayMin < 0) throw new IllegalArgumentException("delay must be >= 0");
        Train t = trainRepo.findById(trainId)
                .orElseThrow(() -> new NotFoundException("train " + trainId));
        t.setDelayMin(delayMin);
        trainRepo.save(t);

        bookingRepo.findByTrainId(trainId).stream()
                .map(Booking::getCustomerEmail)
                .distinct()
                .forEach(email -> emailSvc.sendDelayNotice(email, t, delayMin));
    }
}
