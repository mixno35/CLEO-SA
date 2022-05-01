package com.mixno.cleo_sa.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mixno.cleo_sa.EditActivity;
import com.mixno.cleo_sa.R;
import com.mixno.cleo_sa.model.ScriptFileModel;

import java.util.List;


public class ScriptFileAdapter extends RecyclerView.Adapter<ScriptFileAdapter.ScriptFileHolder> {
    private List<ScriptFileModel> list;
    private Context context;

    public ScriptFileAdapter(List<ScriptFileModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ScriptFileAdapter.ScriptFileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_script_file, parent, false);
        ScriptFileHolder holder = new ScriptFileHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ScriptFileHolder holder, int position) {
        final ScriptFileModel model = list.get(position);

        holder.textName.post(new Runnable() {
            @Override
            public void run() {
                holder.textName.setText(model.getName());
            }
        });

        holder.imageEdit.post(new Runnable() {
            @Override
            public void run() {
                if (model.getName().endsWith(".fxt")) {
                    holder.imageEdit.setVisibility(View.VISIBLE);
                } else {
                    holder.imageEdit.setVisibility(View.GONE);
                }
            }
        });

        holder.imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, EditActivity.class).putExtra("path", model.getPath()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ScriptFileHolder extends RecyclerView.ViewHolder {
        protected TextView textName;
        protected ImageView imageEdit;

        public ScriptFileHolder(View item) {
            super(item);
            textName = item.findViewById(R.id.textName);
            imageEdit = item.findViewById(R.id.imageEdit);
        }
    }
}
