package com.example.mi.rockerfm.UI;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.mi.rockerfm.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TextView mTextView;
    Document mDoc;
    DownloadFilesTask mTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTextView  = (TextView)findViewById(R.id.text1);
        mTask = new DownloadFilesTask();
        mTask.execute();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
    private class DownloadFilesTask extends AsyncTask<Void, Integer, Long> {
        protected Long doInBackground(Void... params) {

            long totalSize = 0;
            try {
                mDoc = Jsoup.connect("http://www.rockerfm.com").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return totalSize;
        }



        protected void onPostExecute(Long result) {

            mTextView.setText(mDoc.toString());
            Elements contents = mDoc.getElementsByTag("article");
            String id = contents.get(1).attr("id");
            String title = contents.get(1).getElementsByTag("a").attr("title");
            String href = contents.get(1).getElementsByTag("a").attr("href");
        }
    }
}