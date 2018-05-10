package br.com.developers.perloti.desafioandroidtechfit.controller.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import br.com.developers.perloti.desafioandroidtechfit.R;
import br.com.developers.perloti.desafioandroidtechfit.model.Like;
import br.com.developers.perloti.desafioandroidtechfit.model.LikePersistence;
import br.com.developers.perloti.desafioandroidtechfit.model.Meal;
import br.com.developers.perloti.desafioandroidtechfit.util.ApplicationUtil;
import br.com.developers.perloti.desafioandroidtechfit.util.CircleTransform;
import br.com.developers.perloti.desafioandroidtechfit.controller.api.ClienteAPI;
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


    private String feed_hash;
    private LinkedTreeMap linkedTreeMapDetail = new LinkedTreeMap();
    private DetailFeedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);
        if (getSupportActionBar() != null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        initFeedHash();

        bindRecycrerView();
        bindSwipeRefresh();
        recyclerView.setNestedScrollingEnabled(false);
        downloadDetail();

        Like likeByHash = LikePersistence.getLikeByHash(feed_hash);
        if (likeByHash == null) {
            imageViewLike.setImageResource(R.drawable.heart_off);
        } else {
            imageViewLike.setImageResource(R.drawable.heart);
        }

        viewClickLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Like likeByHash = LikePersistence.getLikeByHash(feed_hash);
                if (likeByHash == null) {
                    imageViewLike.setImageResource(R.drawable.heart);
                    LikePersistence.saveInCache(new Like(feed_hash));
                    TNUtil.toastLong("SAVE LIKE");
                } else {
                    imageViewLike.setImageResource(R.drawable.heart_off);
                    LikePersistence.removeFromCache(feed_hash);
                    TNUtil.toastLong("REMOVE LIKE");
                }


            }
        });
    }

    private void initFeedHash() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            feed_hash = extras.getString(TNUtil.KEY_FEEDHASH);
            Toast.makeText(this, "ID: " + feed_hash, Toast.LENGTH_LONG).show();
        }
    }

    private void bindRecycrerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        registerForContextMenu(recyclerView);
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
                downloadDetail();
            }
        });
    }

    private void downloadDetail() {
        setRefreshing(true);
        ClienteAPI.MyRetrofit.getInstance().getDetailPost(feed_hash)
                .enqueue(new Callback<LinkedTreeMap>() {
                    @Override
                    public void onResponse(Call<LinkedTreeMap> call, Response<LinkedTreeMap> response) {
                        int statusCode = response.code();
                        Log.e(TNUtil.TNREQUEST, "StatusCode: " + statusCode);
                        if (statusCode == 200) {
                            LinkedTreeMap body = response.body();
                            linkedTreeMapDetail = (LinkedTreeMap) body.get("item");

                            onUpdate();
                        }

                        setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<LinkedTreeMap> call, Throwable t) {
                        Log.e(TNUtil.TNREQUEST, "Error GET DETAIL FEED");
                        setRefreshing(false);
                    }
                });
    }

    private void onUpdate() {
        LinkedTreeMap profile = (LinkedTreeMap) linkedTreeMapDetail.get("profile");
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

        final int id_user = JsonUtil.getInt(profile, "id", 0);
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedDetailActivity.this, ProfileDetailActivity.class);
                intent.putExtra("ID_PROFILE", String.valueOf(id_user));
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
            getSupportActionBar().setTitle(itemMealDate == null ? getString(R.string.detail) : itemMealDate);

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

        ArrayList<LinkedTreeMap> items = (ArrayList<LinkedTreeMap>) linkedTreeMapDetail.get("foods");
        adapter = new DetailFeedAdapter(this, items);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    class DetailFeedAdapter extends RecyclerView.Adapter<FeedDetailActivity.DetailFeedAdapter.Holder> {

        Context context;
        ArrayList<LinkedTreeMap> linkedTreeMapItems = new ArrayList<>();

        public DetailFeedAdapter(Context context, ArrayList<LinkedTreeMap> linkedTreeMapItems) {
            this.context = context;
            this.linkedTreeMapItems = linkedTreeMapItems;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_detail_item, parent, false);
            return new Holder(inflate);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.render(linkedTreeMapItems.get(position));
        }

        @Override
        public int getItemCount() {
            return linkedTreeMapItems.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            @BindView(R.id.textview_cal)
            TextView textViewCal;
            @BindView(R.id.textview_carb)
            TextView textViewCarb;
            @BindView(R.id.textview_gord)
            TextView textViewGord;
            @BindView(R.id.textview_prot)
            TextView textViewProt;
            @BindView(R.id.textview_name_food)
            TextView textViewNameFood;
            @BindView(R.id.textview_units)
            TextView textViewUnits;

            public Holder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

            }

            //            format("%.2f" + getString(R.string.g),
            public void render(LinkedTreeMap item) {
                String description = JsonUtil.getString(item, "description");
                textViewNameFood.setText(description);

                float amount = JsonUtil.getFloat(item, "amount", 0);
                String measure = JsonUtil.getString(item, "measure");
                float weight = JsonUtil.getFloat(item, "weight", 0);
                textViewUnits.setText(amount + " " + measure + " (" + weight + getString(R.string.g) + ")");


                float energy = JsonUtil.getFloat(item, "energy", 0);
                textViewCal.setText(String.format("%.0f" + getString(R.string.kcal), energy));
                float carbohydrate = JsonUtil.getFloat(item, "carbohydrate", 0);
                textViewCarb.setText(String.format("%.0f" + getString(R.string.g), carbohydrate));
                float fat = JsonUtil.getFloat(item, "fat", 0);
                textViewGord.setText(String.format("%.0f" + getString(R.string.g), fat));
                float protein = JsonUtil.getFloat(item, "protein", 0);
                textViewProt.setText(String.format("%.0f" + getString(R.string.g), protein));


            }
        }

    }

}