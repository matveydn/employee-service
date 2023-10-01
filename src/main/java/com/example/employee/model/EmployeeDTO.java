package com.example.employee.model;

import com.example.employee.config.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EmployeeDTO {

    private UUID uuid;

    @NotNull(message = "email is required")
    @Email(regexp="^(.+)@(.+)$", message = "valid email needed")
    @Size(max = 255)
    private String email;

    @NotNull(message = "full name is required")
    @Size(max = 255)
    @Pattern(regexp = "(\\w+)\\s+(\\w+)", message = "First and last name required delimited by whitespace")
    private String fullName;

    @NotNull(message = "birthday is required")
    @ValidDateFormat(message = "birthday must be in format yyyy-MM-dd")
    private String birthday;

    @Size(max = 255)
    private List<String> hobbies;

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday.format(Constants.DATE_FORMAT);
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
