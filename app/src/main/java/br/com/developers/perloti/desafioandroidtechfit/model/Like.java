package br.com.developers.perloti.desafioandroidtechfit.model;

import io.realm.RealmObject;

/**
 * Created by perloti on 10/05/18.
 */

public class Like extends RealmObject {

    private String feedHash;

    @Override
    public String toString() {
        return "Like{" +
                "feedHash='" + feedHash + '\'' +
                '}';
    }

    public Like() {
    }

    public Like(String feedHash) {
        this.feedHash = feedHash;
    }

    public String getFeedHash() {
        return feedHash;
    }

    public void setFeedHash(String feedHash) {
        this.feedHash = feedHash;
    }


}
