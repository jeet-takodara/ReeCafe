package com.parse.starter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    RecyclerView cartRecyclerView;
    List<String> cartItems;
    CartAdapter adapter;
    static TextView totalAmountTextView;
    Button payButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        payButton = findViewById(R.id.payButton);

        cartItems = new ArrayList<>();

        setRecyclerView();

    }

    private void setRecyclerView() {

        for(Map.Entry<String,Integer> e : UserAdapter.cart.entrySet())
            cartItems.add(e.getKey());

        adapter = new CartAdapter(cartItems,getApplicationContext());
        cartRecyclerView.setAdapter(adapter);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(adapter.getItemCount() == 0) {
            Toast.makeText(this, "Your Cart is empty :(", Toast.LENGTH_SHORT).show();
        }

    }

    public void pay(View view) {

        if(CartAdapter.cost>0) {
            Uri uri = Uri.parse("upi://pay").buildUpon()
                    .appendQueryParameter("pa", "parthtakodara1996@oksbi")
                    .appendQueryParameter("pn", "MVJ College Of Engineering")
                    .appendQueryParameter("tn", "Order for " + "MVJ College Of Engineering")
                    .appendQueryParameter("am", String.valueOf(CartAdapter.cost))
                    .appendQueryParameter("cu", "INR")
                    .build();

            Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
            upiPayIntent.setData(uri);

            Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

            if (null != chooser.resolveActivity(getPackageManager())) {
                startActivityForResult(chooser, 0);
            } else {
                Toast.makeText(this, "No UPI app found!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please add something to your cart :)", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if ((resultCode == RESULT_OK) || (resultCode == 11)) {
                if (data != null) {
                    String trxt = data.getStringExtra("response");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add(trxt);
                    upiPaymentDataOperation(dataList);
                }
                else {
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
            }
            else {
                Toast.makeText(this, "Payment cancelled!", Toast.LENGTH_SHORT).show();
                ArrayList<String> dataList = new ArrayList<>();
                dataList.add("nothing");
                upiPaymentDataOperation(dataList);
            }
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if(isNetworkAvailable()) {
            String str = data.get(0);
            String paymentCancel = "";

            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String[] response = str.split("&");

            for (int i = 0; i < response.length; i++) {
                String[] equalStr = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user";
                }
            }

            if(status.contentEquals("success")) {
                Toast.makeText(this, "Transaction Successful!", Toast.LENGTH_SHORT).show();
                Intent gotoQRCode = new Intent(getApplicationContext(),QRCodeActivity.class);
                //gotoQRCode.putExtra("hashmap", (Parcelable) UserAdapter.cartItem);
                startActivity(gotoQRCode);
                finish();
            }

            else if("Payment cancelled by user".contentEquals(paymentCancel)) {
                Intent gotoQRCode = new Intent(getApplicationContext(),QRCodeActivity.class);
                //gotoQRCode.putExtra("hashmap", (Serializable) UserAdapter.cartItem);
                startActivity(gotoQRCode);
                finish();
            }

            else {
                Toast.makeText(this, "Transaction failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Internet Connection not available!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}