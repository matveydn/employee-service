package com.example.employee.rest;

import com.example.employee.model.EmployeeDTO;
import com.example.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Operation(summary = "Get all employees")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found employees",
            content = { @Content(mediaType = "application/json",
                schema = @Schema(implementation = EmployeeDTO.class)) })
    })
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.findAll());
    }

    @Operation(summary = "Get a employee by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the employee",
            content = { @Content(mediaType = "application/json",
                schema = @Schema(implementation = EmployeeDTO.class)) }),
        @ApiResponse(responseCode = "400", description = "Invalid id supplied",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Employee not found",
            content = @Content) })
    @GetMapping("/{uuid}")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable final UUID uuid) {
        return ResponseEntity.ok(employeeService.get(uuid));
    }

    @Operation(summary = "Create employee from given payload")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Employee successfully created",
            content = { @Content(mediaType = "application/json") }),
        @ApiResponse(responseCode = "400", description = "Invalid data supplied",
            content = @Content),
        @ApiResponse(responseCode = "500", description = "Validation error",
            content = @Content) })
    @PostMapping
    public ResponseEntity<Map<String, UUID>> createEmployee(@RequestBody @Valid final EmployeeDTO employeeDTO) {
        final UUID createdUuid = employeeService.create(employeeDTO);
        return new ResponseEntity<>(Map.of("uuid", createdUuid), HttpStatus.CREATED);
    }

    @Operation(summary = "Update employee from given payload")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee successfully updated",
            content = { @Content(mediaType = "application/json") }),
        @ApiResponse(responseCode = "400", description = "Invalid data supplied",
            content = @Content),
        @ApiResponse(responseCode = "500", description = "Validation error",
            content = @Content) })
    @PutMapping("/{uuid}")
    public ResponseEntity<Map<String, UUID>> updateEmployee(@PathVariable final UUID uuid,
            @RequestBody @Valid final EmployeeDTO employeeDTO) {
        final UUID updatedUUID = employeeService.update(uuid, employeeDTO);
        return new ResponseEntity<>(Map.of("uuid", updatedUUID), HttpStatus.OK);
    }

    @Operation(summary = "Delete employee by it's id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Employee successfully deleted",
            content = { @Content(mediaType = "application/json") }),
        @ApiResponse(responseCode = "400", description = "Invalid data supplied",
            content = @Content),
        @ApiResponse(responseCode = "500", description = "Validation error",
            content = @Content) })
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable final UUID uuid) {
        employeeService.delete(uuid);
        return ResponseEntity.noContent().build();
    }

}
