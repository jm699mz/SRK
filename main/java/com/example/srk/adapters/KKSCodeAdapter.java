package com.example.srk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.srk.R;
import com.example.srk.model.KKSCode;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class KKSCodeAdapter extends ArrayAdapter<KKSCode> implements Filterable {

    private ArrayList<KKSCode> kksCodeList;
    private ArrayList<KKSCode> kksCodesListFull;

    public KKSCodeAdapter(Context context, ArrayList<KKSCode> kksCodes){
        super(context, 0, kksCodes);
        this.kksCodeList = kksCodes;
        kksCodesListFull = new ArrayList<>(kksCodeList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        KKSCode kksCode = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.kkscodes_listview_item, parent, false);
        }

        TextView kksCodeLabel = convertView.findViewById(R.id.listview_item_kksCode);
        TextView kksCodeDescription = convertView.findViewById(R.id.listview_item_kksCode_desc);


        kksCodeLabel.setText(kksCode.getLabel());
        kksCodeDescription.setText(kksCode.getDescription());

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                ArrayList<KKSCode> filteredList = new ArrayList<>();

                if(charSequence == null || charSequence.length() == 0){
                    filteredList.addAll(kksCodesListFull);
                }else{
                    String pattern = charSequence.toString();
                    for(KKSCode kksCode : kksCodesListFull){
                        if(Pattern.compile(Pattern.quote(pattern), Pattern.CASE_INSENSITIVE).matcher(kksCode.getLabel()).find() ||
                                Pattern.compile(Pattern.quote(pattern), Pattern.CASE_INSENSITIVE).matcher(kksCode.getDescription()).find())
                        {
                            filteredList.add(kksCode);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                kksCodeList.clear();
                kksCodeList.addAll((ArrayList) filterResults.values);
                notifyDataSetChanged();
            }
        };

        return filter;
    }
}
