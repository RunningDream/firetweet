package org.getlantern.firetweet.activity.support;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View;


import org.getlantern.firetweet.Constants;
import org.getlantern.firetweet.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by todd on 6/15/15.
 */


public class UpdaterActivity extends Activity implements Constants {

    private UpdaterTask mUpdaterTask;
    private TextView updateAvailableText;
    private ProgressBar spinner;
    private ImageView updateIcon;
    private static final String LOG_TAG = "UpdaterActivity";
    private static final String APK_URL = "https://raw.githubusercontent.com/firetweet/downloads/master/firetweet.apk";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_updater);

        spinner = (ProgressBar)findViewById(R.id.pbHeaderProgress);
        spinner.setVisibility(View.GONE);

        updateIcon = (ImageView)findViewById(R.id.update_icon);

        addDefaults();
    }

    private void addDefaults() {

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Semibold.ttf");
        updateAvailableText = (TextView)findViewById(R.id.update_available);
        updateAvailableText.setTextColor(Color.parseColor("black"));
        updateAvailableText.setTypeface(font);

        Button btn=(Button) findViewById(R.id.not_now);
        btn.setTypeface(font);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn = (Button)findViewById(R.id.install_update);
        btn.setTypeface(font);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                runUpdater();
            }
        });
    }

    private void runUpdater() {
        updateIcon.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);

        String[] updaterParams = {APK_URL};
        mUpdaterTask = new UpdaterTask(this);
        mUpdaterTask.execute(updaterParams);
    }

    public void dismissActivity() {

        finish();
    }

    @Override
    public void finish() {
        super.finish();
    }

    static class UpdaterTask extends AsyncTask<String, String, String> {

        private final UpdaterActivity mActivity;
        private final Context context;

        private static final String APK_PATH = "/sdcard/FireTweet.apk";

        UpdaterTask(final UpdaterActivity activity) {
            mActivity = activity;
            context = mActivity.getApplicationContext();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            String path = APK_PATH;
            try {
                Log.d(LOG_TAG, "Attempting to download new APK from " + sUrl[0]);
                URL url = new URL(sUrl[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int fileLength = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(path);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    int progress = (int) (total * 100 / fileLength);
                    publishProgress(Integer.toString(progress));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error installing new APK..");
                Log.e(LOG_TAG, e.getMessage());
                displayInstallError();
            }
            return path;
        }

        // show an alert when the update fails
        // and mention where the user can download the latest version
        // this also dismisses the current updater activity
        protected void displayInstallError() {
            AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
            alertDialog.setTitle("Error Downloading Update");
            alertDialog.setMessage("Try manually installing " +
                    "the latest version from https://firetweet.io");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mActivity.dismissActivity();
                        }
                    });
            alertDialog.show();
        }

        // begin the installation by opening the resulting file
        @Override
        protected void onPostExecute(final String path) {

            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            Log.d(LOG_TAG, "About to install new FireTweet apk");
            this.context.startActivity(i);

            mActivity.dismissActivity();
        }
    }
}