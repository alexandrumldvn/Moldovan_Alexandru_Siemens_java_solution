package com.moldovan.trainapp.service;

import com.moldovan.trainapp.domain.Booking;
import com.moldovan.trainapp.exception.NotFoundException;
import com.moldovan.trainapp.exception.OverbookingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {

    private TestFixtures fx;

    @BeforeEach
    void init() {
        fx = new TestFixtures();
        fx.loadBasic();
    }

    @Test
    void bookHappyPath() {
        Booking b = fx.booking.book("u@x.com", "T1", "A", "C", 2);
        assertNotNull(b.getId());
        assertEquals("T1", b.getTrainId());
        assertEquals(1, fx.email.confirms.size());
    }

    @Test
    void preventOverbooking() {
        fx.booking.book("u1@x.com", "T1", "A", "C", 8);
        fx.booking.book("u2@x.com", "T1", "A", "C", 2);
        OverbookingException ex = assertThrows(OverbookingException.class, () ->
                fx.booking.book("u3@x.com", "T1", "A", "C", 1));
        assertTrue(ex.getMessage().contains("full"));
    }

    @Test
    void rejectUnknownTrain() {
        assertThrows(NotFoundException.class, () ->
                fx.booking.book("u@x.com", "TZ", "A", "C", 1));
    }

    @Test
    void rejectBadSegment() {
        assertThrows(IllegalArgumentException.class, () ->
                fx.booking.book("u@x.com", "T1", "C", "A", 1));
    }

    @Test
    void bookMany() {
        List<BookingService.BookingRequest> reqs = List.of(
                new BookingService.BookingRequest("u@x.com", "T1", "A", "B", 1),
                new BookingService.BookingRequest("u@x.com", "T1", "B", "C", 1)
        );
        List<Booking> out = fx.booking.bookMany(reqs);
        assertEquals(2, out.size());
        assertEquals(2, fx.email.confirms.size());
    }
}
