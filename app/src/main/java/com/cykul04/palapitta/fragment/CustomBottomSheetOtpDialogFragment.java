package com.cykul04.palapitta.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.cykul04.palapitta.activity.MainActivity;
import com.cykul04.palapitta.listeners.OTPListener;
import com.cykul04.palapitta.receivers.OtpReader;
import com.cykul04.palapitta.utils.CommonUtils;
import com.cykul04.palapitta.utils.Prefs;
import com.cykul04.palapitta.utils.Utils;
import com.cykul04.palapitta.views.CustomButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CustomBottomSheetOtpDialogFragment extends BottomSheetDialogFragment implements OTPListener {
    EditText text1, text2, text3, text4, text5, text6;
    TextView text;
    CustomButton submitBtn;
    LinearLayout layout1;
    String otp, otp_value;
    private EditText mCurrentlyFocusedEditText;

    public static CustomBottomSheetOtpDialogFragment newInstance() {
        CustomBottomSheetOtpDialogFragment fragment = new CustomBottomSheetOtpDialogFragment();
        return fragment;
    }
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        OtpReader.bind(CustomBottomSheetOtpDialogFragment.this, "CYKULs");
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_otp, null);
        dialog.setContentView(contentView);
        text1 = (EditText) contentView.findViewById(R.id.text1);
        text2 = (EditText) contentView.findViewById(R.id.text2);
        text3 = (EditText) contentView.findViewById(R.id.text3);
        text4 = (EditText) contentView.findViewById(R.id.text4);
        text5 = (EditText) contentView.findViewById(R.id.text5);
        text6 = (EditText) contentView.findViewById(R.id.text6);
        text = (TextView) contentView.findViewById(R.id.text);
        layout1 = (LinearLayout) contentView.findViewById(R.id.layout1);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        submitBtn = (CustomButton) contentView.findViewById(R.id.submit);
        setFocusListener();
        setOnTextChangeListener();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otpValidation()) {
                    getOTPValues();
                    sendOTP();
                } else {
                    Toast.makeText(getActivity(), "Please enter OTP", Toast.LENGTH_SHORT).show();
                }
            }
                //need to remove just to check flow addend to avoid server call

        });



    }

    public void getOTPValues() {
        otp_value = text1.getText().toString().trim() + text2.getText().toString().trim() + text3.getText().toString().trim() + text4.getText().toString().trim() + text5.getText().toString().trim() + text6.getText().toString().trim();

    }

    @Override
    public void otpReceived(String messageText) {
        Log.e("Otp Received", "" + "Otp Received"+messageText);
        otp = messageText.substring(0, 6);
        Log.e("otp", "" + otp);
        char[] charArray = otp.toCharArray();

        if (charArray.length == 6) {
            text1.setText(String.valueOf(charArray[0]));
            text2.setText(String.valueOf(charArray[1]));
            text3.setText(String.valueOf(charArray[2]));
            text4.setText(String.valueOf(charArray[3]));
            text5.setText(String.valueOf(charArray[4]));
            text6.setText(String.valueOf(charArray[5]));
        } else {
            Toast.makeText(getContext(), "Enter and submit OTP for your mobile verification", Toast.LENGTH_SHORT).show();
        }

    }

    boolean otpValidation() {
        if (CommonUtils.isNotNull(text1.getText().toString())) {
            return false;
        } else if (CommonUtils.isNotNull(text2.getText().toString())) {
            return false;
        } else if (CommonUtils.isNotNull(text3.getText().toString())) {
            return false;
        } else if (CommonUtils.isNotNull(text4.getText().toString())) {
            return false;
        } else if (CommonUtils.isNotNull(text5.getText().toString())) {
            return false;
        } else if (CommonUtils.isNotNull(text6.getText().toString())) {
            return false;
        }
        return true;
    }

    private void sendOTP() {

        if (Utils.isNetConnected(getContext())) {
           /* final String android_id = Settings.Secure.getString(getContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);*/
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.show();
            progressDialog.setMessage("Please Wait...");
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.OTP_VERIFICATION),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                progressDialog.dismiss();
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d("sharath","22222220"+response);
                                String result = jsonObject.getString("result_status");
                                String reportStatus = jsonObject.getString("report_status");
                                if (result.equals("true")) {
                                    Intent login = new Intent(getActivity(), MainActivity.class);
                                    login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getActivity().startActivity(login);
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(getContext(), "" + reportStatus, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(), "Please check your Network", Toast.LENGTH_SHORT).show();
                            } else if (error != null && error.getMessage() != null) {
                                Toast.makeText(getContext(), "error VOLLEY " + error.getMessage(), Toast.LENGTH_LONG).show();
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(getContext(), "Please check your Network", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                //TODO
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("firstname", Prefs.getString("firstname", ""));
                    params.put("lastname", Prefs.getString("lastname", ""));
                    params.put("email_id", Prefs.getString("email_id", ""));
                    params.put("mobile", Prefs.getString("mobile", ""));
                    params.put("dob", Prefs.getString("dob", ""));
                    params.put("organization", Prefs.getString("organization", ""));
                    params.put("type", Prefs.getString("type", ""));
                    params.put("subType",Prefs.getString("subType", ""));
                    params.put("generatedOTP", otp_value);
                    Log.d("sharath","--->"+params);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        } else{
            Utils.showDialog(getContext());

        }

    }

    private void setFocusListener() {
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mCurrentlyFocusedEditText = (EditText) v;
                mCurrentlyFocusedEditText.setSelection(mCurrentlyFocusedEditText.getText().length());
            }
        };
        text1.setOnFocusChangeListener(onFocusChangeListener);
        text2.setOnFocusChangeListener(onFocusChangeListener);
        text3.setOnFocusChangeListener(onFocusChangeListener);
        text4.setOnFocusChangeListener(onFocusChangeListener);
        text5.setOnFocusChangeListener(onFocusChangeListener);
        text6.setOnFocusChangeListener(onFocusChangeListener);
    }

    private void setOnTextChangeListener() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mCurrentlyFocusedEditText.getText().length() >= 1
                        && mCurrentlyFocusedEditText != text6) {
                    mCurrentlyFocusedEditText.focusSearch(View.FOCUS_RIGHT).requestFocus();
                } else {
                    String currentValue = mCurrentlyFocusedEditText.getText().toString();
                    if (currentValue.length() <= 0 && mCurrentlyFocusedEditText.getSelectionStart() <= 0 && mCurrentlyFocusedEditText != text1) {
                        mCurrentlyFocusedEditText.focusSearch(View.FOCUS_LEFT).requestFocus();
                    }
                }
            }
        };
        text1.addTextChangedListener(textWatcher);
        text2.addTextChangedListener(textWatcher);
        text3.addTextChangedListener(textWatcher);
        text4.addTextChangedListener(textWatcher);
        text5.addTextChangedListener(textWatcher);
        text6.addTextChangedListener(textWatcher);
    }
}
