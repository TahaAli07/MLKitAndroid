package com.example.taha.mlkit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    FirebaseVisionImage image;
    TextView textView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    FirebaseVisionTextDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(MainActivity.this);
        textView = findViewById(R.id.textView);
        dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            image = FirebaseVisionImage.fromBitmap(bitmap);
            detector = FirebaseVision.getInstance()
                    .getVisionTextDetector();
            Task<FirebaseVisionText> result =
                    detector.detectInImage(image)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                    // Task completed successfully
                                    textView.setText("START\n");
                                    for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks()) {
                                        Rect boundingBox = block.getBoundingBox();
                                        Point[] cornerPoints = block.getCornerPoints();
                                        String text = block.getText();
                                        textView.append(text);
                                    }
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Code for on failure
                                            Toast.makeText(MainActivity.this, "Image Recognition Failed  " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void recognizeTextfromUri() {

        String path = "/storage/emulated/0/DCIM/Camera/IMG_20180707_131917.jpg";
        try {
            image = FirebaseVisionImage.fromFilePath(MainActivity.this, Uri.fromFile(new File(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        detector = FirebaseVision.getInstance()
                .getVisionTextDetector();
        Task<FirebaseVisionText> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                // Task completed successfully
                                textView.setText("START\n");
                                for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks()) {
                                    Rect boundingBox = block.getBoundingBox();
                                    Point[] cornerPoints = block.getCornerPoints();
                                    String text = block.getText();
                                    textView.append(text);
                                }
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Image Recognition Failed  " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
    }
}
