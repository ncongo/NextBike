package com.bp.project.nextbike;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bp.project.nextbike.Model.Rack;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class returnBike extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();

    int loggedUserId;
    int selectedRackId;

    Button btnReturnBike;
    Spinner spinRacks;
    ArrayList<String> listRacksNames;
    ArrayList<Rack> listRacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_bike);

        Intent intent = getIntent();
        loggedUserId = intent.getIntExtra(Config.KEY_USER_ID, 0);

        Toolbar homeToolbar = (Toolbar)findViewById(R.id.homeToolbar);
        setSupportActionBar(homeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        homeToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(returnBike.this, HomeActivity.class);
                intent.putExtra(Config.KEY_USER_ID, loggedUserId);
                startActivity(intent);
                finish();
            }
        });

        getRacks();

        btnReturnBike = (Button)findViewById(R.id.btnReturnBike);
        btnReturnBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(returnBike.this).setTitle("Vraćanje bicikla").setMessage("Da li ste sigurni da želite vratiti bicikl?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(returnBike.this, "tu treba kod za vracanje bicikla", Toast.LENGTH_SHORT).show();

                                returnBike();

                                //Intent intent = new Intent(returnBike.this, HomeActivity.class);
                                //intent.putExtra(Config.KEY_USER_ID, loggedUserId);
                                //startActivity(intent);
                                //finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    private void getRacks()
    {
        class GetRacks extends AsyncTask<String, String, JSONObject>
        {
            ProgressDialog loading;

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                loading = ProgressDialog.show(returnBike.this, "Fetching...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(JSONObject result)
            {
                loading.dismiss();
                fillRacksList(result);
                final Spinner ddlRacks = (Spinner) findViewById(R.id.spinnerRacks);
                ddlRacks.setAdapter(new ArrayAdapter<String>(returnBike.this, android.R.layout.simple_spinner_dropdown_item, listRacksNames));

                ddlRacks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinRacks = (Spinner) findViewById(R.id.spinnerRacks);

                        selectedRackId = listRacks.get(spinRacks.getSelectedItemPosition()).getId();


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            protected JSONObject doInBackground(String... args) {
                ArrayList params = new ArrayList();
                //params.add(new BasicNameValuePair("id", String.valueOf(loggedUserId)));

                JSONObject json = jsonParser.makeHttpRequest(Config.URL_GET_ALL_RACKS, "GET", params);

                return json;
            }
        }
        GetRacks gj = new GetRacks();
        gj.execute();
    }

    private void fillRacksList(JSONObject result)
    {
        try
        {
            listRacksNames = new ArrayList<String>();
            listRacks = new ArrayList<Rack>();

            JSONArray resultArray = result.getJSONArray(Config.TAG_JSON_ARRAY);

            for(int i=0; i < resultArray.length(); i++) {
                JSONObject c = resultArray.getJSONObject(i);
                Integer id = c.getInt("id");
                String name = c.getString("name");
                Double latitude = c.getDouble("latitude");
                Double longitude = c.getDouble("longitude");
                Integer maxBikes = c.getInt("max_bikes");
                Integer usedBikes = c.getInt("used_bikes");

                Rack r = new Rack();
                r.setId(id);
                r.setName(name);
                r.setLatitude(latitude);
                r.setLongitude(longitude);
                r.setMaxBikes(maxBikes);
                r.setUsedBikes(usedBikes);

                listRacks.add(r);
                listRacksNames.add(c.optString("name"));
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void returnBike()
    {
        class ReturnBike extends AsyncTask<String, String, JSONObject>
        {
            ProgressDialog loading;

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                loading = ProgressDialog.show(returnBike.this, "Fetching...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(JSONObject result)
            {
                loading.dismiss();
                bikeReturned(result);

            }

            @Override
            protected JSONObject doInBackground(String... args) {
                ArrayList params = new ArrayList();
                Log.d("ERROR", "rackid : " + String.valueOf(selectedRackId));
                Log.d("ERROR", "loggeduserid : " + String.valueOf(loggedUserId));
                params.add(new BasicNameValuePair("userId", String.valueOf(loggedUserId)));
                params.add(new BasicNameValuePair("rackId", String.valueOf(selectedRackId)));

                JSONObject json = jsonParser.makeHttpRequest(Config.URL_RETURN_BIKE, "POST", params);

                return json;
            }
        }
        ReturnBike gj = new ReturnBike();
        gj.execute();
    }

    private void bikeReturned(JSONObject result)
    {
        try
        {
            JSONArray resultArray = result.getJSONArray(Config.TAG_JSON_ARRAY);
            JSONObject c = resultArray.getJSONObject(0);
            String success = c.getString("success");
            String message = c.getString("message");

            if(success.equals("1"))
            {
                Toast.makeText(getApplicationContext(), "Uspjesno", Toast.LENGTH_LONG).show();
                //runOnUiThread(new Runnable() {
                //@Override
                //public void run() {
                Intent intent = new Intent(returnBike.this, HomeActivity.class);
                intent.putExtra(Config.KEY_USER_ID, loggedUserId);
                startActivity(intent);
                finish();
                //}
                //});
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Neuspjesno", Toast.LENGTH_LONG).show();
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.myProfile:
                //User chose the "My profile" item
                Intent intentMyProfile = new Intent(returnBike.this, myProfileActivity.class);
                intentMyProfile.putExtra(Config.KEY_USER_ID,loggedUserId);
                startActivity(intentMyProfile);
                finish();
                return true;
            case R.id.signOut:
                //User chose the "Sign out" item
                Intent intentMain = new Intent(returnBike.this, MainActivity.class);
                startActivity(intentMain);
                finish();
                return true;
            default:
                //If we got here, the user's action was not recognized
                //Invoke tje superclass to handle it
                return super.onOptionsItemSelected(item);
        }
    }
}
