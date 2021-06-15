package com.jx.test.simpledoctorappointment.service;

import com.jx.test.simpledoctorappointment.dao.AppointmentDao;
import com.jx.test.simpledoctorappointment.model.Appointment;
import com.jx.test.simpledoctorappointment.model.Doctor;
import com.jx.test.simpledoctorappointment.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentDao appointmentDao;

    @Override
    public Mono<Appointment> fixAppointment(
        String patientId,
        String patientName,
        int patientAge,
        String patientGender,
        String doctorId,
        String doctorName,
        long date,
        int startHour
    ) {
        var patient = Patient.builder().id(patientId).name(patientName).age(patientAge).gender(patientGender).build();
        var doctor = Doctor.builder().id(doctorId).name(doctorName).build();
        return appointmentDao.fixAppointment(patient, doctor, date, startHour);
    }

    @Override
    public Mono<Void> cancel(String patientId, String doctorId, long date, int startHour) {
        var patient = Patient.builder().id(patientId).build();
        var doctor = Doctor.builder().id(doctorId).build();

        return appointmentDao.cancel(
            Appointment.builder().patient(patient).doctor(doctor).date(date).startHour(startHour).build()
        );
    }

    @Override
    public Flux<Appointment> list(String doctorId, long date) {
        return appointmentDao.list(Doctor.builder().id(doctorId).build(), date);
    }
}
