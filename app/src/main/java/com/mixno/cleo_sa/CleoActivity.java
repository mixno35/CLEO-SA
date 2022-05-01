package com.mixno.cleo_sa;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mixno.cleo_sa.adapter.CleoAdapter;
import com.mixno.cleo_sa.model.CleoModel;

import java.util.ArrayList;
import java.util.List;

public class CleoActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private RecyclerView recycler;
    private RecyclerView.Adapter listAdapter;
    private RecyclerView.LayoutManager listLayoutManager;

    public List<CleoModel> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleo);
        recycler = findViewById(R.id.recycler);

        toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            setTitle(getString(R.string.action_cleo_scripts));

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        list.add(new CleoModel("1_CHEATS_1", getString(R.string.cleo_1_cheats_1), "1_CHEATS_1.csi", "1_CHEATS_1.fxt"));
        list.add(new CleoModel("gtasa.flying", getString(R.string.cleo_gtasa_flying), "gtasa.flying.csi", ""));

        listLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(listLayoutManager);
        listAdapter = new CleoAdapter(list, getApplicationContext());
        recycler.setAdapter(listAdapter);
    }
}
