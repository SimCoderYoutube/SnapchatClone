package com.simcoder.snapchatclone.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simcoder.snapchatclone.Adapter.StoryAdapter;
import com.simcoder.snapchatclone.MainActivity;
import com.simcoder.snapchatclone.Objects.UserObject;
import com.simcoder.snapchatclone.R;

import java.util.ArrayList;

/**
 * Fragment responsible for displaying either the chat or the story history.
 */
public class StoryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    public ArrayList<UserObject> results = new ArrayList<>();

    private String chatOrStory = "";

    private View view;

    private SwipeRefreshLayout mRefresh;

    public static StoryFragment newInstance(String chatOrStory) {
        StoryFragment fragment = new StoryFragment();

        Bundle args = new Bundle();
        args.putString("chatOrStory", chatOrStory);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null)
            view = inflater.inflate(R.layout.fragment_story, container, false);
        else
            container.removeView(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!chatOrStory.isEmpty())
            return;

        if (getArguments() != null) {
            chatOrStory = getArguments().getString("chatOrStory", "story");
        }

        initialize();
    }

    /**
     * Initializes the UI elements
     */
    private void initialize() {
        Boolean started = true;

        TextView mType = view.findViewById(R.id.type);
        ScrollView mScroll = view.findViewById(R.id.scroll);
        mRefresh = view.findViewById(R.id.refresh);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter = new StoryAdapter(results, chatOrStory, this, getContext());
        mRecyclerView.setAdapter(mAdapter);

        getData();

        //changes the color and the title displlayed depending on if it is chat or story specific
        switch (chatOrStory) {
            case "story":
                mScroll.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                mType.setText(R.string.no_stories);
                break;
            case "chat":
                mScroll.setBackgroundColor(getActivity().getResources().getColor(R.color.colorAccent));
                mType.setText(R.string.no_chats);
                break;
        }

        //Refreshes the data on screen
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                mRefresh.setRefreshing(false);
            }
        });


    }

    /**
     * checks if the fragment is chat or story specific and fetches the correspondent data
     */
    public void getData() {
        clear();
        switch (chatOrStory) {
            case "story":
                getStories();
                break;
            case "chat":
                getChats();
                break;
        }
    }

    /**
     * Fetches the current logged in user's stories from the users the user is following
     */
    private void getStories() {
        for (int i = 0; i < ((MainActivity) getActivity()).listFollowing.size(); i++) {
            DatabaseReference followingStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(((MainActivity) getActivity()).listFollowing.get(i));
            followingStoryDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    UserObject mUser = new UserObject();

                    mUser.setId(dataSnapshot.getRef().getKey());

                    if (dataSnapshot.child("name").getValue() != null)
                        mUser.setName(dataSnapshot.child("name").getValue().toString());
                    if (dataSnapshot.child("image").getValue() != null)
                        mUser.setImage(dataSnapshot.child("image").getValue().toString());

                    long timestampBeg = 0;
                    long timestampEnd = 0;
                    for (DataSnapshot storySnapshot : dataSnapshot.child("story").getChildren()) {
                        if (storySnapshot.child("timestampBeg").getValue() != null) {
                            timestampBeg = Long.parseLong(storySnapshot.child("timestampBeg").getValue().toString());
                        }
                        if (storySnapshot.child("timestampEnd").getValue() != null) {
                            timestampEnd = Long.parseLong(storySnapshot.child("timestampEnd").getValue().toString());
                        }
                        long timestampCurrent = System.currentTimeMillis();
                        if (timestampCurrent >= timestampBeg && timestampCurrent <= timestampEnd) {
                            if (!results.contains(mUser)) {
                                results.add(mUser);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }


    /**
     * Fetches the current logged in user's chats
     */
    private void getChats() {
        DatabaseReference receivedDb = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("received");
        receivedDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        getUserInfo(snapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /**
     * Fetches the current logged in user's info
     */
    private void getUserInfo(String chatUid) {
        DatabaseReference chatUserDb = FirebaseDatabase.getInstance().getReference().child("users").child(chatUid);
        chatUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserObject mUser = new UserObject();

                    mUser.setId(dataSnapshot.getRef().getKey());

                    if (dataSnapshot.child("name").getValue() != null)
                        mUser.setName(dataSnapshot.child("name").getValue().toString());
                    if (dataSnapshot.child("image").getValue() != null)
                        mUser.setImage(dataSnapshot.child("image").getValue().toString());

                    if (!results.contains(mUser)) {
                        results.add(mUser);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * overrides onResume to get the data
     */
    @Override
    public void onResume() {
        super.onResume();
        if (chatOrStory.equals("chat"))
            getData();
    }


    /**
     * clears the adapter removing every object from the ArrayList
     */
    private void clear() {
        this.results.clear();
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }
}
