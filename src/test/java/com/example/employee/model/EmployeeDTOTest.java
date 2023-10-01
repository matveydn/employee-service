package com.example.employee.model;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmployeeDTOTest {

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {

  }

  @Test
  void setBirthday() throws JsonProcessingException {
    EmployeeDTO employeeDTO = new EmployeeDTO();
    employeeDTO.setBirthday(LocalDate.now());
    employeeDTO.setHobbies(List.of("swimming", "hiking"));
    employeeDTO.setEmail("test@test.com");
    employeeDTO.setFullName("Test Test");
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String json = objectMapper.writeValueAsString(employeeDTO);
    System.out.println(json);
  }
}