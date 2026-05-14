package com.creationtime.domain.team;

import com.creationtime.domain.common.Required;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record TeamInfo(
        String name,
        @Enumerated(EnumType.STRING)
        TeamCategory category,
        int maxMemberCount,
        String requiredTechStack,
        String description
) {
    public TeamInfo {
        name = Required.text(name, "team name");
        maxMemberCount = Required.positive(maxMemberCount, "maxMemberCount");
        requiredTechStack = Required.text(requiredTechStack, "requiredTechStack");
        description = Required.text(description, "description");
        if (category == null) {
            throw new IllegalArgumentException("category is required.");
        }
    }
}
