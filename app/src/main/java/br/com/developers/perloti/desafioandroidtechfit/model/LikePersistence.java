package br.com.developers.perloti.desafioandroidtechfit.model;

import io.realm.Realm;

/**
 * Created by perloti on 10/05/18.
 */

public class LikePersistence {

    public static void saveInCache(Like like) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(like);
        realm.commitTransaction();
        realm.close();
    }

    public static void removeFromCache(String hash) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Like feedHash = realm.where(Like.class).equalTo("feedHash", hash).findFirst();
        feedHash.deleteFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    public static Like getLikeByHash(String s) {
        Realm realm = Realm.getDefaultInstance();

        return realm.where(Like.class).equalTo("feedHash", s).findFirst();

    }

}
