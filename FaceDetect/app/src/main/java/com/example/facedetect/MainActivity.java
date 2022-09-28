package com.example.facedetect;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final int REQ=200;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void faceDetect(View view) {
        Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i,REQ);
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, @Nullable Intent data) {
        super.onActivityResult(requestcode, resultcode, data);

        if(requestcode==REQ){
            if(resultcode == RESULT_OK){
                Bitmap photo=(Bitmap)data.getExtras().get("data");
                faceDetection(photo);
            }else if(resultcode==RESULT_CANCELED){
                Toast.makeText(this,"Operation cancelled by you!",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,"Failed to capture image !",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void faceDetection(Bitmap photo) {
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

        InputImage image = InputImage.fromBitmap(photo,0);
        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);
        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        Toast.makeText(getApplicationContext(),"To close dialog click anywhere outside the box!",Toast.LENGTH_LONG).show();
                                        for (Face face : faces) {

                                            String eyes="",ears="",nose="",mouth="",smile="",righteyeopen="",lefteyeopen="";
                                            FaceLandmark lefteye = face.getLandmark(FaceLandmark.LEFT_EYE);
                                            if (lefteye != null) {
                                                PointF leftEye1 = lefteye.getPosition();
                                                eyes="(L: "+leftEye1.toString();
                                            }
                                            FaceLandmark righteye = face.getLandmark(FaceLandmark.RIGHT_EYE);
                                            if (righteye != null) {
                                                PointF RightEye1 = righteye.getPosition();
                                                eyes=eyes+",\nR:"+RightEye1.toString()+")";
                                            }

                                            FaceLandmark leftear = face.getLandmark(FaceLandmark.LEFT_EAR);
                                            if (leftear != null) {
                                                PointF leftEar1 = leftear.getPosition();
                                                ears="(L: "+leftEar1.toString();
                                            }
                                            FaceLandmark rightear = face.getLandmark(FaceLandmark.RIGHT_EAR);
                                            if (rightear != null) {
                                                PointF RightEar1 = rightear.getPosition();
                                                ears=ears+",\nR:"+RightEar1.toString()+")";
                                            }

                                            FaceLandmark nose1 = face.getLandmark(FaceLandmark.NOSE_BASE);
                                            if (nose1 != null) {
                                                PointF nose2= nose1.getPosition();
                                                nose=nose2.toString();
                                            }
                                            FaceLandmark mouth1 = face.getLandmark(FaceLandmark.MOUTH_LEFT);
                                            if (mouth1 != null) {
                                                PointF mouthp = mouth1.getPosition();
                                                mouth="(L: "+mouthp.toString();
                                            }
                                            FaceLandmark mouth2 = face.getLandmark(FaceLandmark.MOUTH_RIGHT);
                                            if (mouth2 != null) {
                                                PointF mouthp = mouth2.getPosition();
                                                mouth=mouth+"\nR: "+mouthp.toString();
                                            }
                                            FaceLandmark mouth3 = face.getLandmark(FaceLandmark.MOUTH_BOTTOM);
                                            if (mouth3 != null) {
                                                PointF mouthp = mouth3.getPosition();
                                                mouth=mouth+"\nB: "+mouthp.toString()+")";
                                            }

                                            if (face.getSmilingProbability() != null) {
                                                float smileProb = face.getSmilingProbability();
                                                smile=smileProb+" ";

                                            }
                                            if (face.getRightEyeOpenProbability() != null) {
                                                float rightEyeOpenProb = face.getRightEyeOpenProbability();
                                                righteyeopen=rightEyeOpenProb+" ";
                                            }
                                            if (face.getLeftEyeOpenProbability() != null) {
                                                float leftEyeOpenProb = face.getLeftEyeOpenProbability();
                                                lefteyeopen=leftEyeOpenProb+" ";
                                            }

                                            openDialog(eyes,ears,nose,mouth,smile,lefteyeopen,righteyeopen);
                                        }
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"Failed to detect face !",Toast.LENGTH_LONG).show();
                                    }
                                });

    }

    private void openDialog(String eyes,String ears,String nose1,String mouth1, String smile, String lefteyeopen, String righteyeopen) {
        dialog=new Dialog(MainActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fragment_resultdialog);

        TextView eye=dialog.findViewById(R.id.eye);
        eye.setText(eyes);

        TextView ear=dialog.findViewById(R.id.ear);
        ear.setText(ears);

        TextView nose2=dialog.findViewById(R.id.nose);
        nose2.setText(nose1);

        TextView mouTH=dialog.findViewById(R.id.mouth);
        mouTH.setText(mouth1);

        TextView smiling=dialog.findViewById(R.id.smiling);
        smiling.setText(smile);

        TextView lopen=dialog.findViewById(R.id.lopen);
        lopen.setText(lefteyeopen);
        TextView ropen=dialog.findViewById(R.id.ropen);
        ropen.setText(lefteyeopen);
        dialog.show();
    }
}