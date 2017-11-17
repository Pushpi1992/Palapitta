package com.cykul04.palapitta.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.cykul04.palapitta.R;
import com.cykul04.palapitta.utils.Prefs;
import com.paytm.pgsdk.Log;
import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private String amount;
    private String orderId;
    private String customerId;
    private String txn_status;
    private String txn_orderId;
    private String txn_amount;
    private String txn_date;
    private String txn_id;
    private String txn_checksum;
    private String txn_response_msg;
    private TextView available_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        available_tv = (TextView)findViewById(R.id.available_tv);
        available_tv.setText(Prefs.getString("message",""));
        Prefs.putBoolean(Prefs.REGISTERED,true);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
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
        switch (item.getItemId()) {
            case R.id.parkDetails:
                startActivity(new Intent(MainActivity.this,PaytmAndProfileActivity.class));
                break;
            case R.id.contactus:
                break;
           /* case R.id.transactions:
                paytmPayment();
                break;
            case R.id.contactus:
                break;
            case R.id.logout:
                Prefs.logoutUser(this);
                break;*/
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;

    }

    private void initOrderId() {
        Random r = new Random(System.currentTimeMillis());
        orderId = "CYKHRBSUB" + (1 + r.nextInt(2)) * 100000 + r.nextInt(100000);
    }

    private void initCustomerId() {
        Random r = new Random(System.currentTimeMillis());
        customerId = "CUST" + (1 + r.nextInt(2)) * 100000 + r.nextInt(100000);

    }
    private void paytmPayment()
    {
        amount = String.valueOf(500);
        if(!amount.contains(".")){
            amount = amount+".00";
        }
        initOrderId();
        initCustomerId();

        PaytmPGService Service = PaytmPGService.getProductionService();
        Map<String, String> paramMap = new HashMap<String, String>();

        paramMap.put("ORDER_ID", orderId);
        paramMap.put("CUST_ID", customerId);
        paramMap.put("MID", getString(R.string.MID));
        paramMap.put("CHANNEL_ID", getString(R.string.CHANNEL_ID));
        paramMap.put("INDUSTRY_TYPE_ID", getString(R.string.INDUSTRY_TYPE_ID));
        paramMap.put("WEBSITE", getString(R.string.WEBSITE));
        paramMap.put("TXN_AMOUNT", amount);
        paramMap.put("THEME", "merchant");
        PaytmOrder Order = new PaytmOrder(paramMap);

        String MERCHANT1 = getString(R.string.MERCHANT1);
        String MERCHANT2 = getString(R.string.MERCHANT2);


        PaytmMerchant Merchant = new PaytmMerchant(MERCHANT1 , MERCHANT2);


        Service.initialize(Order, Merchant, null);
        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to
                        // initialization of webview.  Error Message details
                        // the error occurred.
                    }


                    @Override
                    public void onTransactionSuccess(Bundle inResponse) {
                        // After successful transaction this method gets called.
                        // // Response bundle contains the merchant response
                        // parameters.
                        Log.d("LOG", "Payment successful " + inResponse);
                        Toast.makeText(MainActivity.this, "Payment Transaction is successful", Toast.LENGTH_LONG).show();
                        txn_status = inResponse.getString("STATUS");
                        txn_orderId = inResponse.getString("ORDERID");
                        txn_amount = inResponse.getString("TXNAMOUNT");
                        txn_date = inResponse.getString("TXNDATE");
                        txn_id = inResponse.getString("TXNID");
                        txn_checksum = inResponse.getString("IS_CHECKSUM_VALID");
                        txn_response_msg = inResponse.getString("RESPMSG");

                       // statusCheck();
                    }


                    @Override
                    public void onTransactionFailure(String inErrorMessage,
                                                     Bundle inResponse) {
                        // This method gets called if transaction failed. //
                        // Here in this case transaction is completed, but with
                        // a failure. // Error Message describes the reason for
                        // failure. // Response bundle contains the merchant
                        // response parameters.
                        Log.d("LOG", "Payment failed " + inErrorMessage);
                        Toast.makeText(MainActivity.this, "Payment Transaction Failed", Toast.LENGTH_LONG).show();
                        txn_status = inResponse.getString("STATUS");
                        txn_orderId = inResponse.getString("ORDERID");
                        txn_amount = inResponse.getString("TXNAMOUNT");
                        txn_date = inResponse.getString("TXNDATE");
                        txn_id = inResponse.getString("TXNID");
                        txn_checksum = inResponse.getString("IS_CHECKSUM_VALID");
                        txn_response_msg = inResponse.getString("RESPMSG");
                        Log.d("Failure", "Failure");

                    }


                    @Override
                    public void networkNotAvailable() { // If network is not
                        // available, then this
                        // method gets called.
                        //Utils.showDialog(AfoActivity.this);
                    }


                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                    }


                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {


                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                    }


                });

    }

}
