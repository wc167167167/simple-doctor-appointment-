package com.jx.test.simpledoctorappointment.controller;

import com.jx.test.simpledoctorappointment.model.Appointment;
import com.jx.test.simpledoctorappointment.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("fix")
    public Mono<String> fixAppointment(
        @RequestParam String patientId,
        @RequestParam String patientName,
        @RequestParam int patientAge,
        @RequestParam String patientGender,
        @RequestParam String doctorId,
        @RequestParam String doctorName,
        @RequestParam long date,
        @RequestParam int startHour
    ) {
        return appointmentService
            .fixAppointment(patientId, patientName, patientAge, patientGender, doctorId, doctorName, date, startHour)
            .map(Appointment::toString)
            .defaultIfEmpty("failed to fix an appointment");
    }

    @GetMapping("cancel")
    public Mono<Void> cancel(
        @RequestParam String patientId,
        @RequestParam String doctorId,
        @RequestParam long date,
        @RequestParam int startHour
    ) {
        return appointmentService.cancel(patientId, doctorId, date, startHour);
    }

    @GetMapping("list")
    public Mono<String> list(@RequestParam String doctorId, @RequestParam long date) {
        return appointmentService
            .list(doctorId, date)
            .map(Appointment::toString)
            .collectList()
            .map(list -> String.join(",\n", list));
    }
}
