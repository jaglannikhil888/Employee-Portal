package com.gfg.jbdl12employeeportal.manager;


import com.gfg.jbdl12employeeportal.ForbiddenException;
import com.gfg.jbdl12employeeportal.model.EmployeeCreationRequest;
import com.gfg.jbdl12employeeportal.model.Employee;
import com.gfg.jbdl12employeeportal.model.HR;
import com.gfg.jbdl12employeeportal.model.Manager;
import com.gfg.jbdl12employeeportal.model.Roles;
import com.gfg.jbdl12employeeportal.repositories.EmployeeRepository;
import com.gfg.jbdl12employeeportal.repositories.ManagerRepository;
import com.gfg.jbdl12employeeportal.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sun.nio.ch.SelectorImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeDetailsServiceImpl implements EmployeeManager {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String employeeId) throws UsernameNotFoundException {
        UserDetails userDetails =  employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() -> new UsernameNotFoundException("employee is not found"));
        return userDetails;
    }

    @Override
    public EmployeeCreationResponse createEmployee(EmployeeCreationRequest employeeCreationRequest) {
        switch (employeeCreationRequest.getType()) {
            case HR:
                Optional<Roles> rolesOptional = roleRepository.findByRole("hr");
                Roles roles = null;
                if(!rolesOptional.isPresent()){
                    roles= Roles.builder().role("hr").build();
                }else{
                    roles = rolesOptional.get();
                }
                HR hr = HR.builder()
                        .employeeId(employeeCreationRequest.getFirstName()
                                .concat(".").concat(employeeCreationRequest.getLastName()))
                        .password(passwordEncoder.encode("password"))
                        .firstName(employeeCreationRequest.getFirstName())
                        .lastName(employeeCreationRequest.getLastName())
                        .roles(Arrays.asList(roles))
                        .accountNonExpired(true)
                        .accountNonLocked(true)
                        .credentialsNonExpired(true)
                        .enabled(true)
                        .build();
                Employee employee = employeeRepository.save(hr);
                return EmployeeCreationResponse.builder()
                        .employeeId(employee.getEmployeeId())
                        .password("password")
                        .build();
            case MANAGER:
                rolesOptional = roleRepository.findByRole("manager");
                roles = null;
                if(!rolesOptional.isPresent()){
                    roles= Roles.builder().role("manager").build();
                }else{
                    roles = rolesOptional.get();
                }
                Manager manager = Manager.builder()
                        .employeeId(employeeCreationRequest.getFirstName()
                                .concat(".").concat(employeeCreationRequest.getLastName()))
                        .firstName(employeeCreationRequest.getFirstName())
                        .lastName(employeeCreationRequest.getLastName())
                        .password(passwordEncoder.encode("password"))
                        .roles(Arrays.asList(roles))
                        .accountNonExpired(true)
                        .accountNonLocked(true)
                        .credentialsNonExpired(true)
                        .enabled(true)
                        .build();
                employee = employeeRepository.save(manager);
                return EmployeeCreationResponse.builder()
                        .employeeId(employee.getEmployeeId())
                        .password("password")
                        .build();
            case EMPLOYEE:
                rolesOptional = roleRepository.findByRole("employee");
                roles = null;
                if(!rolesOptional.isPresent()){
                    roles= Roles.builder().role("employee").build();
                }else{
                    roles = rolesOptional.get();
                }
                 employee = Employee.builder()
                        .employeeId(employeeCreationRequest.getFirstName()
                                .concat(".").concat(employeeCreationRequest.getLastName()))
                         .firstName(employeeCreationRequest.getFirstName())
                         .lastName(employeeCreationRequest.getLastName())
                        .password(passwordEncoder.encode("password"))
                        .roles(Arrays.asList(roles))
                        .accountNonExpired(true)
                        .accountNonLocked(true)
                        .credentialsNonExpired(true)
                        .enabled(true)
                        .build();
                employee = employeeRepository.save(employee);
                return EmployeeCreationResponse.builder()
                        .employeeId(employee.getEmployeeId())
                        .password("password")
                        .build();

        }
        return null;
    }

    @Override
    public void addSubOrdinates(List<String> subOrdinateIds, String managerId) throws Exception {
        Manager manager =  managerRepository.findByEmployeeId(managerId)
                .orElseThrow(
                ()->new Exception("manager is not present."));
        List<Employee> subOrdinates = manager.getSubordinates();
        if(subOrdinates == null){
            subOrdinates = new ArrayList();
        }

        for(String employeeId : subOrdinateIds){
            Employee employee = employeeRepository.findByEmployeeId(employeeId).orElse(null);
            if(employee == null)
                continue;
            subOrdinates.add(employee);
        }
        managerRepository.save(manager);

    }

    @Override
    public void provideRating(String employeeId, Float rating ,UsernamePasswordAuthenticationToken loggedInUser) throws ForbiddenException {
        boolean valid = false;

        for(GrantedAuthority authority :loggedInUser.getAuthorities() ){
            if(authority.getAuthority().equalsIgnoreCase("manager")){
                Manager manager = managerRepository.findByEmployeeId(loggedInUser.getName())
                        .orElseThrow(() -> new UsernameNotFoundException("manager is not found"));

                for(Employee employee : manager.getSubordinates()){
                    if(employee.getEmployeeId().equalsIgnoreCase(employeeId)){
                        valid = true;
                    }
                }
            }else if (authority.getAuthority().equalsIgnoreCase("hr")){
                valid = true;
            }
        }
        if(!valid){
            throw new ForbiddenException("employee is not a subordinate of the manager");
        }

        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new UsernameNotFoundException("employee is not found"));
        employee.setRating(rating);
        employeeRepository.save(employee);
    }

    @Override
    public Float getRating(String employeeId) {
        Employee employee = employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() -> new UsernameNotFoundException("employee is not found"));
        return employee.getRating();
    }


//    @Override
//    public void signUp(UserRequest userRequest) throws Exception {
//        if(userRepository.findByUsername(userRequest.getUsername()).isPresent()){
//            throw new Exception("user is present");
//        }
//        Optional<Roles> rolesOptional = roleRepository.findByRole("user");
//        Roles roles = null;
//        if(!rolesOptional.isPresent()){
//            roles= Roles.builder().role("user").build();
//        }else{
//            roles = rolesOptional.get();
//        }
//        if(userRequest.getType() != null){
//            SuperUser user = (SuperUser) SuperUser.builder()
//                    .username(userRequest.getUsername())
//                    .password(passwordEncoder.encode(userRequest.getPassword()))
//                    .roles(Arrays.asList(roles))
//                    .accountNonExpired(true)
//                    .accountNonLocked(true)
//                    .credentialsNonExpired(true)
//                    .enabled(true)
//                    .build();
//            userRepository.save(user);
//        }else{
//            User user = User.builder()
//                    .username(userRequest.getUsername())
//                    .password(passwordEncoder.encode(userRequest.getPassword()))
//                    .roles(Arrays.asList(roles))
//                    .accountNonExpired(true)
//                    .accountNonLocked(true)
//                    .credentialsNonExpired(true)
//                    .enabled(true)
//                    .build();
//            userRepository.save(user);
//        }
//
//    }
}
