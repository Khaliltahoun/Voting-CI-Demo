package org.example.vote.service;

import org.example.vote.model.Candidate;
import org.example.vote.observer.AuditTrailVoteListener;
import org.example.vote.repo.InMemoryVoteRepository;
import org.example.vote.strategy.MajorityCountingStrategy;
import org.example.vote.strategy.PluralityCountingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VoteServiceTest {
    private VoteService voteService;

    @BeforeEach
    void setUp() {
        voteService = new VoteService(new InMemoryVoteRepository());
        voteService.registerCandidate(new Candidate("alice", "Alice"));
        voteService.registerCandidate(new Candidate("bob", "Bob"));
    }

    @Test
    void candidatesStartEmptyAndDoNotDuplicate() {
        VoteService empty = new VoteService(new InMemoryVoteRepository());
        assertTrue(empty.listCandidates().isEmpty(), "No default candidates should be present");

        empty.registerCandidate(new Candidate("c1", "C1"));
        empty.registerCandidate(new Candidate("c1", "C1 again"));
        assertEquals(1, empty.listCandidates().size(), "Same ID should not create duplicates");
    }

    @Test
    void castAndCountPlurality() {
        voteService.castVote("v1", "alice");
        voteService.castVote("v2", "alice");
        voteService.castVote("v3", "bob");

        CountingResult result = voteService.countVotes(new PluralityCountingStrategy());
        assertEquals(3, result.totalVotes());
        assertEquals(2, result.tallies().get("alice"));
        assertEquals(1, result.tallies().get("bob"));
        assertTrue(result.winner().isPresent());
        assertEquals("alice", result.winner().get());
    }

    @Test
    void majorityStrategyRequiresAbsoluteMajority() {
        voteService.castVote("v1", "alice");
        voteService.castVote("v2", "bob");

        CountingResult noWinner = voteService.countVotes(new MajorityCountingStrategy());
        assertTrue(noWinner.winner().isEmpty(), "No majority with tie");

        voteService.castVote("v3", "alice");
        CountingResult winner = voteService.countVotes(new MajorityCountingStrategy());
        assertEquals("alice", winner.winner().orElseThrow());
    }

    @Test
    void listenersAreNotified() {
        AuditTrailVoteListener listener = new AuditTrailVoteListener();
        voteService.addListener(listener);

        voteService.castVote("v1", "alice");
        voteService.castVote("v2", "bob");

        assertEquals(2, listener.receivedVotes().size());
        assertEquals("alice", listener.receivedVotes().get(0).candidateId());
    }
}
