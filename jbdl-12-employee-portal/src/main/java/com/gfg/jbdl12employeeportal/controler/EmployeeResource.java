package com.gfg.jbdl12employeeportal.controler;

import com.gfg.jbdl12employeeportal.ForbiddenException;
import com.gfg.jbdl12employeeportal.manager.EmployeeManager;
import com.gfg.jbdl12employeeportal.model.EmployeeCreationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class EmployeeResource {

    @Autowired
    private EmployeeManager employeeManager;

    @PostMapping("/employee")
    @PreAuthorize("hasAnyAuthority('admin','hr')")
    ResponseEntity createEmployee(@RequestBody EmployeeCreationRequest employeeCreationRequest){
        return ResponseEntity.ok(employeeManager.createEmployee(employeeCreationRequest));
    }

    @PutMapping("/manager/{manager_id}/suboridnates")
    @PreAuthorize("hasAnyAuthority('admin','hr')")
    ResponseEntity addSubOrdinates(@RequestBody List<String> subOrdinateIds,
                                   @PathVariable("manager_id")  String managerId){
        try {
            employeeManager.addSubOrdinates(subOrdinateIds,managerId);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            log.error(exception.getMessage());
           return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/rating/{employee_id}/{rating}")
    @PreAuthorize("hasAnyAuthority('admin','hr','manager')")
    ResponseEntity addSubOrdinates(@PathVariable("employee_id")  String employeeId,
                                   @PathVariable("rating") Float rating,
                                   Authentication authentication){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
                authentication.getCredentials(),authentication.getAuthorities());
        try {
            employeeManager.provideRating(employeeId, rating, usernamePasswordAuthenticationToken);
            return ResponseEntity.ok().build();
        } catch (ForbiddenException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        catch (Exception exception) {
            log.error(exception.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/rating/{employee_id}/")
    @PreAuthorize("hasAnyAuthority('admin','hr','manager', 'employee')")
    ResponseEntity getRating(@PathVariable("employee_id")  String employeeId,
                             Authentication authentication){

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
                authentication.getCredentials(),authentication.getAuthorities());
       for(GrantedAuthority authority : usernamePasswordAuthenticationToken.getAuthorities()){
           if(authority.getAuthority().equalsIgnoreCase("employee")){
               if(!employeeId.equalsIgnoreCase(usernamePasswordAuthenticationToken.getName())){
                   return ResponseEntity.status(HttpStatus.FORBIDDEN).body("employee are not eligible to get rating for other employee");
               }
           }
       }
       return ResponseEntity.ok(employeeManager.getRating(employeeId));


    }




}
