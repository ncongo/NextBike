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

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();

    int loggedUserId;

    Button btnReturnBike;
    Button btnRentBike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        loggedUserId = intent.getIntExtra(Config.KEY_USER_ID, 0);
        Log.d("ERROR", "ID : " + String.valueOf(loggedUserId));

        Toolbar homeToolbar = (Toolbar)findViewById(R.id.homeToolbar);
        setSupportActionBar(homeToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnRentBike = (Button)findViewById(R.id.btnRentBike);
        btnRentBike.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, rentBike_activity.class);
                intent.putExtra(Config.KEY_USER_ID, loggedUserId);
                startActivity(intent);
                finish();
            }
        });

        btnReturnBike = (Button)findViewById(R.id.btnReturnBike);
        btnReturnBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, returnBike.class);
                intent.putExtra(Config.KEY_USER_ID, loggedUserId);
                startActivity(intent);
                finish();
            }
        });

        setControlVisibility();
    }

    private void setVisibility(JSONObject result)
    {
        try {
            JSONArray resultArray = result.getJSONArray(Config.TAG_JSON_ARRAY);
            JSONObject c = resultArray.getJSONObject(0);
            String hasBike = c.getString("hasBike");
            String success = c.getString("success");

            if(hasBike.equals("1") && success.equals("1"))
            {
                btnReturnBike.setVisibility(View.VISIBLE);
                btnRentBike.setVisibility(View.INVISIBLE);
                Log.d("ERROR2", "hasBike : " + hasBike + "   success: " + success);
            }
            else
            {
                btnReturnBike.setVisibility(View.INVISIBLE);
                btnRentBike.setVisibility(View.VISIBLE);
            }

        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }


    }

    private void setControlVisibility()
    {
        class HasUserTakenBike extends AsyncTask<String, String, JSONObject>
        {
            ProgressDialog loading;
            String hasBike = "";
            String success = "";

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                loading = ProgressDialog.show(HomeActivity.this, "Fetching...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(JSONObject result)
            {
                setVisibility(result);
                loading.dismiss();
            }

            @Override
            protected JSONObject doInBackground(String... args) {
                ArrayList params = new ArrayList();
                params.add(new BasicNameValuePair("id", String.valueOf(loggedUserId)));

                JSONObject json = jsonParser.makeHttpRequest(Config.URL_HAS_USER_TAKEN_BIKE, "GET", params);

                return json;
            }
        }
        HasUserTakenBike gj = new HasUserTakenBike();
        gj.execute();
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
                Intent intentMyProfile = new Intent(HomeActivity.this, myProfileActivity.class);
                intentMyProfile.putExtra(Config.KEY_USER_ID,loggedUserId);
                startActivity(intentMyProfile);
                finish();
                return true;
            case R.id.signOut:
                //User chose the "Sign out" item
                Intent intentMain = new Intent(HomeActivity.this, MainActivity.class);
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
