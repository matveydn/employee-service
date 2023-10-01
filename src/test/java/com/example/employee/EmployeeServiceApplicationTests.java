package com.example.employee;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.example.employee.domain.Employee;
import com.example.employee.repos.EmployeeRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeServiceApplicationTests {

  @LocalServerPort
  private Integer port;

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:16-alpine"
  );

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  EmployeeRepository employeeRepository;

  @BeforeEach
  void setUp() {
    RestAssured.baseURI = "http://localhost:" + port;
    employeeRepository.deleteAll();
  }

  @Test
  void shouldGetAllEmployees() {
    Employee employee = new Employee();
    employee.setFullName("John Doe");
    employee.setEmail("john@doe");
    employee.setBirthday("1990-01-01");
    employee.setHobbies(List.of("swimming", "hiking"));
    Employee employee2 = new Employee();
    employee2.setFullName("John2 Doe2");
    employee2.setEmail("john2@doe2");
    employee2.setBirthday("1990-02-02");
    employee2.setHobbies(List.of("sleeping"));

    given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/employees")
        .then()
        .statusCode(200)
        .body(".", hasSize(0));

    employeeRepository.save(employee);
    employeeRepository.save(employee2);

    given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/employees")
        .then()
        .statusCode(200)
        .body(".", hasSize(2));
  }

  @Test
  void shouldCreateEmployee() {
    var uuid = given()
        .contentType(ContentType.JSON)
        .body("""
            {
              "email": "john@doe",
              "fullName": "John Doe",
              "birthday": "1990-01-01",
              "hobbies": ["swimming", "hiking"]
            }
            """)
        .when()
        .post("/api/employees")
        .then()
        .statusCode(201)
        .body("uuid", is(notNullValue()))
        .extract()
        .as(HashMap.class);

    // validate creation
    given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/employees/" + uuid.get("uuid"))
        .then()
        .statusCode(200)
        .body(
            "birthday", is("1990-01-01"),
            "fullName", is("John Doe"),
            "email", is("john@doe"),
            "hobbies", is(List.of("swimming", "hiking"))
        );
  }

  @Test
  void shouldUpdateEmployeeByUUID() {
    Employee employee = new Employee();
    employee.setFullName("John Doe");
    employee.setEmail("john@doe");
    employee.setBirthday("1990-01-01");
    employee.setHobbies(List.of("swimming", "hiking"));

    Employee employee2 = new Employee();
    employee2.setFullName("John2 Doe2");
    employee2.setEmail("john2@doe2");
    employee2.setBirthday("1990-02-02");
    employee2.setHobbies(List.of("sleeping"));

    employeeRepository.save(employee);
    employeeRepository.save(employee2);

    // update birthday value
    given()
        .contentType(ContentType.JSON)
        .body("""
            {
              "email": "john@doe",
              "fullName": "John Doe",
              "birthday": "1990-03-03",
              "hobbies": ["swimming", "hiking"]
            }
            """)
        .when()
        .put("/api/employees/" + employee.getUuid())
        .then()
        .statusCode(200)
        .body("uuid", is(employee.getUuid().toString()));

    // validate change
    given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/employees/" + employee.getUuid())
        .then()
        .statusCode(200)
        .body("birthday", is("1990-03-03"),
            "fullName", is(employee.getFullName()),
            "email", is(employee.getEmail()),
            "hobbies", is(employee.getHobbies()));
  }

  @Test
  void shouldGetEmployeeByUUID() {
    Employee employee = new Employee();
    employee.setFullName("John Doe");
    employee.setEmail("john@doe");
    employee.setBirthday("1990-01-01");
    employee.setHobbies(List.of("swimming", "hiking"));

    Employee employee2 = new Employee();
    employee2.setFullName("John2 Doe2");
    employee2.setEmail("john2@doe2");
    employee2.setBirthday("1990-02-02");
    employee2.setHobbies(List.of("sleeping"));

    employeeRepository.save(employee);
    employeeRepository.save(employee2);

    given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/employees/" + employee.getUuid())
        .then()
        .statusCode(200)
        .body(
            "birthday", is(employee.getBirthdayFormatted()),
            "fullName", is(employee.getFullName()),
            "email", is(employee.getEmail()),
            "hobbies", is(employee.getHobbies())
        );
  }

  @Test
  void shouldDeleteEmployeeByUUID() {
    Employee employee = new Employee();
    employee.setFullName("John Doe");
    employee.setEmail("john@doe");
    employee.setBirthday("1990-01-01");
    employee.setHobbies(List.of("swimming", "hiking"));

    Employee employee2 = new Employee();
    employee2.setFullName("John2 Doe2");
    employee2.setEmail("john2@doe2");
    employee2.setBirthday("1990-02-02");
    employee2.setHobbies(List.of("sleeping"));

    employeeRepository.save(employee);
    employeeRepository.save(employee2);

    given()
        .contentType(ContentType.JSON)
        .when()
        .delete("/api/employees/" + employee.getUuid())
        .then()
        .statusCode(204);

    // validate deletion
    given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/employees/" + employee.getUuid())
        .then()
        .statusCode(404);
  }
}
