package com.simcoder.snapchatclone.Login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.simcoder.snapchatclone.R;

import java.util.HashMap;
import java.util.Map;


/**
 * Fragment responsible for registering a new user
 */
public class DetailsFragment extends Fragment implements View.OnClickListener {

    private EditText mName;
    private EditText mEmail;
    private EditText mPassword;

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_registration_details, container, false);
        else
            container.removeView(view);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeObjects();
    }


    /**
     * Called any time the user clicks the register button.
     * Does some checks to see if the user is valid and only then does it create the user
     */
    private void register() {
        if (mName.getText().length() == 0) {
            mName.setError("please fill this field");
            return;
        }
        if (mEmail.getText().length() == 0) {
            mEmail.setError("please fill this field");
            return;
        }
        if (mPassword.getText().length() == 0) {
            mPassword.setError("please fill this field");
            return;
        }
        if (mPassword.getText().length() < 6) {
            mPassword.setError("password must have at least 6 characters");
            return;
        }


        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();
        final String name = mName.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Snackbar.make(view.findViewById(R.id.layout), "sign up error", Snackbar.LENGTH_SHORT).show();
                } else {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    //Saves the user's info in the database
                    Map<String, Object> mNewUserMap = new HashMap<>();
                    mNewUserMap.put("email", email);
                    mNewUserMap.put("name", name);

                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(mNewUserMap);
                }
            }
        });

    }

    /**
     * Initializes the UI elements
     */
    private void initializeObjects() {
        mName = view.findViewById(R.id.name);
        mEmail = view.findViewById(R.id.email);
        mPassword = view.findViewById(R.id.password);
        Button mRegister = view.findViewById(R.id.register);

        mRegister.setOnClickListener(this);
    }

    /**
     * Handles onClick events
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.register) {
            register();
        }
    }
}