package com.simcoder.snapchatclone;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simcoder.snapchatclone.Objects.UserObject;
import com.simcoder.snapchatclone.fragment.NewSnap.ChooseReceiverFragment;
import com.simcoder.snapchatclone.fragment.NewSnap.DisplaySnapFragment;
import com.simcoder.snapchatclone.fragment.Other.DisplayImageFragment;
import com.simcoder.snapchatclone.fragment.Other.FindUsersFragment;
import com.simcoder.snapchatclone.fragment.Other.ProfileEditFragment;
import com.simcoder.snapchatclone.fragment.main.CameraViewFragment;
import com.simcoder.snapchatclone.fragment.main.StoryFragment;

import java.util.ArrayList;

/**
 * Every Fragment is controlled through this activity
 * <p>
 * It controls the lifecycle of the application (with the exeption of the auth activities).
 * Does that by replacing fragments, controlling backpresses,...
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FragmentManager fm = getSupportFragmentManager();

    FragmentPagerAdapter adapterViewPager;

    Bitmap bitmap = null;

    UserObject mUser = new UserObject();
    public ArrayList<String> listFollowing = new ArrayList<>();

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUserFollowing();
        getUserInfo();

        initializeButtons();
        initializeViewPager();
    }

    /**
     * Initializes bottom buttons
     */
    void initializeButtons() {
        ImageView mChat = findViewById(R.id.chat);
        ImageView mCapture = findViewById(R.id.capture);
        ImageView mStory = findViewById(R.id.story);

        mChat.setOnClickListener(this);
        mCapture.setOnClickListener(this);
        mStory.setOnClickListener(this);
    }

    /**
     * Initializes the View Pager where fragments will live
     */
    void initializeViewPager() {
        viewPager = findViewById(R.id.viewPager);

        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        viewPager.setCurrentItem(1);
    }

    /**
     * Gets currently logged in user info from the database
     */
    private void getUserInfo() {
        mUser.setId(FirebaseAuth.getInstance().getUid());
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                            mUser.parseData(dataSnapshot);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Gets array of users that the current user is following.
     */
    private void getUserFollowing() {
        listFollowing.clear();
        DatabaseReference userFollowingDB = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("following");
        userFollowingDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    String uid = dataSnapshot.getRef().getKey();
                    if (uid != null && !listFollowing.contains(uid)) {
                        listFollowing.add(uid);
                        if (((StoryFragment) ((MyPagerAdapter) adapterViewPager).getStoryFragment()) == null)
                            return;
                        ((StoryFragment) ((MyPagerAdapter) adapterViewPager).getStoryFragment()).getData();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String uid = dataSnapshot.getRef().getKey();
                    if (uid != null) {
                        listFollowing.remove(uid);
                        if (((StoryFragment) ((MyPagerAdapter) adapterViewPager).getStoryFragment()) == null)
                            return;
                        ((StoryFragment) ((MyPagerAdapter) adapterViewPager).getStoryFragment()).getData();
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Gets the current logged in user.
     *
     * @return the user.
     */
    public UserObject getUser() {
        return mUser;
    }

    ProgressDialog mDialog;

    /**
     * Displays a progress Dialog with a specific message.
     * <p>
     * It is a non cancelable spinner type dialog.
     *
     * @param message - message to display
     */
    public void showProgressDialog(String message) {
        mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage(message);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    /**
     * Dismisses the current dialog being displayed
     */
    public void dismissProgressDialog() {
        //if mDialog is null then do nothing, avoiding a crash
        if (mDialog != null)
            mDialog.dismiss();
    }

    /**
     * Sets the Bitmap of the image to send.
     */
    public void setBitmapToSend(Bitmap bitmapToSend) {
        this.bitmap = bitmapToSend;
    }

    /**
     * Gets the bitmap of the image to send.
     *
     * @return the bitmap.
     */
    public Bitmap getBitmapToSend() {
        return bitmap;
    }

    /**
     * Replaces the current fragment being shown in the container by the DisplayImageFragment
     */
    public void openDisplayImageFragment() {
        fm.beginTransaction()
                .replace(R.id.container, DisplayImageFragment.newInstance(), "DisplayImageFragment")
                .addToBackStack(null)
                .commit();
    }

    /**
     * Replaces the current fragment being shown in the container by the ChooseReceiverFragment
     */
    public void openChooseReceiverFragment() {
        fm.beginTransaction()
                .replace(R.id.container, ChooseReceiverFragment.newInstance(), "ChooseReceiverFragment")
                .addToBackStack(null)
                .commit();
    }

    /**
     * Replaces the current fragment being shown in the container by the ProfileEditFragment
     */
    public void openProfileEditFragment() {
        fm.beginTransaction()
                .replace(R.id.container, ProfileEditFragment.newInstance(), "ProfileEditFragment")
                .addToBackStack(null)
                .commit();
    }

    /**
     * Replaces the current fragment being shown in the container by the FindUsersFragment
     */
    public void openFindUsersFragment() {
        fm.beginTransaction()
                .replace(R.id.container, FindUsersFragment.newInstance(), "FindUsersFragment")
                .addToBackStack(null)
                .commit();
    }

    /**
     * Replaces the current fragment being shown in the container by the DisplaySnapFragment
     */
    public void openDisplaySnapFragment(String userId, String image, String name, String chatOrStory) {
        fm.beginTransaction()
                .replace(R.id.container, DisplaySnapFragment.newInstance(userId, image, name, chatOrStory), "DisplaySnapFragment")
                .addToBackStack(null)
                .commit();
    }

    /**
     * Clears the backstack, calling onBackPressed so the previous fragment is shown
     */
    public void clearBackStack() {
        dismissProgressDialog();
        while (fm.getBackStackEntryCount() > 0)
            onBackPressed();
    }

    /**
     * Handles onClick events
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat:
                if (viewPager.getCurrentItem() != 0)
                    viewPager.setCurrentItem(0);
                break;
            case R.id.capture:
                if (viewPager.getCurrentItem() == 1)
                    ((CameraViewFragment) ((MyPagerAdapter) adapterViewPager).getCameraFragment()).captureImage();
                else
                    viewPager.setCurrentItem(1);
                break;
            case R.id.story:
                if (viewPager.getCurrentItem() != 2)
                    viewPager.setCurrentItem(2);
                break;
        }
    }


    /**
     * This is the adapter for the viewPager. Controlling the position of each fragment
     */
    public static class MyPagerAdapter extends FragmentPagerAdapter {

        Fragment storyFragment, cameraFragment, chatFragment;

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Gets the grafment for a specific index
         *
         * @param position - index to be fetched
         * @return the Fragment in the index
         */
        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (chatFragment == null)
                        chatFragment = StoryFragment.newInstance("chat");
                    return chatFragment;
                case 1:
                    if (cameraFragment == null)
                        cameraFragment = CameraViewFragment.newInstance();
                    return cameraFragment;
                case 2:
                    if (storyFragment == null)
                        storyFragment = StoryFragment.newInstance("story");
                    return storyFragment;
            }
            return null;
        }

        /**
         * Total amount of fragments in the frontpage
         *
         * @return the amount of fragments
         */
        @Override
        public int getCount() {
            return 3;
        }

        /**
         * Gets the Camera Fragment.
         *
         * @return the Camera Fragment.
         */
        Fragment getCameraFragment() {
            return cameraFragment;
        }

        /**
         * Gets the Story Fragment.
         *
         * @return the Story Fragment.
         */
        Fragment getStoryFragment() {
            return storyFragment;
        }
    }
}
