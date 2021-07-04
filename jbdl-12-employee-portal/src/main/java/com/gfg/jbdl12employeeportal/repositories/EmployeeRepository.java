package com.gfg.jbdl12employeeportal.repositories;

import com.gfg.jbdl12employeeportal.model.Employee;
import com.gfg.jbdl12employeeportal.model.Manager;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EmployeeRepository extends CrudRepository<Employee,Long> {
    Optional<Employee> findByEmployeeId(String employeeId);
}
