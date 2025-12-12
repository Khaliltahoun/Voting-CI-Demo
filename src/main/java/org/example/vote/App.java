package org.example.vote;

import org.example.vote.factory.RepositoryFactory;
import org.example.vote.model.Candidate;
import org.example.vote.observer.AuditTrailVoteListener;
import org.example.vote.observer.LoggingVoteListener;
import org.example.vote.service.CountingResult;
import org.example.vote.service.VoteService;
import org.example.vote.strategy.CountingStrategy;
import org.example.vote.strategy.MajorityCountingStrategy;
import org.example.vote.strategy.PluralityCountingStrategy;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class App {
    private final VoteService voteService;
    private final Map<String, CountingStrategy> strategies = new HashMap<>();

    public App(VoteService voteService) {
        this.voteService = voteService;
        // Register default listeners so notifications are emitted at runtime.
        this.voteService.addListener(new LoggingVoteListener());
        this.voteService.addListener(new AuditTrailVoteListener());
        strategies.put("plurality", new PluralityCountingStrategy());
        strategies.put("majority", new MajorityCountingStrategy());
    }

    public static void main(String[] args) {
        String repoType = args.length > 0 ? args[0] : "memory";
        VoteService voteService = new VoteService(RepositoryFactory.createRepo(repoType));
        new App(voteService).run();
    }

    public void run() {
        run(System.in, System.out);
    }

    public void run(InputStream in, PrintStream out) {
        try (Scanner scanner = new Scanner(in)) {
            while (true) {
                out.print("> ");
                String line = scanner.nextLine();
                if (line == null) {
                    break;
                }
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 0 || parts[0].isBlank()) {
                    continue;
                }
                String cmd = parts[0].toLowerCase();
                switch (cmd) {
                    case "help" -> printHelp(out);
                    case "list" -> listCandidates(out);
                    case "add" -> addCandidate(parts, out);
                    case "vote" -> castVote(parts, out);
                    case "count" -> count(parts, out);
                    case "reset" -> reset(out);
                    case "exit", "quit" -> {
                        out.println("Bye!");
                        return;
                    }
                    default -> out.println("Unknown command. Type 'help' to see available commands.");
                }
            }
        }
    }

    private void printHelp(PrintStream out) {
        out.println("help                         - show commands");
        out.println("list                         - list registered candidates");
        out.println("add <id> <name>              - register a new candidate");
        out.println("vote <voterId> <candidateId> - cast a vote");
        out.println("count [strategy]             - count votes using strategy (plurality, majority)");
        out.println("reset                        - clear votes");
        out.println("exit                         - quit application");
    }

    private void listCandidates(PrintStream out) {
        out.println("Candidates:");
        var candidates = voteService.listCandidates();
        if (candidates.isEmpty()) {
            out.println("(no candidates yet)");
            return;
        }
        candidates.forEach(c -> out.printf("- %s (%s)%n", c.name(), c.id()));
    }

    private void addCandidate(String[] parts, PrintStream out) {
        if (parts.length < 3) {
            out.println("Usage: add <id> <name>");
            return;
        }
        String id = parts[1];
        String name = String.join(" ", java.util.Arrays.copyOfRange(parts, 2, parts.length));
        voteService.registerCandidate(new Candidate(id, name));
        out.printf("Candidate %s (%s) registered.%n", name, id);
    }

    private void castVote(String[] parts, PrintStream out) {
        if (parts.length < 3) {
            out.println("Usage: vote <voterId> <candidateId>");
            return;
        }
        try {
            voteService.castVote(parts[1], parts[2]);
            out.printf("Vote recorded for candidate %s by voter %s.%n", parts[2], parts[1]);
        } catch (IllegalArgumentException ex) {
            out.println(ex.getMessage());
        }
    }

    private void count(String[] parts, PrintStream out) {
        CountingStrategy strategy = resolveStrategy(parts.length >= 2 ? parts[1] : "plurality");
        CountingResult result = voteService.countVotes(strategy);
        out.printf("Strategy: %s | Total votes: %d%n", strategy.name(), result.totalVotes());
        voteService.listCandidates().forEach(candidate -> {
            Integer votes = result.tallies().get(candidate.id());
            if (votes != null) {
                out.printf("- %s : %d%n", candidate.id(), votes);
            }
        });
        Optional<String> winner = result.winner();
        out.println("Winner: " + winner.orElse("no majority winner"));
    }

    private CountingStrategy resolveStrategy(String name) {
        CountingStrategy strategy = strategies.get(name.toLowerCase());
        if (strategy == null) {
            System.out.println("Unknown strategy '" + name + "', fallback to plurality.");
            strategy = strategies.get("plurality");
        }
        return strategy;
    }

    private void reset(PrintStream out) {
        voteService.reset();
        out.println("Votes cleared.");
    }
}
