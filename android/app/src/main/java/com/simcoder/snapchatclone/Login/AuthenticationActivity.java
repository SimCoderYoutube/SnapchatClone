package com.simcoder.snapchatclone.Login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.simcoder.snapchatclone.MainActivity;
import com.simcoder.snapchatclone.R;

/**
 * Every Fragment in the auth ecosystem is controlled through this activity
 * <p>
 * It controls the lifecycle of the application (with the exeption of the Main activities).
 * Does that by replacing fragments, controlling backpresses,...
 */
public class AuthenticationActivity extends AppCompatActivity {

    FragmentManager fm = getSupportFragmentManager();

    MenuFragment menuFragment = new MenuFragment();

    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);


        //Listener for the auth state, called any time the user logs in or logs out
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //If the user is logged in then go to the MainActivity
                if (user != null) {
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        fm.beginTransaction()
                .replace(R.id.container, menuFragment, "StartFragment")
                .addToBackStack(null)
                .commit();
    }

    /**
     * Replaces the current fragment being shown in the container by the DetailsFragment
     */
    public void registrationClick() {
        fm.beginTransaction()
                .replace(R.id.container, new DetailsFragment(), "DetailsFragment")
                .addToBackStack(null)
                .commit();
    }

    /**
     * Replaces the current fragment being shown in the container by the LoginFragment
     */
    public void loginClick() {
        fm.beginTransaction()
                .replace(R.id.container, new LoginFragment(), "DetailsFragment")
                .addToBackStack(null)
                .commit();
    }

    /**
     * Overrides the backPressed function, pops the stack to show the previous shown fragment
     */
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    /**
     * Overrides the onStart function allowing the authStateListener to be started
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuthListener);
    }

    /**
     * Overrides the onStop function allowing the authStateListener to be end
     */
    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(firebaseAuthListener);
    }
}
