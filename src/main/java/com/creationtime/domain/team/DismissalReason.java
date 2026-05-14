package com.creationtime.domain.team;

public enum DismissalReason {
    INACTIVE(3),
    MISSED_DEADLINE(3),
    DISRUPTIVE_BEHAVIOR(4),
    PERSONAL_REASON(0);

    private final int penaltyScore;

    DismissalReason(int penaltyScore) {
        this.penaltyScore = penaltyScore;
    }

    public int penaltyScore() {
        return penaltyScore;
    }
}

