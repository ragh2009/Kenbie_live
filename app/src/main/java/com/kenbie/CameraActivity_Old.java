package com.kenbie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.kenbie.views.CameraPreview;
import com.kenbie.util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity_Old extends AppCompatActivity{
    private static final String TAG = "Camera_functions";
    private Camera mCamera;
    private CameraPreview mPreview;
    private String mCurrentPhotoPath;
    private ImageButton btnCameraFace, captureButton, cancelBtn, doneBtn;
    private int bitmapRotation = 0;
    private int currentCameraId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Create an instance of Camera
        mCamera = getCameraInstance();
/*        // get Camera parameters
        Camera.Parameters params = mCamera.getParameters();
        // set the focus mode
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        // set Camera parameters
        mCamera.setParameters(params);*/

        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
//        mPreview.getHolder().addCallback(this);


//        surfaceChanged(0, 0);
//
//        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera, true, null);
        preview.addView(mPreview);
//        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        mCamera = Camera.open(currentCameraId);

//        captureButton = (ImageButton) findViewById(R.id.btn_camera);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );

//        btnCameraFace = (ImageButton) findViewById(R.id.btn_camera_face);
        btnCameraFace.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        switchCamera();
                    }
                }
        );

//        cancelBtn = (ImageButton) findViewById(R.id.btn_camera_cancel);
        cancelBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("Cancel", true);
                        setResult(Constants.CAMERA_MANUAL, intent);
                        finish();
                    }
                }
        );

//        doneBtn = (ImageButton) findViewById(R.id.btn_camera_done);
        doneBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // After capture done
                        Intent intent = new Intent();
                        intent.putExtra("Cancel", false);
                        intent.putExtra("Path", mCurrentPhotoPath);
                        setResult(Constants.CAMERA_MANUAL, intent);
                        finish();
                    }
                }
        );
    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(currentCameraId); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


    private void switchCamera() {
        try {
            mCamera.stopPreview();
            mCamera.release();
            if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            } else {
                currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
            mCamera = Camera.open(currentCameraId);
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, info);
            int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break; //Natural orientation
                case Surface.ROTATION_90:
                    degrees = 90;
                    break; //Landscape left
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;//Upside down
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;//Landscape right
            }
            int rotate = (info.orientation - degrees + 360) % 360;

            //STEP #2: Set the 'rotation' parameter
            Camera.Parameters params = mCamera.getParameters();
            params.setRotation(rotate);
            try {
                mCamera.setPreviewDisplay(mPreview.getHolder());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mCamera.setParameters(params);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] imgData, Camera camera) {
            updateCameraPreviewUi();

            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
/*                if (bitmapRotation != 0) {
                    Bitmap bitmap = BitmapUtils.convertCompressedByteArrayToBitmap(imgData);
                    bitmap = rotateImage(bitmap, bitmapRotation);
                    imgData = BitmapUtils.convertBitmapToByteArray(bitmap);

//                    ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
//                    bitmap.copyPixelsToBuffer(byteBuffer);
//                    data = byteBuffer.array();
                }*/

                fos.write(imgData);
                fos.close();
                System.gc();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Camera.PictureCallback mPicture1 = new Camera.PictureCallback() {
        byte[] imgData;

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            updateCameraPreviewUi();

            imgData = data;
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            if (bitmapRotation != 0) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
                                bitmap = rotateImage(bitmap, bitmapRotation);
                                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, blob);
                                imgData = blob.toByteArray();

//                    ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
//                    bitmap.copyPixelsToBuffer(byteBuffer);
//                    data = byteBuffer.array();
                            }

                            fos.write(imgData);
                            fos.close();
                            imgData = null;
                            System.gc();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        mediaScanIntent.setData(Uri.fromFile(f));
        sendBroadcast(mediaScanIntent);
    }

    private void updateCameraPreviewUi() {
//        galleryAddPic();
        cancelBtn.setVisibility(View.VISIBLE);
        doneBtn.setVisibility(View.VISIBLE);
        captureButton.setVisibility(View.GONE);
        btnCameraFace.setVisibility(View.GONE);
    }

    private File getOutputMediaFile() {
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();
//        releaseCamera();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCamera.release();
        Log.d("CAMERA","Destroy");
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("Cancel", true);
        setResult(Constants.CAMERA_MANUAL, intent);
        finish();
        super.onBackPressed();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break; //Natural orientation
            case Surface.ROTATION_90:
                degrees = 90;
                break; //Landscape left
            case Surface.ROTATION_180:
                degrees = 180;
                break;//Upside down
            case Surface.ROTATION_270:
                degrees = 270;
                break;//Landscape right
        }
        int rotate = (info.orientation - degrees + 360) % 360;

        //STEP #2: Set the 'rotation' parameter
        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(rotate);
        mCamera.setParameters(params);
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("PREVIEW", "surfaceDestroyed");
    }
}
