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
