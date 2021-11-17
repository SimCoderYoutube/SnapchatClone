package com.simcoder.snapchatclone.Login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.simcoder.snapchatclone.R;


/**
 * Fragment responsible for Logging in a new user
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private TextInputEditText mEmail;
    private TextInputEditText mPassword;

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_login, container, false);
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
     * Sends an email to the user in order to start the reseting of the password
     */
    private void forgotPassword() {
        if (mEmail.getText().toString().trim().length() > 0)
            FirebaseAuth.getInstance().sendPasswordResetEmail(mEmail.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make(view.findViewById(R.id.layout), "Email Sent", Snackbar.LENGTH_LONG).show();
                            } else
                                Snackbar.make(view.findViewById(R.id.layout), "Something went wrong", Snackbar.LENGTH_LONG).show();
                        }
                    });
    }


    /**
     * Called any time the user clicks the login button.
     * <p>
     * Does some checks to see if the user is valid and only then does it login the user
     */
    private void login() {

        if (mPassword.getText().length() == 0) {
            mPassword.setError("please fill this field");
            return;
        }
        if (mPassword.getText().length() < 6) {
            mPassword.setError("password must have at least 6 characters");
            return;
        }
        if (mEmail.getText().length() == 0) {
            mEmail.setError("please fill this field");
            return;
        }

        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Snackbar.make(view.findViewById(R.id.layout), "sign in error", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * Handles onClick events
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forgotButton:
                forgotPassword();
                break;
            case R.id.login:
                login();
                break;
        }
    }

    /**
     * Initializes the UI elements
     */
    private void initializeObjects() {
        mEmail = view.findViewById(R.id.email);
        mPassword = view.findViewById(R.id.password);
        TextView mForgotButton = view.findViewById(R.id.forgotButton);
        Button mLogin = view.findViewById(R.id.login);

        mForgotButton.setOnClickListener(this);
        mLogin.setOnClickListener(this);

    }
}