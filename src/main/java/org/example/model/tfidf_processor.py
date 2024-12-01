import sys
import subprocess
import re

# Function to check and install libraries
def install_and_import(package):
    try:
        __import__(package)
    except ImportError:
        print(f"{package} not found. Installing...")
        subprocess.check_call([sys.executable, "-m", "pip", "install", package])

# Ensure required libraries are installed
required_packages = ["scikit-learn", "spacy"]
for package in required_packages:
    install_and_import(package)

# Import libraries after ensuring installation
from sklearn.feature_extraction.text import TfidfVectorizer
import spacy
from spacy.lang.en.stop_words import STOP_WORDS

# Ensure spaCy model is loaded
def load_spacy_model():
    try:
        return spacy.load("en_core_web_sm")
    except OSError:
        print("Downloading spaCy model 'en_core_web_sm'...")
        subprocess.check_call([sys.executable, "-m", "spacy", "download", "en_core_web_sm"])
        return spacy.load("en_core_web_sm")

nlp = load_spacy_model()

def clean_text(text):
    """
    Cleans text by:
    - Removing invalid Unicode characters
    - Converting to lowercase
    - Removing special characters (excluding spaces)
    - Stripping extra whitespace
    """
    text = text.encode("utf-8", "ignore").decode("utf-8")  # Remove invalid Unicode characters
    text = text.lower()  # Convert to lowercase
    text = re.sub(r"[^\w\s]", "", text)  # Remove special characters (keep letters, numbers, spaces)
    text = re.sub(r"\s+", " ", text).strip()  # Remove extra whitespace
    return text


def extract_tokens_to_exclude(document):
    """
    Extracts verbs and from the document using spaCy.
    """
    doc = nlp(clean_text(document))
    excluded_tokens = set()

    for token in doc:
        if token.pos_ == "VERB":
            excluded_tokens.add(token.text.lower())

    return excluded_tokens

def compute_tfidf(documents, target_index):
    """
    Computes TF-IDF for a target document, excluding verbs, numbers, and spaCy stopwords.
    """
    if not documents:
        raise ValueError("Documents list is empty.")

    # Extract stopwords and dynamic tokens to exclude
    stopwords = set(STOP_WORDS)
    stopwords.update(extract_tokens_to_exclude(documents[target_index]))
    numeric_tokens = {str(i) for i in range(10000)}  # Add a wide range of numbers
    stopwords.update(numeric_tokens)

    # Remove purely numeric tokens from all documents
    numeric_tokens = {
        token for doc in documents for token in clean_text(doc).split() if token.isdigit()
    }
    stopwords.update(numeric_tokens)

    # Compute TF-IDF
    vectorizer = TfidfVectorizer(stop_words=list(stopwords))
    tfidf_matrix = vectorizer.fit_transform(documents)
    feature_names = vectorizer.get_feature_names_out()

    # Extract TF-IDF scores for the target document
    target_vector = tfidf_matrix[target_index].toarray().flatten()
    keywords = {feature_names[i]: target_vector[i] for i in range(len(target_vector)) if target_vector[i] > 0}

    # Return keywords sorted by score in descending order
    return dict(sorted(keywords.items(), key=lambda item: item[1], reverse=True))

def main():
    input_data = sys.stdin.read().strip()
    input_lines = input_data.split("\n")

    if len(input_lines) < 2:
        print("Error: Insufficient input. Provide documents and target index.")
        sys.exit(1)

    documents = input_lines[0].split(",")
    try:
        target_index = int(input_lines[1])
        if not 0 <= target_index < len(documents):
            raise IndexError
    except (ValueError, IndexError):
        print("Error: Invalid target index.")
        sys.exit(1)

    try:
        keywords = compute_tfidf(documents, target_index)
        for keyword, score in keywords.items():
            print(f"{keyword}: {score}")
    except ValueError as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    main()
