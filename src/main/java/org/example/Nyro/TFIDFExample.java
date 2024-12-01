package org.example.Nyro;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

public class TFIDFExample {
    public static void main(String[] args) throws IOException {
        // Path to your stopwords file
        String stopwordsPath = "src/main/resources/stopwords.txt";

        // Sample documents (corpus)
        String[] documents = {
                "Disney reached its 100th anniversary in October 2023, marking a magical century of captivating hearts.",
                "disney released the short film Once Upon a Studio.",
                "Discover Dreamwork's most successful movies of all time."
        };

        // Load stopwords from file
        Set<String> stopwords = loadStopwords(stopwordsPath);

        // Tokenize and calculate TF-IDF
        Map<String, Double> tfidfScores = calculateTFIDF(documents, stopwords);

        // Display top keywords
        System.out.println("Top Keywords:");
        tfidfScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(20)
                .forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));
    }

    public static Set<String> loadStopwords(String filePath) {
        Set<String> stopwords = new HashSet<>();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                stopwords.add(scanner.nextLine().trim().toLowerCase());
            }
        } catch (IOException e) {
            System.err.println("Error loading stopwords: " + e.getMessage());
        }
        return stopwords;
    }

    public static Map<String, Double> calculateTFIDF(String[] documents, Set<String> stopwords) throws IOException {
        Map<String, Double> idfScores = calculateIDF(documents, stopwords);
        Map<String, Double> tfidfScores = new HashMap<>();

        // Calculate TF-IDF for each term in the first document
        String firstDocument = documents[2];
        Map<String, Double> tfScores = calculateTF(firstDocument, stopwords);

        for (String term : tfScores.keySet()) {
            double tfidf = tfScores.get(term) * idfScores.getOrDefault(term, 0.0);
            tfidfScores.put(term, tfidf);
        }

        return tfidfScores;
    }

    public static Map<String, Double> calculateTF(String document, Set<String> stopwords) throws IOException {
        Map<String, Double> termFrequency = new HashMap<>();
        int totalTerms = 0;

        // Tokenize document using Lucene's StandardAnalyzer
        StandardAnalyzer analyzer = new StandardAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(document));
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            String term = charTermAttribute.toString();
            if (!stopwords.contains(term.toLowerCase())) {
                termFrequency.put(term, termFrequency.getOrDefault(term, 0.0) + 1);
                totalTerms++;
            }
        }
        tokenStream.end();
        tokenStream.close();

        // Normalize term frequency (TF)
        for (Map.Entry<String, Double> entry : termFrequency.entrySet()) {
            termFrequency.put(entry.getKey(), entry.getValue() / totalTerms);
        }

        return termFrequency;
    }

    public static Map<String, Double> calculateIDF(String[] documents, Set<String> stopwords) throws IOException {
        Map<String, Integer> documentFrequency = new HashMap<>();
        int totalDocuments = documents.length;

        // Count document frequency for each term
        StandardAnalyzer analyzer = new StandardAnalyzer();
        for (String document : documents) {
            Set<String> uniqueTerms = new HashSet<>();
            TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(document));
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String term = charTermAttribute.toString();
                if (!stopwords.contains(term.toLowerCase())) {
                    uniqueTerms.add(term);
                }
            }
            tokenStream.end();
            tokenStream.close();

            for (String term : uniqueTerms) {
                documentFrequency.put(term, documentFrequency.getOrDefault(term, 0) + 1);
            }
        }

        // Calculate Inverse Document Frequency (IDF)
        Map<String, Double> idfScores = new HashMap<>();
        for (Map.Entry<String, Integer> entry : documentFrequency.entrySet()) {
            double idf = Math.log((double) totalDocuments / (1 + entry.getValue()));
            idfScores.put(entry.getKey(), idf);
        }

        return idfScores;
    }
}
