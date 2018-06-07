package com.maps.developer.authenticplaces;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageMarkerAdapter extends RecyclerView.Adapter<ImageMarkerAdapter.CustomViewHolder> {

    private static final String TAG = ImageMarkerAdapter.class.getSimpleName();

    private List<MarkerPhoto> markerPhotos;
    private Context context;

    public ImageMarkerAdapter(Context context, List<MarkerPhoto> markerPhotos) {
        this.context = context;
        this.markerPhotos = markerPhotos;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView photoImageView;

        public CustomViewHolder(View itemView) {
            super(itemView);
            photoImageView = (ImageView) itemView.findViewById(R.id.marker_photo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                MarkerPhoto markerPhoto = markerPhotos.get(position);
                Intent intent = new Intent(context, DetailsPhotoActivity.class);
                intent.putExtra(DetailsPhotoActivity.EXTRA_MARKER_PHOTO, markerPhoto);
                context.startActivity(intent);
            }
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.image_item, parent, false);
        return new CustomViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        MarkerPhoto markerPhoto = markerPhotos.get(position);
        ImageView imageView = holder.photoImageView;

        Glide.with(context)
                .load(markerPhoto.getUri())
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return (markerPhotos.size());
    }
}
