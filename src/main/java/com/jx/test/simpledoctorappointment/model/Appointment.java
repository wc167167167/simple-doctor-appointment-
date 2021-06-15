package com.jx.test.simpledoctorappointment.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@Builder
public class Appointment {

    private String id;

    private Patient patient;
    private Doctor doctor;
    private long date;
    private int startHour;
}
