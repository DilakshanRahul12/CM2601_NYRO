package org.example.Nyro;

import java.time.LocalDateTime;

public class UserPreference {
    int id;
    int favourites;
    int read;
    int dislike;
    LocalDateTime logtime;

    public UserPreference(int id) {
        this.id = id;
    }

    public int getFav() {
        return favourites;
    }

    public void setLiked(int favourites) {
        this.favourites = favourites;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }
}
