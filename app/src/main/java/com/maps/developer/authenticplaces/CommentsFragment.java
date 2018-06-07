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

import java.util.List;

public class CommentsFragment extends Fragment {

    private RecyclerView recyclerViewComments;
    private List<CardContent> cardContents;

    private OnFragmentInteractionListener mListener;

    public CommentsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardContents = CardContent.getTestComments();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        recyclerViewComments = (RecyclerView) view.findViewById(R.id.rv_comments);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerViewComments.setHasFixedSize(true);
        recyclerViewComments.setLayoutManager(linearLayoutManager);
        RecyclerView.Adapter adapter = new CommentsMarkerAdapter(view.getContext(), cardContents);
        recyclerViewComments.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onDetachFragment();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onDetachFragment();
    }
}
