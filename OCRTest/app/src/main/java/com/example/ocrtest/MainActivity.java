package com.example.ocrtest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * In this video, I will teach you
     * how to use the Text Recognizer (OCR)
     * using Machine Learning technique
     * which is provided by ML Kit firebase
     * using Android Studio
     * Visit my blog: www.programmingexperts.site
     * **/

    ImageView imageViewData;
    EditText txtFetchedData;
    Button buttonPickImage, buttonDetectData;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewData = findViewById(R.id.imageView);
        txtFetchedData = findViewById(R.id.txtFetchedData);
        buttonPickImage = findViewById(R.id.btnPickImage);
        buttonDetectData = findViewById(R.id.btnDetectText);


        buttonPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtFetchedData.setText("");
                pickImage(view);
            }
        });

        buttonDetectData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtFetchedData.setText("");
                detectData(view);
            }
        });

    }

    public void detectData(View view){
        if(bitmap==null){
            Toast.makeText(MainActivity.this, "Bitmap is Null", Toast.LENGTH_SHORT).show();
        }
        else {
            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

            firebaseVisionTextRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    process_text(firebaseVisionText);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "No Text Detected !!!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void process_text(@org.jetbrains.annotations.NotNull FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.TextBlock> blockArrayList = firebaseVisionText.getTextBlocks();
        if (blockArrayList.size() == 0){
            Toast.makeText(MainActivity.this, "No Text Detected", Toast.LENGTH_SHORT).show();
        }
        else{
            for (FirebaseVisionText.TextBlock block:firebaseVisionText.getTextBlocks()){
                String text = block.getText();
                text = txtFetchedData.getText().toString() + " " + text;
                txtFetchedData.setText(text);
            }
        }
    }

    public void pickImage(View v){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK){
            try{
                Uri uri = data != null ? data.getData() : null;
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                    imageViewData.setImageBitmap(bitmap);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }
}
