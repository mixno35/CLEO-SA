package com.mixno.cleo_sa.data;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.mixno.cleo_sa.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;

public class DataSA {

    public static String PATH_SCRIPTS = Environment.getExternalStorageDirectory().toString() + File.separator + "Android" + File.separator + "data" + File.separator + "com.rockstargames.gtasa";

    public static void openUrl(Context context, String url) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void deleteScript(Context context, String path) {
        final File directory1 = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Android" + File.separator + "data" + File.separator + "com.rockstargames.gtasa");
        final String tt = new File(path).getName().substring(0, new File(path).getName().lastIndexOf(".")).toLowerCase();
        try {
            File[] files = directory1.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    if (files[i].getName().toLowerCase().startsWith(tt)) {
                        if (new File(files[i].getPath()).delete()) {
                            Toast.makeText(context, String.format(context.getString(R.string.message_file_deleted), files[i].getName()), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        } catch (Exception e) {}
    }

    public static void deScript(Context context, final String path) {
        if (new File(path).getName().endsWith("_disabled")) {
            // Enable
            new File(path).renameTo(new File(path.replace("_disabled", "")));
        } else {
            // Disable
            new File(path).renameTo(new File(path+"_disabled"));
        }
    }

    public static void setGrantPermissionsSettings(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.title_grant_permissions));
        builder.setMessage(context.getString(R.string.message_grant_permissions));
        builder.setCancelable(false);
        builder.setPositiveButton(context.getString(R.string.action_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToSettings(context);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(context.getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static void goToSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String read(File path) {
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = null;
            if (path.getPath().endsWith(".fxt")) {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(path), Charset.forName("IBM866")));
            } else {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            }
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
            return text.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean write(File path, String text) {
        try {
            FileOutputStream stream = new FileOutputStream(path);
            try {
                stream.write(text.getBytes());
            } finally {
                stream.close();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean createScript(String name) throws Exception {
        return new File(PATH_SCRIPTS + File.separator + name).createNewFile();
    }
}
