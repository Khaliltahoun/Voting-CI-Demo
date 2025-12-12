package org.example.vote.repo;

import org.example.vote.model.Vote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryVoteRepository implements VoteRepository {
    private final List<Vote> store = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void save(Vote vote) {
        store.add(vote);
    }

    @Override
    public List<Vote> findAll() {
        return new ArrayList<>(store);
    }

    @Override
    public void clear() {
        store.clear();
    }
}
