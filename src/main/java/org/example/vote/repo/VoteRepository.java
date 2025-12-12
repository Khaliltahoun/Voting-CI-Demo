package org.example.vote.repo;

import org.example.vote.model.Vote;

import java.util.List;

public interface VoteRepository {
    void save(Vote vote);

    List<Vote> findAll();

    void clear();
}
