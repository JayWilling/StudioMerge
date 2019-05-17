package com.example.studiomerge.lib;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;

import com.example.studiomerge.lib.observable.ObservableBoolean;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Validator {
    private static final String TAG = "VALIDATOR";

    public Validator() {}

    /**
     * Validate that a field contains a proper name.
     *
     * A name must be at least one character long, contain no digits
     * and cannot begin with a space.
     *
     * @param field the field to be validated
     * @return      true if the first name meets the requirements; false
     *              otherwise
     */
    public boolean isNameValid(EditText field) {
        String name = field.getText().toString();
        return (
                name.length() > 0 && name.matches("[^0-9]+")
                        && !Character.isWhitespace(name.charAt(0))
        );
    }

    /**
     * Validate that a field contains a proper email address.
     *
     * An email address cannot have a space and must include the @
     * symbol.
     *
     * @param field the field to be validated
     * @return      true if the email address meets the requirements;
     *              false otherwise
     */
    public boolean isEmailValid(EditText field) {
        String email = field.getText().toString();
        return (!email.matches("[ ]+") && email.matches(".*[@].*"));
    }

    /**
     * Check that there is no conflicting user with the same email.
     *
     * Check that there is no conflicting user then update the
     * observableBoolean. The value will be set to true if there is no
     * conflicting account; false otherwise.
     *
     * @param email             email to check for
     * @param observableBoolean instance of ObservableBoolean
     */
    public void isEmailAvailable(final String email, final ObservableBoolean observableBoolean) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            boolean isMatched = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("email").equals(email)) {
                                    observableBoolean.setValue(true);
                                    isMatched = true;
                                }

                                if (++count == task.getResult().size() && !isMatched) {
                                    observableBoolean.setValue(false);
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    /**
     * Validate that a field contains a proper phone number.
     *
     * A phone number must be exactly 10 digits long (mobile phone
     * numbers only).
     *
     * @param field the field to be validated
     * @return      true if the email address meets the requirements;
     *              false otherwise
     */
    public boolean isPhoneValid(EditText field) {
        String phone = field.getText().toString();
        return (
                phone.isEmpty()
                        || (phone.length() == 10 && phone.matches("[0-9]+"))
        );
    }

    /**
     * Validate that a field contains a proper password.
     *
     * A password must be between 6-24 characters long and contain at
     * least one lowercase and uppercase character, one number and one
     * special character.
     *
     * @param field the field to be validated
     * @return      true if the email address meets the requirements;
     *              false otherwise
     */
    public boolean isPasswordValid(EditText field) {
        return (
                field.getText().toString().length() >= 6
                        && field.getText().toString().length() <= 24
                        && field.getText().toString().matches(".*[a-z].*")
                        && field.getText().toString().matches(".*[A-Z].*")
                        && field.getText().toString().matches(".*[0-9].*")
                        && field.getText().toString().matches(".*[^a-zA-Z0-9 ].*")
        );
    }
}
