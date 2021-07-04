package com.gfg.jbdl12employeeportal.model;

import com.gfg.jbdl12employeeportal.model.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@DiscriminatorValue(value = "manager")
public class Manager extends Employee {
    @OneToMany(cascade = CascadeType.ALL)
    List<Employee> Subordinates;
}
