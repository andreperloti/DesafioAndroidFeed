package br.com.developers.perloti.desafioandroidtechfit.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import br.com.developers.perloti.desafioandroidtechfit.BuildConfig;
import br.com.developers.perloti.desafioandroidtechfit.R;

/**
 * Created by perloti on 11/05/18.
 */

public class AdsRemoteManager {

    private final AdView mAdView;
    private FirebaseRemoteConfig instance;
    private Context context;

    public AdsRemoteManager(Context context) {
        this.context = context;
        MobileAds.initialize(context, ApplicationUtil.getContext().getString(R.string.YOUR_ADMOB_APP_ID));
        mAdView = (AdView) ((Activity)context).findViewById(R.id.adView);
        setupAdsByRemoteConfig();
    }

    private void setupAdsByRemoteConfig() {
        instance = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        instance.setConfigSettings(configSettings);
        instance.setDefaults(R.xml.remote_config_defaults);
        refreshRemoteConfig();
    }

    public void refreshRemoteConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds.
        if (instance.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        Log.e(TNUtil.TN, "cacheExpiration: " + cacheExpiration);

        instance.fetch(cacheExpiration)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TNUtil.TN, "RemoteConfig FETCH success. ");
                            instance.activateFetched();
                        } else {
                            Log.e(TNUtil.TN, "RemoteConfig FETCH FAIL. ");
                        }
                        printAdsBannerFooter();
                    }
                });
    }


    public void printAdsBannerFooter() {
        if (instance.getBoolean("ads_footer")) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("F7D305648EE5068F63A4F37DEAE79F1A")
//                    .addTestDevice()
                    .build();
            mAdView.loadAd(adRequest);
            mAdView.setVisibility(View.VISIBLE);
        }else {
            mAdView.setVisibility(View.GONE);
        }
    }



}
