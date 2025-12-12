package org.example.vote.service;

import java.util.Map;
import java.util.Optional;

public record CountingResult(Map<String, Integer> tallies, Optional<String> winner, int totalVotes) {
    public CountingResult {
        tallies = Map.copyOf(tallies);
        winner = winner == null ? Optional.empty() : winner;
    }
}
