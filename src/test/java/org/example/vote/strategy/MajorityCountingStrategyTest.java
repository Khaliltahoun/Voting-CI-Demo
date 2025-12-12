package org.example.vote.strategy;

import org.example.vote.model.Vote;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MajorityCountingStrategyTest {

    @Test
    void winnerEmptyWhenNoAbsoluteMajority() {
        MajorityCountingStrategy strategy = new MajorityCountingStrategy();
        Map<String, Integer> tallies = strategy.count(List.of(
                new Vote("v1", "c1", 1L),
                new Vote("v2", "c2", 2L)
        ));

        assertEquals(2, tallies.size());
        assertTrue(strategy.winner(tallies, 2).isEmpty(), "No candidate has >50%");
    }
}
