package org.example.vote.observer;

import org.example.vote.model.Vote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple listener used for auditing and tests; stores received votes.
 */
public class AuditTrailVoteListener implements VoteListener {
    private final List<Vote> received = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void onVote(Vote vote) {
        received.add(vote);
    }

    public List<Vote> receivedVotes() {
        return new ArrayList<>(received);
    }
}
