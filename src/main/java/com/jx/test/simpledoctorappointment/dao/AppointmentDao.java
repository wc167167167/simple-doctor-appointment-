package com.jx.test.simpledoctorappointment.dao;

import com.jx.test.simpledoctorappointment.model.Appointment;
import com.jx.test.simpledoctorappointment.model.Doctor;
import com.jx.test.simpledoctorappointment.model.Patient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AppointmentDao {
    Mono<Appointment> fixAppointment(Patient patient, Doctor doctor, long date, int startHour);

    Mono<Void> cancel(Appointment appointment);

    Flux<Appointment> list(Doctor doctor, long date);
}
