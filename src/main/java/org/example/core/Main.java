package org.example.core;

import lombok.extern.log4j.Log4j2;
import org.example.exception.CsvValidationException;
import org.example.model.EmployeePair;
import org.example.model.EmployeeWorkDuration;
import org.example.model.PairCollaborationTime;
import org.example.ui.EmployeeCollaborationUI;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.example.util.Constants.*;
import static org.example.util.ErrorConstants.*;
import static org.example.util.LoggingConstants.*;
import static org.example.util.ValidationUtil.*;

@Log4j2
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmployeeCollaborationUI ui = new EmployeeCollaborationUI();
            ui.setVisible(true);
        });
    }

    /**
     * Call this method in main if you only need to execute the logic for solving the task, without UI
     * Input data could be found in src/main/resources/files/input.csv
     * Use input2.csv for simpler data if needed
     */
    private static void executeSolution() {
        var allEmployeeWorkDurations = parseCsvContent(Main.class.getResourceAsStream(CSV_FILE_PATH));
        findLongestCollaboration(allEmployeeWorkDurations);
    }

    public static Set<EmployeeWorkDuration> parseCsvContent(InputStream is) {
        try (is) {
            if (is == null) {
                throw new CsvValidationException(NULL_STREAM);
            }

            var employeeRecords = new HashSet<EmployeeWorkDuration>();

            try (var reader = new BufferedReader(new InputStreamReader(is))) {
                var headerLine = reader.readLine();
                validateHeader(headerLine);

                var lineNumber = 1;
                String line;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    processDataLine(line, lineNumber, employeeRecords);
                }

                validateResults(employeeRecords);

            } catch (IOException e) {
                log.error(CSV_READ_ERROR, e.getMessage());
                throw new CsvValidationException(CSV_READ_FAIL + e.getMessage());
            }

            return employeeRecords;
        } catch (CsvValidationException e) {
            log.error(CSV_PROCESSING_ERROR, e.getMessage());
            throw new RuntimeException(CSV_PROCESS_FAIL, e);
        } catch (IOException e) {
            log.error(IO_ERROR, e);
            throw new RuntimeException(FILE_READ_FAIL, e);
        }
    }

    private static void processDataLine(String line, int lineNumber, Set<EmployeeWorkDuration> records) {
        if (line.trim().isEmpty()) {
            return;
        }

        var parts = line.split(CSV_DELIMITER);
        if (parts.length < EXPECTED_COLUMNS) {
            log.warn(INVALID_COLUMNS, lineNumber, EXPECTED_COLUMNS, parts.length);
            return;
        }

        var employeeWorkDuration = parseEmployeeWorkDuration(parts, lineNumber);
        if (employeeWorkDuration != null) {
            records.add(employeeWorkDuration);
        }
    }


    public static Map<EmployeePair, Map<Integer, Long>> findLongestCollaboration(Set<EmployeeWorkDuration> workDurations) {
        var startTime = System.currentTimeMillis();
        var workDurationsByProjectIdMap = workDurations.stream()
                .collect(Collectors.groupingBy(EmployeeWorkDuration::getProjectId));

        var collaborationTimeByPairAndProjectMap = new ConcurrentHashMap<EmployeePair, Long>();
        var collaborationsByProjectMap = new ConcurrentHashMap<EmployeePair, Map<Integer, Long>>();

        workDurationsByProjectIdMap.entrySet().parallelStream()
                .forEach(entry -> {
                    var projectId = entry.getKey();
                    var durationsInProject = entry.getValue();
                    processOneProject(projectId, durationsInProject, collaborationTimeByPairAndProjectMap, collaborationsByProjectMap);
                });

        var result = collaborationTimeByPairAndProjectMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new PairCollaborationTime(e.getKey(), e.getValue()))
                .orElse(new PairCollaborationTime(new EmployeePair(0, 0), 0));

        logResult(result);
        log.info(PROCESSING_TIME, System.currentTimeMillis() - startTime);

        return collaborationsByProjectMap;
    }

    private static void logResult(PairCollaborationTime result) {
        if (result.getDaysWorkedTogether() > 0) {
            log.info(LONGEST_COLLABORATION,
                    result.getPair().getEmp1(),
                    result.getPair().getEmp2(),
                    result.getDaysWorkedTogether());
        } else {
            log.info(NO_COLLABORATIONS);
        }
    }

    private static void processOneProject(int projectId,
                                          List<EmployeeWorkDuration> durationsInProject,
                                          Map<EmployeePair, Long> globalCollaborations,
                                          Map<EmployeePair, Map<Integer, Long>> projectCollaborations) {
        var byEmployee = durationsInProject.stream()
                .collect(Collectors.groupingBy(EmployeeWorkDuration::getEmpId));

        var mergedByEmployee = byEmployee.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> mergeIntervalsForOneEmployee(e.getValue())
                ));

        var employeeIds = new ArrayList<>(mergedByEmployee.keySet());
        Collections.sort(employeeIds);

        for (var i = 0; i < employeeIds.size() - 1; i++) {
            for (var j = i + 1; j < employeeIds.size(); j++) {
                var emp1 = employeeIds.get(i);
                var emp2 = employeeIds.get(j);
                var overlap = calculateOverlapBetweenTwoEmployees(
                        mergedByEmployee.get(emp1),
                        mergedByEmployee.get(emp2)
                );
                if (overlap > 0) {
                    var pair = new EmployeePair(emp1, emp2);
                    globalCollaborations.merge(pair, overlap, Long::sum);
                    projectCollaborations.computeIfAbsent(pair, k -> new ConcurrentHashMap<>())
                            .put(projectId, overlap);
                    log.debug(PROJECT_OVERLAP, projectId, emp1, emp2, overlap);
                }
            }
        }
    }

    private static List<EmployeeWorkDuration> mergeIntervalsForOneEmployee(List<EmployeeWorkDuration> intervals) {
        if (intervals.size() <= 1) {
            return intervals;
        }

        var sorted = intervals.stream()
                .sorted(Comparator
                        .comparing(EmployeeWorkDuration::getDateFrom)
                        .thenComparing(EmployeeWorkDuration::getDateTo))
                .toList();

        var merged = new ArrayList<EmployeeWorkDuration>();
        var currentStart = sorted.getFirst().getDateFrom();
        var currentEnd = sorted.getFirst().getDateTo();
        var empId = sorted.getFirst().getEmpId();
        var projectId = sorted.getFirst().getProjectId();

        for (var i = 1; i < sorted.size(); i++) {
            var next = sorted.get(i);
            if (!next.getDateFrom().isAfter(currentEnd)) {
                if (next.getDateTo().isAfter(currentEnd)) {
                    currentEnd = next.getDateTo();
                }
            } else {
                merged.add(new EmployeeWorkDuration(empId, projectId, currentStart, currentEnd));
                currentStart = next.getDateFrom();
                currentEnd = next.getDateTo();
            }
        }
        merged.add(new EmployeeWorkDuration(empId, projectId, currentStart, currentEnd));
        return merged;
    }

    private static long calculateOverlapBetweenTwoEmployees(List<EmployeeWorkDuration> intervals1,
                                                            List<EmployeeWorkDuration> intervals2) {
        var total = 0L;
        for (var w1 : intervals1) {
            for (var w2 : intervals2) {
                var overlap = getOverlapInDays(
                        w1.getDateFrom(), w1.getDateTo(),
                        w2.getDateFrom(), w2.getDateTo()
                );
                if (overlap > 0) {
                    total += overlap;
                }
            }
        }
        return total;
    }

    private static long getOverlapInDays(LocalDate start1, LocalDate end1,
                                         LocalDate start2, LocalDate end2) {
        var overlapStart = start1.isAfter(start2) ? start1 : start2;
        var overlapEnd = end1.isBefore(end2) ? end1 : end2;

        return overlapEnd.isBefore(overlapStart) ? 0 :
                ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1;
    }
}