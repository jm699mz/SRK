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
import com.example.srk.model.History;

import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter<History> {

    public HistoryAdapter(Context context, ArrayList<History> historyList) {
        super(context, 0, historyList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        History history = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_listview_item, parent, false);
        }

        TextView codeLabel = convertView.findViewById(R.id.history_code_label);
        TextView scannedBy = convertView.findViewById(R.id.textview_scanned_by);
        TextView scanDate = convertView.findViewById(R.id.textview_scan_date);

        codeLabel.setText(history.getCodeLabel());
        scannedBy.setText(history.getScannedBy());
        scanDate.setText(history.getCreationTime());

        return convertView;
    }
}
