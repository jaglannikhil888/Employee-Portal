package com.gfg.jbdl12employeeportal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeCreationRequest {
    private String firstName;
    private String lastName;
    private Type type;
}
