package org.example.vote.strategy;

import org.example.vote.model.Vote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Winner must have more than 50% of total votes; otherwise no winner is returned.
 */
public class MajorityCountingStrategy implements CountingStrategy {
    @Override
    public String name() {
        return "majority";
    }

    @Override
    public Map<String, Integer> count(List<Vote> votes) {
        validateVotes(votes);
        Map<String, Integer> tallies = new HashMap<>();
        for (Vote vote : votes) {
            tallies.merge(vote.candidateId(), 1, Integer::sum);
        }
        return tallies;
    }

    @Override
    public Optional<String> winner(Map<String, Integer> tallies, int totalVotes) {
        if (tallies.isEmpty() || totalVotes == 0) {
            return Optional.empty();
        }
        String bestCandidate = null;
        int bestScore = -1;
        for (var entry : tallies.entrySet()) {
            if (entry.getValue() > bestScore) {
                bestCandidate = entry.getKey();
                bestScore = entry.getValue();
            }
        }
        return bestScore > totalVotes / 2 ? Optional.of(bestCandidate) : Optional.empty();
    }
}
