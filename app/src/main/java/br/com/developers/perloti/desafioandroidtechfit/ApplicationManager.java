package br.com.developers.perloti.desafioandroidtechfit;

import android.app.Application;

import br.com.developers.perloti.desafioandroidtechfit.util.ApplicationUtil;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

/**
 * Created by perloti on 08/05/18.
 */

public class ApplicationManager extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        ApplicationUtil.setContext(getApplicationContext());

        Realm.init(getApplicationContext());


    }
}
