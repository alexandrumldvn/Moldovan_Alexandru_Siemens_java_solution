package com.moldovan.trainapp.service;

import com.moldovan.trainapp.domain.StopTime;
import com.moldovan.trainapp.notification.FakeEmailService;
import com.moldovan.trainapp.repository.*;

import java.time.LocalTime;
import java.util.List;

public final class TestFixtures {

    public StationRepository stations = new InMemoryStationRepository();
    public RouteRepository routes = new InMemoryRouteRepository();
    public TrainRepository trains = new InMemoryTrainRepository();
    public BookingRepository bookings = new InMemoryBookingRepository();
    public FakeEmailService email = new FakeEmailService();

    public AdminService admin = new AdminService(stations, routes, trains, bookings, email);
    public BookingService booking = new BookingService(trains, routes, bookings, email);
    public ScheduleSearchService search = new ScheduleSearchService(trains, routes);

    public void loadBasic() {
        admin.addStation("A", "alpha");
        admin.addStation("B", "beta");
        admin.addStation("C", "gamma");
        admin.addStation("D", "delta");
        admin.addStation("X", "isolated");

        admin.addRoute("R1", "main", List.of("A", "B", "C"));
        admin.addRoute("R2", "branch", List.of("B", "D"));

        admin.addTrain("T1", "tr-1", "R1", 10, List.of(
                new StopTime("A", LocalTime.of(8, 0),  LocalTime.of(8, 0)),
                new StopTime("B", LocalTime.of(9, 0),  LocalTime.of(9, 10)),
                new StopTime("C", LocalTime.of(10, 0), LocalTime.of(10, 0))
        ));
        admin.addTrain("T2", "tr-2", "R2", 5, List.of(
                new StopTime("B", LocalTime.of(9, 30), LocalTime.of(9, 30)),
                new StopTime("D", LocalTime.of(10, 30), LocalTime.of(10, 30))
        ));
    }
}
