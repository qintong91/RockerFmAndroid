package com.example.mi.rockerfm.UI;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi.rockerfm.Bus.ArticleClickEvent;
import com.example.mi.rockerfm.JsonBeans.Articles;
import com.example.mi.rockerfm.R;
import com.example.mi.rockerfm.utls.Cache;
import com.example.mi.rockerfm.utls.Net;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends MusicBaseActivity {
    @Bind(R.id.recyclerView) RecyclerView mRecyclerview;
    @Bind(R.id.swiperefreshlayout) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.fab)FloatingActionButton mFab;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mRecyclerAdapter;
    private Articles mArticles;
    private boolean mIsFreshing;
    private boolean mIsLoadingMore;
    private static final int PAGE_SIZE = 10;
    private ArticleClickEvent mArticleClickEvent;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /*4176*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerview.setLayoutManager(mLayoutManager);
        mRecyclerAdapter = new MainRecyclerViewAdapter();
        mRecyclerview.setAdapter(mRecyclerAdapter);
        mRecyclerview.addItemDecoration(new DividerItemDecoration());
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mArticles = Cache.getArticleList();
        if (isListEmpty()) {
            //Call<Articles> call = Net.getmApi().Articles();
            Call<Articles> call = Net.getmApi().getArticles(1, PAGE_SIZE,"");
            call.enqueue(new ArticleListCallback());
        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("aa", "invoke onRefresh...");
                mIsFreshing = true;
                refresh();
               /* Call<Articles> call = Net.getmApi().mainArticles(1, PAGE_SIZE);
                mIsFreshing = true;
                call.enqueue(new ArticleListCallback());*/
            }
        });
        //mSwipeRefreshLayout.setRefreshing(true);
        mRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int visibleItemCount;
            int totalItemCount;
            int firstVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                if (!mIsLoadingMore) {
                    if (visibleItemCount + firstVisibleItem >= totalItemCount) {
                        mIsLoadingMore = true;
                        Call<Articles> call = Net.getmApi().getArticles(mArticles.getCurrentPage() + 1, PAGE_SIZE,"");
                        call.enqueue(new LoadMoreCallback());
                        Log.e("aaaa", "loading ");
                    }
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mArticles != null && mArticles.getCurrentCount() != 0) {
            Cache.putArticleList(mArticles);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final class ArticleListCallback implements Callback<Articles> {

        @Override
        public void onResponse(Call<Articles> call, Response<Articles> response) {
            if (isListEmpty()) {
                mArticles = response.body();
            } else {

            }
            mRecyclerAdapter.notifyDataSetChanged();
            if (mIsFreshing) {
                mSwipeRefreshLayout.setRefreshing(false);
                mIsFreshing = false;
            }
        }

        @Override
        public void onFailure(Call<Articles> call, Throwable t) {
            setNetRequestFailure();
            Log.i("aa", t.getMessage());
        }
    }

    private final class LoadMoreCallback implements Callback<Articles> {

        @Override
        public void onResponse(Call<Articles> call, Response<Articles> response) {
            mArticles.getData().addAll(response.body().getData());
            mArticles.setCurrentCount(mArticles.getCurrentCount() + PAGE_SIZE);
            mArticles.setCurrentPage(response.body().getCurrentPage());
            mRecyclerAdapter.notifyDataSetChanged();
            mIsLoadingMore = false;
        }

        @Override
        public void onFailure(Call<Articles> call, Throwable t) {
            setNetRequestFailure();
            Log.i("aa", t.getMessage());
        }
    }

    private final class IfRefreshingCallback implements Callback<Articles> {

        @Override
        public void onResponse(Call<Articles> call, Response<Articles> response) {

            if (mArticles.getTotalCount() >= response.body().getTotalCount()) {
                Toast.makeText(MainActivity.this, "已经是最新了", Toast.LENGTH_LONG).show();
                if (mIsFreshing) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mIsFreshing = false;
                }
            } else {
                call = Net.getmApi().getArticles(1, response.body().getTotalCount() - mArticles.getTotalCount(),"");
                call.enqueue(new RefreshingCallback());
            }
        }

        @Override
        public void onFailure(Call<Articles> call, Throwable t) {
            setNetRequestFailure();
            Log.i("aa", t.getMessage());
        }
    }

    private final class RefreshingCallback implements Callback<Articles> {

        @Override
        public void onResponse(Call<Articles> call, Response<Articles> response) {
            response.body().getData().addAll(mArticles.getData());
            response.body().setCurrentCount(mArticles.getCurrentCount() + response.body().getCurrentCount());
            mArticles = response.body();
            mRecyclerAdapter.notifyDataSetChanged();
            if (mIsFreshing) {
                mSwipeRefreshLayout.setRefreshing(false);
                mIsFreshing = false;
            }
        }

        @Override
        public void onFailure(Call<Articles> call, Throwable t) {
            setNetRequestFailure();
            Log.i("aa", t.getMessage());
        }
    }


    public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {
        //创建新View，被LayoutManager所调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_item, viewGroup, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        //将数据与界面进行绑定的操作
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.mTitleTextView.setText(mArticles.getData().get(position).getTitleAttr());
            viewHolder.mImageView.setImageURI(Uri.parse(mArticles.getData().get(position).getCover()));
            viewHolder.mAuthorTextView.setText(mArticles.getData().get(position).getAuthor().getNickname());
            viewHolder.mTypeTextView.setText(mArticles.getData().get(position).getCategaryMarkClassname());

        }

        //获取数据的数量
        @Override
        public int getItemCount() {
            //return mArticles == null ? 0:mArticles.ArticleList.size();
            return mArticles == null ? 0 : mArticles.getCurrentCount();
        }

        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @Bind(R.id.text_title)
            TextView mTitleTextView;
            @Bind(R.id.image)
            SimpleDraweeView mImageView;
            @Bind(R.id.text_author)
            TextView mAuthorTextView;
            @Bind(R.id.text_type)
            TextView mTypeTextView;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if(mArticleClickEvent == null)
                    mArticleClickEvent = new ArticleClickEvent();
                mArticleClickEvent.setArticle(mArticles.getData().get(getAdapterPosition()));
                 EventBus.getDefault().postSticky(mArticleClickEvent);
                 Intent intent = new Intent(MainActivity.this, RockerFmMainActivity.class);
                startActivity(intent);
            }
        }
    }

    private class DividerItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            c.drawColor(getResources().getColor(R.color.colorDivider));
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            onDrawOver(c, parent);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, 0, 8);
        }
    }

    private boolean isListEmpty() {
        return (mArticles == null || mArticles.getCurrentCount() == 0);
    }

    private void refresh() {
        Call<Articles> call = null;
        if (isListEmpty()) {
            //Call<Articles> call = Net.getmApi().Articles();
            call = Net.getmApi().getArticles(1, PAGE_SIZE,"");
            call.enqueue(new ArticleListCallback());
        } else {
            call = Net.getmApi().getArticles(1, 1,"");
            call.enqueue(new IfRefreshingCallback());
        }
}

    private void setNetRequestFailure() {
        Toast.makeText(MainActivity.this, "网络请求失败", Toast.LENGTH_LONG).show();
        if (mIsFreshing) {
            mSwipeRefreshLayout.setRefreshing(false);
            mIsFreshing = false;
        }
        if (mIsLoadingMore)
            mIsLoadingMore = false;
    }

}
