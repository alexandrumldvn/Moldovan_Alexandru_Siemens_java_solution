package com.moldovan.trainapp.seed;

import com.moldovan.trainapp.domain.StopTime;
import com.moldovan.trainapp.service.AdminService;

import java.time.LocalTime;
import java.util.List;

public final class SeedData {

    private SeedData() {}

    public static void load(AdminService admin) {
        admin.addStation("BUC", "Bucharest");
        admin.addStation("BRA", "Brasov");
        admin.addStation("SIB", "Sibiu");
        admin.addStation("CLU", "Cluj");
        admin.addStation("TIM", "Timisoara");
        admin.addStation("IAS", "Iasi"); // isolated, used for no-link demo

        admin.addRoute("R1", "BUC-CLU main line", List.of("BUC", "BRA", "SIB", "CLU"));
        admin.addRoute("R2", "BUC-TIM express", List.of("BUC", "TIM"));
        admin.addRoute("R3", "SIB-TIM connector", List.of("SIB", "TIM"));

        admin.addTrain("T1", "IR-101", "R1", 100, List.of(
                new StopTime("BUC", LocalTime.of(8, 0),  LocalTime.of(8, 0)),
                new StopTime("BRA", LocalTime.of(10, 0), LocalTime.of(10, 10)),
                new StopTime("SIB", LocalTime.of(12, 0), LocalTime.of(12, 10)),
                new StopTime("CLU", LocalTime.of(14, 0), LocalTime.of(14, 0))
        ));
        admin.addTrain("T2", "IR-105", "R1", 50, List.of(
                new StopTime("BUC", LocalTime.of(14, 0), LocalTime.of(14, 0)),
                new StopTime("BRA", LocalTime.of(16, 0), LocalTime.of(16, 10)),
                new StopTime("SIB", LocalTime.of(18, 0), LocalTime.of(18, 10)),
                new StopTime("CLU", LocalTime.of(20, 0), LocalTime.of(20, 0))
        ));

        admin.addTrain("T3", "IR-200", "R2", 80, List.of(
                new StopTime("BUC", LocalTime.of(9, 0),  LocalTime.of(9, 0)),
                new StopTime("TIM", LocalTime.of(17, 0), LocalTime.of(17, 0))
        ));

        admin.addTrain("T4", "IR-300", "R3", 60, List.of(
                new StopTime("SIB", LocalTime.of(13, 0), LocalTime.of(13, 0)),
                new StopTime("TIM", LocalTime.of(15, 0), LocalTime.of(15, 0))
        ));
    }
}
