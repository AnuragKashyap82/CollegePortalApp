package com.anurag.eduventure.Filters;

import android.widget.Filter;

import com.anurag.eduventure.Adapters.AdapterBooks;
import com.anurag.eduventure.Adapters.AdapterNotice;
import com.anurag.eduventure.Models.ModelBooks;
import com.anurag.eduventure.Models.ModelNotice;

import java.util.ArrayList;

public class FilterBooks extends Filter {

    private AdapterBooks adapter;
    private ArrayList<ModelBooks> filterList;

    public FilterBooks(AdapterBooks adapter, ArrayList<ModelBooks> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected android.widget.Filter.FilterResults performFiltering(CharSequence charSequence) {
        android.widget.Filter.FilterResults results = new android.widget.Filter.FilterResults();
        if (charSequence != null && charSequence.length() > 0) {
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelBooks> filteredModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getBookName().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getAuthorName().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getBookId().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getSubjectName().toUpperCase().contains(charSequence))  {
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, android.widget.Filter.FilterResults filterResults) {

        adapter.booksArrayList = (ArrayList<ModelBooks>) filterResults.values;

        adapter.notifyDataSetChanged();
    }
}
