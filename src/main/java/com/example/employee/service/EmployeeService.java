package com.example.employee.service;

import com.example.employee.domain.Employee;
import com.example.employee.model.EmployeeDTO;
import com.example.employee.repos.EmployeeRepository;
import com.example.employee.util.NotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(final EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeDTO> findAll() {
        final List<Employee> employees = employeeRepository.findAll(Sort.by("uuid"));
        return employees.stream()
                .map(employee -> mapToDTO(employee, new EmployeeDTO()))
                .toList();
    }

    public EmployeeDTO get(final UUID uuid) {
        return employeeRepository.findById(uuid)
                .map(employee -> mapToDTO(employee, new EmployeeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UUID create(final EmployeeDTO employeeDTO) {
        final Employee employee = new Employee();
        mapToEntity(employeeDTO, employee);
        emailExists(employee.getEmail());
        return employeeRepository.save(employee).getUuid();
    }

    public UUID update(final UUID uuid, final EmployeeDTO employeeDTO) {
        final Employee employee = employeeRepository.findById(uuid)
                .orElseThrow(NotFoundException::new);
        if (!employee.getEmail().equalsIgnoreCase(employeeDTO.getEmail())) {
            emailExists(employeeDTO.getEmail());
        }
        mapToEntity(employeeDTO, employee);
        return employeeRepository.save(employee).getUuid();
    }

    public void delete(final UUID uuid) {
        employeeRepository.deleteById(uuid);
    }

    private EmployeeDTO mapToDTO(final Employee employee, final EmployeeDTO employeeDTO) {
        employeeDTO.setUuid(employee.getUuid());
        employeeDTO.setEmail(employee.getEmail());
        employeeDTO.setFullName(employee.getFullName());
        employeeDTO.setBirthday(employee.getBirthday());
        employeeDTO.setHobbies(employee.getHobbies());
        return employeeDTO;
    }

    private Employee mapToEntity(final EmployeeDTO employeeDTO, final Employee employee) {
        employee.setEmail(employeeDTO.getEmail());
        employee.setFullName(employeeDTO.getFullName());
        employee.setBirthday(employeeDTO.getBirthday());
        employee.setHobbies(employeeDTO.getHobbies());
        return employee;
    }

    public void emailExists(final String email) {
        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

}
