package com.anurag.eduventure.Filters;

import android.widget.Filter;


import com.anurag.eduventure.Adapters.AdapterMaterial;
import com.anurag.eduventure.Models.ModelMaterial;

import java.util.ArrayList;

public class FilterMaterial extends Filter {

    private AdapterMaterial adapter;
    private ArrayList<ModelMaterial> filterList;

    public FilterMaterial(AdapterMaterial adapter, ArrayList<ModelMaterial> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence != null && charSequence.length() > 0) {
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelMaterial> filteredModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getSubjectName().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getSubjectName().toUpperCase().contains(charSequence)) {
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        adapter.materialList = (ArrayList<ModelMaterial>) filterResults.values;

        adapter.notifyDataSetChanged();
    }
}
