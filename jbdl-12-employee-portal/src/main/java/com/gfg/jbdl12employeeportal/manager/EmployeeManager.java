package com.gfg.jbdl12employeeportal.manager;

import com.gfg.jbdl12employeeportal.ForbiddenException;
import com.gfg.jbdl12employeeportal.model.EmployeeCreationRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface EmployeeManager extends UserDetailsService {
    EmployeeCreationResponse createEmployee(EmployeeCreationRequest employeeCreationRequest);
    void addSubOrdinates(List<String> subOrdinateIds, String managerId) throws Exception;
    void provideRating(String suboridnate, Float rating , UsernamePasswordAuthenticationToken managerId ) throws ForbiddenException;
    Float getRating(String employee_id);
}
