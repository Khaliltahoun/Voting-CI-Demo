package org.example.vote.service;

import org.example.vote.model.Candidate;
import org.example.vote.repo.InMemoryVoteRepository;
import org.example.vote.strategy.PluralityCountingStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VoteServiceResetTest {

    @Test
    void resetClearsAllVotes() {
        VoteService service = new VoteService(new InMemoryVoteRepository());
        service.registerCandidate(new Candidate("c1", "Alice"));
        service.castVote("v1", "c1");

        CountingResult beforeReset = service.countVotes(new PluralityCountingStrategy());
        assertEquals(1, beforeReset.totalVotes());

        service.reset();
        CountingResult afterReset = service.countVotes(new PluralityCountingStrategy());
        assertEquals(0, afterReset.totalVotes(), "Reset should remove all votes");
    }
}
