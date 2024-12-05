# **Nyro: A Personalized News Recommendation System**

Nyro is a sophisticated platform designed to provide users with a tailored and seamless news-reading experience. It leverages advanced machine learning techniques to categorize and recommend news articles dynamically, ensuring relevance and personalization for every user.

---

## **Key Features**

### **Guest Access**
- Unrestricted access to browse and read news articles without requiring registration or login.

### **Personalized Feed for Registered Users**
- **Tailored Recommendations:** A dynamic feed based on user preferences and behaviors.
- **Interactive Features:** Users can mark articles as:
  - **Favorite**: Adds to personal interest weight.
  - **Read**: Records content engagement.
  - **Dislike**: Reduces content relevance.

### **User Functionality**
- Access to a **read history** and lists of **favorited** or **disliked articles**.
- Update account details, including password changes.

### **Administrative Oversight**
- **Elevated privileges** for administrators to monitor users.
- Ability to **delete user accounts** to maintain platform integrity.

---

## **Personalization Mechanism**

Nyro employs a **category scoring formula** to tailor recommendations. Each interaction type contributes to the scoring system with specific weights:

- **Favorite (Wf):** +3  
- **Read (Wr):** +2  
- **Dislike (Wd):** -1  

The score for each news category \(C\) is calculated as:

$$\
\text{Score}(C) = \frac{W_f \times F_c + W_r \times R_c + W_d \times D_c}{\text{Total Interactions}}
\$$

Where:
- $\(F_c\)$: Number of articles marked as favorite in category $\(C\)$.  
- $\(R_c\)$: Number of articles read in category $\(C\)$.  
- $\(D_c\)$: Number of articles disliked in category $\(C\)$.  

This scoring ensures continuous refinement of recommendations, adapting dynamically to user preferences.

---

## **Technologies and Dependencies**

### **Technologies Used**
- **News API**: For fetching articles.  
- **Machine Learning Model**: [paraphrase-MiniLM-L3-v2](https://huggingface.co/sentence-transformers/paraphrase-MiniLM-L3-v2) for fine-grained categorization.  
- **JavaFX**: For building the user interface.  
- **PostgreSQL**: As the database system.  
- **Flask API**: For backend API development.

### **Dependencies**
- **PostgreSQL Driver**: Version 42.7.2  
- **Apache Commons Math**: Version 3.6.1  
- **Jackson Databind**: Version 2.15.2  
- **JSON Library**: Version 20240303  
- **Google Gson**: Version 2.10.1
- 

## **Prerequisites**

- **JDBC Driver**: Ensure you have the JDBC driver installed for connecting to PostgreSQL.  
- **PGAdmin/PostgreSQL**: Set up PostgreSQL and PGAdmin for managing the database.

### **Setup Steps**
1. Open the `ArticleCategorizer.py` (Flask) and run the script:  
   ```bash
   python ArticleCategorizer.py 
    ```
---

#### _Run the DatabaseCreate main method in the Java project to initialize the database._

### Finally, run the NewsApp to launch the application.
