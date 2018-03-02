package com.simcoder.snapchatclone.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simcoder.snapchatclone.R;
import com.simcoder.snapchatclone.RecyclerViewStory.StoryAdapter;
import com.simcoder.snapchatclone.RecyclerViewStory.StoryObject;

import java.util.ArrayList;

/**
 * Created by simco on 1/3/2018.
 */

public class ChatFragment extends Fragment{


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    String uid;

    public static ChatFragment newInstance(){
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat , container, false);

        uid = FirebaseAuth.getInstance().getUid();

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new StoryAdapter(getDataSet(), getContext());
        mRecyclerView.setAdapter(mAdapter);

        Button mRefresh = view.findViewById(R.id.refresh);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
                listenForData();
            }
        });
        return view;
    }

    private void clear() {
        int size = this.results.size();
        this.results.clear();
        mAdapter.notifyItemRangeChanged(0, size);
    }

    private ArrayList<StoryObject> results = new ArrayList<>();
    private ArrayList<StoryObject> getDataSet() {
        listenForData();
        return results;
    }

    private void listenForData(){
        DatabaseReference receivedDb = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("received");
        receivedDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        getUserInfo(snapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserInfo(String chatUid) {
        DatabaseReference chatUserDb = FirebaseDatabase.getInstance().getReference().child("users").child(chatUid);
        chatUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String email = dataSnapshot.child("email").getValue().toString();
                    String uid = dataSnapshot.getRef().getKey();

                    StoryObject obj = new StoryObject(email, uid, "chat");
                    if(!results.contains(obj)){
                        results.add(obj);
                        mAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
