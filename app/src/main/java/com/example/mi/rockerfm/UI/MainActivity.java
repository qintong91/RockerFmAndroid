package com.example.mi.rockerfm.UI;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi.rockerfm.R;
import com.example.mi.rockerfm.JsonBeans.Articals;
import com.example.mi.rockerfm.utls.Cache;
import com.example.mi.rockerfm.utls.Net;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity{
    private RecyclerView mRecyclerview;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mRecyclerAdapter;
    private Articals mArticals;
    private boolean mIsFreshing;
    private boolean mIsLoadingMore;
    private static final int PAGE_SIZE = 10;
/*4176*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefreshlayout);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerview.setLayoutManager(mLayoutManager);
        mRecyclerAdapter  = new MainRecyclerViewAdapter();
        mRecyclerview.setAdapter(mRecyclerAdapter);
        mRecyclerview.addItemDecoration(new DividerItemDecoration());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mArticals = Cache.getArticalList();
        if(isListEmpty()){
            //Call<Articals> call = Net.getmApi().articals();
            Call<Articals> call = Net.getmApi().mainArticals(1,PAGE_SIZE);
            call.enqueue(new ArticalListCallback());
        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("aa", "invoke onRefresh...");
                mIsFreshing = true;
                refresh();
               /* Call<Articals> call = Net.getmApi().mainArticals(1, PAGE_SIZE);
                mIsFreshing = true;
                call.enqueue(new ArticalListCallback());*/
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
                if(!mIsLoadingMore){
                    if(visibleItemCount + firstVisibleItem >= totalItemCount){
                        mIsLoadingMore = true;
                        Call<Articals> call = Net.getmApi().mainArticals(mArticals.getCurrentPage()+1,PAGE_SIZE);
                        call.enqueue(new LoadMoreCallback());
                        Log.e("aaaa", "loading ");
                    }
                }
            }
        });
    }

    @Override
    public  void onPause(){
        super.onPause();
        if(mArticals != null && mArticals.getCurrentCount()!=0){
            Cache.putArticalList(mArticals);
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
    private final class ArticalListCallback implements Callback<Articals>{

        @Override
        public void onResponse(Call<Articals> call,Response<Articals> response) {
            if(isListEmpty()) {
                mArticals = response.body();
            } else {

            }
            mRecyclerAdapter.notifyDataSetChanged();
            if(mIsFreshing){
                mSwipeRefreshLayout.setRefreshing(false);
                mIsFreshing = false;
            }
        }

        @Override
        public void onFailure(Call<Articals> call,Throwable t) {
            setNetRequestFailure();
            Log.i("aa",t.getMessage());
        }
    }

    private final class LoadMoreCallback implements Callback<Articals>{

        @Override
        public void onResponse(Call<Articals> call,Response<Articals> response) {
            mArticals.getData().addAll(response.body().getData());
            mArticals.setCurrentCount(mArticals.getCurrentCount()+PAGE_SIZE);
            mArticals.setCurrentPage(response.body().getCurrentPage());
            mRecyclerAdapter.notifyDataSetChanged();
            mIsLoadingMore = false;
        }

        @Override
            public void onFailure (Call<Articals> call,Throwable t) {
            setNetRequestFailure();
            Log.i("aa",t.getMessage());
        }
    }
    private final class IfRefreshingCallback implements Callback<Articals>{

        @Override
        public void onResponse(Call<Articals> call,Response<Articals> response) {

            if (mArticals.getTotalCount() >= response.body().getTotalCount()) {
                Toast.makeText(MainActivity.this, "已经是最新了", Toast.LENGTH_LONG).show();
                if (mIsFreshing) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mIsFreshing = false;
                }
            } else {
                call = Net.getmApi().mainArticals(1, response.body().getCurrentCount() - mArticals.getTotalCount());
                call.enqueue(new RefreshingCallback());
            }
        }

        @Override
        public void onFailure(Call<Articals> call,Throwable t) {
            setNetRequestFailure();
            Log.i("aa",t.getMessage());
        }
    }

    private final class RefreshingCallback implements Callback<Articals>{

        @Override
        public void onResponse(Call<Articals> call,Response<Articals> response) {
            response.body().getData().addAll(mArticals.getData());
            response.body().setCurrentCount(mArticals.getCurrentCount() + response.body().getCurrentCount());
            mArticals = response.body();
            mRecyclerAdapter.notifyDataSetChanged();
            if(mIsFreshing){
                mSwipeRefreshLayout.setRefreshing(false);
                mIsFreshing = false;
            }
        }

        @Override
        public void onFailure(Call<Articals> call,Throwable t) {
            setNetRequestFailure();
            Log.i("aa",t.getMessage());
        }
    }


    public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder>{
        //创建新View，被LayoutManager所调用
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_item,viewGroup,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }
        //将数据与界面进行绑定的操作
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.mTitleTextView.setText(mArticals.getData().get(position).getTitleAttr());
            viewHolder.mImageView.setImageURI(Uri.parse(mArticals.getData().get(position).getCover()));
            viewHolder.mAuthorTextView.setText(mArticals.getData().get(position).getAuthor().getNickname());

        }
        //获取数据的数量
        @Override
        public int getItemCount() {
            //return mArticals == null ? 0:mArticals.articalList.size();
            return mArticals == null ? 0:mArticals.getCurrentCount();
        }

        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
            public TextView mTitleTextView;
            public SimpleDraweeView mImageView;
            public TextView mAuthorTextView;
            public ViewHolder(View view){
                super(view);
                mTitleTextView = (TextView) view.findViewById(R.id.text_title);
                mImageView = (SimpleDraweeView)view.findViewById(R.id.image);
                mAuthorTextView = (TextView) view.findViewById(R.id.text_author);
                view.setOnClickListener(this);
            }
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky(mArticals.getData().get(getAdapterPosition()));
                Intent intent = new Intent(MainActivity.this,ArticleActivity.class);
                startActivity(intent);
            }
        }
    }
     private class DividerItemDecoration extends RecyclerView.ItemDecoration{
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
    private boolean isListEmpty(){
        return (mArticals == null || mArticals.getCurrentCount()== 0);
    }
    private void refresh(){
        Call<Articals> call = null;
        if(isListEmpty()){
            //Call<Articals> call = Net.getmApi().articals();
            call = Net.getmApi().mainArticals(1,PAGE_SIZE);
            call.enqueue(new ArticalListCallback());
        } else {
            call = Net.getmApi().mainArticals(1,1);
            call.enqueue(new IfRefreshingCallback());
        }
    }
    private void setNetRequestFailure(){
        Toast.makeText(MainActivity.this, "网络请求失败", Toast.LENGTH_LONG).show();
        if(mIsFreshing){
            mSwipeRefreshLayout.setRefreshing(false);
            mIsFreshing = false;
        }
        if(mIsLoadingMore)
            mIsLoadingMore = false;
    }

}
