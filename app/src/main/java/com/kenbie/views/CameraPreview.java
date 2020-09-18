package com.kenbie.views;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.kenbie.listeners.InfoListener;

import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;


@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Activity mContext;
    private boolean isFlashEnable;
    private InfoListener infoListener;

    public CameraPreview(Activity context, Camera camera, boolean isFlash, InfoListener infoListener) {
        super(context);
        mCamera = camera;
        mContext = context;
        isFlashEnable = isFlash;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setKeepScreenOn(true);
        this.infoListener = infoListener;
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
//            setCamera(mCamera);
        } catch (Exception e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            setCamera(mCamera);
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
            int rotation = mContext.getWindowManager().getDefaultDisplay().getRotation();
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
            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
//            if(isFlashEnable)
//                params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);

            //Setting the exposure Compensation in a unique way.
            params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            int exposureCompensation = params.getMinExposureCompensation();
            int exposureCompensation2 = params.getMaxExposureCompensation();
            int exposureCompensationFinal = (exposureCompensation + exposureCompensation2) / 2;
            params.setExposureCompensation(exposureCompensationFinal);
            mCamera.setParameters(params);

            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            infoListener.getInfoValue(0, 0);
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void setCamera(Camera camera) {
        //method to set a camera instance
        mCamera = camera;
        try {
            Camera.Parameters param;
            param = camera.getParameters();

            Camera.Size bestSize = null;
            List<Camera.Size> sizeList = param.getSupportedPictureSizes();
            bestSize = sizeList.get(0);
            for (int i = 1; i < sizeList.size(); i++) {
                Log.d("Size", "" + sizeList.get(i).width + "," + sizeList.get(i).height);
                if ((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)) {
                    bestSize = sizeList.get(i);
                }
            }
            Log.d("Best Size", "" + bestSize.width + "," + bestSize.height);

//            param.setPictureSize(bestSize.width, bestSize.height);
            param.setPictureSize(1920, 1080);
            List<Integer> supportedPreviewFormats = param.getSupportedPreviewFormats();
            Iterator<Integer> supportedPreviewFormatsIterator = supportedPreviewFormats.iterator();
            while (supportedPreviewFormatsIterator.hasNext()) {
                Integer previewFormat = supportedPreviewFormatsIterator.next();
                if (previewFormat == ImageFormat.JPEG) {
                    param.setPreviewFormat(previewFormat);
                }
            }

            List<Camera.Size> previewSizeList = param.getSupportedPreviewSizes();
            bestSize = null;
            bestSize = previewSizeList.get(0);
            for (int i = 1; i < previewSizeList.size(); i++) {
                if ((previewSizeList.get(i).width * previewSizeList.get(i).height) > (bestSize.width * bestSize.height)) {
                    bestSize = previewSizeList.get(i);
                }
            }

//            param.setPreviewSize(bestSize.width, bestSize.height);
            param.setPreviewSize(1920, 1080);
//            if (param.getSupportedFocusModes().contains(
//                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
//                param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//            }

            mCamera.setParameters(param);
        }
        catch (Exception e){
            Log.e("camera",e.getMessage());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}