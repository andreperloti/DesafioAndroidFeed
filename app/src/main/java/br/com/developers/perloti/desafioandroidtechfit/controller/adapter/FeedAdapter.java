package br.com.developers.perloti.desafioandroidtechfit.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.developers.perloti.desafioandroidtechfit.R;
import br.com.developers.perloti.desafioandroidtechfit.controller.activities.FeedActivity;
import br.com.developers.perloti.desafioandroidtechfit.controller.activities.FeedDetailActivity;
import br.com.developers.perloti.desafioandroidtechfit.controller.activities.ProfileDetailActivity;
import br.com.developers.perloti.desafioandroidtechfit.model.Like;
import br.com.developers.perloti.desafioandroidtechfit.model.LikeRepository;
import br.com.developers.perloti.desafioandroidtechfit.model.Meal;
import br.com.developers.perloti.desafioandroidtechfit.util.ApplicationUtil;
import br.com.developers.perloti.desafioandroidtechfit.util.CircleTransform;
import br.com.developers.perloti.desafioandroidtechfit.util.DateUtil;
import br.com.developers.perloti.desafioandroidtechfit.util.JsonUtil;
import br.com.developers.perloti.desafioandroidtechfit.util.TNUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by perloti on 10/05/18.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.Holder> {

    private List<LinkedTreeMap> linkedTreeMapItems = new ArrayList<>();
    private Context context;

    public FeedAdapter(Context context, List<LinkedTreeMap> linkedTreeMapItems) {
        this.context = context;
        this.linkedTreeMapItems = linkedTreeMapItems;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
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


    public void addAll(ArrayList<LinkedTreeMap> maps) {
        for (LinkedTreeMap map : maps) {
            if (!this.linkedTreeMapItems.contains(map)) {
                this.linkedTreeMapItems.add(map);
            }
        }
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_name)
        TextView textViewName;
        @BindView(R.id.textview_goal)
        TextView textViewGoal;
        @BindView(R.id.textview_meal)
        TextView textViewMealDate;
        @BindView(R.id.textview_kcal)
        TextView textViewKcal;
        @BindView(R.id.imageview_meal)
        ImageView imageViewMeal;
        @BindView(R.id.imageview_profile)
        ImageView imageViewProfile;
        @BindView(R.id.progressBar)
        ProgressBar progressBarImageMeal;
        @BindView(R.id.view_click_like)
        RelativeLayout viewClickLike;
        @BindView(R.id.image_view_like)
        ImageView imageViewLike;

        Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void render(LinkedTreeMap item) {
            LinkedTreeMap profile = (LinkedTreeMap) item.get("profile");
            final int idProfile = JsonUtil.getInt(profile, "id", 0);
            final String feedHash = item.get("feedHash").toString();
            String nameProfile = profile.get("name").toString();
            String goalProfile = profile.get("general_goal").toString();
            int energy = JsonUtil.getInt(item, "energy", 0);
            Date date = DateUtil.stringToDateFeed(item.get("date").toString());
            String energyString = String.valueOf(energy) + ApplicationUtil.getContext().getString(R.string.kcal);

            Meal meal = Meal.getMeal(JsonUtil.getInt(item, "meal", 0));
            String itemMealDate = meal + " " + ApplicationUtil.getContext().getString(R.string.of) + " " + DateUtil.dateToStringMask(date);
            textViewName.setText(nameProfile == null ? "" : nameProfile);
            textViewGoal.setText(goalProfile == null ? "" : goalProfile);
            if (energy != 0) {
                textViewKcal.setText(energyString);
                textViewKcal.setVisibility(View.VISIBLE);
            } else {
                textViewKcal.setVisibility(View.GONE);
            }
            textViewMealDate.setText(itemMealDate);

            int w = ((FeedActivity) context).getWidthLayout();
            int h = (int) (w * 1.2);

            Picasso.with(context).load(item.get("image").toString())
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


            if (profile.get("image") != null) {
                Picasso.with(context).load(profile.get("image").toString())
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .transform(new CircleTransform())
                        .into(imageViewProfile);
            } else {
                Picasso.with(context).load(R.drawable.ic_person)
                        .placeholder(R.drawable.ic_person)
                        .transform(new CircleTransform())
                        .into(imageViewProfile);
            }

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

            imageViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileDetailActivity.class);
                    intent.putExtra(TNUtil.KEY_IDPROFILE, String.valueOf(idProfile));
                    context.startActivity(intent);
                }
            });

            imageViewMeal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, FeedDetailActivity.class);
                    intent.putExtra(TNUtil.KEY_FEEDHASH, String.valueOf(feedHash));
                    context.startActivity(intent);
                }
            });

        }
    }
}
