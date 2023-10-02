package com.example.employee.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.employee.model.EmployeeDTO;
import com.example.employee.service.EmployeeService;
import com.example.employee.util.NotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeResource.class)
class EmployeeResourceTest {

  @MockBean
  private EmployeeService employeeService;
  @Autowired
  private MockMvc mockMvc;

  // Unhappy path, validations and error handling
  @Test
  void employeeNotFound() throws Exception {
    UUID uuid = UUID.randomUUID();
    when(employeeService.get(uuid)).thenThrow(new NotFoundException());

    mockMvc.perform(get("/api/employees/{uuid}", uuid))
        .andExpect(status().isNotFound());
  }

  @Test
  void noEmployeesFound() throws Exception {
    when(employeeService.findAll()).thenReturn(Collections.emptyList());
    mockMvc.perform(get("/api/employees"))
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  void noEmployeeToDelete() throws Exception {
    UUID uuid = UUID.randomUUID();
    doThrow(new NotFoundException()).when(employeeService).delete(uuid);
    mockMvc.perform(delete("/api/employees/{uuid}", uuid))
        .andExpect(status().isNotFound());
  }

  @Test
  void creatEmployeeNotValidEmail() throws Exception {
    String payload = """
            {
              "email": "john?doe",
              "fullName": "John Doe",
              "birthday": "1990-01-01",
              "hobbies": ["swimming", "hiking"]
            }
            """;
    mockMvc.perform(post("/api/employees")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
        .andExpect(status().isBadRequest());
  }
  @Test
  void creatEmployeeNoFullName() throws Exception {
    String payload = """
            {
              "email": "john@doe",
              "fullName": "JohnDoe",
              "birthday": "1990-01-01",
              "hobbies": ["swimming", "hiking"]
            }
            """;
    mockMvc.perform(post("/api/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void creatEmployeeWrongDateFormat() throws Exception {
    String payload = """
            {
              "email": "john@doe",
              "fullName": "John Doe",
              "birthday": "1990/01/01",
              "hobbies": ["swimming", "hiking"]
            }
            """;
    mockMvc.perform(post("/api/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest());
  }

  @Test
  void creatEmployeeMissingHobbies() throws Exception {
    String payload = """
            {
              "email": "john@doe",
              "fullName": "John Doe",
              "birthday": "1990-01-01"
            }
            """;
    UUID uuid = UUID.randomUUID();
    when(employeeService.create(any())).thenReturn(uuid);
    mockMvc.perform(post("/api/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.uuid").value(uuid.toString()));
  }

  @Test
  void updateEmployeeMailAlreadyExist() throws Exception {
    String payload = """
            {
              "email": "john@doe",
              "fullName": "John Doe",
              "birthday": "1990-01-01",
              "hobbies": ["swimming", "hiking"]
            }
            """;
    UUID uuid = UUID.randomUUID();
    when(employeeService.update(any(), any())).thenThrow(new IllegalArgumentException());
    mockMvc.perform(put("/api/employees/{uuid}", uuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isBadRequest());
  }

  // Happy path

  @Test
  void updateEmployee() throws Exception {
    String payload = """
            {
              "email": "john@doe",
              "fullName": "John Doe",
              "birthday": "1990-01-01",
              "hobbies": ["swimming", "hiking"]
            }
            """;
    UUID uuid = UUID.randomUUID();
    when(employeeService.update(any(), any())).thenReturn(uuid);
    mockMvc.perform(put("/api/employees/{uuid}", uuid)
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.uuid").value(uuid.toString()));
  }

  @Test
  void createEmployee() throws Exception {
    String payload = """
            {
              "email": "john@doe",
              "fullName": "John Doe",
              "birthday": "1990-01-01",
              "hobbies": ["swimming", "hiking"]
            }
            """;
    UUID uuid = UUID.randomUUID();
    doReturn(uuid).when(employeeService).create(any());
    mockMvc.perform(post("/api/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.uuid").value(uuid.toString()));
  }

  @Test
  void employeeFound() throws Exception {
    UUID uuid = UUID.randomUUID();
    EmployeeDTO employeeDTO = new EmployeeDTO();
    employeeDTO.setFullName("John2 Doe2");
    employeeDTO.setEmail("john2@doe2");
    employeeDTO.setBirthday("1990-02-02");
    employeeDTO.setHobbies(List.of("sleeping"));

    when(employeeService.get(uuid)).thenReturn(employeeDTO);

    mockMvc.perform(get("/api/employees/{uuid}", uuid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fullName").value(employeeDTO.getFullName()));
  }

  @Test
  void allEmployeesFound() throws Exception {
    EmployeeDTO employeeDTO = new EmployeeDTO();
    employeeDTO.setFullName("John Doe");
    employeeDTO.setEmail("john@doe");
    employeeDTO.setBirthday("1990-02-02");
    employeeDTO.setHobbies(List.of("sleeping"));

    EmployeeDTO employeeDTO2 = new EmployeeDTO();
    employeeDTO2.setFullName("John2 Doe2");
    employeeDTO2.setEmail("john2@doe2");
    employeeDTO2.setBirthday("1990-02-02");
    employeeDTO2.setHobbies(List.of("hiking"));

    when(employeeService.findAll()).thenReturn(List.of(employeeDTO, employeeDTO2));

    mockMvc.perform(get("/api/employees"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].fullName").value(employeeDTO.getFullName()))
        .andExpect(jsonPath("$[1].fullName").value(employeeDTO2.getFullName()));
  }

  @Test
  void employeeDeleted() throws Exception {
    UUID uuid = UUID.randomUUID();
    doNothing().when(employeeService).delete(uuid);
    mockMvc.perform(delete("/api/employees/{uuid}", uuid))
        .andExpect(status().isNoContent());
  }

}