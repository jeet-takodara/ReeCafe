package com.parse.starter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailEditText;
    ConstraintLayout forgotPasswordConstraintLayout;
    Button sendButton;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_forgot_password);

        forgotPasswordConstraintLayout = findViewById(R.id.forgotPasswordConstraintLayout);
        emailEditText = findViewById(R.id.emailEditText);
        sendButton = findViewById(R.id.sendButton);

    }

    public void forgotPassword(View view) {

        email = emailEditText.getText().toString();

        if(email.contentEquals("")) {
            emailEditText.setError("Email required!");
        } else {

            if(isNetworkAvailable()) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("email", email);

                query.getFirstInBackground(new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser object, ParseException e) {
                        if (object == null) {

                            Toast.makeText(ForgotPasswordActivity.this, "No user found for this email address!", Toast.LENGTH_SHORT).show();

                        } else {

                            ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Snackbar.make(forgotPasswordConstraintLayout, "Password reset link sent!", Snackbar.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Internet Connection Not Available!", Toast.LENGTH_LONG).show();
            }

        }

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
