package com.xiaojiang.sbu.receivedata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView Tshow;
    private ImageView Pshow;
    private static final int WRITE_PERMISSION = 0x01;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Tshow = findViewById(R.id.showMessage);
        Pshow = findViewById(R.id.showPhoto);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if(Intent.ACTION_SEND.equals(action) && type!=null){
            if("text/plain".equals(type)){
                handleSendText(intent);

            }else if(type.startsWith("image/")){
                handleSendImage(intent);
            }else if(Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null){
                if(type.startsWith("image/")){
                    handleMultipleImage(intent);
                }
            }else{
                Toast.makeText(MainActivity.this, "No data receive!", Toast.LENGTH_SHORT).show();
            }
        }

        requestReadPermission();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == WRITE_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("LOG_TAG", "Write Permission Failed");
                Toast.makeText(this, "You must allow permission write external storage to your mobile device.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void requestReadPermission() {
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_PERMISSION);
        }
    }

    private void handleSendText(Intent intent) {
        Log.v("saa",""+intent.getExtras());
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if(sharedText!=null){
            //update ui
            Tshow.setText(sharedText);
        }
    }
    private void handleSendImage(Intent intent) {
        String imageUri = intent.getStringExtra(Intent.EXTRA_TEXT);
        if(imageUri!=null){

            displayImage(imageUri);
            Log.v("saasasa", ""+imageUri);
        }
    }
    private void handleMultipleImage(Intent intent) {
        ArrayList<Uri> imgUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if(imgUris != null){
            //update ui
        }
    }
    private void displayImage(String imagePath){
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Pshow.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT);
        }
    }
}
