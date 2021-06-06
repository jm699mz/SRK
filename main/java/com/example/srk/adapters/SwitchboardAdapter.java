package com.example.srk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.srk.R;
import com.example.srk.model.Switchboard;

import java.util.ArrayList;

public class SwitchboardAdapter extends ArrayAdapter<Switchboard> {

    public SwitchboardAdapter(Context context, ArrayList<Switchboard> switchboards){
        super(context, 0, switchboards);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Switchboard switchboard = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.switchboards_listview_item, parent, false);
        }

        TextView switchboardLabel = convertView.findViewById(R.id.listview_item_switchboard);
        switchboardLabel.setText(switchboard.getLabel());

        return convertView;
    }
}
