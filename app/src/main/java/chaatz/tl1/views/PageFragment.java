package chaatz.tl1.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import chaatz.tl1.network.OkHttpClientSingleton;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Callback;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import chaatz.tl1.R;
import chaatz.tl1.config.SharedConsts;
import chaatz.tl1.models.DataItem;

import static android.content.ContentValues.TAG;

/**
 * Created by SHEILD on 3/3/2017.
 */

public class PageFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private boolean isNoMoreData = false;
    private boolean loading = false;
    private int totalItemCount, lastVisibleItem;
    private int visibleThreshold = 2;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;

    public PageFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PageFragment newInstance(int sectionNumber) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecyclerViewAdapter(getContext());
        loadMore(0, SharedConsts.SAMPLE_JSON);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isNoMoreData) {
                    Log.v(TAG, "Full list loaded.");
                    return;
                }

                if(dy > 0) //check for scroll down
                {
                    totalItemCount = mLayoutManager.getItemCount();
                    lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                    if (!loading)
                    {
                        if (totalItemCount <= (lastVisibleItem + visibleThreshold))
                        {
                            loading = true;
                            mAdapter.insertProgressFooter();
                            //Do pagination.. i.e. fetch new data
                            makeLoadMoreRequest();
                        }
                    }
                }
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.id_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.clearData();
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        loadMore(0, SharedConsts.SAMPLE_JSON);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        return rootView;
    }

    private void loadMore(int from, String jsonString) {
        // Demo use
        jsonString = SharedConsts.SAMPLE_JSON;
        try {
            JSONObject obj = new JSONObject(jsonString);
            isNoMoreData = !obj.getBoolean("incomplete_result");

            JSONArray arr = obj.getJSONArray("data");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject singleObj = arr.getJSONObject(i);
                DataItem item = new DataItem(singleObj);
                mAdapter.appendData(item);
            }
            if (from != 0) {
                mAdapter.notifyItemRangeInserted(from, mAdapter.getItemCount());
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException ex) {
            Log.e(TAG, ex.getMessage());
        }
        loading = false;
    }


    private void makeLoadMoreRequest() {
        Request request = new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .build();
        OkHttpClientSingleton.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //remove progress item
                        mAdapter.removeProgressFooter();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                // Read data on the worker thread
                final String responseData = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //remove progress item
                        mAdapter.removeProgressFooter();

                        int from = mAdapter.getItemCount();
                        loadMore(from, responseData);
                    }
                });
            }
        });
    }
}
