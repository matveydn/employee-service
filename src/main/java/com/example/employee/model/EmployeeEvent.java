package com.example.employee.model;

import java.util.StringJoiner;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EmployeeEvent extends EmployeeDTO {
  private UUID eventId;
  private EventTypes eventType;

  public EmployeeEvent() {
    super();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", EmployeeEvent.class.getSimpleName() + "[", "]")
        .add("eventId=" + eventId)
        .add("eventType=" + eventType)
        .toString();
  }
}
