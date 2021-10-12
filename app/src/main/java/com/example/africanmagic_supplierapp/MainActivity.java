package com.example.africanmagic_supplierapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;

import com.example.africanmagic_supplierapp.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String ProductString = "";
    private ArrayList<ClassListSupplier> itemArrayList;  //List items Array
    private MyAppAdapter myAppAdapter; //Array Adapter
    private ListView listView; // Listview
    private boolean success = false; // boolean
    public ConnectionClass connectionClass; //Connection Class Variable

    public String prodStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView); //Listview Declaration
        connectionClass = new ConnectionClass(); // Connection Class Initialization
        itemArrayList = new ArrayList<ClassListSupplier>(); // Arraylist Initialization


        listView.setAdapter(myAppAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String phone = myAppAdapter.suppliesList.get(i).getProductString();
                prodStr = phone;
                Toast.makeText(MainActivity.this, "Book Shipment", Toast.LENGTH_LONG).show();
                openDetails();
            }
        });

        // Calling Async Task
        SyncData orderData = new SyncData();
        orderData.execute("");
    }

    public void openDetails(){
        Intent intent = new Intent(this, CreateShipmentActivity.class);
        intent.putExtra(ProductString, prodStr);
        startActivity(intent);
    }

    // Async Task has three overrided methods,
    private class SyncData extends AsyncTask<String, String, String>
    {
        String msg = "Check Credentials!";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dialog
        {
            progress = ProgressDialog.show(MainActivity.this, "Synchronising",
                    "Pending Shipments Loading! Please Wait...", true);
        }

        @Override
        protected String doInBackground(String... strings)  // Connect to the database, write query and add items to array list
        {
            try
            {
                Connection conn = connectionClass.CONN(); //Connection Object
                if (conn == null)
                {

                    success = false;
                }
                else {
                    // Change below query according to your own database.
                    String query = "SELECT * FROM dbo.PurchaseOrders;";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next())
                        {
                            try {
                                itemArrayList.add(new ClassListSupplier(rs.getString("ProductNeeded"),rs.getInt("PurchaseOrderID")));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        msg = "Fetched Deliveries From Server.";
                        success = true;
                    } else {
                        msg = "No Data found!";
                        success = false;
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                msg = writer.toString();
                success = false;
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) // disimissing progress dialoge, showing error and setting up my listview
        {
            progress.dismiss();
            Toast.makeText(MainActivity.this, msg + "", Toast.LENGTH_LONG).show();
            if (success == false)
            {
            }
            else {
                try {
                    myAppAdapter = new MyAppAdapter(itemArrayList, MainActivity.this);
                    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    listView.setAdapter((ListAdapter) myAppAdapter);
                } catch (Exception ex)
                {

                }

            }
        }
    }

    public class MyAppAdapter extends BaseAdapter //has a class viewholder which holds
    {
        public class ViewHolder
        {
            TextView textName;

        }

        public List<ClassListSupplier> suppliesList;

        public Context context;
        ArrayList<ClassListSupplier> arraylist;

        private MyAppAdapter(ArrayList<ClassListSupplier> apps, MainActivity context)
        {
            this.suppliesList = apps;
            this.context = context;
            arraylist = new ArrayList<ClassListSupplier>();
            arraylist.addAll(suppliesList);
        }

        @Override
        public int getCount() {
            return suppliesList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) // inflating the layout and initializing widgets
        {

            View rowView = convertView;
            ViewHolder viewHolder= null;
            if (rowView == null)
            {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.list_content, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textName = (TextView) rowView.findViewById(R.id.textName);
                rowView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //String[] products = suppliesList.get(position).getProductString().split(",");

            viewHolder.textName.setText("Purchase #: " + suppliesList.get(position).getOrderId() + " " + suppliesList.get(position).getProductString());
            return rowView;
        }
    }

}