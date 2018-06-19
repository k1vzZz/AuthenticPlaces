package com.maps.developer.authenticplaces.account;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.maps.developer.authenticplaces.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private AccountInfo accountInfo;
    private TextView textView;
    private CircleImageView circleImageView;
    private GoogleSignInClient mGoogleSignInClient;
    private SignOutListener mListener;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        circleImageView = view.findViewById(R.id.profile_image);
        textView = view.findViewById(R.id.textAuth);
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(view.getContext(), gso);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignOutListener) {
            mListener = (SignOutListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SignOutListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        accountInfo = mListener.getAccountInfo();
        circleImageView.setVisibility(View.VISIBLE);
        textView.setText(accountInfo.getAccount().getDisplayName());
        Glide.with(this)
                .load(accountInfo.getPhotoUrl())
                .into(circleImageView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.sign_out).setVisible(true);
        menu.findItem(R.id.option_profile).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out:
                mGoogleSignInClient.signOut();
                mListener.signOutAccount();
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        accountInfo = null;
        mGoogleSignInClient = null;
        mListener = null;
    }

    public interface SignOutListener {
        void signOutAccount();
        AccountInfo getAccountInfo();
    }
}
