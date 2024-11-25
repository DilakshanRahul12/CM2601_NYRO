package org.example.Nyro;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;

import java.io.File;
import java.util.*;

public class NLPdemo {

    public static void main(String[] args) {
        // Example input text
        String text = """
                Disney reached its 100th anniversary in October 2023, marking a magical century of captivating the hearts of children and adults alike.
                To honor the milestone, Disney released the short film Once Upon a Studio, uniting several of its most beloved characters on screen for the first time, including Mickey Mouse, Moana, and Sleeping Beauty. But have you ever wondered which Disney films made the most at the box office?\s
                In celebration of the studio's centennial, we've compiled a list of its most successful movies of all time. The total gross of each film has been adjusted for inflation to give you an idea of what each flick made in today's money.\s
                From recent releases to retro cartoons, read on to discover which Disney movies have made the most money. Some entries are bound to surprise you...   \s
                """;

        // Call the function
        List<String> keywords = extractKeywords(text, "src/main/resources/stopwords.txt");

        // Print the extracted keywords
        System.out.println("Extracted Keywords: " + String.join(", ", keywords));
    }

    /**
     * Extracts keywords from the given text, excluding stopwords.
     *
     * @param text       The text to analyze.
     * @param stopwordsPath Path to the stopwords file.
     * @return A list of extracted keywords.
     */
    public static List<String> extractKeywords(String text, String stopwordsPath) {
        // Initialize StanfordCoreNLP pipeline
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Initialize sets for keywords and stop words
        Set<String> keywords = new HashSet<>();
        Set<String> stopWords = new HashSet<>();

        // Load stopwords from file
        try (Scanner scanner = new Scanner(new File(stopwordsPath))) {
            while (scanner.hasNextLine()) {
                stopWords.add(scanner.nextLine().trim().toLowerCase());
            }
        } catch (Exception e) {
            System.err.println("Error loading stopwords: " + e.getMessage());
            return Collections.emptyList();
        }

        try {
            // Create a document object
            CoreDocument document = new CoreDocument(text);
            pipeline.annotate(document);

            // Extract keywords
            for (CoreLabel token : document.tokens()) {
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                // Include only relevant parts of speech and filter out stopwords
                if ((pos.startsWith("NN") || pos.startsWith("JJ") || pos.startsWith("VB"))
                        && !stopWords.contains(lemma.toLowerCase())) {
                    keywords.add(lemma.toLowerCase());
                }
            }
        } catch (Exception e) {
            System.err.println("Error during NLP processing: " + e.getMessage());
            return Collections.emptyList();
        }

        // Return sorted keywords as a list
        List<String> sortedKeywords = new ArrayList<>(keywords);
        Collections.sort(sortedKeywords);
        return sortedKeywords;
    }
}
