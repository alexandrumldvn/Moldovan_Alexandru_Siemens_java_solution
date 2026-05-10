package com.moldovan.trainapp.notification;

import com.moldovan.trainapp.domain.Booking;
import com.moldovan.trainapp.domain.Train;

public class ConsoleEmailService implements EmailService {

    @Override
    public void sendBookingConfirmation(String email, Booking booking, Train train) {
        System.out.println();
        System.out.println("---- email (booking confirm) ----");
        System.out.println("to:      " + email);
        System.out.println("subject: booking " + booking.getId() + " confirmed");
        System.out.println("body:    your booking on train " + train.getName()
                + " from " + booking.getFromStationId()
                + " to " + booking.getToStationId()
                + " for " + booking.getSeatCount() + " seat(s) is confirmed.");
        System.out.println("---------------------------------");
    }

    @Override
    public void sendDelayNotice(String email, Train train, int delayMin) {
        System.out.println();
        System.out.println("---- email (delay notice) ----");
        System.out.println("to:      " + email);
        System.out.println("subject: train " + train.getName() + " delayed");
        System.out.println("body:    your train " + train.getName()
                + " is delayed by " + delayMin + " min.");
        System.out.println("------------------------------");
    }
}
