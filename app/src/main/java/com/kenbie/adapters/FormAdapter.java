package com.kenbie.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.model.OptionsData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FormAdapter extends ArrayAdapter<OptionsData> implements Filterable {
    private ArrayList<OptionsData> originalDataArrayList;
    private ArrayList<OptionsData> optionsDataArrayList;
    private ArrayList<OptionsData> suggestions;
    private Context mContext;
    private int viewResourceId;

    public FormAdapter(Context context, int resourceId, ArrayList<OptionsData> OptionsData) {
        super(context, resourceId, OptionsData);
        mContext = context;
        optionsDataArrayList = OptionsData;
        if (optionsDataArrayList == null) optionsDataArrayList = new ArrayList<>();
        suggestions = new ArrayList<>();
        originalDataArrayList = (ArrayList<com.kenbie.model.OptionsData>) optionsDataArrayList.clone();
        this.viewResourceId = resourceId;
    }


    public void refreshData(ArrayList<OptionsData> optionsDataArrayList) {
        this.optionsDataArrayList = optionsDataArrayList;
        if (optionsDataArrayList == null) this.optionsDataArrayList = new ArrayList<>();
        notifyDataSetChanged();
    }


/*
    @Override
    public int getCount() {
        return optionsDataArrayList.size();
    }

    @Override
    public OptionsData getItem(int position) {
        return optionsDataArrayList.get(position);
    }
*/

 /*   @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    filterResults.count = getCount();
                }
//                if (constraint != null) {
//                    try {
//                        //get data from the web
//                        String term = constraint.toString();
////                        optionsDataArrayList = new DownloadCountry().execute(term).get();
//                        optionsDataArrayList = new LocationSync().execute(term).get();
//                    } catch (Exception e) {
//                        Log.d("HUS", "EXCEPTION " + e);
//                    }
//                    filterResults.values = optionsDataArrayList;
//                    filterResults.count = optionsDataArrayList.size();
//                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };*/

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((OptionsData) (resultValue)).getName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null && constraint.length() > 0) {
                suggestions.clear();
                for (OptionsData optionsData : originalDataArrayList) {
                    if (optionsData.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(optionsData);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                FilterResults filterResults = new FilterResults();
                filterResults.values = originalDataArrayList;
                filterResults.count = originalDataArrayList.size();
                return filterResults;
//                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<OptionsData> filteredList = (ArrayList<OptionsData>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (OptionsData od : filteredList) {
                    add(od);
                }
                notifyDataSetChanged();
            }
        }
    };


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
//        LayoutInflater inflater = LayoutInflater.from(getContext());
//        @SuppressLint("ViewHolder")
//        View view = inflater.inflate(R.layout.location_cell_view, parent, false);

        //get Country
        OptionsData cityValue = optionsDataArrayList.get(position);

        TextView locTitle = (TextView) v.findViewById(R.id.loc_title);
        locTitle.setText(cityValue.getName());
        locTitle.setTypeface(KenbieApplication.S_NORMAL);
        return v;
    }
}