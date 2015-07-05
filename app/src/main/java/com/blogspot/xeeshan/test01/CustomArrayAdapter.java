package com.blogspot.xeeshan.test01;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Zeeshan on 21/06/2015.
 */
public class CustomArrayAdapter<String> extends ArrayAdapter <String>
{
    private TextView tempTextView;
    private int counter;
    public CustomArrayAdapter(Context context,int layoutResource,int textViewResource)
    {
        super(context,layoutResource,textViewResource);
        counter=0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View tempView = super.getView(position, convertView, parent);
        if (tempView.getTag()==null)
        {
            tempTextView = (TextView) tempView.findViewById(R.id.textView_01);
            //can set tag to the textview itself as well! don't really need a view holder!!
            tempView.setTag(new MyViewHolder(tempTextView));
            counter++;
        }
        else
        {
            tempTextView = ((MyViewHolder) tempView.getTag()).textView_01;
        }
        tempTextView.setText("crikey "+counter);
        return tempView;

    }
    public class MyViewHolder
    {
        public TextView textView_01;
        public MyViewHolder(TextView textView_01)
        {
            this.textView_01=textView_01;
        }
    }
}
