package br.com.developers.perloti.desafioandroidtechfit.controller.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

import br.com.developers.perloti.desafioandroidtechfit.R;
import br.com.developers.perloti.desafioandroidtechfit.controller.adapter.FeedAdapter;
import br.com.developers.perloti.desafioandroidtechfit.controller.api.ClienteAPI;
import br.com.developers.perloti.desafioandroidtechfit.util.AdsRemoteManager;
import br.com.developers.perloti.desafioandroidtechfit.util.CallbackRequestTN;
import br.com.developers.perloti.desafioandroidtechfit.util.CallbackRequestUtil;
import br.com.developers.perloti.desafioandroidtechfit.util.EndlessRecyclerOnScrollListener;
import br.com.developers.perloti.desafioandroidtechfit.util.JsonUtil;
import br.com.developers.perloti.desafioandroidtechfit.util.TNUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedActivity extends AppCompatActivity {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_feed)
    RecyclerView recyclerViewFeed;
    @BindView(R.id.view_empty_error)
    View viewEE;
    @BindView(R.id.button_empty_error)
    Button buttonEE;
    @BindView(R.id.textview_empty_error)
    TextView textViewEE;
    @BindView(R.id.view)
    RelativeLayout rlayout;

    private LinkedTreeMap linkedTreeMapFeed = new LinkedTreeMap();
    private FeedAdapter adapter;
    private CallbackRequestTN cb;
    private AdsRemoteManager adsRemoteManager;

    @Override
    protected void onResume() {
        super.onResume();
        int positionCurrent = getPositionCurrent();
        if (positionCurrent > -1) {
            adapter.notifyItemRangeChanged(positionCurrent - 1, 3);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ButterKnife.bind(this);
        setupCallbackRequest();
        bindSwipeRefresh();
        bindRecycrerViewFeed();
        downloadFeed();

        adsRemoteManager = new AdsRemoteManager(this);
        adsRemoteManager.printAdsBannerFooter();
    }


    private void setupCallbackRequest() {
        cb = new CallbackRequestUtil(this, new CallbackRequestUtil.MyListener() {
            @Override
            public void onClick() {
                downloadFeed();
            }
        }).getCallbackRequestTN();
    }

    public int getWidthLayout() {
        return rlayout.getWidth();
    }

    private int getPositionCurrent() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerViewFeed.getLayoutManager();
        return layoutManager.findFirstVisibleItemPosition();
    }

    private void bindRecycrerViewFeed() {
        recyclerViewFeed.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFeed.setItemAnimator(new DefaultItemAnimator());
        registerForContextMenu(recyclerViewFeed);
    }

    private void setRefreshing(final boolean refreshing) {
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
                downloadFeed();
            }
        });
    }

    private void downloadFeed() {
        setRefreshing(true);
        ClienteAPI.MyRetrofit.getInstance().getFeed()
                .enqueue(new Callback<LinkedTreeMap>() {
                    @Override
                    public void onResponse(Call<LinkedTreeMap> call, Response<LinkedTreeMap> response) {
                        int statusCode = response.code();
                        Log.e(TNUtil.TNREQUEST, "getFeed - statusCode: " + statusCode);
                        if (statusCode == 200) {
                            linkedTreeMapFeed = response.body();
                            if (linkedTreeMapFeed != null && !linkedTreeMapFeed.isEmpty()) {
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
                        Log.e(TNUtil.TNREQUEST, "Error GET FEED");
                        cb.error();
                        setRefreshing(false);
                    }
                });
    }

    private void onUpdate() {
        List<LinkedTreeMap> items = JsonUtil.getList(linkedTreeMapFeed, "items");
        adapter = new FeedAdapter(this, items);
        recyclerViewFeed.setAdapter(adapter);
        recyclerViewFeed.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                updateFeedOld();
            }
        });
    }

    private void updateFeedOld() {
        long p = JsonUtil.getLong(linkedTreeMapFeed, "p", 0);
        p++;
        long t = JsonUtil.getLong(linkedTreeMapFeed, "t", 0);

        ClienteAPI.MyRetrofit.getInstance().getFeed(String.valueOf(p), String.valueOf(t))
                .enqueue(new Callback<LinkedTreeMap>() {
                    @Override
                    public void onResponse(Call<LinkedTreeMap> call, Response<LinkedTreeMap> response) {
                        int statusCode = response.code();
                        Log.e(TNUtil.TNREQUEST, "getFeed(p,t) statusCode: " + statusCode);
                        if (statusCode == 200) {
                            linkedTreeMapFeed = response.body();
                            ArrayList<LinkedTreeMap> items = (ArrayList<LinkedTreeMap>) JsonUtil.getList(linkedTreeMapFeed, "items");
                            adapter.addAll(items);
                        } else {
                            Log.e(TNUtil.TNREQUEST, "Error loading FEED OLD/ statusCode: " + statusCode);
                            TNUtil.toastLong(getString(R.string.error_try_again_later));
                        }
                    }

                    @Override
                    public void onFailure(Call<LinkedTreeMap> call, Throwable t) {
                        Log.e(TNUtil.TNREQUEST, "Error loading FEED OLD");
                        TNUtil.toastLong(getString(R.string.error_try_again_later));
                    }
                });

    }

}
