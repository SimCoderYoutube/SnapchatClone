package com.simcoder.snapchatclone.fragment.Other;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.simcoder.snapchatclone.Adapter.FollowAdapter;
import com.simcoder.snapchatclone.Objects.UserObject;
import com.simcoder.snapchatclone.R;

import java.util.ArrayList;

/**
 * Fragment which allows the user to find other user and follow/unfollow them
 */
public class FindUsersFragment extends Fragment implements View.OnClickListener {

    private RecyclerView.Adapter mAdapter;

    private ArrayList<UserObject> results = new ArrayList<>();

    private EditText mInput;

    private View view;

    private Query query;
    private ChildEventListener childEventListener;

    public static FindUsersFragment newInstance() {
        return new FindUsersFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_find_users, container, false);

        initializeObjects();

        return view;
    }

    /**
     * Initialize UI Elemetns
     */
    private void initializeObjects() {
        mInput = view.findViewById(R.id.input);
        ImageButton mBack = view.findViewById(R.id.back);
        ImageButton mClear = view.findViewById(R.id.clear);

        mBack.setOnClickListener(this);
        mClear.setOnClickListener(this);

        //Initializing the recycler view
        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter = new FollowAdapter(results, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        //listens for changes in the inputText, if there is a change then clear the list and fetch the new data
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (childEventListener != null)
                    query.removeEventListener(childEventListener);

                clear();

                if (!mInput.getText().toString().isEmpty())
                    listenForData();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Fetches the data by making a query which tries to find users that contain what the
     * current user typed in the inputText
     */
    private void listenForData() {
        DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("users");
        query = usersDb.orderByChild("name").startAt(mInput.getText().toString()).endAt(mInput.getText().toString() + "\uf8ff");
        childEventListener = query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                UserObject mUser = new UserObject();
                mUser.parseData(dataSnapshot);

                if (!mUser.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    results.add(mUser);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Clears the user list
     */
    private void clear() {
        this.results.clear();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Handle onClick events
     *
     * @param v - view clicked on
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                getActivity().onBackPressed();
                break;
            case R.id.clear:
                mInput.setText("");
                break;
        }
    }
}