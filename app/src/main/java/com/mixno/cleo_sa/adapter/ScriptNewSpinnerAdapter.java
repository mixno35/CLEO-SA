package com.mixno.cleo_sa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mixno.cleo_sa.R;

public class ScriptNewSpinnerAdapter extends ArrayAdapter<String> {

    private String[] objects;

    public ScriptNewSpinnerAdapter(final Context context, int textViewResId, String[] objects) {
        super(context, textViewResId, objects);
        this.objects = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(final int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_script_new_spinner, parent, false);
        final TextView label = row.findViewById(R.id.textName);
        final TextView desc = row.findViewById(R.id.textDescription);
        label.post(new Runnable() {
            @Override
            public void run() {
                label.setText(objects[position]);
            }
        });
        desc.post(new Runnable() {
            @Override
            public void run() {
                if (objects[position].equals(".csa")) {
                    desc.setText(getContext().getString(R.string.message_info_csa));
                    return;
                } if (objects[position].equals(".csi")) {
                    desc.setText(getContext().getString(R.string.message_info_csi));
                }
            }
        });
        return  row;
    }
}
