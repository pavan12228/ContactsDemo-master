package com.example.welcome.contactsdemo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.welcome.contactsdemo.contacts.Utils.ApiServiceCall;
import com.example.welcome.contactsdemo.contacts.Utils.Utils;
import com.example.welcome.contactsdemo.contacts.adapters.CustomAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private CustomAdapter customAdapter;
    private ArrayList<String> contactModelArrayList;
    private String sNumber;
    private String user_id,phoneNumber="9704897141";
    Button btnpostData;
    private String requestedResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle b=this.getIntent().getExtras();
        if(b!=null){
            user_id=b.getString("user_id");
        }
        listView = (ListView) findViewById(R.id.listView);
        btnpostData= (Button) findViewById(R.id.postData);


        contactModelArrayList = new ArrayList<>();

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
        while (phones.moveToNext())
        {
             phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replaceAll("[\\D]", "");
            if (phoneNumber.length() >= 10) {
                 phoneNumber = phoneNumber.substring(phoneNumber.length() - 10);
                if (!contactModelArrayList.contains(phoneNumber)) {
                    contactModelArrayList.add(phoneNumber);
                    //  contactModelArrayList.add(contactModel);
                    Log.d("phone>>phone>>", "" + phoneNumber);
                }
                 }
              }
        phones.close();

        customAdapter = new CustomAdapter(this,contactModelArrayList);
        listView.setAdapter(customAdapter);


        btnpostData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//               PostContatcsData();
               /* *//* start from Last to first *//*
                for (int i = contactModelArrayList.size(); i >0  ; i= i-5) {
                    *//* if x is total contacts , only x-5 contacts will be uploaded. *//*
                    if ( (contactModelArrayList.size() - i)+5  < contactModelArrayList.size() ) {
                        *//* 1 st loop  sublist(230 - 230 ,5)
                         * 2 nd loop sublist(5, 10)
                          * 3 rd loop sublist(10,15) ... so on upto end of loop *//*
                        new ParseJSONTask(contactModelArrayList.subList(contactModelArrayList.size() - i
                                ,(contactModelArrayList.size() - i)+5)).execute();
                        Log.d("MainActivity", "called 5 "+(contactModelArrayList.size() - i)+" and"+(contactModelArrayList.size() - i)+5);
                    }
                }*/
                          /*if(contactModelArrayList.size() < 500)
                new ParseJSONTask(contactModelArrayList.subList(0
                        ,contactModelArrayList.size())).execute();
                          else if (contactModelArrayList.size() >= 500) {
                              new ParseJSONTask(contactModelArrayList.subList(0
                                      ,500)).execute();
                              new ParseJSONTask(contactModelArrayList.subList(500
                                      ,contactModelArrayList.size())).execute();
                              Log.d("MainActivity5", "contacts sub list "+contactModelArrayList.subList(0
                                      ,500).size()+" second list::"+contactModelArrayList.subList(500
                                      ,contactModelArrayList.size()).size());
                          }*/


                if (contactModelArrayList.size() <= 600){
                    new ParseJSONTask(contactModelArrayList.subList(0, contactModelArrayList.size())).execute();
                    //Log.d("partition1",""+contacxtModelArrayList.size());

                } else if(contactModelArrayList.size()>=600&&contactModelArrayList.size()<=1200)
            {
                new ParseJSONTask(contactModelArrayList.subList(0, 600)).execute();
                new ParseJSONTask(contactModelArrayList.subList(600, contactModelArrayList.size())).execute();
                Log.d("",""+contactModelArrayList.subList(0,contactModelArrayList.subList(0, 600).size()));
                Log.d("MainActivity", "contacts sub list "+contactModelArrayList.subList(0
                        ,600).size());
                Log.d("MainActivity", "contacts sub list "+contactModelArrayList.subList(600,contactModelArrayList.size()).size());

                //Log.d("partition2",""+contactModelArrayList.subList(0,contactModelArrayList.size()));


            }else if(contactModelArrayList.size()>=1200&&contactModelArrayList.size()<=1800)

            {
                new ParseJSONTask(contactModelArrayList.subList(0, 600)).execute();
                new ParseJSONTask(contactModelArrayList.subList(600, 1200)).execute();
                new ParseJSONTask(contactModelArrayList.subList(1200, contactModelArrayList.size())).execute();
            }else if(contactModelArrayList.size()>=1800&&contactModelArrayList.size()<=2400)

            {
                new ParseJSONTask(contactModelArrayList.subList(0, 600)).execute();
                new ParseJSONTask(contactModelArrayList.subList(600, 1200)).execute();
                new ParseJSONTask(contactModelArrayList.subList(1200, 1800)).execute();
                new ParseJSONTask(contactModelArrayList.subList(1800, contactModelArrayList.size())).execute();
            }


        }




        });


    }
     class ParseJSONTask extends AsyncTask<Void, Void, Void> {
        private List<String> mFiveContactList= new ArrayList<>();

         public ParseJSONTask(List<String> mFiveContactList) {
             this.mFiveContactList = mFiveContactList;
         }

         @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog.setMessage("Loading.....");
            // dialog.setCancelable(false);
            // dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //  if (dialog.isShowing()) {
            try {

                String[] result1;
                String status;
                String u_id, mobile;

                JSONObject jsonObject = new JSONObject(requestedResponse);
                status = jsonObject.getString("success");
                if (status.contains("true")) {

                    JSONArray data = jsonObject.getJSONArray("data");

                    // looping through all items
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject object = data.getJSONObject(i);
                        mobile = object.getString("mobile");
                        u_id = object.getString("user_id");


                        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(mobile));

                        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

                        String contactName = "";
                        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

                        if (cursor != null) {
                            if (cursor.moveToFirst()) {
                                contactName = cursor.getString(0);

                                Log.e("Name", contactName);
                            }
                            cursor.close();
                        }




                    }





                } else if (status.contains("false")) {
                    Toast.makeText(getApplicationContext(), "No Records Found", Toast.LENGTH_LONG).show();
                    //dialog.dismiss();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            // dialog.dismiss();
            // }

        }

        @Override
        protected Void doInBackground(Void... params) {
            String contactsList = "";
            int totalSize  = mFiveContactList.size();


            if (mFiveContactList.size()<= totalSize ) {
                for (int i = 0; i < totalSize; i++) {
                    /* first time comma is not added to string */
                        if(i  == 0)
                            contactsList= mFiveContactList.get(i);
                            else
                    contactsList = contactsList+","+mFiveContactList.get(i)+""; // here , comma will be added every iteration.
                }
            }
            String api = "http://52.66.43.145/rama/app/get_numbers.php?contact_list=" +
                    contactsList+"&sender_id="+user_id;
            Log.d("MainActivity",contactsList);
            String response = null;
            Log.e("URL", api);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(api);
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);
                requestedResponse = response;

                Log.e("Response", response);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }







    private void PostContatcsData() {
        RestAdapter adapter = new RestAdapter.Builder().setEndpoint("http://52.66.43.145/").build();

        ApiServiceCall api = adapter.create(ApiServiceCall.class);
        api.PushContactsAPi("9704897141", new Callback<JsonObject>() {
          @Override
          public void success(JsonObject jsonObject, Response response) {

              Log.d("response", jsonObject.toString());
              JsonObject jsonObject1 = jsonObject.getAsJsonObject();
              String message = jsonObject1.get("success").getAsString();
              if (message.equals("true")) {
                  //status = jsonObject1.get("error").getAsString();
                  //Utils.message(getApplicationContext(), status);
                  JsonArray jsonArray = jsonObject1.get("data").getAsJsonArray();
                  for (int i = 0; i < jsonArray.size(); i++) {
                      JsonObject jsonObject2 = jsonArray.get(i).getAsJsonObject();
                      user_id = jsonObject2.get("user_id").getAsString();
                      String RegisteredNumber = jsonObject2.get("mobile").getAsString();
                      Log.d("user_id", user_id);
                      Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                      intent.putExtra("user_id", user_id);
                      startActivity(intent);
                  }

              }
              }

          @Override
          public void failure(RetrofitError error) {
              Utils.message(getApplicationContext(), "retrofit error" + error.toString());
              Log.d("MainActivity", "failure: "+error.getMessage());
          }
      });

      }

}