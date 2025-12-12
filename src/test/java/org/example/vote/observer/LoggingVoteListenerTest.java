package org.example.vote.observer;

import org.example.vote.model.Vote;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggingVoteListenerTest {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(buffer));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void printsMessageWhenVoteReceived() {
        LoggingVoteListener listener = new LoggingVoteListener();
        listener.onVote(new Vote("v1", "c1", 123L));

        assertTrue(buffer.toString().contains("[LOG] Vote received"), "Listener should log incoming vote");
    }
}
