package com.maps.developer.authenticplaces;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsMarkerAdapter extends RecyclerView.Adapter<CommentsMarkerAdapter.CardViewHolder> {

    private List<CardContent> cardContentList;
    private Context context;

    CommentsMarkerAdapter(Context context, List<CardContent> cardContentList){
        this.context = context;
        this.cardContentList = cardContentList;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder{
        CircleImageView accountImage;
        TextView accountLogin;
        TextView comment;
        TextView commentTime;

        CardViewHolder(View cardView){
            super(cardView);
            accountImage = (CircleImageView) cardView.findViewById(R.id.account_image);
            accountLogin = (TextView) cardView.findViewById(R.id.account_login);
            comment = (TextView) cardView.findViewById(R.id.comment);
            commentTime = (TextView) cardView.findViewById(R.id.comment_time);
        }
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_content, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardContent cardContent = cardContentList.get(position);
        Glide.with(context)
                .load(cardContent.getImageUri())
                .asBitmap()
                .error(R.drawable.ic_default_image)
                .into(holder.accountImage);
        holder.accountLogin.setText(cardContent.getAuthor());
        holder.comment.setText(cardContent.getContent());
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a yyyy/MM/dd", Locale.getDefault());
        holder.commentTime.setText(dateFormat.format(cardContent.getTime()));
    }

    @Override
    public int getItemCount() {
        return cardContentList.size();
    }
}
