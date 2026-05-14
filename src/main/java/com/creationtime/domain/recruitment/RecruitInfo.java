package com.creationtime.domain.recruitment;

import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.common.Required;
import com.creationtime.domain.team.TeamCategory;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

@Embeddable
public record RecruitInfo(
        @Enumerated(EnumType.STRING)
        TeamCategory category,
        String recruitField,
        String techStack,
        String requiredRole,
        int recruitCount,
        String progressMethod,
        String contactLink,
        LocalDateTime deadline
) {
    public RecruitInfo {
        if (category == null) {
            throw new DomainException("category is required.");
        }
        recruitField = Required.text(recruitField, "recruitField");
        techStack = Required.text(techStack, "techStack");
        requiredRole = Required.text(requiredRole, "requiredRole");
        recruitCount = Required.positive(recruitCount, "recruitCount");
        progressMethod = Required.text(progressMethod, "progressMethod");
        contactLink = Required.text(contactLink, "contactLink");
        if (deadline == null || deadline.isBefore(LocalDateTime.now())) {
            throw new DomainException("deadline must be in the future.");
        }
    }
}
