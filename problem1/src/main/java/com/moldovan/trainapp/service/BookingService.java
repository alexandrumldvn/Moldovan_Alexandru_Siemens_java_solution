package com.moldovan.trainapp.service;

import com.moldovan.trainapp.domain.Booking;
import com.moldovan.trainapp.domain.Route;
import com.moldovan.trainapp.domain.Train;
import com.moldovan.trainapp.exception.NotFoundException;
import com.moldovan.trainapp.exception.OverbookingException;
import com.moldovan.trainapp.notification.EmailService;
import com.moldovan.trainapp.repository.BookingRepository;
import com.moldovan.trainapp.repository.RouteRepository;
import com.moldovan.trainapp.repository.TrainRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingService {

    private final TrainRepository trainRepo;
    private final RouteRepository routeRepo;
    private final BookingRepository bookingRepo;
    private final EmailService emailSvc;

    public BookingService(TrainRepository trainRepo,
                          RouteRepository routeRepo,
                          BookingRepository bookingRepo,
                          EmailService emailSvc) {
        this.trainRepo = trainRepo;
        this.routeRepo = routeRepo;
        this.bookingRepo = bookingRepo;
        this.emailSvc = emailSvc;
    }

    public Booking book(String email, String trainId,
                        String fromStationId, String toStationId, int seatCount) {
        if (seatCount <= 0) {
            throw new IllegalArgumentException("seatCount must be > 0");
        }
        Train train = trainRepo.findById(trainId)
                .orElseThrow(() -> new NotFoundException("train not found: " + trainId));
        Route route = routeRepo.findById(train.getRouteId())
                .orElseThrow(() -> new NotFoundException("route not found: " + train.getRouteId()));

        if (!route.covers(fromStationId, toStationId)) {
            throw new IllegalArgumentException(
                    "train " + trainId + " does not cover "
                            + fromStationId + " -> " + toStationId);
        }

        int already = bookingRepo.findByTrainId(trainId).stream()
                .mapToInt(Booking::getSeatCount)
                .sum();
        if (already + seatCount > train.getCapacity()) {
            throw new OverbookingException(
                    "train " + train.getName() + " is full: " + already + "/" + train.getCapacity()
                            + ", req " + seatCount);
        }

        Booking booking = new Booking(
                UUID.randomUUID().toString().substring(0, 8),
                email, trainId, fromStationId, toStationId, seatCount);
        bookingRepo.save(booking);
        emailSvc.sendBookingConfirmation(email, booking, train);
        return booking;
    }

    public List<Booking> bookMany(List<BookingRequest> requests) {
        List<Booking> done = new ArrayList<>();
        for (BookingRequest req : requests) {
            Booking b = book(req.email, req.trainId, req.fromStationId, req.toStationId, req.seatCount);
            done.add(b);
        }
        return done;
    }

    public static class BookingRequest {
        public final String email;
        public final String trainId;
        public final String fromStationId;
        public final String toStationId;
        public final int seatCount;
        public BookingRequest(String email, String trainId,
                              String fromStationId, String toStationId, int seatCount) {
            this.email = email;
            this.trainId = trainId;
            this.fromStationId = fromStationId;
            this.toStationId = toStationId;
            this.seatCount = seatCount;
        }
    }
}
