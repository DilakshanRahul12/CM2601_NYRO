package org.example.Nyro;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPreference {
    private final User user; // Association with User
    private Map<Integer, LocalDateTime> favourites = new HashMap<>();
    private Map<Integer, LocalDateTime> read = new HashMap<>();
    private Map<Integer, LocalDateTime> dislike = new HashMap<>();

    public UserPreference(User user) {
        this.user = user; // Associate with the user
    }

    public void addToFavourites(int articleId, LocalDateTime time) {
        favourites.put(articleId, time);
    }

    public void addToRead(int articleId, LocalDateTime time) {
        read.put(articleId, time);
    }

    public void addToDislike(int articleId, LocalDateTime time) {
        dislike.put(articleId, time);
    }

    public Map<Integer, LocalDateTime> getFavourites() {
        return favourites;
    }

    public Map<Integer, LocalDateTime> getRead() {
        return read;
    }

    public Map<Integer, LocalDateTime> getDislike() {
        return dislike;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "UserPreference{" +
                "user=" + user.getEmail() +
                ", favourites=" + favourites +
                ", read=" + read +
                ", dislike=" + dislike +
                '}';
    }
}
