package br.com.developers.perloti.desafioandroidtechfit.controller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import br.com.developers.perloti.desafioandroidtechfit.R;
import br.com.developers.perloti.desafioandroidtechfit.controller.adapter.DetailFeedAdapter;
import br.com.developers.perloti.desafioandroidtechfit.controller.api.ClienteAPI;
import br.com.developers.perloti.desafioandroidtechfit.model.Like;
import br.com.developers.perloti.desafioandroidtechfit.model.LikeRepository;
import br.com.developers.perloti.desafioandroidtechfit.model.Meal;
import br.com.developers.perloti.desafioandroidtechfit.util.AdsRemoteManager;
import br.com.developers.perloti.desafioandroidtechfit.util.ApplicationUtil;
import br.com.developers.perloti.desafioandroidtechfit.util.CallbackRequestTN;
import br.com.developers.perloti.desafioandroidtechfit.util.CallbackRequestUtil;
import br.com.developers.perloti.desafioandroidtechfit.util.CircleTransform;
import br.com.developers.perloti.desafioandroidtechfit.util.DateUtil;
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

public class FeedDetailActivity extends AppCompatActivity {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.imageview_profile)
    ImageView imageViewProfile;
    @BindView(R.id.textview_name)
    TextView textViewName;
    @BindView(R.id.textview_goal)
    TextView textViewGoal;
    @BindView(R.id.imageview_meal)
    ImageView imageViewMeal;
    @BindView(R.id.recyclerview_details)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBarImageMeal;
    @BindView(R.id.view_click_like)
    RelativeLayout viewClickLike;
    @BindView(R.id.textview_cal_total)
    TextView textViewCal;
    @BindView(R.id.textview_carb_total)
    TextView textViewCarb;
    @BindView(R.id.textview_gord_total)
    TextView textViewGord;
    @BindView(R.id.textview_prot_total)
    TextView textViewProt;
    @BindView(R.id.imageview_like)
    ImageView imageViewLike;
    @BindView(R.id.view)
    RelativeLayout rlayout;

    private String feedHash;
    private LinkedTreeMap linkedTreeMapDetail = new LinkedTreeMap();
    private DetailFeedAdapter adapter;
    private CallbackRequestTN callbackRequestTN;
    private AdsRemoteManager adsRemoteManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        initFeedHash();
        bindViewLike();
        bindRecycrerView();
        bindSwipeRefresh();

        setupCallbackRequest();
        downloadDetail();
        adsRemoteManager = new AdsRemoteManager(this);
        adsRemoteManager.printAdsBannerFooter();
    }


    private void setupCallbackRequest() {
        callbackRequestTN = new CallbackRequestUtil(this, new CallbackRequestUtil.MyListener() {
            @Override
            public void onClick() {
                downloadDetail();
            }
        }).getCallbackRequestTN();
    }

    private void bindViewLike() {
        Like likeByHash = LikeRepository.getLikeByHash(feedHash);
        if (likeByHash == null) {
            imageViewLike.setImageResource(R.drawable.heart_off);
        } else {
            imageViewLike.setImageResource(R.drawable.heart);
        }

        viewClickLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Like likeByHash = LikeRepository.getLikeByHash(feedHash);
                if (likeByHash == null) {
                    imageViewLike.setImageResource(R.drawable.heart);
                    LikeRepository.saveInCache(new Like(feedHash));
                } else {
                    imageViewLike.setImageResource(R.drawable.heart_off);
                    LikeRepository.removeFromCache(feedHash);
                }
            }
        });
    }

    private void initFeedHash() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            feedHash = extras.getString(TNUtil.KEY_FEEDHASH);
        }
    }

    private void bindRecycrerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        registerForContextMenu(recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
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
                downloadDetail();
            }
        });
    }

    private void downloadDetail() {
        setRefreshing(true);
        ClienteAPI.MyRetrofit.getInstance().getDetailPost(feedHash)
                .enqueue(new Callback<LinkedTreeMap>() {
                    @Override
                    public void onResponse(Call<LinkedTreeMap> call, Response<LinkedTreeMap> response) {
                        int statusCode = response.code();
                        Log.d(TNUtil.TNREQUEST, "getDetailPost - statusCode: " + statusCode);
                        if (statusCode == 200) {
                            LinkedTreeMap body = response.body();
                            linkedTreeMapDetail = JsonUtil.getObject(body, "item");
                            if (linkedTreeMapDetail != null && !linkedTreeMapDetail.isEmpty()) {
                                onUpdate();
                                callbackRequestTN.success();
                            } else {
                                callbackRequestTN.empty();
                            }
                        } else {
                            callbackRequestTN.error();
                        }
                        setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<LinkedTreeMap> call, Throwable t) {
                        Log.e(TNUtil.TNREQUEST, "Error GET DETAIL FEED");
                        callbackRequestTN.error();
                        setRefreshing(false);
                    }
                });
    }

    private void onUpdate() {
        LinkedTreeMap profile = JsonUtil.getObject(linkedTreeMapDetail, "profile");
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

        final int idProfile = JsonUtil.getInt(profile, "id", 0);
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedDetailActivity.this, ProfileDetailActivity.class);
                intent.putExtra(TNUtil.KEY_IDPROFILE, String.valueOf(idProfile));
                startActivity(intent);
            }
        });

        String nameProfile = profile.get("name").toString();
        String goalProfile = profile.get("general_goal").toString();
        textViewName.setText(nameProfile == null ? "" : nameProfile);
        textViewGoal.setText(goalProfile == null ? "" : goalProfile);

        float energy = JsonUtil.getFloat(linkedTreeMapDetail, "energy", 0);
        textViewCal.setText(String.format("%.0f" + getString(R.string.kcal), energy));
        float carbohydrate = JsonUtil.getFloat(linkedTreeMapDetail, "carbohydrate", 0);
        textViewCarb.setText(String.format("%.0f" + getString(R.string.g), carbohydrate));
        float fat = JsonUtil.getFloat(linkedTreeMapDetail, "fat", 0);
        textViewGord.setText(String.format("%.0f" + getString(R.string.g), fat));
        float protein = JsonUtil.getFloat(linkedTreeMapDetail, "protein", 0);
        textViewProt.setText(String.format("%.0f" + getString(R.string.g), protein));

        Date date = DateUtil.stringToDateFeed(linkedTreeMapDetail.get("date").toString());
        Meal meal = Meal.getMeal(JsonUtil.getInt(linkedTreeMapDetail, "meal", 0));
        String itemMealDate = meal + " " +
                ApplicationUtil.getContext().getString(R.string.of) +
                " " + DateUtil.dateToStringMask(date);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(itemMealDate);

        int w = rlayout.getWidth();
        int h = (int) (w * 1.2);

        Picasso.with(this).load(linkedTreeMapDetail.get("image").toString())
                .resize(w, h)
                .into(imageViewMeal, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        viewClickLike.setVisibility(View.VISIBLE);
                        if (progressBarImageMeal != null)
                            progressBarImageMeal.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        viewClickLike.setVisibility(View.VISIBLE);
                        imageViewMeal.setContentDescription("Erro");
                        if (progressBarImageMeal != null)
                            progressBarImageMeal.setVisibility(View.GONE);
                    }
                });


        ArrayList<LinkedTreeMap> items = (ArrayList<LinkedTreeMap>) JsonUtil.getList(linkedTreeMapDetail, "foods");
        adapter = new DetailFeedAdapter(this, items);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


}