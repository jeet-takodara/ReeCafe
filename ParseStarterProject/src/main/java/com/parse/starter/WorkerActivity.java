package com.parse.starter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class WorkerActivity extends AppCompatActivity implements View.OnClickListener {

    TextView workerNameTextView;
    Button scanButton;
    String name,contents;
    Intent gotoOrders;
    static List<String> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        orders = new ArrayList<>();

        workerNameTextView = findViewById(R.id.workerNameTextView);
        scanButton = findViewById(R.id.scanButton);
        gotoOrders = new Intent(this, WorkerOrderActivity.class);

        scanButton.setOnClickListener(this);

        name = ParseUser.getCurrentUser().getString("Name");

        workerNameTextView.setText(String.format("Hello :)\n\n%s", name));

    }

    @Override
    public void onClick(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            } else {
                startScan();
            }
        }
    }

    public void startScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setPrompt("Place the QRCode inside the rectangle");
        integrator.initiateScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled!", Toast.LENGTH_SHORT).show();
            } else {
                contents = decrypt(result.getContents());

                //Check if qr code is used or not
                checkOrder(contents);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String decrypt(String encrypted) {

        try {

            IvParameterSpec iv = new IvParameterSpec(QRCodeActivity.initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(QRCodeActivity.key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted.getBytes()));

            return new String(original);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.worker_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.workerLogout:
                logout();
                break;

            case R.id.order_list:
                startActivity(gotoOrders);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        if(isNetworkAvailable()) {
            ParseUser.logOut();
            Intent backtoLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(backtoLogin);
            finish();
        } else {
            Toast.makeText(this, "Internet Connection Not Available!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void checkOrder(String order) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("TransactionDetails");

        query.getInBackground(""+order,new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null) {

                    if(object.getString("status").contentEquals("complete")){

                        new AlertDialog.Builder(WorkerActivity.this).
                                setTitle("Order Already Completed").
                                setMessage("Person Name: "+object.getString("name")).
                                setIcon(android.R.drawable.ic_dialog_alert).
                                setPositiveButton("OK",null).show();

                    } else if(object.getString("status").contentEquals("inProgress")) {

                        Toast.makeText(WorkerActivity.this, "Order already in progress", Toast.LENGTH_SHORT).show();

                    } else {
                        //orders.add(object.getString("itemList")+"@"+object.getString("name")+"@"+object.getObjectId());
                        addToOrderDatabase(object.getString("itemList"),object.getString("name"), object.getObjectId());
                        Toast.makeText(WorkerActivity.this, object.getString("itemList"), Toast.LENGTH_LONG).show();

                        try {
                            object.put("scannedBy",ParseUser.getCurrentUser().getString("Name"));
                            object.put("status","inProgress");
                            object.save();
                        } catch (Exception ex) {
                            Toast.makeText(WorkerActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            ex.printStackTrace();
                        }

                        //Intent gotoOrderActivity = new Intent(getApplicationContext(),WorkerOrderActivity.class);
                        //startActivity(gotoOrderActivity);
                    }

                } else {
                    Toast.makeText(WorkerActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void addToOrderDatabase (String list, String name, String id) {

        try {

            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("Orders", MODE_PRIVATE, null);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Details (id varchar, name varchar, list varchar)");


            SQLiteStatement statement = sqLiteDatabase.compileStatement("INSERT INTO Details (id,name,list) VALUES (?,?,?)");
            statement.bindString(1,id);
            statement.bindString(2,name);
            statement.bindString(3,list);

            statement.executeInsert();

        } catch(Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
}