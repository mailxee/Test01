package com.blogspot.xeeshan.test01;

import android.net.Uri;
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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Zeeshan on 04/07/2015.
 */
public class FetchWeather extends AsyncTask<Void,Void,String>
{
    private static final String LOG_TAG = "FetchWeather";
    private static final String CITY = "karachi";

    private ArrayAdapter<String> myArrayAdapter;
    FetchWeather(ArrayAdapter<String> myArrayAdapter)
    {
        super();
        this.myArrayAdapter=myArrayAdapter;
    }

    @Override
    protected void onPostExecute(String result)
    {
        if (result == null)
        {
            myArrayAdapter.add("Could not load weather. Check your connection and API key.");
            myArrayAdapter.notifyDataSetChanged();
            return;
        }
        try
        {
            // The free forecast endpoint returns 3-hourly entries, so group them
            // by day and keep each day's lowest min and highest max.
            // LinkedHashMap keeps the days in the order they arrive.
            Map<String, double[]> dayMinMax = new LinkedHashMap<>();
            JSONObject weather = new JSONObject(result);
            JSONArray entries = weather.getJSONArray("list");
            int limit=entries.length();
            Log.d("Limit is",Integer.toString(limit));
            for (int i = 0; i < limit; ++i)
            {
                JSONObject entry = entries.getJSONObject(i);
                JSONObject temperatureInfo = entry.getJSONObject("main");
                Date curDate=new Date (entry.getLong("dt")*1000);
                String curDateStr= android.text.format.DateFormat.format("dd MMM yyyy", curDate).toString();
                double min = temperatureInfo.getDouble("temp_min");
                double max = temperatureInfo.getDouble("temp_max");
                double[] minMax = dayMinMax.get(curDateStr);
                if (minMax == null)
                {
                    dayMinMax.put(curDateStr, new double[]{min, max});
                }
                else
                {
                    minMax[0] = Math.min(minMax[0], min);
                    minMax[1] = Math.max(minMax[1], max);
                }
            }
            for (Map.Entry<String, double[]> day : dayMinMax.entrySet())
            {
                myArrayAdapter.add
                        (
                                ("Day= " + day.getKey() +
                                        " Max= " + day.getValue()[1] +
                                        " Min= " + day.getValue()[0])
                        );
            }
        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG, "Error parsing JSON string", e);
            myArrayAdapter.add("Could not read the weather data.");
        }

        myArrayAdapter.notifyDataSetChanged();


    }
    @Override
    protected String doInBackground(Void... params)
    {
        if (BuildConfig.OWM_API_KEY.isEmpty())
        {
            Log.e(LOG_TAG, "No OpenWeatherMap API key; set OWM_API_KEY when building");
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try
        {
            // Construct the URL for the OpenWeatherMap 5 day / 3 hour forecast query
            // (available on the free plan): https://openweathermap.org/forecast5
            Uri uri = Uri.parse("https://api.openweathermap.org/data/2.5/forecast").buildUpon()
                    .appendQueryParameter("q", CITY)
                    .appendQueryParameter("mode", "json")
                    .appendQueryParameter("units", "metric")
                    .appendQueryParameter("appid", BuildConfig.OWM_API_KEY)
                    .build();
            URL url = new URL(uri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK)
            {
                // e.g. 401 for a missing/invalid key, 404 for an unknown city
                Log.e(LOG_TAG, "OpenWeatherMap returned HTTP " + responseCode);
                return null;
            }

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
            Log.e(LOG_TAG, "Error ", e);
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
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return forecastJsonStr;
    }
}
