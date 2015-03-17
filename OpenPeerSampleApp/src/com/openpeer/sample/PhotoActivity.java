package com.openpeer.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.openpeer.sample.R;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PhotoActivity extends Activity {
    public static final String ARG_IMAGE_URI = "imageUri";
    String imageUri;
    @InjectView(R.id.imageView)
    ImageView imageView;

    public static void start(Context context, String imageUri) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(ARG_IMAGE_URI, imageUri);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        imageUri = getIntent().getStringExtra(ARG_IMAGE_URI);
        ButterKnife.inject(this);
        Picasso.with(this).load(Uri.parse(imageUri)).into(imageView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.imageView)
    public void onImageClick(View view) {
        if (!isFinishing()) {
            finish();
        }
    }
}
