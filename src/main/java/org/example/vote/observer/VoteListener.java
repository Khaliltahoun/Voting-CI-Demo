package org.example.vote.observer;

import org.example.vote.model.Vote;

public interface VoteListener {
    void onVote(Vote vote);
}
