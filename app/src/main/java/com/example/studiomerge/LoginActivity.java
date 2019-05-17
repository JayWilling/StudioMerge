package com.example.studiomerge;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.studiomerge.lib.Hash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Observable;
import com.example.studiomerge.lib.observable.*;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN_CONTROLLER";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        /**
         * Visibility attribute doesn't seem to update properly so I
         * force the change here. */
        TextView tvLoginHint = findViewById(R.id.tvLoginHint);
        tvLoginHint.setVisibility(View.INVISIBLE);
    }
    /**
     * Make a login attempt.
     *
     * Search the database for a matching set of credentials and
     * display the appropriate hints/error messages. Advance to the
     * next activity if a match is found.
     *
     * @param v the view that received the onClick event
     */
    public void onLogin(View v) {
        final EditText inputEmail = findViewById(R.id.inputEmail);
        final EditText inputPassword = findViewById(R.id.inputPassword);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ObservableBoolean isMatchFound = new ObservableBoolean(false);
                            isMatchFound.addObserver(new BooleanObserver() {
                                @Override
                                public void update(Observable o, Object arg) {
                                    if ((Boolean) arg) {
                                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // Show login hint
                                        TextView tvLoginHint = findViewById(R.id.tvLoginHint);
                                        tvLoginHint.setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                            mAuth.getCurrentUser().reload();
                            FirebaseUser user = mAuth.getCurrentUser();

                            int count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (
                                        document.get("email").equals(inputEmail.getText().toString())
                                                && document.get("password").equals(new Hash().hash(
                                                inputPassword.getText().toString()))
                                                && user.isEmailVerified()
                                ) {
                                    isMatchFound.setValue(true);
                                }

                                if (++count == task.getResult().size()) isMatchFound.setValue(false);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    /**
     * Go to the registration screen.
     *
     * @param v the view that received the onClick event
     */
    public void onNewUser(View v) {
        Intent intent = new Intent(this, RegisterUser.class);
        startActivity(intent);
    }
}
