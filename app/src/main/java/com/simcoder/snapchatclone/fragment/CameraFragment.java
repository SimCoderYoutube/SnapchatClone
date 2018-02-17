package com.simcoder.snapchatclone.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.simcoder.snapchatclone.FindUsersActivity;
import com.simcoder.snapchatclone.R;
import com.simcoder.snapchatclone.ShowCaptureActivity;
import com.simcoder.snapchatclone.loginRegistration.SplashScreenActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by simco on 1/3/2018.
 */

public class CameraFragment extends Fragment implements SurfaceHolder.Callback{

    Camera camera;


    Camera.PictureCallback jpegCallback;

    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;

    final int CAMERA_REQUEST_CODE = 1;
    public static CameraFragment newInstance(){
        CameraFragment fragment = new CameraFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera , container, false);

        mSurfaceView = view.findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }else{
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        Button mLogout = view.findViewById(R.id.logout);
        Button mFindUsers = view.findViewById(R.id.findUsers);
        Button mCapture = view.findViewById(R.id.capture);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
        mFindUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FindUsers();
            }
        });
        mCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });


        jpegCallback = new Camera.PictureCallback(){
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {

                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                Bitmap rotateBitmap = rotate(decodedBitmap);

                String fileLocation = SaveImageToStorage(rotateBitmap);
                if(fileLocation!= null){
                    Intent intent = new Intent(getActivity(), ShowCaptureActivity.class);
                    startActivity(intent);
                    return;
                }
            }
        };

        return view;
    }


    public String SaveImageToStorage(Bitmap bitmap){
        String fileName = "imageToSend";
        try{
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();
        }catch(Exception e){
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    private Bitmap rotate(Bitmap decodedBitmap) {
        int w = decodedBitmap.getWidth();
        int h = decodedBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(90);

        return Bitmap.createBitmap(decodedBitmap, 0, 0, w, h, matrix, true);

    }

    private void captureImage() {
        camera.takePicture(null, null, jpegCallback);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera = Camera.open();

        Camera.Parameters parameters;
        parameters = camera.getParameters();

        camera.setDisplayOrientation(90);
        parameters.setPreviewFrameRate(30);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);


        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
        bestSize = sizeList.get(0);
        for(int i = 1; i < sizeList.size(); i++){
            if((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)){
                bestSize = sizeList.get(i);
            }
        }

        parameters.setPictureSize(bestSize.width, bestSize.height);


        camera.setParameters(parameters);

        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        camera.startPreview();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mSurfaceHolder.addCallback(this);
                    mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                }else{
                    Toast.makeText(getContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void FindUsers() {
        Intent intent = new Intent(getContext(), FindUsersActivity.class);
        startActivity(intent);
        return;
    }

    private void LogOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return;
    }
}
