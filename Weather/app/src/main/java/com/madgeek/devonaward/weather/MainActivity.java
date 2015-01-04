package com.madgeek.devonaward.weather;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ParseException;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    TextView theCity;
    TextView theTemperature;
    TextView theTitle;


    Typeface nunitoLight;

    double kelvin;
    double KtoF;
    int finalTemp;
    String cityName;

    //Locate user settings
    LocateUser locateUser;
    double latitude;
    double longitude;

    //URL will contain the current location of the user.
    private static String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Customize action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#d3d3d3")));

        //TextViews
        theCity = (TextView)findViewById(R.id.city);
        theTemperature = (TextView)findViewById(R.id.degreesF);
        theTitle = (TextView)findViewById(R.id.appTitle);

        //Nunito-Light font added to TextViews
        nunitoLight = Typeface.createFromAsset(this.getAssets(),"NunitoLight.ttf");
        theCity.setTypeface(nunitoLight);
        theTemperature.setTypeface(nunitoLight);
        theTitle.setTypeface(nunitoLight);

        //Get current location
        locateUser = new LocateUser(MainActivity.this);
        // check if GPS enabled
        if(locateUser.GetLocation()){
            latitude = locateUser.getLatitude();
            longitude = locateUser.getLongitude();
            Log.i("LOCATION FOUND","Your Location is - Lat: " + latitude + " Long: " + longitude);
        }else{
            //Unable to get location
            //GPS or Network is not enabled
            //Display location settings
            locateUser.SettingsAlert();
        }
        //URL with current location of user
        url = "http://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude;
        new GetData().execute();
    }

    //Convert Kelvin to Fahrenheit
    public void setFahrenheit(double kelvin) {
        this.kelvin = kelvin;
        KtoF = (kelvin - 273.15)* 1.8000 + 32.00;
        //Round double and convert to int
        finalTemp = (int)Math.round(KtoF);
    }

    //Get the data and display from API
    private class GetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("WORKING", "WORKING ON GETTING DATA.");
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //Setup the service
            API_JSON AJ = new API_JSON();

            //Make the request for data
            String jsonStr = AJ.makeServiceCall(url, API_JSON.GET);
            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    //Getting JSON data
                    cityName = jsonObj.getString("name");
                    kelvin = Double.parseDouble(jsonObj.getJSONObject("main").getString("temp"));
                    //Check results
                    Log.i("WEATHER KELVIN", "CITY: "+cityName+" KELVIN: "+kelvin);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "UNABLE TO RETRIEVE DATA");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.i("API WORKING", "SUCCESS");

            theCity.setText(cityName);

            //Convert results to Fahrenheit
            setFahrenheit(kelvin);
            Log.i("KELVIN CONVERTED", "K CONVERTED TO F: " + finalTemp);
            theTemperature.setText(finalTemp+ "\u00B0");
        }
    }

}
