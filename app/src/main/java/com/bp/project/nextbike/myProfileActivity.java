package com.bp.project.nextbike;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class myProfileActivity extends AppCompatActivity {

    EditText txtusername, txtpassword, txtname, txtsurname;
    Button btnSaveChanges;

    //String URL_SAVE_CHANGES = Config.URL_SAVE_CHANGES_USER_DATA;
    //String URL_GET_USER = Config.URL_GET_USER_BY_ID;


    JSONParser jsonParser = new JSONParser();

    private int loggedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        Intent intent = getIntent();
        loggedUserId = intent.getIntExtra(Config.KEY_USER_ID, 0);

        Toolbar homeToolbar = (Toolbar)findViewById(R.id.homeToolbar);
        setSupportActionBar(homeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        homeToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(myProfileActivity.this, HomeActivity.class);
                intent.putExtra(Config.KEY_USER_ID, loggedUserId);
                startActivity(intent);
                finish();
            }
        });

        getUser();

        txtusername = (EditText)findViewById(R.id.txtUsername);
        txtpassword = (EditText)findViewById(R.id.txtPassword);
        txtname = (EditText)findViewById(R.id.txtName);
        txtsurname = (EditText)findViewById(R.id.txtSurname);

        btnSaveChanges = (Button)findViewById(R.id.btnSaveChanges);

        btnSaveChanges.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                if(txtname.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Molimo unesite ime", Toast.LENGTH_LONG);
                    return;
                }
                if(txtsurname.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Molimo unesite prezime", Toast.LENGTH_LONG);
                    return;
                }
                if(txtusername.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Molimo unesite korisničko ime", Toast.LENGTH_LONG);
                    return;
                }
                if(txtpassword.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Molimo unesite šifru", Toast.LENGTH_LONG);
                    return;
                }

                AttemptUpdateUser attemptUpdateUser = new AttemptUpdateUser();
                attemptUpdateUser.execute(String.valueOf(loggedUserId), txtname.getText().toString(), txtsurname.getText().toString(), txtusername.getText().toString(), txtpassword.getText().toString());


            }
        });
    }

    private void getUser()
    {
        class GetUser extends AsyncTask<String, String, JSONObject>
        {
            ProgressDialog loading;

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                loading = ProgressDialog.show(myProfileActivity.this, "Fetching...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(JSONObject result)
            {
                loading.dismiss();
                showUser(result);
            }

            @Override
            protected JSONObject doInBackground(String... args) {
                ArrayList params = new ArrayList();
                params.add(new BasicNameValuePair("id", String.valueOf(loggedUserId)));

                JSONObject json = jsonParser.makeHttpRequest(Config.URL_GET_USER_BY_ID, "GET", params);

                return json;
            }
        }
        GetUser gj = new GetUser();
        gj.execute();
    }

    private void showUser(JSONObject result)
    {
        try
        {
            JSONArray resultArray = result.getJSONArray(Config.TAG_JSON_ARRAY);
            JSONObject c = resultArray.getJSONObject(0);
            String name = c.getString("name");
            String surname = c.getString("surname");
            String username = c.getString("username");
            String password = c.getString("password");

            txtname.setText(name);
            txtsurname.setText(surname);
            txtusername.setText(username);
            txtpassword.setText(password);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    private class AttemptUpdateUser extends AsyncTask<String, Void, JSONObject>
    {
        @Override
        public JSONObject doInBackground(String... args)
        {
            String id = args[0];
            String name = args[1];
            String surname = args[2];
            String username = args[3];
            String password = args[4];

            ArrayList params = new ArrayList();
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("surname", surname));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));

            JSONObject json = jsonParser.makeHttpRequest(Config.URL_SAVE_CHANGES_USER_DATA, "POST", params);
            return json;
        }

        @Override
        public void onPreExecute()
        {
            super.onPreExecute();
        }

        public void onPostExecute(JSONObject result)
        {
            try
            {
                JSONArray resultArray = result.getJSONArray(Config.TAG_JSON_ARRAY);
                JSONObject c = resultArray.getJSONObject(0);
                String success = c.getString("success");
                String message = c.getString("message");

                if(result != null)
                {
                    if(success.equals("1")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(myProfileActivity.this, HomeActivity.class);
                                intent.putExtra(Config.KEY_USER_ID, loggedUserId);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve any server", Toast.LENGTH_LONG);
                }
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }

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
                //Intent intentMyProfile = new Intent(myProfileActivity.this, myProfileActivity.class);
                //intentMyProfile.putExtra(Config.KEY_USER_ID,loggedUserId);
                //startActivity(intentMyProfile);
                //finish();
                return true;
            case R.id.signOut:
                //User chose the "Sign out" item
                Intent intentMain = new Intent(myProfileActivity.this, MainActivity.class);
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
