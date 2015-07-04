package com.blogspot.xeeshan.test01;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //find the listView in xml
        ListView myListView=(ListView)findViewById(R.id.listView);
        //create a custom array adapter which will store the objects (strings in this case)
        ArrayAdapter<String> myArrayAdapter=
                new CustomArrayAdapter<>(this, R.layout.listview_item_complex, R.id.textView);
        //populate the array adapter
//        for (int i=0;i<100;++i)
//            myArrayAdapter.add("String number " + i);
        //attach the array adapter to the list view
        myListView.setAdapter(myArrayAdapter);
        FetchWeather myFetchWeather=new FetchWeather(myArrayAdapter);
        myFetchWeather.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
