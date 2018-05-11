package br.com.developers.perloti.desafioandroidtechfit.controller.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.com.developers.perloti.desafioandroidtechfit.R;
import br.com.developers.perloti.desafioandroidtechfit.controller.adapter.ProfileDetailAdapter;
import br.com.developers.perloti.desafioandroidtechfit.controller.api.ClienteAPI;
import br.com.developers.perloti.desafioandroidtechfit.util.AdsRemoteManager;
import br.com.developers.perloti.desafioandroidtechfit.util.CallbackRequestTN;
import br.com.developers.perloti.desafioandroidtechfit.util.CallbackRequestUtil;
import br.com.developers.perloti.desafioandroidtechfit.util.CircleTransform;
import br.com.developers.perloti.desafioandroidtechfit.util.EndlessRecyclerOnScrollListener;
import br.com.developers.perloti.desafioandroidtechfit.util.JsonUtil;
import br.com.developers.perloti.desafioandroidtechfit.util.TNUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by perloti on 08/05/18.
 */

public class ProfileDetailActivity extends AppCompatActivity {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.imageview_profile)
    ImageView imageViewProfile;
    @BindView(R.id.textview_name)
    TextView textViewName;
    @BindView(R.id.textview_goal)
    TextView textViewGoal;
    @BindView(R.id.recyclerview_meals)
    RecyclerView recyclerView;

    private String id_profile;
    LinkedTreeMap linkedTreeMapProfile = new LinkedTreeMap();
    private ProfileDetailAdapter adapter;
    private CallbackRequestTN cb;
    private AdsRemoteManager adsRemoteManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initIdProfile();
        bindRecyclerView();
        bindSwipeRefresh();
        setupCallbackRequest();
        downloadProfile();

        adsRemoteManager = new AdsRemoteManager(this);
        adsRemoteManager.printAdsBannerFooter();
    }


    private void setupCallbackRequest() {
        cb = new CallbackRequestUtil(this, new CallbackRequestUtil.MyListener() {
           @Override
           public void onClick() {
               downloadProfile();
           }
       }).getCallbackRequestTN();
    }

    private void initIdProfile() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id_profile = extras.getString(TNUtil.KEY_IDPROFILE);
        }
    }

    private void bindRecyclerView() {
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
    }

    public void setRefreshing(final boolean refreshing) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(refreshing);
            }
        });
    }

    private void bindSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.color_primary, R.color.color_primary_dark, R.color.color_primary, R.color.color_primary_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adsRemoteManager.refreshRemoteConfig();
                downloadProfile();
            }
        });
    }

    private void downloadProfile() {
        setRefreshing(true);
        ClienteAPI.MyRetrofit.getInstance().getProfile(id_profile)
                .enqueue(new Callback<LinkedTreeMap>() {
                    @Override
                    public void onResponse(Call<LinkedTreeMap> call, Response<LinkedTreeMap> response) {
                        int statusCode = response.code();
                        Log.e(TNUtil.TNREQUEST, "getProfile - statusCode: " + statusCode);
                        if (statusCode == 200) {
                            linkedTreeMapProfile = response.body();
                            if (linkedTreeMapProfile != null && !linkedTreeMapProfile.isEmpty()){
                                onUpdate();
                                cb.success();
                            } else {
                                cb.empty();
                            }
                        } else {
                            cb.error();
                        }

                        setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<LinkedTreeMap> call, Throwable t) {
                        Log.e(TNUtil.TNREQUEST, "Error GET Profile");
                        setRefreshing(false);
                    }
                });
    }

    private void onUpdate() {
        LinkedTreeMap profile = (LinkedTreeMap) linkedTreeMapProfile.get("profile");
        int items_count = JsonUtil.getInt(profile, "items_count", 0);
        Log.d(TNUtil.TN, "TOTAL Meals: " + items_count);
        if (profile.get("image") != null) {
            Picasso.with(this).load(profile.get("image").toString())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .transform(new CircleTransform())
                    .into(imageViewProfile);
        } else {
            Picasso.with(this).load(R.drawable.ic_person)
                    .placeholder(R.drawable.ic_person)
                    .transform(new CircleTransform())
                    .into(imageViewProfile);
        }
        String nameProfile = profile.get("name").toString();
        String goalProfile = profile.get("general_goal").toString();

        textViewName.setText(nameProfile == null ? "" : nameProfile);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(nameProfile == null ? "Perfil" : nameProfile);
        textViewGoal.setText(goalProfile == null ? "" : goalProfile);

        ArrayList<LinkedTreeMap> linkedTreeMapItems =
                (ArrayList<LinkedTreeMap>) JsonUtil.getList(linkedTreeMapProfile,"items");
        adapter = new ProfileDetailAdapter(this, linkedTreeMapItems);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                updateMeals();
            }
        });
    }

    private void updateMeals() {
        long p = JsonUtil.getLong(linkedTreeMapProfile, "p", 0);
        p++;
        long t = JsonUtil.getLong(linkedTreeMapProfile, "t", 0);
        ClienteAPI.MyRetrofit.getInstance().getProfile(id_profile, String.valueOf(p), String.valueOf(t))
                .enqueue(new Callback<LinkedTreeMap>() {
                    @Override
                    public void onResponse(Call<LinkedTreeMap> call, Response<LinkedTreeMap> response) {
                        int statusCode = response.code();
                        Log.e(TNUtil.TNREQUEST, "getProfile(p,t) - statusCode: " + statusCode);
                        if (statusCode == 200) {
                            linkedTreeMapProfile = response.body();
                            ArrayList<LinkedTreeMap> linkedTreeMapItems = (ArrayList<LinkedTreeMap>) JsonUtil.getList(linkedTreeMapProfile,"items");
                            adapter.addAll(linkedTreeMapItems);
                        }
                    }

                    @Override
                    public void onFailure(Call<LinkedTreeMap> call, Throwable t) {
                        Log.e(TNUtil.TNREQUEST, "Error GET UPDATE");
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


}
