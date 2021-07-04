package com.gfg.jbdl12employeeportal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@AllArgsConstructor
@Getter
@SuperBuilder
@Setter
@DiscriminatorValue(value = "hr")
public class HR extends Employee{
}
