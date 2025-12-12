package org.example.vote.factory;

import org.example.vote.repo.VoteRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryFactoryTest {

    @Test
    void createsInMemoryRepositoryWhenRequestedOrBlank() {
        VoteRepository repoExplicit = RepositoryFactory.createRepo("memory");
        VoteRepository repoDefault = RepositoryFactory.createRepo("   ");

        assertNotNull(repoExplicit);
        assertNotNull(repoDefault);
        assertNotSame(repoExplicit, repoDefault, "Factory should create new instances");
    }

    @Test
    void throwsOnUnknownRepositoryType() {
        assertThrows(IllegalArgumentException.class, () -> RepositoryFactory.createRepo("file"));
    }
}
