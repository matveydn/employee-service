package com.example.employee.domain;

import com.example.employee.config.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Employee {

    @Id
    @Column(nullable = false, updatable = false)
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    private UUID uuid;

    @Column(unique = true)
    private String email;

    @Column
    private String fullName;

    @Past(message="date of birth must be less than today")
    @Column
    private LocalDate birthday;

    @Convert(converter = StringListConverter.class)
    @Column
    private List<String> hobbies;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

    public void setBirthday(String birthday) {
        this.birthday = LocalDate.parse(birthday, Constants.DATE_FORMAT);
    }

    public String getBirthdayFormatted() {
        return birthday.format(Constants.DATE_FORMAT);
    }

}
