package com.parse.starter;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class AddItemActivity extends AppCompatActivity {

    EditText itemNameEditText, itemPriceEditText;
    AdminActivity a;
    ImageView itemImageView;
    ConstraintLayout addItemLayout;
    Drawable drawable;
    ProgressDialog progressBar;
    Bitmap bitmap;
    byte[] byteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addItemLayout = findViewById(R.id.addItemLayout);
        itemNameEditText = findViewById(R.id.itemNameEditText);
        itemPriceEditText = findViewById(R.id.itemPriceEditText);
        itemImageView = findViewById(R.id.foodImageView);
        a = new AdminActivity();
        drawable = getDrawable(R.drawable.dinner);

        progressBar = new ProgressDialog(itemImageView.getContext());
        progressBar.setMessage("Adding...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        itemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        getPhoto();
                    }
                }
            }
        });

    }

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null) {
            Snackbar.make(addItemLayout,"No Image Selected",Snackbar.LENGTH_SHORT).show();
        }
        else {
            Uri selectedImage = data.getData();

            if (requestCode == 1 && resultCode == RESULT_OK) {

                try {

                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50 , stream);
                    itemImageView.setImageBitmap(bitmap);

                    byteArray = stream.toByteArray();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public void addItem(View view) {

        if(itemNameEditText.getText().toString().contentEquals("") ||
            itemPriceEditText.getText().toString().contentEquals("") ||
            itemImageView.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.dinner).getConstantState())) {

            if(itemNameEditText.getText().toString().contentEquals(""))
                itemNameEditText.setError("Name Required!");

            if(itemPriceEditText.getText().toString().contentEquals(""))
                itemPriceEditText.setError("Price Required!");

            if(itemImageView.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.dinner).getConstantState()))
                Snackbar.make(addItemLayout,"Please select an Image!",Snackbar.LENGTH_SHORT).show();
        } else {

            if(isNetworkAvailable()) {

                progressBar.show();

                ParseFile file = new ParseFile(itemNameEditText.getText().toString() + ".jpeg", byteArray);

                ParseObject object = new ParseObject("ItemDetails");

                object.put("image", file);
                object.put("name", itemNameEditText.getText().toString());
                object.put("price", Integer.valueOf(itemPriceEditText.getText().toString()));

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            progressBar.dismiss();
                            itemPriceEditText.setText("");
                            itemNameEditText.setText("");
                            itemImageView.setImageDrawable(drawable);
                            AdminActivity.list.add(new ItemData(itemNameEditText.getText().toString()));
                            Toast.makeText(AddItemActivity.this, "Item Added Successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            progressBar.dismiss();
                            Snackbar.make(addItemLayout, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            else {
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
