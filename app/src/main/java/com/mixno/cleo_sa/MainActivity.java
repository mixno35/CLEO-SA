package com.mixno.cleo_sa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mixno.cleo_sa.adapter.ScriptAdapter;
import com.mixno.cleo_sa.adapter.ScriptFileAdapter;
import com.mixno.cleo_sa.adapter.ScriptNewSpinnerAdapter;
import com.mixno.cleo_sa.data.DataSA;
import com.mixno.cleo_sa.model.ScriptFileModel;
import com.mixno.cleo_sa.model.ScriptModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private static RecyclerView recycler;
    private static SwipeRefreshLayout swipe;

    private static RecyclerView.Adapter listAdapter;
    private static RecyclerView.LayoutManager listLayoutManager;

    public static List<ScriptModel> list = new ArrayList<>();

    private static RecyclerView.Adapter listAdapterFiles;
    private static RecyclerView.LayoutManager listLayoutManagerFiles;

    private static List<ScriptFileModel> listF = new ArrayList<>();

    private static final int REQUEST_CODE_PERMISSION = 15;
    private static final int REQUEST_ACTION_OPEN_DOCUMENT_TREE = 26;
    private static final int REQUEST_ACTION_OPEN_DOCUMENT_MANAGER = 27;

    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

    private FloatingActionButton fab;

    private AdView adView;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        recycler = findViewById(R.id.recycler);
        swipe = findViewById(R.id.swipe);
        fab = findViewById(R.id.fab);
        adView = findViewById(R.id.adView);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (sp.getString("disable_ads", "").equals("banner") || sp.getString("disable_ads", "").equals("all")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adView.setVisibility(View.GONE);
                }
            });
        } else {
            MobileAds.initialize(this, "ca-app-pub-2543059856849154~7048317671");
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("F4A58C794027AEB7BE7E8A365FA6877E").build();
            adView.loadAd(adRequest);
        }

//        Toast.makeText(this, getString(R.string.message_hello), Toast.LENGTH_SHORT).show();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            setTitle(getString(R.string.app_name_full));
        }

//        loadList(MainActivity.this);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadList(MainActivity.this);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newScript();
            }
        });

//        StorageManager storageManagerStat = (StorageManager) this.getSystemService(Context.STORAGE_STATS_SERVICE);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            StorageManager storageManager = (StorageManager) this.getSystemService(Context.STORAGE_SERVICE);
//            Intent intentStorage = storageManager.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
//            String startDir = "Android" + File.separator + "data";
//            Uri uri = intentStorage.getParcelableExtra("android.provider.extra.INITIAL_URI");
//            String scheme = uri.toString();
//            scheme = scheme.replace("/root/", "/document/");
//            startDir = startDir.replace("/", "%2F");
//            scheme += "%3A" + startDir;
//            uri = Uri.parse(scheme);
//            intentStorage.putExtra("android.provider.extra.INITIAL_URI", uri);
//            startActivityForResult(intentStorage, REQUEST_ACTION_OPEN_DOCUMENT_TREE);
//
//        }

        checkPermission();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PERMISSION && resultCode == Activity.RESULT_OK) {
            loadList(MainActivity.this);
        } if (requestCode == REQUEST_ACTION_OPEN_DOCUMENT_TREE && resultCode == Activity.RESULT_OK) {
            loadList(MainActivity.this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void checkPermission () {
        // 1-8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            }
            return;
        }
        // 9-10
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                DataSA.setGrantPermissionsSettings(MainActivity.this);
            }
            return;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                loadList(MainActivity.this);
            }
        }
        // 11+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                Toast.makeText(getApplicationContext(), "Permission Android 11+ Granded!", Toast.LENGTH_SHORT).show();
                StorageManager storageManager = (StorageManager) this.getSystemService(Context.STORAGE_SERVICE);
                Intent intentStorage = null;
                intentStorage = storageManager.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
                String startDir = "Android" + File.separator + "data";
                Uri uri = intentStorage.getParcelableExtra("android.provider.extra.INITIAL_URI");
                String scheme = uri.toString();
                scheme = scheme.replace("/root/", "/document/");
                startDir = startDir.replace("/", "%2F");
                scheme += "%3A" + startDir;
                uri = Uri.parse(scheme);
                intentStorage.putExtra("android.provider.extra.INITIAL_URI", uri);
                startActivityForResult(intentStorage, REQUEST_ACTION_OPEN_DOCUMENT_TREE);
                // loadList(MainActivity.this);
            } else {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityForResult(intent, REQUEST_ACTION_OPEN_DOCUMENT_MANAGER);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.actionInformation) {
            try {
                information();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (item.getItemId() == R.id.actionCleo) {
            startActivity(new Intent(MainActivity.this, CleoActivity.class));
        }
        return true;
    }

    public static void loadList(final Context context) {
        File directory = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Android" + File.separator + "data" + File.separator + "com.rockstargames.gtasa");

        list.clear();

        try {
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    if (files[i].getName().endsWith(".csi") || files[i].getName().endsWith(".csa") || files[i].getName().endsWith(".csi_disabled") || files[i].getName().endsWith(".csa_disabled")) {
                        list.add(new ScriptModel(files[i].getName(), files[i].getPath(), files[i].lastModified()));
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        swipe.setRefreshing(true);

//        Toast.makeText(this, directory.getPath(), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listLayoutManager = new LinearLayoutManager(context);
                recycler.setLayoutManager(listLayoutManager);
                listAdapter = new ScriptAdapter(list, context);
                recycler.setAdapter(listAdapter);

                swipe.setRefreshing(false);

                if (list.size() <= 0) {
                    Toast.makeText(context, context.getString(R.string.toast_no_scripts), Toast.LENGTH_SHORT).show();
                }
            }
        }, 500);
    }

    private void newScript() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View inflate = getLayoutInflater().inflate(R.layout.alert_create_script, null);
        builder.setView(inflate);
        builder.setTitle(getString(R.string.title_create_script));

        final TextInputEditText editName = inflate.findViewById(R.id.editName);
        final MaterialButton actionCreate = inflate.findViewById(R.id.actionCreate);
        final AppCompatSpinner spinn = inflate.findViewById(R.id.spinn);

        String[] arr = getResources().getStringArray(R.array.script_file);

        spinn.setAdapter(new ScriptNewSpinnerAdapter(this, R.layout.row_script_new_spinner, arr));

        actionCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editName.getText().toString().length() < 5) {
                    editName.setError("Minimal value 5 charsets!");
                    return;
                } if (editName.getText().toString().length() > 25) {
                    editName.setError("Maximum value 25 charsets!");
                    return;
                }
                String format = ".csa";
                if (spinn.getSelectedItemPosition() == 0) {
                    format = ".csa";
                } if (spinn.getSelectedItemPosition() == 1) {
                    format = ".csi";
                }
                try {
                    if (DataSA.createScript(editName.getText().toString() + format)) {
                        Toast.makeText(MainActivity.this, "Script created!", Toast.LENGTH_SHORT).show();
                        loadList(MainActivity.this);
                    } else {
                        Toast.makeText(MainActivity.this, "Error script create!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });



        AlertDialog alert = builder.create();
        alert.show();
    }

    private void information() throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View inflate = getLayoutInflater().inflate(R.layout.alert_information, null);
        builder.setView(inflate);
        builder.setTitle(getString(R.string.action_information)+" | v."+getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        builder.setIcon(R.drawable.ic_info_black_24dp);

        final MaterialButton actionCleoApk = inflate.findViewById(R.id.actionCleoApk);
        final MaterialButton actionCleoScript = inflate.findViewById(R.id.actionCleoScript);

        final MaterialButton actionSocialInstagram = inflate.findViewById(R.id.actionSocialInstagram);
        final MaterialButton actionSocialVK = inflate.findViewById(R.id.actionSocialVK);
        final MaterialButton actionSocialTelegram = inflate.findViewById(R.id.actionSocialTelegram);

        final MaterialButton actionAdsActivateAll = inflate.findViewById(R.id.actionAdsActivateAll);
        final MaterialButton actionAdsDisableAll = inflate.findViewById(R.id.actionAdsDisableAll);
        final MaterialButton actionAdsDisableBanner = inflate.findViewById(R.id.actionAdsDisableBanner);

        actionCleoApk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSA.openUrl(MainActivity.this, "https://libertycity.ru/files/gta-san-andreas-ios-android/156957-cleo-gta-sa-2.00-android-11-fix-flm-6.0.html");
            }
        });
        actionCleoScript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSA.openUrl(MainActivity.this, "https://libertycity.ru/files/gta-san-andreas-ios-android/mody/cleo-skripty/");
            }
        });

        actionSocialInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSA.openUrl(MainActivity.this, "https://instagram.com/xianitt");
            }
        });
        actionSocialVK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSA.openUrl(MainActivity.this, "https://vk.com/xianitt");
            }
        });
        actionSocialTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSA.openUrl(MainActivity.this, "https://t.me/xianitt");
            }
        });

        actionAdsActivateAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString("disable_ads", "").apply();
                Toast.makeText(MainActivity.this, "All ads activated!", Toast.LENGTH_SHORT).show();
            }
        });
        actionAdsDisableAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString("disable_ads", "all").apply();
                Toast.makeText(MainActivity.this, "All ads disabled!", Toast.LENGTH_SHORT).show();
            }
        });
        actionAdsDisableBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString("disable_ads", "banner").apply();
                Toast.makeText(MainActivity.this, "Banner disabled!", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void openMenu(final Context context, final String path) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        View inflate = ((Activity)context).getLayoutInflater().inflate(R.layout.menu_script, null);
        dialog.setContentView(inflate);

        final TextView textName = inflate.findViewById(R.id.textName);
        final TextView textPath = inflate.findViewById(R.id.textPath);
        final TextView actionEnableDisable = inflate.findViewById(R.id.textActionEnableDisable);
        final TextView actionEditScript = inflate.findViewById(R.id.textActionEditScript);
        final TextView actionDelete = inflate.findViewById(R.id.textActionDelete);
        final RecyclerView listFiles = inflate.findViewById(R.id.listFiles);

        listF.clear();


        actionEnableDisable.post(new Runnable() {
            @Override
            public void run() {
                if (new File(path).getName().endsWith("_disabled")) {
                    actionEnableDisable.setText(context.getString(R.string.action_enable_script));
                } else {
                    actionEnableDisable.setText(context.getString(R.string.action_disable_script));
                }
            }
        });

        textPath.setText("");

        textName.post(new Runnable() {
            @Override
            public void run() {
                textName.setText(new File(path).getName().substring(0, new File(path).getName().lastIndexOf(".")));
            }
        });

        final File directory1 = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Android" + File.separator + "data" + File.separator + "com.rockstargames.gtasa");
        final String tt = new File(path).getName().substring(0, new File(path).getName().lastIndexOf(".")).toLowerCase();

        try {
            File[] files = directory1.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    if (files[i].getName().toLowerCase().startsWith(tt)) {
                        listF.add(new ScriptFileModel(files[i].getName().replace("_disabled", ""), files[i].getPath(), 0));
                    }
                }
            }
        } catch (Exception e) {}

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listLayoutManagerFiles = new LinearLayoutManager(context);
                listFiles.setLayoutManager(listLayoutManagerFiles);
                listAdapterFiles = new ScriptFileAdapter(listF, context);
                listFiles.setAdapter(listAdapterFiles);
            }
        }, 500);

        actionEnableDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSA.deScript(context, path);
                MainActivity.loadList(context);
                dialog.cancel();
            }
        });

        actionEditScript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, EditActivity.class).putExtra("path", path));
            }
        });

        actionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.action_delete_script));
                builder.setMessage(String.format(context.getString(R.string.message_delete_script), new File(path).getName().substring(0, new File(path).getName().lastIndexOf("."))));
                builder.setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DataSA.deleteScript(context, path);
                        MainActivity.loadList(context);
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton(context.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        dialog.show();
    }
}
