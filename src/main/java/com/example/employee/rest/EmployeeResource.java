package com.example.employee.rest;

import com.example.employee.model.EmployeeDTO;
import com.example.employee.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/employees", produces = MediaType.APPLICATION_JSON_VALUE)
public class EmployeeResource {


    private final EmployeeService employeeService;

    @Autowired
    public EmployeeResource(final EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.findAll());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable final UUID uuid) {
        return ResponseEntity.ok(employeeService.get(uuid));
    }

    @PostMapping
    public ResponseEntity<UUID> createEmployee(@RequestBody @Valid final EmployeeDTO employeeDTO) {
        final UUID createdUuid = employeeService.create(employeeDTO);
        return new ResponseEntity<>(createdUuid, HttpStatus.CREATED);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<UUID> updateEmployee(@PathVariable final UUID uuid,
            @RequestBody @Valid final EmployeeDTO employeeDTO) {
        employeeService.update(uuid, employeeDTO);
        return ResponseEntity.ok(uuid);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable final UUID uuid) {
        employeeService.delete(uuid);
        return ResponseEntity.noContent().build();
    }

}
