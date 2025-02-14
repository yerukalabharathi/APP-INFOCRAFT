import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class PrecisModule {
    private final StanfordCoreNLP pipeline;

    public PrecisModule() {
        // Initialize the Stanford NLP pipeline with the required annotators
        Properties props = PropertiesUtils.asProperties("annotators", "tokenize,ssplit,pos,parse,lemma,natlog,openie");
        this.pipeline = new StanfordCoreNLP(props);
    }

    public String generateSummary(String inputText, int numSentences) {
        Annotation document = new Annotation(inputText);
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        if (sentences.size() < numSentences) {
            return "Cannot summarize; the document is too short.";
        }

        // Calculate the importance score for each sentence based on NLP analysis
        List<SentenceScore> sentenceScores = calculateSentenceImportance(sentences);

        // Sort sentences by importance and select the top 'numSentences' sentences
        sentenceScores.sort(Comparator.comparing(SentenceScore::getScore).reversed());
        List<CoreMap> selectedSentences = sentenceScores.stream()
                .limit(numSentences)
                .map(SentenceScore::getSentence)
                .collect(Collectors.toList());

        // Generate the summary by combining selected sentences
        StringBuilder summary = new StringBuilder();
        for (CoreMap sentence : selectedSentences) {
            summary.append(sentence.toString()).append(" ");
        }

        return summary.toString(); // Return the summary as a string
    }

    private List<SentenceScore> calculateSentenceImportance(List<CoreMap> sentences) {
        return sentences.stream()
                .map(sentence -> {
                    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
                    int nounCount = 0;
                    int verbCount = 0;

                    for (CoreLabel token : tokens) {
                        String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                        if (pos.startsWith("NN") || pos.startsWith("NNS")) {
                            // Consider nouns and plural nouns as important
                            nounCount++;
                        } else if (pos.startsWith("VB")) {
                            // Consider verbs as important
                            verbCount++;
                        }
                    }

                    // You can define your own scoring formula based on these counts
                    // For example, you can assign different weights to nouns and verbs
                    // and create a weighted score.

                    // For this example, we'll use a simple scoring formula

                    double score = 0.5 * nounCount + 0.5 * verbCount;
                    return new SentenceScore(sentence, score);
                })
                .collect(Collectors.toList());
    }

    private static class SentenceScore {
        private final CoreMap sentence;
        private final double score;

        public SentenceScore(CoreMap sentence, double score) {
            this.sentence = sentence;
            this.score = score;
        }

        public CoreMap getSentence() {
            return sentence;
        }

        public double getScore() {
            return score;
        }
    }
}
