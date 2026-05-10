package com.moldovan.trainapp.service;

import com.moldovan.trainapp.domain.Booking;
import com.moldovan.trainapp.domain.Train;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdminServiceTest {

    private TestFixtures fx;

    @BeforeEach
    void init() {
        fx = new TestFixtures();
        fx.loadBasic();
    }

    @Test
    void addRemoveRoute() {
        fx.admin.addRoute("R9", "tmp", List.of("A", "B"));
        assertTrue(fx.routes.findById("R9").isPresent());
        fx.admin.removeRoute("R9");
        assertTrue(fx.routes.findById("R9").isEmpty());
    }

    @Test
    void renameTrainAndChangeCap() {
        fx.admin.renameTrain("T1", "renamed");
        fx.admin.changeTrainCapacity("T1", 99);
        Train t = fx.trains.findById("T1").orElseThrow();
        assertEquals("renamed", t.getName());
        assertEquals(99, t.getCapacity());
    }

    @Test
    void bookingsForTrainListed() {
        fx.booking.book("a@x.com", "T1", "A", "B", 1);
        fx.booking.book("b@x.com", "T1", "B", "C", 2);
        List<Booking> all = fx.admin.bookingsForTrain("T1");
        assertEquals(2, all.size());
    }

    @Test
    void delayNotifiesAllCustomers() {
        fx.booking.book("a@x.com", "T1", "A", "B", 1);
        fx.booking.book("b@x.com", "T1", "B", "C", 1);
        fx.booking.book("a@x.com", "T1", "A", "C", 1);

        fx.email.delays.clear();
        fx.admin.markDelayed("T1", 20);

        assertEquals(2, fx.email.delays.size());
        Train t = fx.trains.findById("T1").orElseThrow();
        assertEquals(20, t.getDelayMin());
    }
}
