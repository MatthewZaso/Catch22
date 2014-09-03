package com.example.mattzaso.catch22;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PredictionActivity extends Activity {

    private static final String QUERY_URL = "http://proximobus.appspot.com/agencies/sf-muni/";
    private static final String CHESTNUT_ST_ID = "14609";
    private static final String FILLMORE_BUS_ID = "22";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        Button refreshButton = (Button) findViewById(R.id.button_refresh);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryBusPredictions(CHESTNUT_ST_ID, FILLMORE_BUS_ID);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.prediction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    private void queryBusPredictions(String stopId, String busId) {
        AsyncHttpClient client = new AsyncHttpClient();

        String queryStringWithParams = QUERY_URL + "stops/" + stopId + "/predictions/by-route/" + busId + ".json";

        client.get(queryStringWithParams,
                new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                        Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();

                        Log.d("omg android", jsonObject.toString());

                        JSONArray baseItems = jsonObject.optJSONArray("items");

                        List<String> listContents = new ArrayList<String>(baseItems.length());

                        for (int i = 0; i < baseItems.length(); i++) {
                            String value = "";

                            try {
                                value = baseItems.getJSONObject(i).optString("minutes");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            value = value.replaceAll("\\.0*$", "");

                            listContents.add(value + " minutes");
                        }

                        Log.d("omg android", listContents.toString());

                        ListView chestnutList = (ListView) findViewById(R.id.chestnut_list);
                        chestnutList.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, listContents));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error){
                        Toast.makeText(getApplicationContext(), "Error: " + statusCode + " ", Toast.LENGTH_LONG).show();

                        Log.e("omg android", statusCode + " " + throwable.getMessage());
                    }
                });
    }
}
