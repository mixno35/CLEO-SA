package com.mixno.cleo_sa;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.mixno.cleo_sa.data.DataSA;
import com.mixno.cleo_sa.widget.Editor;

import java.io.File;

public class EditActivity extends AppCompatActivity {

    private String PATH = "";
    private Toolbar toolbar;

    private Editor editText;

    private InterstitialAd interstitialAd;

    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        toolbar = findViewById(R.id.toolbar);
        editText = findViewById(R.id.editScript);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            setTitle(getString(R.string.action_edit_script));

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        PATH = getIntent().getStringExtra("path");

//        Toast.makeText(this, PATH, Toast.LENGTH_SHORT).show();

        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.setCode(PATH);
            }
        });

        if (sp.getString("disable_ads", "").equals("all")) {} else {
            MobileAds.initialize(this, "ca-app-pub-2543059856849154~7048317671");
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("F4A58C794027AEB7BE7E8A365FA6877E").build();
            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId("ca-app-pub-2543059856849154/5010485300");
            interstitialAd.loadAd(adRequest);

            interstitialAd.setAdListener(new AdListener() {
                public void onAdClosed() {
                    saveFxt();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSave:
                if (sp.getString("disable_ads", "").equals("all")) {
                    saveFxt();
                } else {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    } else {
                        saveFxt();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveFxt() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
        builder.setTitle(getString(R.string.title_edit_script_save));
        builder.setMessage(getString(R.string.message_edit_script_save));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataSA.write(new File(PATH), editText.getText().toString());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
