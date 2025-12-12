package org.example.vote;

import org.example.vote.repo.InMemoryVoteRepository;
import org.example.vote.service.VoteService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void fullFlowRegistersListenersAndCountsVotes() {
        String commands = String.join(System.lineSeparator(),
                "add c1 Alice",
                "vote v1 c1",
                "count plurality",
                "exit",
                "");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out)); // capture listener logs that go to System.out

        VoteService service = new VoteService(new InMemoryVoteRepository());
        App app = new App(service);
        app.run(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)), new PrintStream(out, true));

        String output = out.toString();
        assertTrue(output.contains("Candidate Alice (c1) registered."), "Add command should register candidate");
        assertTrue(output.contains("Vote recorded for candidate c1"), "Vote command should record vote");
        assertTrue(output.contains("Strategy: plurality"), "Count should use requested strategy");
        assertTrue(output.contains("[LOG] Vote received"), "Observer should log when vote is cast");
        assertTrue(output.contains("Winner:"), "Count should print winner line");
    }

    @Test
    void unknownStrategyFallsBackToPlurality() {
        String commands = String.join(System.lineSeparator(),
                "add c1 Alice",
                "vote v1 c1",
                "count ranked",
                "exit",
                "");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        VoteService service = new VoteService(new InMemoryVoteRepository());
        App app = new App(service);
        app.run(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)), new PrintStream(out, true));

        String output = out.toString();
        assertTrue(output.contains("Unknown strategy 'ranked'"), "App should warn and fallback on unknown strategy");
        assertTrue(output.contains("Strategy: plurality"), "Fallback strategy should be plurality");
    }
}
