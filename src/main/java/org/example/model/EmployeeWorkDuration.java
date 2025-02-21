package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
public class EmployeeWorkDuration {
    int empId;
    int projectId;
    LocalDate dateFrom;
    LocalDate dateTo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeWorkDuration other)) return false;
        return empId == other.empId
                && projectId == other.projectId
                && Objects.equals(dateFrom, other.dateFrom)
                && Objects.equals(dateTo, other.dateTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empId, projectId, dateFrom, dateTo);
    }
}

