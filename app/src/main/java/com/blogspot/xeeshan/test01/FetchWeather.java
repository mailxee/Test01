package com.blogspot.xeeshan.test01;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by Zeeshan on 04/07/2015.
 */
public class FetchWeather extends AsyncTask<Void,Void,String>
{
    private ArrayAdapter<String> myArrayAdapter;
    FetchWeather(ArrayAdapter<String> myArrayAdapter)
    {
        super();
        this.myArrayAdapter=myArrayAdapter;
    }

    @Override
    protected void onPostExecute(String result)
    {
        //myArrayAdapter.addOpenWeatherJSON(result);
        try
        {

            JSONObject weather;
            weather = new JSONObject(result);
            JSONArray days = weather.getJSONArray("list");
            int limit=days.length();
            Log.d("Limit is",Integer.toString(limit));
            for (int i = 0; i < limit; ++i)
            {
                JSONObject dayInfo = days.getJSONObject(i);
                JSONObject temperatureInfo = dayInfo.getJSONObject("temp");
                Date curDate=new Date (dayInfo.getLong("dt")*1000);
                //String curDateStr=curDate.toString();
                //"yyyy-MM-dd hh:mm:ss"
                String curDateStr= (String) android.text.format.DateFormat.format("dd MMM yyyy", curDate);
                myArrayAdapter.add
                        (
                                ("Day= " + curDateStr +
                                        " Max= " + temperatureInfo.getDouble("max") +
                                        " Min= " + temperatureInfo.getDouble("min"))
                        );
            }
        }
        catch (JSONException e)
        {
            Log.e("PlaceholderFragment", "Error parsing JSON string", e);
        }

//        myArrayAdapter.add(result);
        myArrayAdapter.notifyDataSetChanged();


    }
    @Override
    protected String doInBackground(Void... params)
    {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try
        {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=karachi&mode=json&units=metric&cnt=7");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null)
            {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null)
            {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0)
            {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e)
        {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
            if (reader != null)
            {
                try
                {
                    reader.close();
                } catch (final IOException e)
                {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return forecastJsonStr;
    }
}