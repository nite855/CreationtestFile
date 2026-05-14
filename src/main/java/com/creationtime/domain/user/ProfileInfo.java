package com.creationtime.domain.user;

import com.creationtime.domain.common.Required;
import jakarta.persistence.Embeddable;

@Embeddable
public record ProfileInfo(String name, String email, String loginId, String password) {
    public ProfileInfo {
        name = Required.text(name, "name");
        email = Required.text(email, "email");
        loginId = Required.text(loginId, "loginId");
        password = Required.text(password, "password");
    }
}
