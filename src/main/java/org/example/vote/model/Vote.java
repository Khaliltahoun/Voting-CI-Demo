package org.example.vote.model;

import java.util.Objects;

/**
 * Represents a single vote cast by a voter for a candidate at a given timestamp.
 */
public record Vote(String voterId, String candidateId, long timestamp) {
    public Vote {
        Objects.requireNonNull(voterId, "voterId must not be null");
        Objects.requireNonNull(candidateId, "candidateId must not be null");
    }
}
