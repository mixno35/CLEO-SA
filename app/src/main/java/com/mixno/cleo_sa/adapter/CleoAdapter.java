package com.mixno.cleo_sa.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.mixno.cleo_sa.MainActivity;
import com.mixno.cleo_sa.R;
import com.mixno.cleo_sa.model.CleoModel;

import java.io.File;
import java.util.List;

public class CleoAdapter extends RecyclerView.Adapter<CleoAdapter.CleoHolder> {
    private List<CleoModel> list;
    private Context context;

    public CleoAdapter(List<CleoModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public CleoAdapter.CleoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cleo, parent, false);
        CleoHolder holder = new CleoHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final CleoHolder holder, int position) {
        final CleoModel model = list.get(position);

        holder.textName.post(new Runnable() {
            @Override
            public void run() {
                holder.textName.setText(model.getName());
            }
        });
        holder.textDescription.post(new Runnable() {
            @Override
            public void run() {
                holder.textDescription.setText(model.getDescription());
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CleoHolder extends RecyclerView.ViewHolder {
        protected TextView textName;
        protected TextView textDescription;

        public CleoHolder(View item) {
            super(item);
            textName = item.findViewById(R.id.cleoTitle);
            textDescription = item.findViewById(R.id.cleoDescription);
        }
    }
}
