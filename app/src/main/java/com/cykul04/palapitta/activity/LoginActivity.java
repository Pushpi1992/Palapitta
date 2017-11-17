package com.cykul04.palapitta.activity;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.cykul04.palapitta.R;
import com.cykul04.palapitta.utils.CommonUtils;
import com.cykul04.palapitta.views.CustomButton;
import com.cykul04.palapitta.views.CustomEditText;
import com.cykul04.palapitta.views.CustomFontTextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private CustomEditText contactEmailEt;
    private CustomEditText contactPasswordEt;
    private CustomButton btnLogin;
    private TextInputLayout input_layout_email, input_layout_password, input_layout_company_name;
    private CustomFontTextView tv_forgot_password;
    private CustomButton btn_sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        contactEmailEt = (CustomEditText) findViewById(R.id.contact_email_et);
        contactPasswordEt = (CustomEditText) findViewById(R.id.contact_password_et);
        tv_forgot_password = (CustomFontTextView) findViewById(R.id.tv_forgot_password);
        input_layout_email = (TextInputLayout) findViewById(R.id.input_layout_email);
        input_layout_password = (TextInputLayout) findViewById(R.id.input_layout_password);
        btnLogin = (CustomButton) findViewById(R.id.btn_login);
        btn_sign_up = (CustomButton) findViewById(R.id.btn_sign_up);
        btnLogin.setOnClickListener(this);
        btn_sign_up.setOnClickListener(this);
        tv_forgot_password.setOnClickListener(this);
    }


    boolean validation() {
        if (contactEmailEt == null && contactPasswordEt == null) {
            return false;
        }
        if (CommonUtils.isNotNull(contactEmailEt.getText().toString())) {
            input_layout_email.setError("Please enter your email");
            return false;
        } else {
            input_layout_email.setErrorEnabled(false);
        }
        if (!CommonUtils.validate(contactEmailEt.getText().toString())) {
            input_layout_email.setError("Please enter valid email");
            return false;
        } else {
            input_layout_email.setErrorEnabled(false);
        }
        if (contactPasswordEt.getText().toString().trim().equalsIgnoreCase("")) {
            input_layout_password.setError("Please enter Password");
            return false;
        } else {
            input_layout_password.setErrorEnabled(false);
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                break;
            case R.id.btn_sign_up:
                break;
        }
    }
}
