package com.cykul04.palapitta.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    //Email Pattern
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    public static boolean validate(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isNotNull(String txt) {
        return !(txt != null && txt.trim().length() > 0);
    }
    //Toast Message Display ...EMP 32
    public static void showToastMessage(Context context, String s) {
        if (context != null)
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

    }
    public static void alertDialog(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //finish();
                    }
                });
               /* .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                }*/

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("");
        alert.show();
    }
    public static String imageAsByteArrayString(String selectedImagePath, ImageView ivpfUpload, boolean circleimage) {

        File imgFile = new File(selectedImagePath);

        Bitmap bitmap = CommonUtils.decodeFile(imgFile);
        Bitmap bitmap1 = rotateImage(bitmap, imgFile);

        if (circleimage) {
            Bitmap resized = Bitmap.createScaledBitmap(bitmap1, 200, 200, true);
            ivpfUpload.setImageBitmap(resized);
        } else {
            ivpfUpload.setVisibility(View.VISIBLE);
            ivpfUpload.setImageBitmap(bitmap1);
            ivpfUpload.getFitsSystemWindows();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

    }

    public static File rotateImageAndStoreLocalFile(String pictureImagePath) {
        String filename = "palapitta.png";
        File sd = Environment.getExternalStorageDirectory();
        File dest = new File(sd, filename);
        try {
            FileOutputStream out = new FileOutputStream(dest);
            CommonUtils.rotateImage(CommonUtils.decodeFile(new File(pictureImagePath)), new File(pictureImagePath)).compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dest;
    }
    private static Bitmap decodeFile(File f) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_SIZE = 400;

            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap rotateImage(Bitmap bitmap, File fileName) {
        int rotateAngle = 0;
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(fileName.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotateAngle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotateAngle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotateAngle = 270;
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                break;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateAngle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
    }
}
