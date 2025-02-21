package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PairCollaborationTime {

    private final EmployeePair pair;
    private final long daysWorkedTogether;

}
