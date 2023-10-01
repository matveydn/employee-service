package com.example.employee.service;

import com.example.employee.domain.Employee;
import com.example.employee.model.EmployeeDTO;
import com.example.employee.model.EmployeeEvent;
import com.example.employee.model.EventTypes;
import com.example.employee.repos.EmployeeRepository;
import com.example.employee.util.NotFoundException;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class EmployeeService {
    private static final Logger LOGGER = LogManager.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public EmployeeService(final EmployeeRepository employeeRepository,
        RabbitTemplate rabbitTemplate) {
        this.employeeRepository = employeeRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<EmployeeDTO> findAll() {
        LOGGER.info("Getting all employees");
        final List<Employee> employees = employeeRepository.findAll(Sort.by("uuid"));
        return employees.stream()
                .map(employee -> mapToDTO(employee, new EmployeeDTO()))
                .toList();
    }

    public EmployeeDTO get(final UUID uuid) {
        LOGGER.info("Getting employee: {}", uuid);
        return employeeRepository.findById(uuid)
                .map(employee -> mapToDTO(employee, new EmployeeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UUID create(final EmployeeDTO employeeDTO) {
        final Employee employee = new Employee();
        mapToEntity(employeeDTO, employee);
        emailExists(employee.getEmail());
        LOGGER.info("Creating employee with id: {}", employee.getUuid());
        UUID uuid = employeeRepository.save(employee).getUuid();
        sendEvent(employee, EventTypes.CREATED);
        return uuid;
    }

    public UUID update(final UUID uuid, final EmployeeDTO employeeDTO) {
        final Employee employee = employeeRepository.findById(uuid)
                .orElseThrow(NotFoundException::new);
        if (!employee.getEmail().equalsIgnoreCase(employeeDTO.getEmail())) {
            emailExists(employeeDTO.getEmail());
        }
        mapToEntity(employeeDTO, employee);
        LOGGER.info("Updating employee with id: {}", employee.getUuid());
        UUID updatedUUID = employeeRepository.save(employee).getUuid();
        sendEvent(employee, EventTypes.UPDATED);
        return updatedUUID;
    }

    public void delete(final UUID uuid) {
        final Employee employee = employeeRepository.findById(uuid)
            .orElseThrow(NotFoundException::new);
        LOGGER.info("Deleting employee with id: {}", employee.getUuid());
        employeeRepository.deleteById(uuid);
        sendEvent(employee, EventTypes.DELETED);
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

    private EmployeeEvent mapToEvent(final Employee employee, final EmployeeEvent employeeEvent, 
        EventTypes eventType) {
        employeeEvent.setEventId(UUID.randomUUID());
        employeeEvent.setEventType(eventType);
        employeeEvent.setUuid(employee.getUuid());
        employeeEvent.setEmail(employee.getEmail());
        employeeEvent.setFullName(employee.getFullName());
        employeeEvent.setBirthday(employee.getBirthday());
        employeeEvent.setHobbies(employee.getHobbies());
        return employeeEvent;
    }

    public void emailExists(final String email) {
        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            LOGGER.warn("Email already exists: {}", email);
            throw new IllegalArgumentException("Email already exists");
        }
    }

    private void sendEvent(Employee employee, EventTypes eventType) {
        EmployeeEvent employeeEvent = new EmployeeEvent();
        mapToEvent(employee, employeeEvent, eventType);
        LOGGER.info("Sending event: {}", employeeEvent);
        rabbitTemplate.convertAndSend("q.employee-updates", employeeEvent);
    }

}
