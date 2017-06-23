package com.coderalone.admin.funnyringtones.util;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFileAsynTask extends AsyncTask<String, Integer, String> {

    private DownloadFileAsynTaskInterface mDownloadFileAsynTaskInterface;

    public interface DownloadFileAsynTaskInterface {

        void onProgressUpdate(int progress);

        void onDownloadStarted();

        void onDownloadCompleted(String filePath);

        void onDownloadError();

    }

    public DownloadFileAsynTask(DownloadFileAsynTaskInterface downloadFileAsynTaskInterface) {
        this.mDownloadFileAsynTaskInterface = downloadFileAsynTaskInterface;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDownloadFileAsynTaskInterface.onDownloadStarted();
    }

    @Override
    protected String doInBackground(String... params) {
        if (params == null || params.length == 0) {
            return null;
        }

        String filePath = null;
        String fileName;

        fileName = params[0].replace(Constant.DATA_UPLOAD_URL, "");

        InputStream input = null;
        OutputStream outputFile = null;

        try {
            String urlString = params[0];

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.connect();
            int fileLength = connection.getContentLength();

            // Input stream to read file - with 8k buffer
            input = new BufferedInputStream(url.openStream(), 8192);
            // Create local path for file downloaded.
            filePath = Utils.createLocalFilePath(fileName);

            if (!TextUtils.isEmpty(filePath)) {
                // Output stream to write file
                outputFile = new FileOutputStream(filePath);
            }

            byte data[] = new byte[1024];

            int count;
            long total = 0;
            while ((count = input.read(data)) != -1) {

                total += count;
                if (fileLength > 0) {
                    publishProgress((int) (total * 100 / fileLength));
                }

                if (outputFile != null) {
                    outputFile.write(data, 0, count);
                }

            }
        } catch (IOException e) {
            mDownloadFileAsynTaskInterface.onDownloadError();
        } finally {
            try {
                if (outputFile != null) {
                    // Flushing output file.
                    outputFile.flush();
                    // Closing streams file.
                    outputFile.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mDownloadFileAsynTaskInterface.onProgressUpdate(values[0]);
    }

    @Override
    protected void onPostExecute(String filePath) {
        super.onPostExecute(filePath);
        mDownloadFileAsynTaskInterface.onDownloadCompleted(filePath);
    }

}