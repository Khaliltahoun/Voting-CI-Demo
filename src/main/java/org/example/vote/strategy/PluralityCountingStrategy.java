package org.example.vote.strategy;

import org.example.vote.model.Vote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluralityCountingStrategy implements CountingStrategy {
    @Override
    public String name() {
        return "plurality";
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
}
