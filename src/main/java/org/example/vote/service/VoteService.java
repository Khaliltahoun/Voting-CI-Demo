package org.example.vote.service;

import org.example.vote.model.Candidate;
import org.example.vote.model.Vote;
import org.example.vote.observer.VoteListener;
import org.example.vote.repo.VoteRepository;
import org.example.vote.strategy.CountingStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class VoteService {
    private final VoteRepository repository;
    private final Map<String, Candidate> candidates = new LinkedHashMap<>();
    private final List<VoteListener> listeners = new ArrayList<>();

    public VoteService(VoteRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    public void registerCandidate(Candidate candidate) {
        Objects.requireNonNull(candidate, "candidate must not be null");
        candidates.putIfAbsent(candidate.id(), candidate);
    }

    public List<Candidate> listCandidates() {
        return Collections.unmodifiableList(new ArrayList<>(candidates.values()));
    }

    public Vote castVote(String voterId, String candidateId) {
        if (!candidates.containsKey(candidateId)) {
            throw new IllegalArgumentException("Unknown candidate: " + candidateId);
        }
        Vote vote = new Vote(voterId, candidateId, System.currentTimeMillis());
        repository.save(vote);
        listeners.forEach(listener -> listener.onVote(vote));
        return vote;
    }

    public CountingResult countVotes(CountingStrategy strategy) {
        Objects.requireNonNull(strategy, "strategy must not be null");
        List<Vote> votes = repository.findAll();
        Map<String, Integer> tallies = strategy.count(votes);
        Optional<String> winner = strategy.winner(tallies, votes.size());
        return new CountingResult(tallies, winner, votes.size());
    }

    public void addListener(VoteListener listener) {
        listeners.add(Objects.requireNonNull(listener, "listener must not be null"));
    }

    public void reset() {
        repository.clear();
    }
}
