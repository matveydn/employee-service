package com.example.employee.model;

import com.example.employee.config.Constants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateFormatValidator implements ConstraintValidator<ValidDateFormat, String> {
  @Override
  public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext) {
    try {
      LocalDate.parse(date, Constants.DATE_FORMAT);
    } catch (DateTimeParseException e) {
      return false;
    }
    return true;
  }
}
