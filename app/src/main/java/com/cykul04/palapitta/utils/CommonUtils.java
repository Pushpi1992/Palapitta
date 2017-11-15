package com.cykul04.palapitta.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class CommonUtils {
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
}
