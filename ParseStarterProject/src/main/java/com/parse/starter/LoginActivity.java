package com.parse.starter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText emailEditText,passwordEditText;
    ProgressDialog progressDialog;
    String email,password;
    TextView forgotPassword, createAccount;
    ConstraintLayout loginLayout;
    Button loginButton;
    Intent gotoAdmin,gotoUser,gotoWorker;

    public void checkNewAccountCreated() {
        if(getIntent() != null) {
            String EMAIL;
            EMAIL = getIntent().getStringExtra("email");
            if(EMAIL != null) {
                emailEditText.setText(EMAIL);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        loginLayout = findViewById(R.id.loginLayout);
        forgotPassword = findViewById(R.id.forgotPasswordTextView);
        createAccount = findViewById(R.id.createAccountTextView);

        gotoAdmin = new Intent(getApplicationContext(), AdminActivity.class);
        gotoUser = new Intent(getApplicationContext(), UserMenuActivity.class);
        gotoWorker = new Intent(getApplicationContext(), WorkerActivity.class);

        progressDialog = new ProgressDialog(loginButton.getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please Wait");

        checkNewAccountCreated();

        forgotPassword.setOnClickListener(this);
        createAccount.setOnClickListener(this);

        ParseUser user = ParseUser.getCurrentUser();

        if(user != null) {

            switch(user.get("Label").toString()) {

                case "admin":
                    startActivity(gotoAdmin);
                    finish();
                    break;

                case "user":
                    startActivity(gotoUser);
                    finish();
                    break;

                case "worker":
                    startActivity(gotoWorker);
                    finish();
                    break;
            }

        }

    }

    public void logIn(View view) {

        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        if(email.contentEquals("") || password.contentEquals("")) {
            if(email.contentEquals("")) {
                emailEditText.setError("Email id required!");
            }

            if(password.contentEquals("")){
                passwordEditText.setError("Password is required!");
            }
        } else {
            if(isNetworkAvailable()) {

                progressDialog.show();

                ParseUser.logInInBackground(email, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e == null) {

                            if (user != null) {

                                switch (user.get("Label").toString()) {

                                    case "admin":
                                        progressDialog.dismiss();
                                        startActivity(gotoAdmin);
                                        finish();
                                        break;

                                    case "user":
                                        progressDialog.dismiss();
                                        startActivity(gotoUser);
                                        finish();
                                        break;

                                    case "worker":
                                        progressDialog.dismiss();
                                        startActivity(gotoWorker);
                                        finish();
                                        break;

                                }
                            }

                        } else {
                            progressDialog.dismiss();
                            Snackbar.make(loginLayout, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Internet Connection Not Available!", Toast.LENGTH_LONG).show();
            }
        }

    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {

            case R.id.forgotPasswordTextView:
                emailEditText.setText("");
                passwordEditText.setText("");
                Intent forgotpass = new Intent(getApplicationContext(),ForgotPasswordActivity.class);
                startActivity(forgotpass);
                break;

            case R.id.createAccountTextView:
                emailEditText.setText("");
                passwordEditText.setText("");
                Intent createacc = new Intent(getApplicationContext(),CreateAccountActivity.class);
                startActivity(createacc);
                break;

        }
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
