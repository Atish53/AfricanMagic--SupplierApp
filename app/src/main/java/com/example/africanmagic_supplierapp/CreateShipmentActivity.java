package com.example.africanmagic_supplierapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.telephony.SmsManager;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.africanmagic_supplierapp.R;

public class CreateShipmentActivity extends AppCompatActivity {

    public ConnectionClass connectionClass; //Connection Class Variable

    public String subject = "";
    public String description = "";
    public String date = "";
    public String time = "";
    public Boolean confirmed = false; //Int 0 for false anyway...
    public String notes = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shipment);
        connectionClass = new ConnectionClass(); // Connection Class Initialization

        final Intent intent = getIntent();
        final String prostr = intent.getStringExtra(MainActivity.ProductString);

        TextView descriptionText = findViewById(R.id.productText);
        descriptionText.setText(prostr);
        description = prostr;

        Button insertBtn = findViewById(R.id.confirmedUpdate);
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder successSQL = new AlertDialog.Builder(CreateShipmentActivity.this);
                successSQL.setMessage("Confirm New Shipment").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //SQL Update Occurs Here............

                                EditText subjectT = (EditText)findViewById(R.id.subjectS);
                                subject = subjectT.getText().toString();

                                DatePicker datePicker = (DatePicker)findViewById(R.id.datePick);
                                int dayi;
                                int monthi;
                                int yeari;
                                dayi = datePicker.getDayOfMonth();
                                monthi = datePicker.getMonth()+1;
                                yeari = datePicker.getYear();
                                String dateStr = dayi+"/"+monthi+"/"+yeari;
                                date = dateStr;
                                TimePicker timePicker = (TimePicker)findViewById(R.id.timePick) ;
                                int timemi;
                                int timehi;
                                timemi = timePicker.getMinute();
                                timehi = timePicker.getHour();
                                String timeStr = timehi+":"+timemi;
                                time = timeStr;

                                try {
                                    Connection conn = connectionClass.CONN(); //Connection Object
                                    if (conn == null) {
                                        Toast.makeText(CreateShipmentActivity.this, "Connection Failed", Toast.LENGTH_LONG).show();
                                    } else {
                                        String query = "INSERT INTO SupplierShippings (Subject,Description,Date,Time,Confirmed,Notes) VALUES ('"+subject+"','"+description+"','"+date+"','"+time+"',0,'"+"No additional notes"+"');";
                                        Statement stmt = conn.createStatement();
                                        stmt.executeQuery(query);
                                    }
                                } catch (Exception e) {
                                    //Error
                                    Toast.makeText(CreateShipmentActivity.this, "Successfully Created Shipment Request", Toast.LENGTH_LONG).show();
                                }
                                Intent refresh = new Intent(CreateShipmentActivity.this, MainActivity.class);
                                startActivity(refresh);//Start the same Activity
                                finish(); //finish Activity.
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(CreateShipmentActivity.this, "Cancelled New Shipment", Toast.LENGTH_LONG).show();
                            }
                        });
                AlertDialog alertDialog = successSQL.create();
                alertDialog.setTitle("Shipment");
                alertDialog.show();
            }
        });
    }
}