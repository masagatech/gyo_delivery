package com.goyo.grocery_goyo;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.goyo.grocery.R;
import com.goyo.grocery_goyo.Global.global;
import com.vstechlab.easyfonts.EasyFonts;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class CustomerBill extends AppCompatActivity {
    private ExpandableListView mListView;
    private ExpandableListAdapter expandableListAdapter;
    private TextView txtBillDisplay, txtAmountDisplay, txtDeliveryDisplay, txtAmountValue, txtDeliveryValue;
    private Button btnForPayment;

    private android.support.v7.app.ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_bill);
        Intent io = getIntent();
        // customerBillDetailsList= (ArrayList<CustomerBillDetails>)io.getSerializableExtra("bill");
        mListView = (ExpandableListView)findViewById(R.id.list_display_bill);
        ArrayList<CustomerBillDetails> cc = new ArrayList<>();
        for (Map.Entry<Integer, CustomerBillDetails> entry : global.myCart.entrySet()) {

            cc.add(entry.getValue());
            //System.out.println(entry.getKey() + "/" + entry.getValue());
        }
        txtAmountValue = (TextView) findViewById(R.id.txtAmountValue);
        btnForPayment = (Button) findViewById(R.id.btnForPayment);
        //Setting the total amount of the bill in the final cart
        txtAmountValue.setText(ResturantProfile.totalAmount.getText().toString() + ".00");
        btnForPayment.setText(btnForPayment.getText().toString()+" "+ResturantProfile.totalAmount.getText().toString() + ".00");
        HashMap<List<String>,List<CustomerBillDetails>> details=new HashMap<List<String>,List<CustomerBillDetails>>();
        details.put(global.resturantNames,cc);
        expandableListAdapter=new CustomBillAdapter(CustomerBill.this,global.resturantNames,details);
        mListView.setAdapter(expandableListAdapter);
        actionBar = getSupportActionBar();
        actionBar.hide();
        setFonts();
    }
    private void setFonts() {
        txtBillDisplay = (TextView) findViewById(R.id.txtBillDisplay);
        txtAmountDisplay = (TextView) findViewById(R.id.txtAmountDisplay);
        txtDeliveryDisplay = (TextView) findViewById(R.id.txtDeliveryDisplay);
        txtDeliveryValue = (TextView) findViewById(R.id.txtDeliveryValue);
        //EasyFonts library added by me in the gradle dependency to use
        //custom fonts for displaying on the screens
        txtBillDisplay.setTypeface(EasyFonts.robotoLight(this));
        txtAmountValue.setTypeface(EasyFonts.droidSerifItalic(this));
        txtAmountDisplay.setTypeface(EasyFonts.caviarDreamsBold(this));
        txtDeliveryDisplay.setTypeface(EasyFonts.caviarDreamsBold(this));
        txtDeliveryValue.setTypeface(EasyFonts.droidSerifItalic(this));
    }
}
