package com.kenbie.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.WindowManager;

import com.kenbie.listeners.InfoListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@SuppressWarnings("deprecation")
public class ImageProcessing {
    private Context mContext;
    private static LayoutInflater inflater = null;
    private ProgressDialog progressBar;
    private InfoListener infoListener;

    public ImageProcessing(Context context, InfoListener infoListener) {
        mContext = context;
        this.infoListener = infoListener;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void createImage(File picturePath, byte[] data) throws IOException {
        new SaveImageTask(data, picturePath).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");

    }

    private class SaveImageTask extends AsyncTask<String, Void, String> {
        private byte[] data;
        private File picturePath;

        public SaveImageTask(byte[] b, File file) {
            data = b;
            picturePath = file;
        }

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(mContext);
            progressBar.setCancelable(false);
            progressBar.setMessage("Creating image ...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setProgress(0);
            progressBar.setMax(100);
            progressBar.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            processImage(data, picturePath);
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.dismiss();
            infoListener.getInfoValue(0, 500);
//            Toast.makeText(mContext,"Image saved at " + picturePath.getAbsolutePath(),Toast.LENGTH_SHORT).show();
        }
    }

    private void processImage(byte[] data, File file) {
        Bitmap realImage;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;

        options.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared

        options.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future


        realImage = BitmapFactory.decodeByteArray(data, 0, data.length, options);

        try {
            realImage = rotate(realImage, getOrientation());
        } catch (Exception e) {
            Log.e("TAG", "Rotating exception - " + e.getMessage());
        }
        try {
            FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile());
            realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            realImage = null;
            fos.close();
//            new SingleMediaScanner(mContext, file);
        } catch (Exception e) {
            Log.e("TAG", "Updated file exception - " + e.getMessage());
        }
    }

    public static Bitmap rotate(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        matrix.postScale(-1, 1);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, false);
    }

    private int getOrientation() {

        //Camera.Parameters parameters = mCamera.getParameters();

        android.hardware.Camera.CameraInfo camInfo =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, camInfo);


        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 180;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        result = (camInfo.orientation + degrees) % 360;
        return (360 - result) % 360;
//        return (camInfo.orientation - degrees + 360) % 360;;
    }


    public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

        private MediaScannerConnection mMs;
        private File mFile;

        public SingleMediaScanner(Context context, File f) {
            mFile = f;
            mMs = new MediaScannerConnection(context, this);
            mMs.connect();
        }

        @Override
        public void onMediaScannerConnected() {
            mMs.scanFile(mFile.getAbsolutePath(), null);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            mMs.disconnect();
        }

    }

}