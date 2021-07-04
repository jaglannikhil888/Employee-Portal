package com.gfg.jbdl12employeeportal.repositories;

import com.gfg.jbdl12employeeportal.model.Employee;
import com.gfg.jbdl12employeeportal.model.Manager;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ManagerRepository extends CrudRepository<Manager,Long> {
    Optional<Manager> findByEmployeeId(String employeeId);
}
