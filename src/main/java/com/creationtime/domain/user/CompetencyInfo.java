package com.creationtime.domain.user;

import com.creationtime.domain.common.Required;
import jakarta.persistence.Embeddable;

@Embeddable
public record CompetencyInfo(String interestField, String techStack, String desiredRole) {
    public CompetencyInfo {
        interestField = Required.text(interestField, "interestField");
        techStack = Required.text(techStack, "techStack");
        desiredRole = Required.text(desiredRole, "desiredRole");
    }
}
