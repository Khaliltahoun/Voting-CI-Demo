package org.example.vote.strategy;

import org.example.vote.model.Vote;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public interface CountingStrategy {
    String name();

    Map<String, Integer> count(List<Vote> votes);

    default Optional<String> winner(Map<String, Integer> tallies, int totalVotes) {
        return tallies.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey);
    }

    default void validateVotes(List<Vote> votes) {
        Objects.requireNonNull(votes, "votes must not be null");
    }
}
