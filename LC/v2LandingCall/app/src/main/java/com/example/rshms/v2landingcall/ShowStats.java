package com.example.rshms.v2landingcall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ShowStats extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_stats);
        //Calling the display API
        Api.URL_READ_HEROES = Api.URL_READ_HEROES;
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_HEROES,null, CODE_GET_REQUEST);
        request.execute();


    }

    public static void refreshHeroList(JSONArray fall) throws JSONException {
        //clearing previous heroes
        //heroList.clear();
        Log.d("obj", fall.toString());

        List<Fall> fallList = new ArrayList();
        //traversing through all the items in the json array
        //the json we got from the response
        for (int i = 0; i < fall.length(); i++) {

            //getting each hero object
            JSONObject obj = fall.getJSONObject(i);
            //adding the hero to the list
            fallList.add(new Fall(

                    obj.getInt("id"),
                    obj.getString("event"),
                    obj.getString("deviceip")

            ));
        }

        //creating the adapter and setting it to the listview
        //HeroAdapter adapter = new HeroAdapter(heroList);
    //    Toast toast = Toast.makeText(getApplicationContext(),"Test Message Sent!",Toast.LENGTH_LONG);
      //  toast.show();
       Log.d("from database",fallList.get(0).getDeviceip());

    }
}
