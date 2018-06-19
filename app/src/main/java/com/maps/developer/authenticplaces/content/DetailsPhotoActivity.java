package com.maps.developer.authenticplaces.content;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.maps.developer.authenticplaces.R;

import static android.support.v4.app.FrameMetricsAggregator.ANIMATION_DURATION;

public class DetailsPhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = DetailsPhotoActivity.class.getSimpleName();

    public static final String EXTRA_MARKER_PHOTO = "DetailsPhotoActivity.MARKER_PHOTO";

    private FrameLayout parentView;
    private ImageView imageViewDetails;
    private FloatingActionButton buttonRotateImage;
    private float fromDegrees = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_photo);

        parentView = (FrameLayout) findViewById(R.id.container_details);
        imageViewDetails = (ImageView) findViewById(R.id.image_details);
        buttonRotateImage = (FloatingActionButton) findViewById(R.id.btn_rotate_image);
        buttonRotateImage.setOnClickListener(this);
        MarkerPhoto markerPhoto = getIntent().getParcelableExtra(EXTRA_MARKER_PHOTO);
        parentView.setBackgroundColor(Color.BLACK);

        Glide.with(this)
                .load(markerPhoto.getUri())
                .into(imageViewDetails);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_rotate_image:
                RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees,fromDegrees + 90,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);
                rotateAnimation.setInterpolator(new LinearInterpolator());
                rotateAnimation.setDuration(ANIMATION_DURATION);
                rotateAnimation.setFillAfter(true);
                imageViewDetails.startAnimation(rotateAnimation);
                fromDegrees += 90;
                if (fromDegrees == 360){
                    fromDegrees = 0;
                }
                break;
        }
    }
}
