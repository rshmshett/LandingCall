package com.example.rshms.v2landingcall;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
public class ShowStats extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    public static List<Fall> fallList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_stats);
        //Calling the display API
        Api.URL_READ_HEROES = Api.URL_READ_HEROES;

        String date = "";
        final DateFormat dateTimeFormatter = DateFormat.getDateTimeInstance();
        Log.d("whatssent" , "something like this "+fallList.size());
        Date dates[] = new Date[fallList.size()];

        DateFormat format = new SimpleDateFormat("yyyy-MM-DD hh:mm:ss", Locale.ENGLISH);
        DateFormat reqd = new SimpleDateFormat("yyyy-MM-DD hh:mm:ss", Locale.ENGLISH);
        HashMap<String, Integer>plotdata = new HashMap<String, Integer>();
        int myarr[] = new int[fallList.size()];
        for (int i = 0; i < fallList.size(); i++) {
            try {
                String tempdate =fallList.get(i).getEvent();
                tempdate = tempdate.substring(5,7);
                Log.d("dateformatebro" , "something like this "+fallList.get(i).getEvent());

                myarr[i] = Integer.parseInt(tempdate);
                if(plotdata.containsKey(tempdate))
                {
                    plotdata.put(tempdate,plotdata.get(tempdate)+1);
                }else{
                    plotdata.put(tempdate,1);
                }
            }catch (Exception e){}
        }
        int marray[] =myarr;
        Arrays.sort(marray);
        System.out.println(" total months"+marray.length);

        int count = 1;
        for (int i = 0; i < marray.length - 1 ; i++) {
            System.out.println(" month"+marray[i]);
            if (marray[i] != marray[i + 1]) {
                count++;
            }
        }
        System.out.println(count);
        int myar[] = new int[count];
        int mycount[] = new int[count];
        int i=0;
        int ci = 0;
        while(i<(marray.length - 1)){
            int tc = 1;
            int j =i;
            while(marray[j+1] == marray[j]){
                tc++;
                j=j+1;
                System.out.println("inner index"+j);
                if(j == (marray.length - 1)) break;
            }
            mycount[ci]=tc;
            myar[ci++] = marray[i] ;

            i = j+1;
            System.out.println(i+" month"+myar[ci-1]);
        }
        if (marray[marray.length - 1] != marray[marray.length - 2])
        {
            mycount[ci]=1;
            myar[ci++] = marray[marray.length - 1] ;
        }

        //DateFormat finalversion = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        DataPoint dp[] = new DataPoint[count];
        // for (Map.Entry<String, Integer> entry : plotdata.entrySet()) {
        for (i = 0; i < count; i++) {

//            String key = entry.getKey();
            //          Integer value = entry.getValue();
            try {

                //Date d = finalversion.parse(key);
                dp[i] = new DataPoint(myar[i],mycount[i]);
                Log.d("insideinfromation" , "something like this "+myar[i]+" "+mycount[i] );
            }catch (Exception e ){}


        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);
        series.setAnimated(true);
        graph.addSeries(series);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time (Month)");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Number of falls detected per month");
    }

    public static void refreshHeroList(JSONArray fall) throws JSONException {
        //clearing previous heroes
        //heroList.clear();
        Log.d("obj", fall.toString());

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

    public void Log_clicked(View view) {
        startActivity(new Intent(getApplicationContext(), FullStats.class));
    }
}
