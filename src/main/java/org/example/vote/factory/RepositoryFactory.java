package org.example.vote.factory;

import org.example.vote.repo.InMemoryVoteRepository;
import org.example.vote.repo.VoteRepository;

public final class RepositoryFactory {
    private RepositoryFactory() {
    }

    public static VoteRepository createRepo(String type) {
        if ("memory".equalsIgnoreCase(type) || type == null || type.isBlank()) {
            return new InMemoryVoteRepository();
        }
        throw new IllegalArgumentException("Unknown repo type " + type);
    }
}
