package com.newstar.scorpiodata.activitys;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.newstar.scorpiodata.R;
import com.newstar.scorpiodata.utils.PictureUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String PHOTO_PATH = "photo_path";
    public static final String CAMERA_TYPE = "camera_type";
    public static final String CAMERA_FRONT = "camera_front";
    public static final String CAMERA_BACK = "camera_back";
    private Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    PreviewView mPreviewView;
    ImageView captureImage;
    ImageView swap_camera;
    private FrameLayout preview;
    private ImageView preview_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_main);

        String cameraType = getIntent().getStringExtra(CAMERA_TYPE);
        if(cameraType!=null){
            if(cameraType.endsWith(CAMERA_FRONT)){
                cameraSelectorIndex = CameraSelector.LENS_FACING_FRONT;
            }else if(cameraType.endsWith(CAMERA_BACK)){
                cameraSelectorIndex = CameraSelector.LENS_FACING_BACK;
            }
        }
        mPreviewView = findViewById(R.id.previewView);
        captureImage = findViewById(R.id.captureImg);
        swap_camera = findViewById(R.id.swap_camera);
        swap_camera.setOnClickListener(this);
        preview = findViewById(R.id.preview_view);
        preview_image = findViewById(R.id.preview_image);
        Button cancel = findViewById(R.id.cancel);
        Button ok = findViewById(R.id.ok);
        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
        if(allPermissionsGranted()){
            startCamera(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }
    ProcessCameraProvider cameraProvider;
    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    if(cameraProvider!=null){
                        cameraProvider.unbindAll();
                    }else{
                        cameraProvider = cameraProviderFuture.get();
                    }
                    bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cameraSelectorIndex)
                .build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        // Query if extension is available (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();

        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);
        captureImage.setOnClickListener(v -> {

            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
            File file = new File(getBatchDirectoryName(), mDateFormat.format(new Date())+ ".jpg");
            path = file.getAbsolutePath();
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
            imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback () {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CameraActivity.this, "Image Saved successfully", Toast.LENGTH_SHORT).show();
                            handler.sendEmptyMessage(0);
                        }
                    });
                }
                @Override
                public void onError(@NonNull ImageCaptureException error) {
                    error.printStackTrace();
                }
            });
        });
    }

    public String getBatchDirectoryName() {

        String app_folder_path = "";
        app_folder_path = Environment.getExternalStorageDirectory().toString() + "/images";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {

        }

        return app_folder_path;
    }

    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.swap_camera) {
            toggleFrontBackCamera();
        } else if (id == R.id.ok) {
            Intent intent = new Intent();
            //把返回数据存入Intent
            intent.putExtra(PHOTO_PATH, path);
            //设置返回数据
            setResult(RESULT_OK, intent);
            //关闭Activity
            finish();
        } else if (id == R.id.cancel) {
            preview.setVisibility(View.GONE);
        }

    }

    int cameraSelectorIndex = CameraSelector.LENS_FACING_BACK;
    private void toggleFrontBackCamera() {
        if (cameraSelectorIndex == CameraSelector.LENS_FACING_FRONT) {
            cameraSelectorIndex = CameraSelector.LENS_FACING_BACK;
        }else if (cameraSelectorIndex == CameraSelector.LENS_FACING_BACK) {
            cameraSelectorIndex = CameraSelector.LENS_FACING_FRONT;
        }
        startCamera();
    }

    String path;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            com.newstar.scorpiodata.utils.Callback<File, Exception> callback = new com.newstar.scorpiodata.utils.Callback<File, Exception>() {
                @Override
                public void resolve(File res) {
                    try {
                        preview.setVisibility(View.VISIBLE);
                        preview_image.setImageDrawable(BitmapDrawable.createFromPath(res.getAbsolutePath()));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void reject(Exception err) {
                    err.printStackTrace();
                }
            };

            try {
                PictureUtils.compressBitmap(CameraActivity.this, path, 1024, callback);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
}
