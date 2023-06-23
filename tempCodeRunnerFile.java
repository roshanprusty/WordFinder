import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class BookIndexer {

    private static final String EXCLUDE_WORDS_FILE = "exclude-words.txt";
    private static final String INDEX_FILE = "index.txt";

    public static void main(String[] args) throws IOException {
        List<String> excludeWords = readExcludeWordsFile();
        Map<String, List<Integer>> wordIndex = new HashMap<>();
        for (String fileName : Arrays.asList("Page1.txt", "Page2.txt", "Page3.txt")) {
            indexFile(fileName, excludeWords, wordIndex);
        }
        writeIndex(wordIndex, INDEX_FILE);
    }

    private static List<String> readExcludeWordsFile() throws IOException {
        List<String> excludeWords = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(EXCLUDE_WORDS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                excludeWords.add(line);
            }
        }
        return excludeWords;
    }

    private static void indexFile(String fileName, List<String> excludeWords, Map<String, List<Integer>> wordIndex) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                for (String word : line.split(" ")) {
                    word = word.toLowerCase();
                    if (!excludeWords.contains(word)) {
                        List<Integer> pageNumbers = wordIndex.get(word);
                        if (pageNumbers == null) {
                            pageNumbers = new ArrayList<>();
                        }
                        pageNumbers.add(lineNum);
                        wordIndex.put(word, pageNumbers);
                    }
                }
                lineNum++;
            }
        }
    }

    private static void writeIndex(Map<String, List<Integer>> wordIndex, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String word : wordIndex.keySet()) {
                List<Integer> pageNumbers = wordIndex.get(word);
                String pageNumbersStr = pageNumbers.stream()
                    .map(Object::toString)
                    .collect(Collectors.mapping(Object::toString, Collectors.joining(",")));
                writer.write(word + ":" + pageNumbersStr + "\n");
            }
        }
    }
}
