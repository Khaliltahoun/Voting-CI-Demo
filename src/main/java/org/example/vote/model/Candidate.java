package org.example.vote.model;

import java.util.Objects;

/**
 * Represents a candidate who can receive votes.
 */
public record Candidate(String id, String name) {
    public Candidate {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
    }
}
