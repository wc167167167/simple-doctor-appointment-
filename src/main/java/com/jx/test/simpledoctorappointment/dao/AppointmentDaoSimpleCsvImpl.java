package com.jx.test.simpledoctorappointment.dao;

import com.jx.test.simpledoctorappointment.model.Appointment;
import com.jx.test.simpledoctorappointment.model.Doctor;
import com.jx.test.simpledoctorappointment.model.Patient;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class AppointmentDaoSimpleCsvImpl implements AppointmentDao {

    private static final String RESOURCE_FILE = "test.csv";

    private Map<Long, Map<Doctor, Map<Integer, Appointment>>> appointments = new HashMap<>();

    @PostConstruct
    public void init() throws IOException {
        var reader = new BufferedReader(
            new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(RESOURCE_FILE))
        );

        var line = reader.readLine();
        while (line != null) {
            tryParse(line)
                .ifPresent(
                    appointment -> {
                        if (appointment.startHour() > 16 || appointment.startHour() < 8) {
                            log.error("invalid time slot {}", appointment.startHour());
                            return;
                        }

                        var slots = appointments
                            .computeIfAbsent(appointment.date(), d -> new HashMap<>())
                            .computeIfAbsent(appointment.doctor(), d -> new HashMap<>());

                        if (slots.containsKey(appointment.startHour())) {
                            log.error("overlapped slots.");
                        }

                        slots.put(appointment.startHour(), appointment);
                    }
                );

            line = reader.readLine();
        }

        reader.close();
    }

    @Override
    public Mono<Appointment> fixAppointment(Patient patient, Doctor doctor, long date, int startHour) {
        var appointment = Appointment
            .builder()
            .id(UUID.randomUUID().toString())
            .patient(patient)
            .doctor(doctor)
            .date(date)
            .startHour(startHour)
            .build();
        var appointmentsOfDate = appointments
            .computeIfAbsent(date, d -> new HashMap<>())
            .computeIfAbsent(doctor, d -> new HashMap<>());

        if (appointmentsOfDate.containsKey(startHour)) {
            return Mono.empty();
        }

        appointmentsOfDate.put(startHour, appointment);

        log.info("fixed an appointment {}", appointment);

        return Mono.just(appointment);
    }

    @Override
    public Mono<Void> cancel(Appointment appointment) {
        if (appointment == null) {
            return Mono.empty();
        }

        var appointmentsOfDate = appointments.get(appointment.date());
        if (appointmentsOfDate == null) {
            return Mono.empty();
        }

        var appointmentsOfDoctor = appointmentsOfDate.get(appointment.doctor());
        if (appointmentsOfDoctor == null) {
            return Mono.empty();
        }

        var a = appointmentsOfDoctor.get(appointment.startHour());
        if (a != null && a.patient().equals(appointment.patient())) {
            appointmentsOfDoctor.remove(appointment.startHour());
        }

        log.info("cancelled an appointment {}", a);

        return Mono.empty();
    }

    @Override
    public Flux<Appointment> list(Doctor doctor, long date) {
        var appointmentsOfDate = appointments.get(date);
        if (appointmentsOfDate == null) {
            return Flux.empty();
        }

        var appointmentsOfDoctor = appointmentsOfDate.get(doctor);
        if (appointmentsOfDoctor == null) {
            return Flux.empty();
        }

        return Flux.fromIterable(appointmentsOfDoctor.values());
    }

    private static Optional<Appointment> tryParse(String line) {
        log.info("found record {}", line);

        try {
            var strs = line.split(",");

            var doctorId = strs[0].trim();
            var doctorName = strs[1].trim();

            var patientId = strs[2].trim();
            var patientName = strs[3].trim();
            var patientAge = Integer.parseInt(strs[4].trim());
            var patientGender = strs[5].trim();

            var appointmentId = strs[6].trim();

            var day = LocalDate.parse(strs[7].trim(), DateTimeFormatter.ofPattern("ddMMuuuu hh:mm:ss")).toEpochDay();

            var calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("ddMMyyyy HH:mm:ss").parse(strs[7].trim()));
            var hour = calendar.get(Calendar.HOUR_OF_DAY);

            var patient = Patient
                .builder()
                .id(patientId)
                .name(patientName)
                .age(patientAge)
                .gender(patientGender)
                .build();
            var doctor = Doctor.builder().id(doctorId).name(doctorName).build();

            return Optional.of(
                Appointment
                    .builder()
                    .id(appointmentId)
                    .patient(patient)
                    .doctor(doctor)
                    .date(day)
                    .startHour(hour)
                    .build()
            );
        } catch (Exception e) {
            log.error("unexpected error {}" + e);
            return Optional.empty();
        }
    }
}
