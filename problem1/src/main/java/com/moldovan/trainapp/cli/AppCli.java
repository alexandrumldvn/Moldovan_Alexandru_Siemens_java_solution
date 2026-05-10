package com.moldovan.trainapp.cli;

import com.moldovan.trainapp.domain.*;
import com.moldovan.trainapp.exception.NotFoundException;
import com.moldovan.trainapp.exception.OverbookingException;
import com.moldovan.trainapp.repository.RouteRepository;
import com.moldovan.trainapp.repository.StationRepository;
import com.moldovan.trainapp.repository.TrainRepository;
import com.moldovan.trainapp.service.AdminService;
import com.moldovan.trainapp.service.BookingService;
import com.moldovan.trainapp.service.ScheduleSearchService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class AppCli {

    private final AdminService admin;
    private final BookingService booking;
    private final ScheduleSearchService search;
    private final StationRepository stationRepo;
    private final RouteRepository routeRepo;
    private final TrainRepository trainRepo;

    private final Scanner in = new Scanner(System.in);

    public AppCli(AdminService admin, BookingService booking, ScheduleSearchService search,
                  StationRepository stationRepo, RouteRepository routeRepo, TrainRepository trainRepo) {
        this.admin = admin;
        this.booking = booking;
        this.search = search;
        this.stationRepo = stationRepo;
        this.routeRepo = routeRepo;
        this.trainRepo = trainRepo;
    }

    public void run() {
        System.out.println("welcome to the train ticketing app");
        while (true) {
            System.out.println();
            System.out.println("=== main menu ===");
            System.out.println("1) book ticket(s)");
            System.out.println("2) search journey");
            System.out.println("3) admin");
            System.out.println("0) exit");
            String choice = prompt("choose: ");
            try {
                switch (choice) {
                    case "1" -> bookFlow();
                    case "2" -> searchFlow();
                    case "3" -> adminMenu();
                    case "0" -> { System.out.println("bye"); return; }
                    default -> System.out.println("unknown option");
                }
            } catch (Exception e) {
                System.out.println("error: " + e.getMessage());
            }
        }
    }


    private void bookFlow() {
        String email = prompt("customer email: ");
        int count = parseInt(prompt("how many bookings to make: "));
        List<BookingService.BookingRequest> reqs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            System.out.println("-- booking " + (i + 1) + " --");
            String trainId = prompt("train id: ");
            String from = prompt("from station id: ");
            String to = prompt("to station id: ");
            int seats = parseInt(prompt("seat count: "));
            reqs.add(new BookingService.BookingRequest(email, trainId, from, to, seats));
        }
        try {
            List<Booking> done = booking.bookMany(reqs);
            System.out.println("booked " + done.size() + " ticket(s):");
            done.forEach(b -> System.out.println("  " + b));
        } catch (OverbookingException | NotFoundException | IllegalArgumentException e) {
            System.out.println("booking failed: " + e.getMessage());
        }
    }

    private void searchFlow() {
        String from = prompt("from station id: ");
        String to = prompt("to station id: ");
        try {
            List<Journey> results = search.search(from, to);
            System.out.println("found " + results.size() + " journey(s):");
            results.forEach(j -> System.out.println(j));
        } catch (NotFoundException e) {
            System.out.println("error: " + e.getMessage());
        }
    }


    private void adminMenu() {
        while (true) {
            System.out.println();
            System.out.println("=== admin menu ===");
            System.out.println(" 1) list stations         2) add station");
            System.out.println(" 3) remove station        4) rename station");
            System.out.println(" 5) list routes           6) add route");
            System.out.println(" 7) remove route          8) rename route");
            System.out.println(" 9) update route stops   10) list trains");
            System.out.println("11) add train            12) remove train");
            System.out.println("13) rename train         14) change train capacity");
            System.out.println("15) show bookings        16) mark train delayed");
            System.out.println(" 0) back");
            String c = prompt("choose: ");
            try {
                switch (c) {
                    case "1" -> stationRepo.findAll().forEach(s -> System.out.println("  " + s));
                    case "2" -> {
                        String id = prompt("id: ");
                        String name = prompt("name: ");
                        admin.addStation(id, name);
                    }
                    case "3" -> admin.removeStation(prompt("id: "));
                    case "4" -> admin.renameStation(prompt("id: "), prompt("new name: "));
                    case "5" -> routeRepo.findAll().forEach(r ->
                            System.out.println("  " + r + " stops=" + r.getStationIds()));
                    case "6" -> {
                        String id = prompt("id: ");
                        String name = prompt("name: ");
                        List<String> stops = Arrays.asList(prompt("station ids (comma sep): ").split("\\s*,\\s*"));
                        admin.addRoute(id, name, stops);
                    }
                    case "7" -> admin.removeRoute(prompt("id: "));
                    case "8" -> admin.renameRoute(prompt("id: "), prompt("new name: "));
                    case "9" -> {
                        String id = prompt("route id: ");
                        List<String> stops = Arrays.asList(prompt("new station ids (comma sep): ").split("\\s*,\\s*"));
                        admin.updateRouteStations(id, stops);
                    }
                    case "10" -> trainRepo.findAll().forEach(t ->
                            System.out.println("  " + t + " route=" + t.getRouteId()
                                    + " cap=" + t.getCapacity() + " delay=" + t.getDelayMin() + "min"));
                    case "11" -> addTrainFlow();
                    case "12" -> admin.removeTrain(prompt("id: "));
                    case "13" -> admin.renameTrain(prompt("id: "), prompt("new name: "));
                    case "14" -> admin.changeTrainCapacity(prompt("id: "), parseInt(prompt("new cap: ")));
                    case "15" -> {
                        String id = prompt("train id: ");
                        List<Booking> bookings = admin.bookingsForTrain(id);
                        System.out.println("bookings for " + id + ": " + bookings.size());
                        bookings.forEach(b -> System.out.println("  " + b));
                    }
                    case "16" -> admin.markDelayed(prompt("train id: "), parseInt(prompt("delay min: ")));
                    case "0" -> { return; }
                    default -> System.out.println("unknown option");
                }
            } catch (Exception e) {
                System.out.println("error: " + e.getMessage());
            }
        }
    }

    private void addTrainFlow() {
        String id = prompt("id: ");
        String name = prompt("name: ");
        String routeId = prompt("route id: ");
        int cap = parseInt(prompt("capacity: "));
        int stopsN = parseInt(prompt("number of stops: "));
        List<StopTime> sched = new ArrayList<>();
        for (int i = 0; i < stopsN; i++) {
            String sid = prompt(" stop " + (i + 1) + " station id: ");
            LocalTime arr = LocalTime.parse(prompt(" stop " + (i + 1) + " arrival HH:mm: "));
            LocalTime dep = LocalTime.parse(prompt(" stop " + (i + 1) + " departure HH:mm: "));
            sched.add(new StopTime(sid, arr, dep));
        }
        admin.addTrain(id, name, routeId, cap, sched);
    }


    private String prompt(String label) {
        System.out.print(label);
        return in.nextLine().trim();
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("not a number: " + s); }
    }
}
