package com.creationtime.domain.collaboration;

import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.common.Required;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class VoteOption {
    private long id;
    private String text;

    protected VoteOption() {
    }

    public VoteOption(long id, String text) {
        if (id <= 0) {
            throw new DomainException("option id must be positive.");
        }
        this.id = id;
        this.text = Required.text(text, "option text");
    }

    public long id() {
        return id;
    }

    public String text() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VoteOption that)) {
            return false;
        }
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
