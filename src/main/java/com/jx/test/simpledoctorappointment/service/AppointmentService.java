package com.jx.test.simpledoctorappointment.service;

import com.jx.test.simpledoctorappointment.model.Appointment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AppointmentService {
    Mono<Appointment> fixAppointment(
        String patientId,
        String patientName,
        int patientAge,
        String patientGender,
        String doctorId,
        String doctorName,
        long date,
        int startHour
    );

    Mono<Void> cancel(String patientId, String doctorId, long date, int startHour);

    Flux<Appointment> list(String doctorId, long date);
}
