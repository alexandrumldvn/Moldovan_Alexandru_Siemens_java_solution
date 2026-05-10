package com.moldovan.trainapp;

import com.moldovan.trainapp.cli.AppCli;
import com.moldovan.trainapp.notification.ConsoleEmailService;
import com.moldovan.trainapp.notification.EmailService;
import com.moldovan.trainapp.repository.*;
import com.moldovan.trainapp.seed.SeedData;
import com.moldovan.trainapp.service.AdminService;
import com.moldovan.trainapp.service.BookingService;
import com.moldovan.trainapp.service.ScheduleSearchService;

public class Main {

    public static void main(String[] args) {
        StationRepository stationRepo = new InMemoryStationRepository();
        RouteRepository routeRepo = new InMemoryRouteRepository();
        TrainRepository trainRepo = new InMemoryTrainRepository();
        BookingRepository bookingRepo = new InMemoryBookingRepository();

        EmailService emailSvc = new ConsoleEmailService();

        AdminService adminSvc = new AdminService(stationRepo, routeRepo, trainRepo, bookingRepo, emailSvc);
        BookingService bookingSvc = new BookingService(trainRepo, routeRepo, bookingRepo, emailSvc);
        ScheduleSearchService searchSvc = new ScheduleSearchService(trainRepo, routeRepo);

        SeedData.load(adminSvc);

        new AppCli(adminSvc, bookingSvc, searchSvc, stationRepo, routeRepo, trainRepo).run();
    }
}
