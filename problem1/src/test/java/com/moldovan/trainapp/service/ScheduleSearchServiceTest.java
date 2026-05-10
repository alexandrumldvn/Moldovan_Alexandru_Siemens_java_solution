package com.moldovan.trainapp.service;

import com.moldovan.trainapp.domain.Journey;
import com.moldovan.trainapp.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleSearchServiceTest {

    private TestFixtures fx;

    @BeforeEach
    void init() {
        fx = new TestFixtures();
        fx.loadBasic();
    }

    @Test
    void findsDirectJourney() {
        List<Journey> res = fx.search.search("A", "C");
        assertFalse(res.isEmpty());
        assertTrue(res.stream().anyMatch(Journey::isDirect));
    }

    @Test
    void findsChangeoverJourney() {
        List<Journey> res = fx.search.search("A", "D");
        assertFalse(res.isEmpty());
        Journey j = res.get(0);
        assertEquals(2, j.getLegs().size());
        assertEquals("T1", j.getLegs().get(0).getTrainId());
        assertEquals("T2", j.getLegs().get(1).getTrainId());
    }

    @Test
    void noLinkRaises() {
        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                fx.search.search("A", "X"));
        assertTrue(ex.getMessage().contains("no link"));
    }

    @Test
    void sameStationRejected() {
        assertThrows(IllegalArgumentException.class, () -> fx.search.search("A", "A"));
    }
}
