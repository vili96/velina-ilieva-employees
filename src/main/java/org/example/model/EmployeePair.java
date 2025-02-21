package org.example.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public class EmployeePair {
    private final int emp1;
    private final int emp2;
    private final String key;  // Cache the key

    public EmployeePair(int id1, int id2) {
        this.emp1 = Math.min(id1, id2);
        this.emp2 = Math.max(id1, id2);
        this.key = emp1 + "_" + emp2;  // Pre-compute the key
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeePair that)) return false;
        return key.equals(that.key);  // Compare using cached key
    }

    @Override
    public int hashCode() {
        return key.hashCode();  // Use cached key's hash
    }
}
