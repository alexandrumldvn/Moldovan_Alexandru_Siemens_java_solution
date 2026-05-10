package com.moldovan.trainapp.notification;

import com.moldovan.trainapp.domain.Booking;
import com.moldovan.trainapp.domain.Train;

public interface EmailService {

    void sendBookingConfirmation(String email, Booking booking, Train train);

    void sendDelayNotice(String email, Train train, int delayMin);
}
