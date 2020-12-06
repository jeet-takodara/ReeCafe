package com.parse.starter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class QRCodeActivity extends AppCompatActivity {

    //Intent getIntent;
    //Map<String,Integer> items;
    //List<String> qrcodeitems;
    Bitmap image;
    ImageView qrcodeImageView;
    StringBuilder itemList= new StringBuilder();
    String objectId;
    public static final String key = "rEcAfE9591098211";
    public static final String initVector = "9723381459jeetta";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_code);

        //getIntent = getIntent();
        //items = (Map<String,Integer>) getIntent.getSerializableExtra("hashmap");

        qrcodeImageView = findViewById(R.id.qrcodeImageView);
        //qrcodeitems = new ArrayList<>();

        /*for(Map.Entry<String,Integer> e : items.entrySet()) {
            qrcodeitems.add(e.getKey()+"  "+e.getValue());
        }*/

        uploadToServer();

        //qrcodeitems.add(0,objectId);

        UserAdapter.cartItem.clear();
        UserAdapter.cart.clear();

        try {
            image = generateQRCode(objectId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        qrcodeImageView.setImageBitmap(image);

    }

    private Bitmap generateQRCode(String text) {

        text = encrypt(text);

        BitMatrix bitMatrix = null;

        try {
            bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE,350,350,null);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        int bitmatrixWidth = bitMatrix.getWidth();
        int bitmatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitmatrixWidth * bitmatrixHeight];

        for (int y = 0; y < bitmatrixHeight; y++) {
            int offset = y * bitmatrixWidth;

            for (int x = 0; x < bitmatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bitmatrixWidth, bitmatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0,350  , 0, 0, bitmatrixWidth, bitmatrixHeight);

        return bitmap;
    }

    public String encrypt(String value) {

        try {

            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8),"AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec,iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return new String(Base64.encodeBase64(encrypted));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void uploadToServer() {

        for(Map.Entry<String,Integer> e : UserAdapter.cartItem.entrySet()) {
            itemList.append(e.getKey()).append(" ").append(e.getValue()).append(" ");
        }

        ParseObject order = new ParseObject("TransactionDetails");
        order.put("name", ParseUser.getCurrentUser().getString("Name"));
        order.put("amount", CartAdapter.cost);
        order.put("itemList",itemList.toString());

        try {
            order.save();
        } catch (Exception e) {
            e.printStackTrace();
        }

        objectId = order.getObjectId();

    }

}