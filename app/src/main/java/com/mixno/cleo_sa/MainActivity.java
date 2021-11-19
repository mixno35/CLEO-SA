package com.mixno.cleo_sa;

import androidx.annotation.NonNull;
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
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        recycler = findViewById(R.id.recycler);
        swipe = findViewById(R.id.swipe);
        fab = findViewById(R.id.fab);

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

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                DataSA.setGrantPermissionsSettings(MainActivity.this);
                return;
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        } else {
            loadList(MainActivity.this);
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

        actionCleoApk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSA.openUrl(MainActivity.this, "https://libertycity.ru/files/gta-san-andreas-ios-android/143037-cleo-gta-sa-2.0-bez-root-prav.html");
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
                DataSA.openUrl(MainActivity.this, "https://instagram.com/mixno35");
            }
        });
        actionSocialVK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSA.openUrl(MainActivity.this, "https://vk.com/mixno35");
            }
        });
        actionSocialTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSA.openUrl(MainActivity.this, "https://t.me/mixno35");
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
