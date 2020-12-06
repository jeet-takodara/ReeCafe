package com.parse.starter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class AddWorker extends AppCompatActivity {

    EditText workerNameEditText,workerEmailEditText,workerPhoneEditText,workerPasswordEditText,workerConfirmPasswordEditText;
    Button createButton;
    LinearLayout addWorkerLinearLayout;
    String nm,em,ph,pw;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(!workerPasswordEditText.getText().toString().contentEquals(workerConfirmPasswordEditText.getText().toString()))
                workerConfirmPasswordEditText.setError("Passwords do not match!");
        }

        @Override
        public void afterTextChanged(Editable editable) { }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_worker);

        addWorkerLinearLayout = findViewById(R.id.addWorkerLinearLayout);
        workerNameEditText = findViewById(R.id.workerNameEditText);
        workerEmailEditText = findViewById(R.id.workerEmailEditText);
        workerPhoneEditText = findViewById(R.id.workerPhoneEditText);
        workerPasswordEditText = findViewById(R.id.workerPasswordEditText);
        workerConfirmPasswordEditText = findViewById(R.id.workerConfirmPasswordEditText);
        createButton = findViewById(R.id.workerCreateButton);

        workerConfirmPasswordEditText.addTextChangedListener(textWatcher);

    }

    public void upload(View view) {

        createButton.setClickable(false);

        nm = workerNameEditText.getText().toString();
        ph = workerPhoneEditText.getText().toString();
        em = workerEmailEditText.getText().toString();
        pw = workerPasswordEditText.getText().toString();

        if (nm.contentEquals("") ||
                em.contentEquals("") ||
                ph.contentEquals("") ||
                pw.contentEquals("") ||
                workerConfirmPasswordEditText.getText().toString().contentEquals("")) {

            if(nm.contentEquals(""))
                workerNameEditText.setError("Field Required");
            else if(em.contentEquals(""))
                workerEmailEditText.setError("Field Required");
            else if(ph.contentEquals(""))
                workerPhoneEditText.setError("Field Required");
            else if(pw.contentEquals(""))
                workerPasswordEditText.setError("Field Required");
            else if(workerConfirmPasswordEditText.getText().toString().contentEquals(""))
                workerConfirmPasswordEditText.setError("Field Required");

        }    else {

            if(isNetworkAvailable() && pw.contentEquals(workerConfirmPasswordEditText.getText().toString())) {

                ParseUser user = new ParseUser();
                user.setUsername(em);
                user.setEmail(em);
                user.setPassword(pw);
                user.put("Label","worker");
                user.put("Name",nm.toUpperCase());
                user.put("Number","+91 "+ph);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            Toast.makeText(AddWorker.this, "Account Created Successfully!", Toast.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(addWorkerLinearLayout, e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

            } else {
                if(!isNetworkAvailable()) {
                    Toast.makeText(this, "Internet Connection Not Available!", Toast.LENGTH_SHORT).show();
                }
                if(!pw.contentEquals(workerConfirmPasswordEditText.getText().toString())) {
                    workerConfirmPasswordEditText.setError("Passwords do not match!");
                }
            }

        }

        createButton.setClickable(true);

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}