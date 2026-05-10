package com.moldovan.trainapp.notification;

import com.moldovan.trainapp.domain.Booking;
import com.moldovan.trainapp.domain.Train;

import java.util.ArrayList;
import java.util.List;

public class FakeEmailService implements EmailService {

    public final List<String> confirms = new ArrayList<>();
    public final List<String> delays = new ArrayList<>();

    @Override
    public void sendBookingConfirmation(String email, Booking booking, Train train) {
        confirms.add(email + ":" + booking.getId() + ":" + train.getId());
    }

    @Override
    public void sendDelayNotice(String email, Train train, int delayMin) {
        delays.add(email + ":" + train.getId() + ":" + delayMin);
    }
}
