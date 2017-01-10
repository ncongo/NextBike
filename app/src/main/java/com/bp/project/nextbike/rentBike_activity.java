package com.bp.project.nextbike;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bp.project.nextbike.Model.Bike;
import com.bp.project.nextbike.Model.Rack;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class rentBike_activity extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();

    private int loggedUserId;
    Button btnGetBike;
    Spinner spinRacks;
    Spinner spinBikes;

    int selectedRackId, selectedBikeId;

    ArrayList<String> listRacksNames;
    ArrayList<Rack> listRacks;

    ArrayList<Integer> listAvailableBikesIDs;
    ArrayList<Bike> listAvailableBikes;

    //int selectedRackId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_bike_activity);

        Intent intent = getIntent();
        loggedUserId = intent.getIntExtra(Config.KEY_USER_ID, 0);

        Toolbar homeToolbar = (Toolbar)findViewById(R.id.homeToolbar);
        setSupportActionBar(homeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        homeToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(rentBike_activity.this, HomeActivity.class);
                intent.putExtra(Config.KEY_USER_ID, loggedUserId);
                startActivity(intent);
                finish();
            }
        });

        getRacks();
        spinRacks = (Spinner) findViewById(R.id.spinnerRacks);
        spinBikes = (Spinner) findViewById(R.id.spinnerBikes);
        btnGetBike = (Button)findViewById(R.id.btnGetBike);
        btnGetBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinRacks.getSelectedItem() != null && spinBikes.getSelectedItem() != null) {
                    //selectedRackId = (int) spinRacks.getSelectedItemId() + 1;
                    selectedRackId = listRacks.get(spinRacks.getSelectedItemPosition()).getId();
                    selectedBikeId = listAvailableBikesIDs.get(spinBikes.getSelectedItemPosition()); //(int) spinBikes.getSelectedItemId() + 1;
                    Log.d("ERROR", "selectedBikeId : " + String.valueOf(selectedBikeId));
                    if (selectedRackId != 0 && selectedBikeId != 0) {
                        takeBike();
                    }
                }
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
                loading = ProgressDialog.show(rentBike_activity.this, "Fetching...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(JSONObject result)
            {
                loading.dismiss();
                fillRacksList(result);
                final Spinner ddlRacks = (Spinner) findViewById(R.id.spinnerRacks);
                ddlRacks.setAdapter(new ArrayAdapter<String>(rentBike_activity.this, android.R.layout.simple_spinner_dropdown_item, listRacksNames));

                ddlRacks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        TextView txtMaxBikes = (TextView) findViewById(R.id.txtMaxBikes);
                        TextView txtUsedBikes = (TextView) findViewById(R.id.txtUsedBikes);
                        txtMaxBikes.setText("Ukupno : "+ listRacks.get(position).getMaxBikes());
                        txtUsedBikes.setText("Zauzeta bicikla : " + listRacks.get(position).getMaxBikes());

                        Log.d("ERROR", "rackid : " + String.valueOf(id));

                        getAvailableBikes((int)id + 1);
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

    private void getAvailableBikes(final int rackId)
    {
        class GetAvailableBikes extends AsyncTask<String, String, JSONObject>
        {
            ProgressDialog loading;
            int selectedRackID = rackId;

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                loading = ProgressDialog.show(rentBike_activity.this, "Fetching...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(JSONObject result)
            {
                loading.dismiss();
                fillAvailableBikesList(result);
                Spinner ddlAvailableBikes = (Spinner) findViewById(R.id.spinnerBikes);
                ddlAvailableBikes.setAdapter(new ArrayAdapter<Integer>(rentBike_activity.this, android.R.layout.simple_spinner_dropdown_item, listAvailableBikesIDs));

                ddlAvailableBikes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            protected JSONObject doInBackground(String... args) {
                ArrayList params = new ArrayList();
                params.add(new BasicNameValuePair("id", String.valueOf(selectedRackID)));

                JSONObject json = jsonParser.makeHttpRequest(Config.URL_GET_AVAILABLE_BIKES_BY_RACKID, "GET", params);

                return json;
            }
        }
        GetAvailableBikes gj = new GetAvailableBikes();
        gj.execute();
    }

    private void fillAvailableBikesList(JSONObject result)
    {
        try
        {
            listAvailableBikesIDs = new ArrayList<Integer>();
            listAvailableBikes = new ArrayList<Bike>();

            JSONArray resultArray = result.getJSONArray(Config.TAG_JSON_ARRAY);

            for(int i=0; i < resultArray.length(); i++) {
                JSONObject c = resultArray.getJSONObject(i);
                Integer id = c.getInt("id");
                Integer active = c.getInt("active");
                Integer UserId = c.getInt("UserId");
                Integer RackId = c.getInt("RackId");

                Bike b = new Bike();
                b.setId(id);
                b.setActive(active);
                b.setUserId(UserId == null ? 0 : UserId);
                b.setRackId(RackId == null ? 0 : RackId);

                listAvailableBikes.add(b);
                listAvailableBikesIDs.add(c.optInt("id"));
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void takeBike()
    {
        class TakeBike extends AsyncTask<String, String, JSONObject>
        {
            ProgressDialog loading;

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                loading = ProgressDialog.show(rentBike_activity.this, "Fetching...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(JSONObject result)
            {
                loading.dismiss();
                bikeTaken(result);

            }

            @Override
            protected JSONObject doInBackground(String... args) {
                ArrayList params = new ArrayList();
                params.add(new BasicNameValuePair("userId", String.valueOf(loggedUserId)));
                params.add(new BasicNameValuePair("rackId", String.valueOf(selectedRackId)));
                params.add(new BasicNameValuePair("bikeId", String.valueOf(selectedBikeId)));

                JSONObject json = jsonParser.makeHttpRequest(Config.URL_TAKE_BIKE, "POST", params);

                return json;
            }
        }
        TakeBike gj = new TakeBike();
        gj.execute();
    }

    private void bikeTaken(JSONObject result)
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
                        Intent intent = new Intent(rentBike_activity.this, HomeActivity.class);
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
                Intent intentMyProfile = new Intent(rentBike_activity.this, myProfileActivity.class);
                intentMyProfile.putExtra(Config.KEY_USER_ID,loggedUserId);
                startActivity(intentMyProfile);
                finish();
                return true;
            case R.id.signOut:
                //User chose the "Sign out" item
                Intent intentMain = new Intent(rentBike_activity.this, MainActivity.class);
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
