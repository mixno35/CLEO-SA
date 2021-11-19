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
import com.mixno.cleo_sa.model.ScriptModel;

import java.io.File;
import java.util.List;

public class ScriptAdapter extends RecyclerView.Adapter<ScriptAdapter.ScriptHolder> {
    private List<ScriptModel> list;
    private Context context;

    public ScriptAdapter(List<ScriptModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ScriptAdapter.ScriptHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_script, parent, false);
        ScriptHolder holder = new ScriptHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ScriptHolder holder, int position) {
        final ScriptModel model = list.get(position);
        final String name = model.getName();

        holder.textName.post(new Runnable() {
            @Override
            public void run() {
                holder.textName.setText(name.substring(0, name.lastIndexOf(".")));
            }
        });

        holder.textPath.post(new Runnable() {
            @Override
            public void run() {
                holder.textPath.setText(name.replace("_disabled", ""));
            }
        });

        holder.textStatus.post(new Runnable() {
            @Override
            public void run() {
                if (name.endsWith("_disabled")) {
                    holder.textStatus.setText(context.getString(R.string.action_script_off));
                    holder.textStatus.setTextColor(context.getResources().getColor(R.color.statusOff));
                } else {
                    holder.textStatus.setText(context.getString(R.string.action_script_on));
                    holder.textStatus.setTextColor(context.getResources().getColor(R.color.statusOn));
                }
            }
        });

        holder.textDescription.post(new Runnable() {
            @Override
            public void run() {
                if (name.replace("_disabled", "").endsWith(".csa")) {
                    holder.textDescription.setText(context.getString(R.string.message_info_csa));
                } if (name.replace("_disabled", "").endsWith(".csi")) {
                    holder.textDescription.setText(context.getString(R.string.message_info_csi));
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.openMenu(context, model.getPath());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ScriptHolder extends RecyclerView.ViewHolder {
        protected TextView textName;
        protected TextView textPath;
        protected TextView textDescription;
        protected TextView textStatus;

        public ScriptHolder(View item) {
            super(item);
            textName = item.findViewById(R.id.textName);
            textPath = item.findViewById(R.id.textPath);
            textDescription = item.findViewById(R.id.textDescription);
            textStatus = item.findViewById(R.id.textStatus);
        }
    }
}
