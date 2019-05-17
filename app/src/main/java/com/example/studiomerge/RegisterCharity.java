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

import com.example.studiomerge.lib.Constant;
import com.example.studiomerge.lib.Hash;
import com.example.studiomerge.lib.Validator;
import com.example.studiomerge.lib.observable.BooleanObserver;
import com.example.studiomerge.lib.observable.ObservableBoolean;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class RegisterCharity extends AppCompatActivity {
    private static final String TAG = "RegisterCharity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_charity);

        final EditText inputEmail = findViewById(R.id.inputEmail);
        TextView tvOrganisationError = findViewById(R.id.tvOrganisationError);
        TextView tvPasswordHint = findViewById(R.id.tvPasswordHint);
        final TextView tvEmailError = findViewById(R.id.tvEmailError);

        final ObservableBoolean isEmailAvailable = new ObservableBoolean(false);
        /*isEmailAvailable.addObserver(new BooleanObserver() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(arg);
                if ((Boolean) arg) {
                    tvEmailError.setVisibility(View.VISIBLE);
                } else {
                    tvEmailError.setVisibility(View.INVISIBLE);
                }
            }
        });
        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    new Validator().isEmailAvailable(
                            inputEmail.getText().toString(), isEmailAvailable);
                }
            }
        });*/

        /**
         * Visibility attribute doesn't seem to update properly so I
         * force the change here. */
        tvOrganisationError.setVisibility(View.INVISIBLE);
        tvPasswordHint.setVisibility(View.INVISIBLE);
        tvEmailError.setVisibility(View.INVISIBLE);
    }

    /**
     * Create a new account.
     *
     * Check the completion of each required field and display the
     * appropriate hints/error messages. If no errors are found, create
     * a new account and add it to the database.
     *
     * @param v the view that received the onClick event
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void createCharityAccount(View v) {
        EditText inputOrganisation = findViewById(R.id.inputOrganisation);
        EditText inputEmail = findViewById(R.id.inputEmail);
        EditText inputPhone = findViewById(R.id.inputPhone);
        EditText inputPassword = findViewById(R.id.inputPassword);
        TextView tvEmailError = findViewById(R.id.tvEmailError);

        Validator validator = new Validator();
        boolean isValidOrganisation = validator.isNameValid(inputOrganisation);
        boolean isValidEmail = validator.isEmailValid(inputEmail);
        boolean isValidPhone = validator.isPhoneValid(inputPhone);
        boolean isValidPassword = validator.isPasswordValid(inputPassword);

        if (
                isValidOrganisation && isValidEmail && isValidPhone
                        && tvEmailError.getVisibility() == View.INVISIBLE
                        && isValidPassword
        ) {
            Map<String, String> donor = new HashMap<>();
            donor.put("type", "charity");
            donor.put("organisation", inputOrganisation.getText().toString());
            donor.put("email", inputEmail.getText().toString());
            donor.put("phone", inputPhone.getText().toString());
            donor.put("password", new Hash().hash(
                    inputPassword.getText().toString()));

            // Add to the database
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .add(donor)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });

            Intent intent = new Intent(this, RegisterSuccessController.class);
            intent.putExtra(Constant.EMAIL, inputEmail.getText().toString());
            intent.putExtra(Constant.PASSWORD,
                    new Hash().hash(inputPassword.getText().toString()));
            startActivity(intent);
        } else {
            TextView tvPasswordHint = findViewById(R.id.tvPasswordHint);
            TextView tvOrganisationError = findViewById(R.id.tvOrganisationError);

            hideTextViews();
            // Display the appropriate hints/error messages
            if (!isValidOrganisation) tvOrganisationError.setVisibility(View.VISIBLE);
            if (!isValidPassword) tvPasswordHint.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hide the appropriate TextViews.
     *
     * Hide the tvFirstNameError and tvPasswordHint widgets.
     */
    private void hideTextViews() {
        TextView tvOrganisationError = findViewById(R.id.tvOrganisationError);
        tvOrganisationError.setVisibility(View.INVISIBLE);

        TextView tvPasswordHint = findViewById(R.id.tvPasswordHint);
        tvPasswordHint.setVisibility(View.INVISIBLE);
    }

    /**
     * Go to the login screen.
     *
     * @param v the view that received the onClick event
     */
    public void alreadyMemberOnClick(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
