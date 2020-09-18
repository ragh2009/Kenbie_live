package com.kenbie.fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.adapters.ImageGalleryAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.ProfileOptionListener;
import com.kenbie.model.OptionsData;
import com.kenbie.model.ProfileInfo;
import com.kenbie.util.Constants;
import com.kenbie.util.RuntimePermissionUtils;
import com.kenbie.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class PhotoGalleryFragment extends BaseFragment implements APIResponseHandler, ProfileOptionListener, View.OnClickListener {
    private Uri IMAGE_CAPTURE_URI;
    private ProfileInfo profileInfo;
    private RecyclerView recyclerView = null;
    private ImageGalleryAdapter mAdapter;
    private String imgPath, mCurrentPhotoPath;
    private ArrayList<OptionsData> galleryList;
    private LinearLayout gAction;
    private ImageView gDownAction;
    private TextView gMakeAvatarAction, gShareAction, gDeleteAction;
    private OptionsData mImageData;

    public PhotoGalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            profileInfo = (ProfileInfo) getArguments().getSerializable("ProfileInfo");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_gallery);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        gAction = view.findViewById(R.id.g_action_layout);
        gDownAction = view.findViewById(R.id.g_down_action);
        gDownAction.setOnClickListener(this);
        gMakeAvatarAction = view.findViewById(R.id.g_make_avatar_action);
        gMakeAvatarAction.setText(mActivity.mPref.getString("238", "Make it Avatar"));
        gMakeAvatarAction.setTypeface(KenbieApplication.S_NORMAL);
        gMakeAvatarAction.setOnClickListener(this);
        gShareAction = view.findViewById(R.id.g_share_action);
        gShareAction.setText(mActivity.mPref.getString("239", "Share Photo"));
        gShareAction.setTypeface(KenbieApplication.S_NORMAL);
        gShareAction.setOnClickListener(this);
        gDeleteAction = view.findViewById(R.id.g_delete_action);
        gDeleteAction.setText(mActivity.mPref.getString("240", "Delete Photo"));
        gDeleteAction.setTypeface(KenbieApplication.S_NORMAL);
        gDeleteAction.setOnClickListener(this);

        refreshGalleryData();
    }

    private void refreshGalleryData() {
        galleryList = getGalleryImages(profileInfo.getAllimgs());
//        galleryList = profileInfo.getGalleryList();
        if (galleryList == null)
            galleryList = new ArrayList<>();

        mAdapter = new ImageGalleryAdapter(mActivity, galleryList, this, mActivity.mPref);
        recyclerView.setAdapter(mAdapter);
    }

    private void getProfileDetails() {
        if (mActivity.isOnline()) {
//            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("profile_user_id", profileInfo.getId() + "");
            new MConnection().postRequestWithHttpHeaders(mActivity, "getProfileDetail", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
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

                if (APICode == 101) {
                    if (jo.has("status") && jo.getBoolean("status"))
                        parseProfileInfo(jo.getString("data"));
                    else
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                    mActivity.showProgressDialog(false);
                } else if (APICode == 102) { // Delete photos
                    getProfileDetails();
                } else if (APICode == 103) { // MakeItAvatar
                    if (jo.has("status") && jo.getBoolean("status") && jo.has("success"))
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("success"));
                    else
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                    mAdapter.refreshView(-1);
                    mActivity.showProgressDialog(false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseProfileInfo(String response) {
        try {
            JSONObject jo = new JSONObject(response);
            if (jo.has("profile_info")) {
                JSONObject jp = new JSONObject(jo.getString("profile_info"));
                profileInfo.setId(jp.getInt("id"));
                SharedPreferences.Editor mEditor = mActivity.mPref.edit();
                mEditor.putString("ProfilePic", jp.getString("user_pic"));
                mEditor.apply();
                profileInfo.setAllimgs(jo.getString("allimgs"));
//                profileInfo.setGalleryList(getGalleryImages(jo.getString("allimgs")));
                refreshGalleryData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gallery images
    private ArrayList<OptionsData> getGalleryImages(String allimgs) {
        ArrayList<OptionsData> values = new ArrayList<>();
        try {
            OptionsData od = new OptionsData();
            od.setId(-1);
            values.add(od);
            if (allimgs != null && allimgs.length() > 10) {
                JSONArray imageArray = new JSONArray(allimgs);

                for (int i = 0; i < imageArray.length(); i++) {
                    OptionsData value = new OptionsData();
                    value.setId((i + 1));
                    value.setName(imageArray.getString(i));
                    values.add(value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }


    @Override
    public void networkError(String error, int APICode) {
        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        mActivity.showProgressDialog(false);
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(15, mActivity.mPref.getString("236", "PHOTO GALLERY"), false, true, false);
        super.onResume();
    }

    @Override
    public void getAction(OptionsData value) {

    }

    @Override
    public void getDataList(ArrayList<OptionsData> value, int position, int type) {
        if (type == 1) { //Start Camera
            if (checkPermissions()) {
                selectProfileImage();
            }
        } else if (type == 2) { // View Photo Action
            mAdapter.refreshView(position);
            mImageData = value.get(position);
            gAction.setVisibility(View.VISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void selectProfileImage() {
        Utility.showPictureDialog(mActivity, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    startCamera();
                }
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RuntimePermissionUtils.checkPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openPhoneGallery();
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RuntimePermissionUtils.REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            }
        });
    }

    private void openPhoneGallery() {
        try {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, Constants.GALLERY_CLICK);
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, Constants.GALLERY_CLICK);
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CAMERA_CLICK && resultCode == RESULT_OK) {
            galleryAddPic();
            imgPath = mCurrentPhotoPath;
            imageUpload(imgPath);
          /*  if (IMAGE_CAPTURE_URI != null) {
                imgPath = IMAGE_CAPTURE_URI;
                if (imgPath != null)
                    imageUpload(imgPath);
            }*/
        } else if (requestCode == Constants.GALLERY_CLICK && resultCode == mActivity.RESULT_OK) {
            try {
                if (data != null) {
                    Uri _uri = data.getData();
                    if (_uri != null) {

                        Cursor cursor = mActivity.getContentResolver().query(_uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                        if (cursor != null)
                            cursor.moveToFirst();
                        try {
                            imgPath = cursor.getString(0);
//                            imgPath = new PathUtil().getPath(mActivity, _uri);
                            cursor.close();
/*                            if (Build.VERSION.SDK_INT < 11)
                                imgPath = RealPathUtils.getRealPathFromURI_BelowAPI11(mActivity, _uri);

                                // SDK >= 11 && SDK < 19
                            else if (Build.VERSION.SDK_INT < 19)
                                imgPath = RealPathUtils.getRealPathFromURI_API11to18(mActivity, _uri);

                                // SDK > 19 (Android 4.4)
                            else
                                imgPath = RealPathUtils.getRealPathFromURI_API19(mActivity, _uri);

                            Log.d("Gallery", "File Path: " + imgPath);*/
                            if (imgPath != null)
                                imageUpload(imgPath);
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

    public void imageUpload(String imgPath) {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, MConnection.API_BASE_URL + "addGalleryPhoto",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response", response);
                            try {
                                JSONObject jObj = new JSONObject(response);
                                if (jObj.has("status") && jObj.getBoolean("status")) {
                                    if (jObj.has("success"))
                                        getProfileDetails();
                                    else
                                        Toast.makeText(mActivity, mActivity.mPref.getString("270", "Something Wrong! Please try later."), Toast.LENGTH_LONG).show();
                                } else if (jObj.has("error"))
                                    Toast.makeText(mActivity, jObj.getString("error"), Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(mActivity, mActivity.mPref.getString("270", "Something Wrong! Please try later."), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mActivity.showProgressDialog(false);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null && error.getMessage() != null)
                        Toast.makeText(mActivity, error.getMessage(), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(mActivity, mActivity.mPref.getString("270", "Something Wrong! Please try later."), Toast.LENGTH_LONG).show();
                    mActivity.showProgressDialog(false);
                }

               /* public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Accept", "application/json");
                    return headers;
                }*/
            });

            smr.addStringParam("X-API-KEY", MConnection.API_KEY);
            smr.addStringParam("user_id", mActivity.mPref.getString("UserId", ""));
            smr.addStringParam("login_key", mActivity.mPref.getString("LoginKey", ""));
            smr.addStringParam("login_token", mActivity.mPref.getString("LoginToken", ""));
            smr.addStringParam("device_id", mActivity.mPref.getString("DeviceId", ""));
            smr.addStringParam("lang", mActivity.mPref.getString("UserSavedLangCode", "en") ); // Language
//            smr.addStringParam("android_token", FirebaseInstanceId.getInstance().getToken()); //TODO  Remove token
            smr.addStringParam("img_url ", "");
            smr.addFile("userimg[]", imgPath);
//            smr.addMultipartParam("userimg", "multipart/form-data", imgPath);
            Volley.newRequestQueue(mActivity).add(smr);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private boolean checkPermissions() {
        List<String> neededPermissions = new ArrayList<>();

        if (RuntimePermissionUtils.checkPermission(mActivity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(android.Manifest.permission.CAMERA);
        }
        if (RuntimePermissionUtils.checkPermission(mActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RuntimePermissionUtils.REQUEST_CAMERA:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        startCamera();
                    } else {
                        startCamera();
                    }
                break;
            case RuntimePermissionUtils.REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openPhoneGallery();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.g_make_avatar_action:
                gAction.setVisibility(View.GONE);
                makeItAvatar();
                break;
            case R.id.g_share_action:
                gAction.setVisibility(View.GONE);
                downLoadImageToShare();
                break;
            case R.id.g_delete_action:
                gAction.setVisibility(View.GONE);
                deleteGalleryImage();
                break;
            case R.id.g_down_action:
                gAction.setVisibility(View.GONE);
                break;
        }
    }

    private void deleteGalleryImage() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("filename", getFileName(mImageData.getName()));
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "deleteGalleryPhoto", this, params, 102);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void makeItAvatar() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("filename", getFileName(mImageData.getName()));
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "makeItAvatar", this, params, 103);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private String getFileName(String imgUrl) {
        String fileName = "";
        try {
//            https:\/\/d3jgxcdxnzlzyv.cloudfront.net\/users\/thumbnail\/19d722f988736cd7adfdfe19d5c755d9.jpg

            String[] img = imgUrl.replace("/", "-").split("-");
            int length = img.length;
            return img[length - 1];

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    private void downLoadImageToShare() {
        Glide.with(mActivity)
                .asBitmap()
                .load(mImageData.getName())
                .into(new CustomTarget<Bitmap>(256, 256) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        sharePhoto(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private void sharePhoto(Bitmap bmp) {
        try {
            if (bmp != null) {
                // Construct a ShareIntent with link to image
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                String path = MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), bmp, "Image Description", null);
                Uri uri = Uri.parse(path);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, "Share Image"));
            } else {
                Toast.makeText(mActivity, "Sharing Failed !!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
