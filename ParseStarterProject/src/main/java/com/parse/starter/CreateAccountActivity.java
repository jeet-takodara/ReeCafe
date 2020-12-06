package com.parse.starter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class CreateAccountActivity extends AppCompatActivity {

    ConstraintLayout createAccountConstraintLayout;
    EditText firstNameEditText,lastNameEditText,emailEditText,passwordEditText,confirmPasswordEditText;
    Button createButton;
    String fn,ln,email,pwd;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!passwordEditText.getText().toString().contentEquals(confirmPasswordEditText.getText().toString()))
                confirmPasswordEditText.setError("Passwords do not match!");
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_create_account);

        createAccountConstraintLayout = findViewById(R.id.createAccountConstraintLayout);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        createButton = findViewById(R.id.createButton);

        confirmPasswordEditText.addTextChangedListener(textWatcher);

    }

    public void signUp(View view) {

        createButton.setClickable(false);

        fn = firstNameEditText.getText().toString();
        ln = lastNameEditText.getText().toString();
        email = emailEditText.getText().toString();
        pwd = passwordEditText.getText().toString();

        if(fn.contentEquals("") || ln.contentEquals("") || email.contentEquals("") || pwd.contentEquals("") || confirmPasswordEditText.getText().toString().contentEquals("")) {

            if(fn.contentEquals(""))
                firstNameEditText.setError("Field required!");

            if(ln.contentEquals(""))
                lastNameEditText.setError("Field required!");

            if(email.contentEquals(""))
                emailEditText.setError("Field required!");

            if(pwd.contentEquals(""))
                passwordEditText.setError("Field required!");

            if(confirmPasswordEditText.getText().toString().contentEquals(""))
                confirmPasswordEditText.setError("Field required!");

        }

        else {

            if(isNetworkAvailable() && pwd.contentEquals(confirmPasswordEditText.getText().toString())) {

                ParseUser user = new ParseUser();
                user.setUsername(email);
                user.setEmail(email);
                user.setPassword(pwd);
                user.put("Label", "user");
                user.put("Name", fn.toUpperCase() + " " + ln.toUpperCase());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(CreateAccountActivity.this, "Account Verification link sent to your email!", Toast.LENGTH_SHORT).show();
                            Intent backToLogin = new Intent(getApplicationContext(), LoginActivity.class);
                            backToLogin.putExtra("email", email);
                            startActivity(backToLogin);
                        } else {
                            Snackbar.make(createAccountConstraintLayout, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                if(!isNetworkAvailable())
                    Toast.makeText(this, "Internet Connection Not Available!", Toast.LENGTH_SHORT).show();
                if(!pwd.contentEquals(confirmPasswordEditText.getText().toString())){
                    confirmPasswordEditText.setError("Passwords do not match!");
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
