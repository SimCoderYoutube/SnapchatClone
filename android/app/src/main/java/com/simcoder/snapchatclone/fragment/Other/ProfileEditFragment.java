package com.simcoder.snapchatclone.fragment.Other;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.simcoder.snapchatclone.Login.LauncherActivity;
import com.simcoder.snapchatclone.MainActivity;
import com.simcoder.snapchatclone.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Fragment responsible for allowing the user to edit the current user's information
 */
public class ProfileEditFragment extends Fragment implements View.OnClickListener {

    private EditText mName;

    private ImageView mProfileImage;

    private DatabaseReference mUserDatabase;

    private String userId = "";
    private String name = "--";
    private String image = "--";

    private Uri resultUri;

    public static ProfileEditFragment newInstance() {
        return new ProfileEditFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initializeObjects(view);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        return view;
    }

    /**
     * Gets user info from the MainActivity mUser object
     */
    private void getUserInfo() {
        name = ((MainActivity) getActivity()).getUser().getName();
        image = ((MainActivity) getActivity()).getUser().getImage();

        if (((MainActivity) getActivity()).getUser().getName() != null)
            mName.setText(((MainActivity) getActivity()).getUser().getName());

        if (((MainActivity) getActivity()).getUser().getImage() != null && getActivity() != null)
            Glide.with(getActivity())
                    .load(image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mProfileImage);
    }

    /**
     * Update the user information.
     * Does some checks to see if the user is valid and only then does it update the user
     */
    private void saveUserInformation() {
        ((MainActivity) getActivity()).showProgressDialog("Saving Data...");

        if (!mName.getText().toString().isEmpty())
            name = mName.getText().toString();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", name);

        if (image != null)
            userInfo.put("image", image);

        mUserDatabase.updateChildren(userInfo);

        if (resultUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_image").child(userId);

            UploadTask uploadTask = filePath.putFile(resultUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ((MainActivity) getActivity()).clearBackStack();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("image", uri.toString());
                            mUserDatabase.updateChildren(newImage);

                            ((MainActivity) getActivity()).clearBackStack();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            ((MainActivity) getActivity()).clearBackStack();
                        }
                    });
                }
            });
        } else {
            ((MainActivity) getActivity()).dismissProgressDialog();
            getActivity().onBackPressed();
        }
    }

    /**
     * Logs the user out and sends the user back to the LauncherActivity
     */
    private void LogOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    /**
     * override the onActivityResult to get the image that the user picked for profile image
     *
     * @param requestCode - code of the intent
     * @param resultCode  - status of the request
     * @param data        - data of the result
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            resultUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
                Glide.with(getActivity())
                        .load(bitmap) // Uri of the picture
                        .apply(RequestOptions.circleCropTransform())
                        .into(mProfileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * handle onClick events
     *
     * @param view - view that was clicked
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                ((MainActivity) getActivity()).clearBackStack();
                break;
            case R.id.confirm:
                saveUserInformation();
                break;
            case R.id.logout:
                LogOut();
                break;
        }
    }

    /**
     * Initialize UI elements
     *
     * @param view - parent view
     */
    private void initializeObjects(View view) {
        mName = view.findViewById(R.id.name);
        mProfileImage = view.findViewById(R.id.profileImage);
        ImageView mBack = view.findViewById(R.id.back);
        ImageView mConfirm = view.findViewById(R.id.confirm);
        Button mLogout = view.findViewById(R.id.logout);
        mBack.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        mLogout.setOnClickListener(this);
    }
}
