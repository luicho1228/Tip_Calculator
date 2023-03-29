package com.example.tipcalculator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements TextWatcher {

    private EditText editTextBillAmount;
    private TextView textViewBillAmount;
    private TextView textViewPercent;
    private TextView tipTextView;
    private SeekBar seekBar;
    private TextView totalTextView;
    private Spinner spinner;
    private TextView totalPLabel;
    private TextView totalP;
    private ArrayAdapter<CharSequence> adapter;
    private RadioGroup radioGroup;
    private ImageButton infoButton;
    private double billAmount = 0;
    private double percent = .15;
    private double tip;
    private double total;
    private double totalPerPerson;
    private int numOfPeople;
    private boolean isTipRounded;
    private boolean isTotalRounded;
    private static final DecimalFormat dFormat = new DecimalFormat("0.00");
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();
    private String amount;
    private String tipAmount;
    private String totalBill;
    private String bill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextBillAmount = (EditText)findViewById(R.id.editText_BillAmount);
        editTextBillAmount.addTextChangedListener((TextWatcher) this);
        textViewBillAmount = (TextView)findViewById(R.id.textView_BillAmount);
        textViewPercent = (TextView) findViewById(R.id.textViewPercent);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        seekBar = (SeekBar) findViewById(R.id.seekBar3);
        totalTextView = (TextView) findViewById(R.id.totalTextView);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                percent =((double) progress/100);
                calculate();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        spinner = (Spinner)findViewById(R.id.numPeople_Spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.numberPeople, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        totalPLabel = (TextView) findViewById(R.id.totalPersonLabel);
        totalP = (TextView) findViewById(R.id.totalPerPerson);
        totalPLabel.setVisibility(totalPLabel.INVISIBLE);
        totalP.setVisibility(totalP.INVISIBLE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0) {
                        totalPLabel.setVisibility(totalPLabel.VISIBLE);
                        totalP.setVisibility(totalP.VISIBLE);
                        numOfPeople = position+1;
                        calculate();
                    }
                if(position == 0) {
                    totalPLabel.setVisibility(totalPLabel.INVISIBLE);
                    totalP.setVisibility(totalP.INVISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                totalPLabel.setVisibility(totalPLabel.INVISIBLE);
                totalP.setVisibility(totalP.INVISIBLE);
            }
        });
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == 1 ){
                    isTipRounded = true;
                    isTotalRounded = false;
                    calculate();
                }
                else if(checkedId == 2){
                    isTotalRounded = true;
                    isTipRounded = false;
                   calculate();
                }
                else{
                    isTipRounded = false;
                    isTotalRounded = false;
                    calculate();
                }
            }
        });
        infoButton = (ImageButton) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                displayInfo();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
       try {
           billAmount = Double.parseDouble(charSequence.toString()) / 100;
       } catch (NumberFormatException e) {
           e.printStackTrace();
       }
        textViewBillAmount.setText(currencyFormat.format(billAmount));
         calculate();
    }
    @Override
    public void afterTextChanged(Editable s) {}

    private void calculateRounded() {
        if (isTipRounded) {
            tip = Math.ceil(tip);
            total = tip + billAmount;
            totalPerPerson =  (total)/numOfPeople;
            String tipRoundedText= " + Tips: $" + dFormat.format(tip);
            bill = amount +"\n" + tipRoundedText + "\n" + totalBill;
        }
        if (isTotalRounded) {
            total = Math.ceil(total);
            totalPerPerson =  total/numOfPeople;
            totalBill ="-------------------------------" + "\n" + "Total Bill amount: $" + dFormat.format(total);
            bill = amount +"\n" + tipAmount + "\n" + total;
        }

    }
    private void calculate() {
        textViewPercent.setText(percentFormat.format(percent));
        tip = billAmount * percent;
        total = tip + billAmount;
        totalPerPerson = total / numOfPeople;
        amount = "Amount: $" + billAmount;
        tipAmount = "+ Tips: $" + dFormat.format(tip);
        totalBill ="-------------------------------------"
                + "\n" +
                "Total Bill amount: $" + dFormat.format(total);
        bill = amount
                +"\n" +
                tipAmount
                + "\n" +
                totalBill;

        calculateRounded();
        if (numOfPeople > 1){
            bill =  amount
                    +"\n" +
                    tipAmount
                    + "\n" +
                    "Bill split into: " + numOfPeople + " People"
                    + "\n" +
                    "total per person: $" + dFormat.format(totalPerPerson)
                    + "\n" +
                    totalBill;
        }
        setBillText(tip,total,totalPerPerson);
    }
    private void setBillText(double tip,double total, double totalPerPerson){
        tipTextView.setText(currencyFormat.format(tip));
        totalTextView.setText(currencyFormat.format(total));
        totalP.setText(currencyFormat.format(totalPerPerson));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Tip Calculator Receipt");
        intent.putExtra(Intent.EXTRA_TEXT, bill);
        startActivity(Intent.createChooser(intent, "Share via"));
        return super.onOptionsItemSelected(item);
    }

    public void displayInfo(){
        Context context = getApplicationContext();
        CharSequence info = "The dropdown menu is used to split the bill between a fixed number of people.(Up to 6 people)";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, info, duration);
        toast.show();
    }

}