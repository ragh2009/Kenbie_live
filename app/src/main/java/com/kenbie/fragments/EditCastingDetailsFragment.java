package com.kenbie.fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.BuildConfig;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.util.Constants;
import com.kenbie.util.RuntimePermissionUtils;
import com.kenbie.util.Utility;

import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditCastingDetailsFragment extends BaseFragment implements APIResponseHandler, View.OnClickListener {
    private Uri IMAGE_CAPTURE_URI;
    private String mCurrentPhotoPath;
    private EditText etTitle, etReq;
    private LinearLayout imgBlankLayout;
    private CircleImageView profileImg;

    public EditCastingDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_casting_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView backBtn = view.findViewById(R.id.m_back_button);
        backBtn.setOnClickListener(this);
        TextView screenTitle = view.findViewById(R.id.m_title);
        screenTitle.setTypeface(KenbieApplication.S_NORMAL);
        screenTitle.setText(mActivity.mPref.getString("101", "CASTING"));
        TextView saveBtn = view.findViewById(R.id.m_save_btn);
        saveBtn.setText(mActivity.mPref.getString("137", "NEXT"));
        saveBtn.setTypeface(KenbieApplication.S_NORMAL);
        saveBtn.setOnClickListener(this);

        TextView title = view.findViewById(R.id.title);
        title.setText(Html.fromHtml(mActivity.mPref.getString("135", "Title") + getString(R.string.asteriskred)));
        title.setTypeface(KenbieApplication.S_NORMAL);

        etTitle = (EditText) view.findViewById(R.id.et_title);
        etTitle.setHint(mActivity.mPref.getString("136", "Enter your casting title heading"));
        etTitle.setTypeface(KenbieApplication.S_NORMAL);
        if (mActivity.castingParams != null && mActivity.castingParams.containsKey("casting_title"))
            etTitle.setText(mActivity.castingParams.get("casting_title"));

        TextView reqTitle = view.findViewById(R.id.req_title);
        reqTitle.setText(Html.fromHtml(mActivity.mPref.getString("104", "Requirements") + getString(R.string.asteriskred)));
        reqTitle.setTypeface(KenbieApplication.S_NORMAL);

        etReq = (EditText) view.findViewById(R.id.et_req);
        etReq.setHint(mActivity.mPref.getString("104", "Requirements"));
        etReq.setTypeface(KenbieApplication.S_NORMAL);

        imgBlankLayout = (LinearLayout) view.findViewById(R.id.img_blank_layout);
        LinearLayout imgClick = (LinearLayout) view.findViewById(R.id.img_click);
        profileImg = (CircleImageView) view.findViewById(R.id.profile_img);
        imgClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfileImage();
            }
        });

        if (mActivity.castingParams != null && mActivity.castingParams.containsKey("requirements")) {
            etReq.setText(mActivity.castingParams.get("requirements"));

            if (mActivity.profilePicBitmap != null) {
                imgBlankLayout.setVisibility(View.GONE);
                profileImg.setVisibility(View.VISIBLE);
                profileImg.setImageBitmap(mActivity.utility.getCroppedBitmap(mActivity.profilePicBitmap));
            } else if (mActivity.castingParams != null && mActivity.castingParams.containsKey("casting_id")) {
                imgBlankLayout.setVisibility(View.GONE);
                profileImg.setVisibility(View.VISIBLE);
                RequestOptions options = new RequestOptions()
                        .circleCrop()
                        .placeholder(getResources().getDrawable(R.drawable.img_c_user_dummy))
                        .priority(Priority.HIGH);
                Glide.with(mActivity).load(mActivity.castingParams.get("casting_img")).apply(options).into(profileImg);
            } else {
                imgBlankLayout.setVisibility(View.VISIBLE);
                profileImg.setVisibility(View.GONE);
            }
        }

 /*       TextView saveBtn = (TextView) view.findViewById(R.id.save_btn);
        saveBtn.setText(mActivity.mPref.getString("137", "NEXT"));
        saveBtn.setTypeface(KenbieApplication.S_NORMAL);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInfoValid())
                    nextCastingStep();
//                    updateCastingDetails();
            }
        });*/
    }

    private void nextCastingStep() {
        if (mActivity.castingParams != null && mActivity.castingParams.containsKey("casting_id")) ;
        else
            mActivity.castingParams = new HashMap<String, String>();

        mActivity.castingParams.put("user_id", mActivity.mPref.getString("UserId", ""));
        mActivity.castingParams.put("login_key", mActivity.mPref.getString("LoginKey", ""));
        mActivity.castingParams.put("login_token", mActivity.mPref.getString("LoginToken", ""));
        mActivity.castingParams.put("casting_title", etTitle.getText().toString());
        mActivity.castingParams.put("requirements", etReq.getText().toString());

        mActivity.replaceFragment(new EditCastingPreferencesFragment(), true, false);
    }

    private void updateCastingDetails() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("casting_title", etTitle.getText().toString());
            params.put("requirements", etReq.getText().toString());

            new MConnection().postRequestWithHttpHeaders(mActivity, "addCasting", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private boolean isInfoValid() {
        if (!(mActivity.castingParams != null && mActivity.castingParams.containsKey("casting_id")) && mActivity.profilePicBitmap == null) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("138", "Please upload image"));
            return false;
        } else if (etTitle.getText().toString().equalsIgnoreCase("")) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("136", "Enter your casting title heading"));
            return false;
        } else if (etReq.getText().toString().equalsIgnoreCase("")) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("353", "Please enter the requirements"));
            return false;
        }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void selectProfileImage() {
        Utility.showPictureDialog(mActivity, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    dispatchTakePictureIntent();
                }
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkGalleryPermissions()) {
                    openPhoneGallery();
                }
            }
        });
    }

    private void startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mActivity,
                        "com.kenbie.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Constants.CAMERA_CLICK);
            }
        }
    }


/*    private void startCamera() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        IMAGE_CAPTURE_URI = Utility.getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, IMAGE_CAPTURE_URI);
        startActivityForResult(intent, Constants.CAMERA_CLICK);
    }*/

    private void startCamera1() {
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri IMAGE_CAPTURE_URI = FileProvider.getUriForFile(mActivity,
                    BuildConfig.APPLICATION_ID + ".provider",
                    createImageFile());
//        IMAGE_CAPTURE_URI = FileProvider.getUriForFile(mActivity, mActivity.getApplicationContext().getPackageName() + ".com.kenbie.name.provider", Utility.getOutputMediaFile());
//        IMAGE_CAPTURE_URI = Utility.getOutputMediaFileUri();
//            IMAGE_CAPTURE_URI = Uri.fromFile(createImageFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, IMAGE_CAPTURE_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, Constants.CAMERA_CLICK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Constants.CAMERA_CLICK);
            }
        }
    }*/

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mActivity,
                        "com.kenbie.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Constants.CAMERA_CLICK);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private File createImageFileOld() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void openPhoneGallery() {
        try {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            String[] mimeTypes = {"image/jpeg", "image/png"};
            photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//            photoPickerIntent.addFlags(
//                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(photoPickerIntent, Constants.GALLERY_CLICK);
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, Constants.GALLERY_CLICK);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case RuntimePermissionUtils.REQUEST_CAMERA:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        dispatchTakePictureIntent();
                    } else {
                        dispatchTakePictureIntent();
                    }
                break;
            case RuntimePermissionUtils.REQUEST_WRITE_EXTERNAL_STORAGE_GALLERY:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openPhoneGallery();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.CAMERA_CLICK && resultCode == mActivity.RESULT_OK) {
//            Uri imageUri = Uri.parse(mCurrentPhotoPath);
//            try {
//                File file = new File(imageUri.getPath());
//                InputStream ims = new FileInputStream(file);
//                mActivity.profilePicBitmap = BitmapFactory.decodeStream(ims);
//            } catch (Exception e) {
//               e.printStackTrace();
//            }
//
            /*----------Old Code ---------------*/
/*            mActivity.imgPath = IMAGE_CAPTURE_URI.getPath();
            mActivity.profilePicBitmap = Utility.getImageFromCamera(mActivity, IMAGE_CAPTURE_URI);
            if (mActivity.profilePicBitmap != null) {
                imgBlankLayout.setVisibility(View.GONE);
                profileImg.setVisibility(View.VISIBLE);
                profileImg.setImageBitmap(mActivity.utility.getCroppedBitmap(mActivity.profilePicBitmap));
            } else {
                imgBlankLayout.setVisibility(View.VISIBLE);
                profileImg.setVisibility(View.GONE);
            }*/


            galleryAddPic();
            mActivity.imgPath = mCurrentPhotoPath;
            try {
//                mActivity.profilePicBitmap = getImageBitmap(1, mActivity.imgPath);
                mActivity.profilePicBitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), IMAGE_CAPTURE_URI);

                if (mActivity.profilePicBitmap != null) {
                    imgBlankLayout.setVisibility(View.GONE);
                    profileImg.setVisibility(View.VISIBLE);
                    mActivity.profilePicBitmap = rotateImageIfRequired(mActivity.profilePicBitmap, IMAGE_CAPTURE_URI);
                    mActivity.profilePicBitmap = getResizedBitmap(mActivity.profilePicBitmap, 500);
                    profileImg.setImageBitmap(mActivity.profilePicBitmap);
                } else {
                    imgBlankLayout.setVisibility(View.VISIBLE);
                    profileImg.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == Constants.GALLERY_CLICK && resultCode == mActivity.RESULT_OK) {
            try {
                if (data != null) {
                  /*  Uri selectedImage = data.getData();
                    profileImg.setImageURI(selectedImage);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    // Get the cursor
                    Cursor cursor = mActivity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //Gets the String value in the column
                    mActivity.imgPath = cursor.getString(columnIndex);
                    cursor.close();*/


                    /* Old code*/
                   Uri _uri = data.getData();
                    if (_uri != null) {
                        Cursor cursor = mActivity.getContentResolver().query(_uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
//                        Cursor cursor = mActivity.getContentResolver().query(_uri, new String[]{MediaStore.Images.Media.RELATIVE_PATH}, null, null, null);
                        if (cursor != null)
                            cursor.moveToFirst();
                        try {
                            mActivity.imgPath  = cursor.getString(0);

//                            File f = new File(mCurrentPhotoPath);
//                            IMAGE_CAPTURE_URI = Uri.fromFile(f);
//                            mActivity.imgPath = IMAGE_CAPTURE_URI.toString();

//                            mCurrentPhotoPath = cursor.getString(0);
                            cursor.close();
//                            galleryAddPic();

                            mActivity.profilePicBitmap = getBitmapFromUri(_uri);
//                            mActivity.profilePicBitmap = getBitmapFromUri(IMAGE_CAPTURE_URI);
//                            mActivity.profilePicBitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), IMAGE_CAPTURE_URI);

//                            profilePicBitmap = StaticUtils.getResizeImage(mActivity, StaticUtils.PROFILE_IMAGE_SIZE, StaticUtils.PROFILE_IMAGE_SIZE, ScalingUtilities.ScalingLogic.CROP, true, imagePath, _uri);
                            if (mActivity.profilePicBitmap != null) {
                                imgBlankLayout.setVisibility(View.GONE);
                                profileImg.setVisibility(View.VISIBLE);
                                profileImg.setImageBitmap(mActivity.utility.getCroppedBitmap(mActivity.profilePicBitmap));
                            } else {
                                imgBlankLayout.setVisibility(View.VISIBLE);
                                profileImg.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        IMAGE_CAPTURE_URI = Uri.fromFile(f);
        mediaScanIntent.setData(IMAGE_CAPTURE_URI);
        mActivity.sendBroadcast(mediaScanIntent);
    }

    private Bitmap getImageBitmap(int type, String imgPath) {
        Bitmap bitmap = null;
        try {
            Uri uri = Uri.parse(imgPath);
            if (type == 1)
                bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);
            else
                bitmap = getBitmapFromUri(uri);

            if (bitmap == null)
                bitmap = getBitmapFromUri(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    mActivity.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            image = rotateImageIfRequired(image, Uri.fromFile(new File(mActivity.imgPath)));
//            image = getResizedBitmap(image, 500);

//            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean checkPermissions() {
        List<String> neededPermissions = new ArrayList<>();

        if (RuntimePermissionUtils.checkPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.CAMERA);
        }
        if (RuntimePermissionUtils.checkPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        String[] permissions = neededPermissions.toArray(new String[neededPermissions.size()]);
        if (neededPermissions.size() > 0) {
            requestPermissions(permissions, RuntimePermissionUtils.REQUEST_CAMERA);
        } else {
            return true;
        }
        return false;
    }

    private boolean checkGalleryPermissions() {
        List<String> neededPermissions = new ArrayList<>();

        if (RuntimePermissionUtils.checkPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (RuntimePermissionUtils.checkPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        String[] permissions = neededPermissions.toArray(new String[neededPermissions.size()]);
        if (neededPermissions.size() > 0) {
            requestPermissions(permissions, RuntimePermissionUtils.REQUEST_WRITE_EXTERNAL_STORAGE_GALLERY);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void getError(String error, int APICode) {
        if (error != null)
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), error);
        else
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));

        mActivity.showProgressDialog(false);
    }

    @Override
    public void getResponse(String response, int APICode) {
        try {
            if (response != null) {
                JSONObject jo = new JSONObject(response);

                if (APICode == 101) { // profile details
                    if (jo.has("status") && jo.getBoolean("status")) {
//                        mActivity.onBackPressed();
                        if (jo.has("success"))
                            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("success"));
                        else
                            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("330", "UPDATED SUCCESSFULLY !"));
                    } else if (jo.has("error"))
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("error"));
                    else
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mActivity.showProgressDialog(false);
    }

    @Override
    public void networkError(String error, int APICode) {
        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        mActivity.showProgressDialog(false);
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(29, mActivity.mPref.getString("101", "CASTING DETAILS"), false, true, true);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.m_back_button:
                mActivity.onBackPressed();
                break;
            case R.id.m_save_btn:
                if (isInfoValid())
                    nextCastingStep();
                break;
        }
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
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

    private static Bitmap rotateImage(Bitmap img, int degree) {
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
}
