import subprocess
import sys

def install_and_import(package_name, import_name=None):
    """
    Installs a package if not already installed and then imports it.
    :param package_name: The name of the package to install.
    :param import_name: The name of the module to import (if different from package_name).
    """
    import_name = import_name or package_name
    try:
        __import__(import_name)
    except ImportError:
        print(f"{import_name} not found. Installing...")
        subprocess.check_call([sys.executable, "-m", "pip", "install", package_name])
    finally:
        globals()[import_name] = __import__(import_name)

# Install and import required packages
install_and_import("scikit-learn", "sklearn")
install_and_import("sentence-transformers", "sentence_transformers")

from sklearn.metrics.pairwise import cosine_similarity
from sentence_transformers import SentenceTransformer

# Pretrained model for embeddings
embedding_model = SentenceTransformer("all-MiniLM-L6-v2")

# Predefined categories
categories = ["Politics", "Business", "Technology", "Health", "Sports",
              "Entertainment", "Science", "E-Sports", "Lifestyle", "Environment"]

def categorize_description(description, caption):
    """
    Maps the provided description and caption to the most relevant predefined category.
    """
    # Combine description and caption into a single text
    combined_text = description + " " + caption

    # Generate embeddings for the combined text and category names
    text_embedding = embedding_model.encode([combined_text])[0]
    category_embeddings = embedding_model.encode(categories)

    # Compute similarity between the text and each category
    similarities = cosine_similarity([text_embedding], category_embeddings)[0]

    # Find the category with the highest similarity
    best_category_index = similarities.argmax()
    best_category = categories[best_category_index]

    return best_category


def main():
    if len(sys.argv) < 3:
        print("Usage: python3 categorizer.py <description> <caption>")
        sys.exit(1)

    description = sys.argv[1]
    caption = sys.argv[2]

    # Call the categorization function
    category = categorize_description(description, caption)
    print(category)  # Print the result for Java to capture

if __name__ == "__main__":
    main()
