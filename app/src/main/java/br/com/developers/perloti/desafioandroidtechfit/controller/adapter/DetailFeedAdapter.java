package br.com.developers.perloti.desafioandroidtechfit.controller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

import br.com.developers.perloti.desafioandroidtechfit.R;
import br.com.developers.perloti.desafioandroidtechfit.util.JsonUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by perloti on 10/05/18.
 */

public class DetailFeedAdapter extends RecyclerView.Adapter<DetailFeedAdapter.Holder> {

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

        public void render(LinkedTreeMap item) {
            String description = JsonUtil.getString(item, "description");
            textViewNameFood.setText(description);

            float amount = JsonUtil.getFloat(item, "amount", 0);
            String measure = JsonUtil.getString(item, "measure");
            float weight = JsonUtil.getFloat(item, "weight", 0);
            textViewUnits.setText(amount + " " + measure + " (" + weight + context.getString(R.string.g) + ")");


            float energy = JsonUtil.getFloat(item, "energy", 0);
            textViewCal.setText(String.format("%.0f" + context.getString(R.string.kcal), energy));
            float carbohydrate = JsonUtil.getFloat(item, "carbohydrate", 0);
            textViewCarb.setText(String.format("%.0f" + context.getString(R.string.g), carbohydrate));
            float fat = JsonUtil.getFloat(item, "fat", 0);
            textViewGord.setText(String.format("%.0f" + context.getString(R.string.g), fat));
            float protein = JsonUtil.getFloat(item, "protein", 0);
            textViewProt.setText(String.format("%.0f" + context.getString(R.string.g), protein));


        }
    }

}