package com.maps.developer.authenticplaces;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class DetailsPhotoActivity extends AppCompatActivity implements RequestListener<String, Bitmap> {

    private static final String TAG = DetailsPhotoActivity.class.getSimpleName();

    public static final String EXTRA_MARKER_PHOTO = "DetailsPhotoActivity.MARKER_PHOTO";

    private FrameLayout parentView;
    private ImageView imageViewDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_photo);

        parentView = (FrameLayout) findViewById(R.id.container_details);
        imageViewDetails = (ImageView) findViewById(R.id.image_details);
        MarkerPhoto markerPhoto = getIntent().getParcelableExtra(EXTRA_MARKER_PHOTO);

        Glide.with(this)
                .load(markerPhoto.getUri())
                .asBitmap()
                .error(R.drawable.ic_default_image)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(this)
                .into(imageViewDetails);
    }

    @Override
    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
        onPalette(Palette.from(resource).generate());
        imageViewDetails.setImageBitmap(resource);
        return false;
    }

    public void onPalette(Palette palette){
        if (palette != null){
            parentView.setBackgroundColor(palette.getDarkVibrantColor(Color.BLACK));
        }
    }
}
