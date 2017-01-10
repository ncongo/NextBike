package com.bp.project.nextbike;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText txtusername, txtpassword, txtname, txtsurname;
    Button btnSignIn, btnSignUp;

    //String URL = Config.URL_INDEX_NEW;
    //String URL = "http://192.168.0.20:80/indexNew.php";

    JSONParser jsonParser = new JSONParser();

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtusername = (EditText)findViewById(R.id.txtUsername);
        txtpassword = (EditText)findViewById(R.id.txtPassword);
        txtname = (EditText)findViewById(R.id.txtName);
        txtsurname = (EditText)findViewById(R.id.txtSurname);

        btnSignIn = (Button)findViewById(R.id.btnPrijaviSe);
        btnSignUp = (Button)findViewById(R.id.btnRegistracija);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AttemptLogin attemptLogin = new AttemptLogin();

                attemptLogin.execute(txtusername.getText().toString(), txtpassword.getText().toString(), "", "");
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
        public void onClick(View view)
            {
                if(i == 0)
                {
                    i = 1;
                    txtname.setVisibility(View.VISIBLE);
                    txtsurname.setVisibility(View.VISIBLE);
                    btnSignIn.setVisibility(View.GONE);
                    btnSignUp.setText("Kreiraj profil");
                }
                else
                {
                    btnSignUp.setTag("Registriraj se");
                    txtname.setVisibility(View.GONE);
                    txtsurname.setVisibility(View.GONE);
                    btnSignIn.setVisibility(View.VISIBLE);
                    i = 0;

                    AttemptLogin attemptLogin = new AttemptLogin();
                    attemptLogin.execute(txtusername.getText().toString(), txtpassword.getText().toString(), txtname.getText().toString(), txtsurname.getText().toString());
                }
            }
        });
    }

    private class AttemptLogin extends AsyncTask<String, Void, JSONObject> {
        ProgressDialog loading;

        @Override
        protected JSONObject doInBackground(String... args) {
            String username = args[0];
            String password = args[1];
            String name = args[2];
            String surname = args[3];

            ArrayList params = new ArrayList();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            if(name.length() > 0 && surname.length() > 0) {
                params.add(new BasicNameValuePair("name", name));
                params.add(new BasicNameValuePair("surname", surname));
            }

            JSONObject json = jsonParser.makeHttpRequest(Config.URL_INDEX_NEW, "POST", params);

            return json;
        }

        //@Override
        //protected Object doInBackground(Object[] params) {
        //    return null;
        //}

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            loading = ProgressDialog.show(MainActivity.this, "Fetching...", "Wait...", false, false);
        }

        protected void onPostExecute(JSONObject result){
            try
            {
                //JSONArray resultArray = result.getJSONArray(Config.TAG_JSON_ARRAY);
                //JSONObject c = resultArray.getJSONObject(0);
                loading.dismiss();
                String success = result.getString("success");
                //Log.d("ERROR", "success : " + success);
                if(result != null)
                {
                    Toast.makeText(getApplicationContext(), result.getString("message"), Toast.LENGTH_LONG).show();
                    if(i == 0) //If user is successfully logged in, go to home page
                    {
                        final int userId = Integer.parseInt(result.getString("userid"));//getUserId(result);
                        //Log.d("ERROR", "ID : " + String.valueOf(userId));
                        if(success == "1") {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.putExtra(Config.KEY_USER_ID, userId);
                            startActivity(intent);
                            finish();
                                }
                            });
                        }

                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), R.string.cant_retrieve_server, Toast.LENGTH_LONG).show();
                }
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

}
