package com.cykul04.palapitta.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cykul04.palapitta.R;
import com.cykul04.palapitta.fragment.CustomBottomSheetOtpDialogFragment;
import com.cykul04.palapitta.utils.CommonUtils;
import com.cykul04.palapitta.utils.Constants;
import com.cykul04.palapitta.utils.Prefs;
import com.cykul04.palapitta.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private EditText organisationEt, first_name, last_name, mobile_number;
    private ProgressDialog progressDialog;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private TextInputLayout first_name_layout, last_name_layout, event_code_layout, mobile_number_layout;
    private TextInputLayout password_layout;
    private TextInputLayout confirm_password_layout;
    private EditText password_edt;
    private EditText confirm_password_edt;
    private EditText email_edt;
    private TextInputLayout email_layout;
    private TextInputLayout birth_layout;
    private EditText date_of_birth_edt;
    private Spinner pass_spinner;
    private Spinner category_spinner;
    private String type;
    private String subType;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);
        getSupportActionBar().setElevation(0);
        checkAndRequestPermissions();
        organisationEt = (EditText) findViewById(R.id.eventcode_edt);
        email_edt = (EditText) findViewById(R.id.email_edt);
        date_of_birth_edt = (EditText) findViewById(R.id.date_of_birth_edt);
        // confirm_password_edt = (EditText) findViewById(R.id.confirm_password_edt);
        first_name = (EditText) findViewById(R.id.fname);
        last_name = (EditText) findViewById(R.id.lname);
        mobile_number = (EditText) findViewById(R.id.mobileNo_edt);
        mobile_number_layout = (TextInputLayout) findViewById(R.id.mobile_number_layout);
        Button submitbtn = (Button) findViewById(R.id.submitBtn);
        Button loginBtn = (Button) findViewById(R.id.btn_sign_up);
        first_name_layout = (TextInputLayout) findViewById(R.id.first_name_layout);
        email_layout = (TextInputLayout) findViewById(R.id.email_layout);
        birth_layout = (TextInputLayout) findViewById(R.id.birth_layout);
        last_name_layout = (TextInputLayout) findViewById(R.id.last_name_layout);
        event_code_layout = (TextInputLayout) findViewById(R.id.event_code_layout);
        spinnerValues();
        //confirm_password_layout = (TextInputLayout) findViewById(R.id.confirm_password_layout);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    registration();
                }
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        date_of_birth_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        int s = monthOfYear + 1;
                        String a = dayOfMonth + "/" + s + "/" + year;
                        date_of_birth_edt.setText("" + a);
                    }
                };

                Time date = new Time();
                DatePickerDialog d = new DatePickerDialog(RegistrationActivity.this, dpd, date.year, date.month, date.monthDay);
                d.show();

            }
        });
    }

    private void spinnerValues() {
        String[] celebrities = {"Subscription", "One Time Visit", "Monthly"};
        String[] category = {"Category", "Adult", "Child(below 12 years)"};
        pass_spinner = (Spinner) findViewById(R.id.pass_spinner);
        category_spinner = (Spinner) findViewById(R.id.spinner);
        setSpinnerView(celebrities, pass_spinner);
        setSpinnerView(category, category_spinner);
        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1){
                    subType = "Adult";
                }else if (position == 2){
                    subType = "Child(below 12 years)";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                subType = "";
            }
        });

        pass_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1){
                    type = "One Time Visit";
                }else if (position == 2){
                    type = "Monthly";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                type = "";
            }
        });
    }

    private void setSpinnerView(String[] celebrities, Spinner spinner) {


        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.support_simple_spinner_dropdown_item, celebrities) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the second item from Spinner
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }


    private void registration() {
        if (Utils.isNetConnected(this)) {
            progressDialog = new ProgressDialog(this);
            progressDialog.show();
            progressDialog.setMessage("Please Wait...");
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.REGISTRATION),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressDialog.dismiss();
                                Log.d("sharath", "----->" + response);
                                JSONObject jsonObject = new JSONObject(response);
                                String result = jsonObject.getString(Constants.RESULT_STATUS);
                                String reportStatus = jsonObject.getString(Constants.REPORT_STATUS);
                                if (result.equals(Constants.TRUE)) {
                                    Prefs.putString("firstname", first_name.getText().toString());
                                    Prefs.putString("lastname", last_name.getText().toString());
                                    Prefs.putString("email_id", email_edt.getText().toString());
                                    Prefs.putString("mobile", mobile_number.getText().toString());
                                    Prefs.putString("dob", date_of_birth_edt.getText().toString());
                                    Prefs.putString("organization", organisationEt.getText().toString());
                                    Prefs.putString("type", type);
                                    Prefs.putString("subType", subType);
                                    CustomBottomSheetOtpDialogFragment.newInstance().show(getSupportFragmentManager(), "CustomBottomSheetOtpDialogFragment");
                                    Toast.makeText(RegistrationActivity.this, reportStatus, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegistrationActivity.this, reportStatus, Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(RegistrationActivity.this, "Please check your Network", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(RegistrationActivity.this, "Please check your Network", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                //TODO
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("firstname", first_name.getText().toString());
                    params.put("lastname", last_name.getText().toString());
                    params.put("email_id", email_edt.getText().toString());
                    params.put("mobile", mobile_number.getText().toString());
                    params.put("dob", date_of_birth_edt.getText().toString());
                    params.put("organization", organisationEt.getText().toString());
                    params.put("type", type);
                    params.put("subType", subType);
                    //params.put("password",password_edt.getText().toString());
                    return params;
                }
            };

            requestQueue.add(stringRequest);
        } else
            Utils.showDialog(this);
    }

    boolean validation() {
        if (CommonUtils.isNotNull(first_name.getText().toString())) {
            first_name_layout.setError("Enter firstname");
            return false;
        } else {
            first_name_layout.setErrorEnabled(false);
        }
        if (CommonUtils.isNotNull(last_name.getText().toString())) {
            last_name_layout.setError("Enter lastname");
            return false;
        } else {
            last_name_layout.setErrorEnabled(false);
        }

        if (CommonUtils.isNotNull(mobile_number.getText().toString())) {
            mobile_number_layout.setError("Enter mobile number");
            return false;
        } else {
            mobile_number_layout.setErrorEnabled(false);
        }

        if (CommonUtils.isNotNull(email_edt.getText().toString())) {
            email_layout.setError("Enter email");
            return false;
        } else {
            email_layout.setErrorEnabled(false);
        }
        if (!CommonUtils.validate(email_edt.getText().toString())) {
            email_layout.setError("Please enter valid email");
            return false;
        } else {
            email_layout.setErrorEnabled(false);
        }
        if (CommonUtils.isNotNull(date_of_birth_edt.getText().toString())) {
            birth_layout.setError("Enter email");
            return false;
        } else {
            birth_layout.setErrorEnabled(false);
        }
        if (CommonUtils.isNotNull(organisationEt.getText().toString())) {
            event_code_layout.setError("Enter your organisation");
            return false;
        } else {
            event_code_layout.setErrorEnabled(false);
        }
        if (CommonUtils.isNotNull(type)) {
            CommonUtils.showToastMessage(this,"select subscription type");
            return false;
        } else {
        }
        if (CommonUtils.isNotNull(subType)) {
            CommonUtils.showToastMessage(this,"select category type");
            return false;
        } else {
        }
        return true;
    }


    private boolean checkAndRequestPermissions() {
        int permissionSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int phoneStatePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int smsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (phoneStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (smsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showDialogOK("Camera ,Call ,SMS, Storage Service Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();

                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

}