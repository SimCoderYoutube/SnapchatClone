package com.simcoder.snapchatclone.fragment.Other;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.simcoder.snapchatclone.MainActivity;
import com.simcoder.snapchatclone.R;

/**
 * Fragment that is displayed in between taking a shot and choosing the receiver(s)
 * <p>
 * It allows the user to see the image before proceeding.
 */
public class DisplayImageFragment extends Fragment implements View.OnClickListener {

    private View view;

    public static DisplayImageFragment newInstance() {
        return new DisplayImageFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_display_capture, container, false);

        initializeObjects();

        return view;
    }

    /**
     * Initialize UI elements
     */
    private void initializeObjects() {
        ImageView mImage = view.findViewById(R.id.image);
        ImageButton mSend = view.findViewById(R.id.send);

        mSend.setOnClickListener(this);

        mImage.setImageBitmap(((MainActivity) getActivity()).getBitmapToSend());
    }

    /**
     * Handle onClick events
     *
     * @param v - clicked view
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send) {
            ((MainActivity) getActivity()).openChooseReceiverFragment();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
