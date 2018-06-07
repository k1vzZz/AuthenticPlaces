package com.maps.developer.authenticplaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity{

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private AccountInfo accountInfo;
    private TextView textView;
    private CircleImageView circleImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        GoogleSignInAccount account = getIntent().getParcelableExtra(AccountInfo.RECEIVED_ACCOUNT);
        accountInfo = new AccountInfo(account);
        circleImageView = findViewById(R.id.profile_image);
        circleImageView.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(accountInfo.getPhotoUrl())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(circleImageView);
        textView = findViewById(R.id.textAuth);
        textView.setText(account.getDisplayName());
    }
}
