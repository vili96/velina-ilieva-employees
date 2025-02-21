package org.example.comparator;

import org.example.model.EmployeePair;

import java.util.Comparator;
import java.util.Map;

public class EmployeeCollaborationComparator implements Comparator<EmployeePair> {
    private final Map<EmployeePair, Map<Integer, Long>> collaborationsByProject;

    public EmployeeCollaborationComparator(Map<EmployeePair, Map<Integer, Long>> collaborationsByProject) {
        this.collaborationsByProject = collaborationsByProject;
    }

    @Override
    public int compare(EmployeePair pair1, EmployeePair pair2) {
        long totalTime1 = calculateTotalTime(pair1);
        long totalTime2 = calculateTotalTime(pair2);
        int sumComparison = Long.compare(totalTime2, totalTime1);
        if (sumComparison != 0) {
            return sumComparison;
        }

        long maxOverlap1 = calculateMaxOverlap(pair1);
        long maxOverlap2 = calculateMaxOverlap(pair2);
        int maxOverlapComparison = Long.compare(maxOverlap2, maxOverlap1);
        if (maxOverlapComparison != 0) {
            return maxOverlapComparison;
        }

        return compareByEmployeeIds(pair1, pair2);
    }

    private long calculateTotalTime(EmployeePair pair) {
        return collaborationsByProject
                .getOrDefault(pair, Map.of())
                .values()
                .stream()
                .mapToLong(Long::longValue)
                .sum();
    }

    private long calculateMaxOverlap(EmployeePair pair) {
        return collaborationsByProject
                .getOrDefault(pair, Map.of())
                .values()
                .stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
    }

    private int compareByEmployeeIds(EmployeePair pair1, EmployeePair pair2) {
        int emp1Comparison = Integer.compare(pair1.getEmp1(), pair2.getEmp1());
        if (emp1Comparison != 0) {
            return emp1Comparison;
        }
        return Integer.compare(pair1.getEmp2(), pair2.getEmp2());
    }
}
