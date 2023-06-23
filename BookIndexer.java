import java.io.*;
import java.util.*;

class BookIndexGenerator {
    private Set<String> excludeWords;
    private Map<String, Set<Integer>> wordIndex;

    public BookIndexGenerator() {
        excludeWords = new HashSet<>();
        wordIndex = new TreeMap<>();
    }

    public void readExcludeWords(String excludeWordsFilePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(excludeWordsFilePath));
        String line;
        while ((line = reader.readLine()) != null) {
            excludeWords.add(line.trim().toLowerCase());
        }
        reader.close();
    }

    public void generateIndex(String[] pageFilePaths) throws IOException {
        for (int i = 0; i < pageFilePaths.length; i++) {
            BufferedReader reader = new BufferedReader(new FileReader(pageFilePaths[i]));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    String cleanWord = word.trim().toLowerCase();
                    if (!cleanWord.matches(".*\\d.*") && !excludeWords.contains(cleanWord)) {
                        Set<Integer> pages = wordIndex.getOrDefault(cleanWord, new HashSet<>());
                        pages.add(i + 1);
                        wordIndex.put(cleanWord, pages);
                    }
                }
            }
            reader.close();
        }
    }

    public void writeIndex(String indexPath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(indexPath));
        for (Map.Entry<String, Set<Integer>> entry : wordIndex.entrySet()) {
            writer.write(entry.getKey() + " : " + formatPages(entry.getValue()));
            writer.newLine();
        }
        writer.close();
    }

    private String formatPages(Set<Integer> pages) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int page : pages) {
            if (!first) {
                sb.append(",");
            }
            sb.append(page);
            first = false;
        }
        return sb.toString();
    }
}

public class BookIndexer {
    public static void main(String[] args) {
        String[] pageFilePaths = { "Page1.txt", "Page2.txt", "Page3.txt" };
        String excludeWordsFilePath = "exclude-words.txt";
        String indexPath = "index.txt";

        try {
            BookIndexGenerator indexer = new BookIndexGenerator();
            indexer.readExcludeWords(excludeWordsFilePath);
            indexer.generateIndex(pageFilePaths);
            indexer.writeIndex(indexPath);
            System.out.println("Index generated successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
