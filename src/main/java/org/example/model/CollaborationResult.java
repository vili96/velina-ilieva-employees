package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CollaborationResult {

    private final int employee1Id;
    private final int employee2Id;
    private final int projectId;
    private final long daysWorked;
}
