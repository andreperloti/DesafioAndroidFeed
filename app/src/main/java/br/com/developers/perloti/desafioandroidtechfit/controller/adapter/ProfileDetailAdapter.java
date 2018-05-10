package br.com.developers.perloti.desafioandroidtechfit.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.com.developers.perloti.desafioandroidtechfit.R;
import br.com.developers.perloti.desafioandroidtechfit.controller.activities.FeedDetailActivity;
import br.com.developers.perloti.desafioandroidtechfit.util.ApplicationUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by perloti on 10/05/18.
 */


public class ProfileDetailAdapter extends RecyclerView.Adapter<ProfileDetailAdapter.Holder> {

    private Context context;
    private ArrayList<LinkedTreeMap> meals = new ArrayList<>();

    public ProfileDetailAdapter(Context context, ArrayList<LinkedTreeMap> meals) {
        this.context = context;
        this.meals = meals;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_meal_item, parent, false);
        return new Holder(inflate);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.render(meals.get(position));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void addAll(ArrayList<LinkedTreeMap> meals) {
        for (LinkedTreeMap map : meals) {
            if (!this.meals.contains(map)) {
                this.meals.add(map);
            }
        }
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageview_meal)
        ImageView imageViewMeal;
        @BindView(R.id.progressBar)
        ProgressBar progressBarImageMeal;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void render(LinkedTreeMap meal) {
            final String id = meal.get("id").toString();
            final String feedHash = meal.get("feedHash").toString();

            Picasso.with(context).load(meal.get("image").toString())
                    .resize(100, 100)
                    .into(imageViewMeal, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (progressBarImageMeal != null)
                                progressBarImageMeal.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            imageViewMeal.setContentDescription("Erro");
                            if (progressBarImageMeal != null)
                                progressBarImageMeal.setVisibility(View.GONE);
                        }
                    });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ApplicationUtil.getContext(), "FeedHash: " + feedHash, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, FeedDetailActivity.class);
                    intent.putExtra("FEED_HASH", String.valueOf(feedHash));
                    context.startActivity(intent);
                }
            });

        }
    }
}
