package com.maps.developer.authenticplaces;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.maps.developer.authenticplaces.account.AccountInfo;
import com.maps.developer.authenticplaces.content.CardContent;
import com.maps.developer.authenticplaces.content.CommentsMarkerAdapter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CommentsFragment extends Fragment implements View.OnClickListener {

//    private RecyclerView recyclerViewComments;
    private AccountInfo accountInfo;
    private boolean additionMarker = false;
    private List<CardContent> cardContents;
    private List<CardContent> addedComments;
    private RecyclerView.Adapter adapter;
    private EditText editTextComment;

    private OnFragmentInteractionListener mListener;

    public CommentsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        ImageButton buttonSend = (ImageButton) view.findViewById(R.id.btn_send);
        buttonSend.setOnClickListener(this);
        editTextComment = (EditText) view.findViewById(R.id.edit_comment);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SignOutListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        accountInfo = mListener.getAccountInfo();
        cardContents = mListener.getCardContentList();
        additionMarker = mListener.isAdditionMarker();
        addedComments = null;
        RecyclerView recyclerViewComments = (RecyclerView) getView().findViewById(R.id.rv_comments);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewComments.setHasFixedSize(true);
        recyclerViewComments.setLayoutManager(linearLayoutManager);
        adapter = new CommentsMarkerAdapter(getContext(), cardContents);
        recyclerViewComments.setAdapter(adapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (additionMarker) {
            mListener.onDetachFragment(addedComments);
        }
        accountInfo = null;
        cardContents = null;
        addedComments = null;
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send:
                if (accountInfo != null && accountInfo.getAccount() != null) {
                    addComment();
                } else {
                    mListener.authorization();
                }
                break;
        }
    }

    public void addComment(){
        String comment = editTextComment.getText().toString();
        if (comment.equals("")){
            return;
        }
        CardContent cardContent = new CardContent(accountInfo.getEmail(), comment,
                new Timestamp(System.currentTimeMillis()), accountInfo.getPhotoUrl());
        cardContents.add(cardContent);
        if (additionMarker) {
            if (addedComments == null){
                addedComments = new ArrayList<>();
            }
            addedComments.add(cardContent);
        } else {
            mListener.sendComment(cardContent);
        }
        editTextComment.setText("");
        adapter.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        List<CardContent> getCardContentList();
        boolean isAdditionMarker();
        AccountInfo getAccountInfo();
        void onDetachFragment(List<CardContent> addedComments);
        void sendComment(CardContent cardContent);
        void authorization();
    }
}
