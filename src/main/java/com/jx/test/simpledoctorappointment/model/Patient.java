package com.jx.test.simpledoctorappointment.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@Builder
public class Patient {

    private String id;

    @EqualsAndHashCode.Exclude
    private String name;

    @EqualsAndHashCode.Exclude
    private int age;

    @EqualsAndHashCode.Exclude
    private String gender;
}
