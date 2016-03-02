package com.example.mi.rockerfm.UI;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.example.mi.rockerfm.R;
import com.example.mi.rockerfm.beans.Articals;
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

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerview;
    private LinearLayoutManager mLayoutManager;
    RecyclerView.Adapter mRecyclerAdapter;
    private Articals mArticals;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerview.setLayoutManager(mLayoutManager);
        mRecyclerAdapter  = new MainRecyclerViewAdapter();
        mRecyclerview.setAdapter(mRecyclerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Call<Articals> call = Net.getmApi().articals();
        call.enqueue(new ArticalListCallback());
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
        public void onResponse(Response<Articals> response, Retrofit retrofit) {
            mArticals = response.body();
            mRecyclerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(Throwable t) {
            Log.i("aa",t.getMessage());
        }
    }
    public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {
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
            viewHolder.mTitleTextView.setText(mArticals.articalList.get(position).getTitle());
            viewHolder.mImageView.setImageURI(Uri.parse(mArticals.articalList.get(position).getImgHref()));
            viewHolder.mAuthorTextView.setText(mArticals.articalList.get(position).getAuthor());
        }
        //获取数据的数量
        @Override
        public int getItemCount() {
            return mArticals == null ? 0:mArticals.articalList.size();
        }
        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTitleTextView;
            public SimpleDraweeView mImageView;
            public TextView mAuthorTextView;
            public ViewHolder(View view){
                super(view);
                mTitleTextView = (TextView) view.findViewById(R.id.text_title);
                mImageView = (SimpleDraweeView)view.findViewById(R.id.image);
                mAuthorTextView = (TextView) view.findViewById(R.id.text_author);
            }
        }
    }

}
