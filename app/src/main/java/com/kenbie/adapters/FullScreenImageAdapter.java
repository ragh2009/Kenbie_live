package com.kenbie.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.R;
import com.kenbie.listeners.ProfileOptionListener;
import com.kenbie.model.OptionsData;
import com.kenbie.views.SwipeListener;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FullScreenImageAdapter extends PagerAdapter {
    private RequestOptions options = null;
    private Activity _activity;
    private ArrayList<OptionsData> _imageArray;
    private LayoutInflater inflater;
    private ProfileOptionListener mediaListeners;
    private SwipeListener sl;

    // constructor
    public FullScreenImageAdapter(Activity activity, ArrayList<OptionsData> imagePaths, ProfileOptionListener mediaListeners, SwipeListener sl) {
        this._activity = activity;
        this._imageArray = imagePaths;
        this.mediaListeners = mediaListeners;
        this.sl = sl;
        options = new RequestOptions()
                .optionalCenterInside()
                .priority(Priority.HIGH);

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this._imageArray.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View viewLayout = inflater.inflate(R.layout.full_gallery_item, container,
                false);

        ((RelativeLayout) viewLayout.findViewById(R.id.gallery_view_close)).setOnTouchListener(sl);
        ImageView touchImageView = (ImageView) viewLayout.findViewById(R.id.media_display);
//        Glide.with(_activity).load(IMAGE_LARGE_BASE_URL + _imageArray.get(position).getName()).apply(options).into(touchImageView);
        if (_imageArray.get(position).getId() == -1) {
            Bitmap bitmap = getImageBitmap(_imageArray.get(position).getImgId(), _imageArray.get(position).getOptionCode());
            if (bitmap != null)
                touchImageView.setImageBitmap(bitmap);
            else
                Glide.with(_activity).load(_imageArray.get(position).getName()).apply(options).into(touchImageView);
        } else
            Glide.with(_activity).load(_imageArray.get(position).getName()).apply(options).into(touchImageView);

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    private Bitmap getImageBitmap(int type, String imgPath) {
        Bitmap bitmap = null;
        try {
            Uri uri = Uri.parse(imgPath);
            if (type == 1) {
                bitmap = MediaStore.Images.Media.getBitmap(_activity.getContentResolver(), uri);
                if (bitmap != null) {
                    bitmap = rotateImageIfRequired(bitmap, uri);
//                    bitmap = getResizedBitmap(bitmap, 500);
                }
            } else
                bitmap = getBitmapFromUri(uri);

            if (bitmap == null)
                bitmap = getBitmapFromUri(uri);

//            if(bitmap != null)
//                bitmap = addWaterMark(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = _activity.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 4;
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            image = rotateImageIfRequired(image, uri);
//            image = getResizedBitmap(image, 500);
            System.gc();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
        InputStream input = _activity.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

//        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private Bitmap addWaterMark(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Bitmap waterMark = BitmapFactory.decodeResource(_activity.getResources(), R.drawable.img_watermark);
        canvas.drawBitmap(waterMark, 0, 0, null);

        return result;
    }
}