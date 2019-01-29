package com.readrops.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.readrops.readropslibrary.HtmlParser;
import com.readrops.readropslibrary.ParsingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AddFeedDialog extends Dialog implements View.OnClickListener {

    private int layoutId;
    private Button button;
    private TextInputEditText textInputEditText;
    private ProgressBar progressBar;

    private RecyclerView recyclerView;
    private AddFeedListAdapter adapter;

    public AddFeedDialog(@NonNull Context context, int layoutId) {
        super(context);
        if (context instanceof Activity)
            setOwnerActivity((Activity) context);
        this.layoutId = layoutId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);

        button = findViewById(R.id.add_feed_validate);
        button.setOnClickListener(this);
        textInputEditText = findViewById(R.id.add_feed_edit_text);
        progressBar = findViewById(R.id.add_feed_progressbar);
    }


    @Override
    public void onClick(View view) {
        if (recyclerView != null && recyclerView.getVisibility() == View.VISIBLE)
            recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        parseUrl(textInputEditText.getText().toString());
    }

    private void parseUrl(String url) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<ParsingResult> results = HtmlParser.getFeedLink(url);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> displayResults(results));
        });
    }

    private void displayResults(List<ParsingResult> results) {
        recyclerView = findViewById(R.id.add_feed_recyclerview);
        adapter = new AddFeedListAdapter(results);

        adapter.setOnItemClickListener((parsingResult -> {
            ((MainActivity) getOwnerActivity()).insertNewFeed(parsingResult);
            dismiss();
        }));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), ((LinearLayoutManager) layoutManager).getOrientation());
        recyclerView.addItemDecoration(decoration);

        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}
