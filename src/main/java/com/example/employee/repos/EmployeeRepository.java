package com.example.employee.repos;

import com.example.employee.domain.Employee;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    boolean existsByEmailIgnoreCase(String email);

}
