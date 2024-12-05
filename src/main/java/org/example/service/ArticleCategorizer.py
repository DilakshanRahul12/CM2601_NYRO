from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity

app = Flask(__name__)

# Load the model once and keep it in memory
embedding_model = SentenceTransformer("paraphrase-MiniLM-L3-v2")

# Predefined categories and their embeddings
categories = ["Politics", "Business", "Technology", "Health", "Sports",
              "Entertainment", "Science", "E-Sports", "Lifestyle", "Environment"]
category_embeddings = embedding_model.encode(categories)

@app.route('/categorize', methods=['POST'])
def categorize():
    """
    Categorizes articles based on the provided description and caption.
    Expects JSON input: {"description": "some text", "caption": "some text"}
    """
    data = request.json
    description = data.get("description", "")
    caption = data.get("caption", "")
    combined_text = description + " " + caption

    # Generate embedding for the combined text
    text_embedding = embedding_model.encode([combined_text])[0]

    # Compute similarity
    similarities = cosine_similarity([text_embedding], category_embeddings)[0]
    best_category = categories[similarities.argmax()]

    return jsonify({"category": best_category})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
