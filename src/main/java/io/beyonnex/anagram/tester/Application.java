package io.beyonnex.anagram.tester;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Application {
    private static final Map<String, Set<String>> ANAGRAMS = new HashMap<>();

    public static void main(String[] args) throws IOException {
        LineReader reader = LineReaderBuilder.builder()
                .terminal(TerminalBuilder.terminal())
                .build();

        while (true) {
            String line = reader.readLine("angrtst> ");
            if (line == null || line.equalsIgnoreCase("exit")) {
                break;
            }
            if (line.equalsIgnoreCase("--help")
                    || line.equalsIgnoreCase("-h")) {
                help();
            } else if (line.equalsIgnoreCase("test")) {
                test(reader);
            } else if (line.toLowerCase().startsWith("print")) {
                List<String> words = reader.getParsedLine().words();
                if (words.size() == 2) {
                    print(words.get(1));
                } else {
                    System.out.println("Incorrect number of arguments");
                    help();
                }
            } else {
                System.out.println("Unknown command");
                help();
            }
        }
    }

    private static void test(LineReader lineReader) {
        String leftText = lineReader.readLine("Please enter the first text\n> ");
        String rightText = lineReader.readLine("Please enter the second text\n> ");
        Map<Character, Integer> leftTextParsed = parseText(leftText);
        char[] rightTextParsed = rightText.toLowerCase().toCharArray();
        for (char rightTextChar : rightTextParsed) {
            if (!Character.isAlphabetic(rightTextChar)) {
                continue;
            }
            rightTextChar = Character.toLowerCase(rightTextChar);
            if (!leftTextParsed.containsKey(rightTextChar)) {
                System.out.println("False");
                return;
            }
            @SuppressWarnings("DataFlowIssue")
            Integer characterCount = leftTextParsed.compute(rightTextChar, (key, value) -> --value);
            if (characterCount == 0) {
                leftTextParsed.remove(rightTextChar);
            }
        }
        System.out.println("True");
        ANAGRAMS.computeIfAbsent(leftText, text -> new HashSet<>()).add(rightText);
        ANAGRAMS.computeIfAbsent(rightText, text -> new HashSet<>()).add(leftText);
        insertAnagrams(leftText);
        insertAnagrams(rightText);
    }

    private static void print(String text) {
        Set<String> anagrams = ANAGRAMS.get(text);
        if (anagrams == null) {
            return;
        }
        anagrams.forEach(System.out::println);
    }

    private static void insertAnagrams(String leftText) {
        Set<String> visited = new LinkedHashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(leftText);
        while (!queue.isEmpty()) {
            String text = queue.poll();
            for (String anagram : ANAGRAMS.get(text)) {
                if (!visited.contains(anagram)) {
                    visited.add(anagram);
                    queue.add(anagram);
                }
            }
        }
        ANAGRAMS.get(leftText).addAll(visited);
        visited.forEach(text -> ANAGRAMS.get(text).add(leftText));
    }

    private static Map<Character, Integer> parseText(String text) {
        char[] chars = text.toCharArray();
        Map<Character, Integer> result = new HashMap<>();
        for (char aChar : chars) {
            if (!Character.isAlphabetic(aChar)) {
                continue;
            }
            aChar = Character.toLowerCase(aChar);
            result.compute(aChar, (character, quantity) -> quantity != null ? ++quantity : 1);
        }
        return result;
    }

    private static void help() {
        String helpText = """
                test          Prompt to enter two texts, check if the texts are anagrams. Print True if so, False otherwise.
                print <text>  Print all known anagrams to <text> if there are any. Print nothing otherwise.
                exit          Exit the program.
                -h, --help    Show this help message.
                """;
        System.out.print(helpText);
    }
}