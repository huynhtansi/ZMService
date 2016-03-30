package com.htsi.zmservice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by htsi.
 * Since: 1/5/16 on 4:27 PM
 * Project: ZMService
 */
public class DownloadMusicTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private ProgressDialog dialog;

    public DownloadMusicTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage(context.getResources().getString(R.string.loading));
        dialog.setMax(100);
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        URL url;
        try {
            url = new URL(params[0]);
            URLConnection connection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)connection;

            int response = httpURLConnection.getResponseCode();

            if (response == 200 && params.length == 4) {
                InputStream is = httpURLConnection.getInputStream();

                int contentLength = httpURLConnection.getContentLength();

                File dir = new File(Environment.getExternalStorageDirectory(), "ZingMp3");
                if (!dir.exists())
                    if (!dir.mkdir())
                        return null;

                String filename = params[1] +
                        "_" + params[2] +
                        "_-" + params[3] +
                        ".mp3";

                File file = new File(dir.getPath(), filename);

                byte[] bytes = new byte[1024*4];

                FileOutputStream fos = new FileOutputStream(file);
                BufferedInputStream bis = new BufferedInputStream(is);

                int elapsed = 0;

                int read;
                while ((read = bis.read(bytes)) != -1) {
                    fos.write(bytes, 0, read);
                    elapsed += read;
                    publishProgress(elapsed, contentLength);
                }

                fos.flush();
                fos.close();
                bis.close();

                return file.getAbsolutePath();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int elapsed = values[0];
        int contentLength = values[1];
        int delta = elapsed*100/contentLength;
        if (delta > dialog.getProgress())
            dialog.setProgress(delta);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s != null) {
            dialog.dismiss();
            Toast.makeText(context, context.getString(R.string.storage) + s, Toast.LENGTH_LONG).show();
            ((Activity)context).finish();
        } else {
            Toast.makeText(context, R.string.failure, Toast.LENGTH_SHORT).show();
        }
    }
}
