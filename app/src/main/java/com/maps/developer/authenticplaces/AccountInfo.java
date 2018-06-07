package com.maps.developer.authenticplaces;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class AccountInfo {

    private static final String TAG = AccountInfo.class.getSimpleName();

    public static final String RECEIVED_ACCOUNT = "RECEIVED_ACCOUNT";
    public static final int REQUEST_SIGN = 50;

    private GoogleSignInAccount account;

    public AccountInfo(GoogleSignInAccount account) {
        this.account = account;
    }

    public void setAccount(GoogleSignInAccount account) {
        this.account = account;
    }

    public GoogleSignInAccount getAccount() {
        return account;
    }

    public Uri getPhotoUrl(){
        return account.getPhotoUrl();
    }
}
