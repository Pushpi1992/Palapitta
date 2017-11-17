package com.cykul04.palapitta.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.cykul04.palapitta.BuildConfig;
import com.cykul04.palapitta.R;
import com.cykul04.palapitta.utils.CommonUtils;
import com.cykul04.palapitta.utils.Constants;
import com.cykul04.palapitta.utils.FileUtils;
import com.cykul04.palapitta.utils.Utils;
import com.cykul04.palapitta.views.CircularImageView;
import com.cykul04.palapitta.views.CustomButton;

import java.io.File;

public class ProfileAndIdproofActivity extends AppCompatActivity implements View.OnClickListener {

    private CircularImageView imv_take_pic;
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private String pictureImagePath;
    private CustomButton select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_and_idproof);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imv_take_pic = (CircularImageView) findViewById(R.id.imv_take_pic);
        select = (CustomButton) findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        imv_take_pic.setOnClickListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imv_take_pic:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Utils.checkLocationPermission(this, Manifest.permission.CAMERA)) {
                        if (Utils.checkLocationPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                            if (Utils.checkLocationPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE))
                                selectImage();
                    }
                } else {
                    selectImage();
                }
                break;
        }
    }
    public void selectImage() {
        final CharSequence[] items = {Constants.TAKE_PHOTO, Constants.CANCEL};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Constants.ADD_PHOTO);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(Constants.TAKE_PHOTO)) {
                    openCemera();

                }  else if (items[item].equals(Constants.CANCEL)) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void openCemera() {
        String imageFileName = System.currentTimeMillis() / 1000 + ".jpg";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator;
        File outputDir = new File(path);
        outputDir.mkdirs();
       /* File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);*/
        if (outputDir != null && outputDir.exists()) {
            pictureImagePath = outputDir.getAbsolutePath() + "/" + imageFileName;
            if (!pictureImagePath.equalsIgnoreCase("")) {
                File file = new File(pictureImagePath);
                //in nagaut you file Uri.fromFile(file) will give crash so we nee to add this FileProvider
                Uri outputFileUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID, file);
                if (outputFileUri != null) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(cameraIntent, REQUEST_CAMERA);
                } else {
                    CommonUtils.showToastMessage(this, Constants.IMAGE_FORMAT_NOT_SUPPORTED);
                }
            } else {
                CommonUtils.showToastMessage(this, Constants.IMAGE_FORMAT_NOT_SUPPORTED);
            }
        } else {
            CommonUtils.showToastMessage(this, Constants.IMAGE_FORMAT_NOT_SUPPORTED);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            if (pictureImagePath != null && !pictureImagePath.equalsIgnoreCase("")) {
               convertimageAsByteArrayString(pictureImagePath, CommonUtils.rotateImageAndStoreLocalFile(pictureImagePath));
            } else {
                CommonUtils.showToastMessage(this, Constants.IMAGE_FORMAT_NOT_SUPPORTED);
            }


        } else if (requestCode == SELECT_FILE) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                try {
                    String s = FileUtils.getPath(this, selectedImageUri);
                    if (s != null) {
                        File imageFile = new File(s);
                        //convertimageAsByteArrayString(s, imageFile);
                    } else {
                        CommonUtils.showToastMessage(this, Constants.IMAGE_FORMAT_NOT_SUPPORTED);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CommonUtils.showToastMessage(this, Constants.UNABLE_TO_TAKE_IMAGE);
                }
            }
        }
    }

    private void convertimageAsByteArrayString(String pictureImagePath, File file) {
        CommonUtils.imageAsByteArrayString(pictureImagePath,imv_take_pic,true);
    }
}
