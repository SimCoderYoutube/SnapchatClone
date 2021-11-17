package com.simcoder.snapchatclone.fragment.NewSnap;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simcoder.snapchatclone.R;

import java.util.ArrayList;


/**
 * Fragment that allows viewing of a post or a sequence of posts
 *
 * each post is on a timer and will end when the timer reaches the end
 * or if the user taps the screen
 */
public class DisplaySnapFragment extends Fragment {
    private int TIME_TO_DISPLAY_SNAP = 10 * 1000; //milliseconds that the snap is displayed

    private String userId;
    private String currentUid;

    private ImageView mImage;
    private ImageView mProfile;

    private TextView mName;

    private boolean started = false;

    private View view;

    public static DisplaySnapFragment newInstance(String userId, String image, String name, String chatOrStory) {
        DisplaySnapFragment fragment = new DisplaySnapFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        args.putString("image", image);
        args.putString("name", name);
        args.putString("chatOrStory", chatOrStory);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_display_snap, container, false);

        initializeObjects();

        return view;
    }

    /**
     * initialize UI elements
     */
    private void initializeObjects() {
        currentUid = FirebaseAuth.getInstance().getUid();

        userId = getArguments().getString("userId", "");
        String image = getArguments().getString("image", "");
        String name = getArguments().getString("name", "");
        String chatOrStory = getArguments().getString("chatOrStory", "chat");

        mProfile = view.findViewById(R.id.profile);
        mName = view.findViewById(R.id.name);

        mName.setText(name);
        if (image != null)
            Glide.with(getActivity())
                    .load(image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mProfile);


        mImage = view.findViewById(R.id.image);

        switch (chatOrStory) {
            case "chat":
                listenForChat();
                break;
            case "story":
                listenForStory();
                break;
        }
    }

    private ArrayList<String> imageUrlList = new ArrayList<>();

    /**
     * Fetches for chats in the database for the user that the current user clicked on
     */
    private void listenForChat() {
        final DatabaseReference chatDb = FirebaseDatabase.getInstance().getReference().child("users").child(currentUid).child("received").child(userId);
        chatDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageUrl = "";
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {

                    if (chatSnapshot.child("imageUrl").getValue() != null) {
                        imageUrl = chatSnapshot.child("imageUrl").getValue().toString();
                    }
                    imageUrlList.add(imageUrl);
                    if (!started) {
                        started = true;
                        initializeDisplay();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Fetches for story in the database for the user that the current user clicked on
     */
    private void listenForStory() {
        DatabaseReference followingStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        followingStoryDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageUrl = "";
                long timestampBeg = 0;
                long timestampEnd = 0;
                for (DataSnapshot storySnapshot : dataSnapshot.child("story").getChildren()) {
                    if (storySnapshot.child("timestampBeg").getValue() != null) {
                        timestampBeg = Long.parseLong(storySnapshot.child("timestampBeg").getValue().toString());
                    }
                    if (storySnapshot.child("timestampEnd").getValue() != null) {
                        timestampEnd = Long.parseLong(storySnapshot.child("timestampEnd").getValue().toString());
                    }
                    if (storySnapshot.child("imageUrl").getValue() != null) {
                        imageUrl = storySnapshot.child("imageUrl").getValue().toString();
                    }
                    long timestampCurrent = System.currentTimeMillis();
                    if (timestampCurrent >= timestampBeg && timestampCurrent <= timestampEnd) {
                        imageUrlList.add(imageUrl);
                        if (!started) {
                            started = true;
                            initializeDisplay();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private int imageIterator = 0;

    Handler h = new Handler();

    /**
     * Handles screen taps which will force an image skip
     */
    private void initializeDisplay() {
        mProfile.setVisibility(View.VISIBLE);
        mName.setVisibility(View.VISIBLE);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage();
            }
        });

        changeImage();
    }


    /**
     * handles time that a image is shown before getting skipped
     */
    private void changeImage() {
        if (h != null)
            h.removeCallbacksAndMessages(null);

        if (imageIterator == imageUrlList.size()) {
            if (getActivity() == null)
                return;
            getActivity().onBackPressed();
            return;
        }

        if (getActivity() == null)
            return;
        Glide.with(getActivity()).load(imageUrlList.get(imageIterator)).into(mImage);
        imageIterator++;


        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeImage();
            }
        }, TIME_TO_DISPLAY_SNAP);
    }
}