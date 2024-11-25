//import ai.djl.Application;
//import ai.djl.Model;
//import ai.djl.ModelException;
//import ai.djl.inference.Predictor;
//import ai.djl.modality.Classifications;
//import ai.djl.modality.nlp.translator.TextClassificationTranslator;
//import ai.djl.translate.TranslateException;
//
//import java.io.IOException;
//
//public class HuggingFaceTextClassifier {
//
//    public static void main(String[] args) {
//        try {
//            // Load a Hugging Face model from DJL's Model Zoo
//            String modelUrl = "https://huggingface.co/distilbert-base-uncased-finetuned-sst-2-english";
//
//            // Load the model
//            Model model = Model.newInstance(Application.NLP.TEXT_CLASSIFICATION, modelUrl);
//
//            // Create a translator for text classification
//            TextClassificationTranslator translator = TextClassificationTranslator.builder()
//                    .optApplySoftmax(true) // Convert logits to probabilities
//                    .build();
//
//            // Create a predictor
//            try (Predictor<String, Classifications> predictor = model.newPredictor(translator)) {
//                // Input text to classify
//                String text = "Artificial intelligence is transforming industries.";
//
//                // Perform classification
//                Classifications result = predictor.predict(text);
//
//                // Output results
//                System.out.println("Classification Results:");
//                result.items().forEach(item ->
//                        System.out.printf("Category: %s, Probability: %.3f%n", item.getClassName(), item.getProbability()));
//            }
//        } catch (IOException | ModelException | TranslateException e) {
//            e.printStackTrace();
//        }
//    }
//}
