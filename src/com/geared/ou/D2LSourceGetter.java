/**
 *
 * @author David Findley (ThinksInBits)
 * 
 * The source for this application may be found in its entirety at 
 * https://github.com/ThinksInBits/OU-Mobile-App
 * 
 * This application is published on the Google Play Store under
 * the title: OU Mobile Alpha:
 * https://play.google.com/store/apps/details?id=com.geared.ou
 * 
 * If you want to follow the official development of this application
 * then check out my Trello board for the project at:
 * https://trello.com/board/ou-app/4f1f697a28390abb75008a97
 * 
 * Please email me at: thefindley@gmail.com with questions.
 * 
 */

package com.geared.ou;

import android.util.Log;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


/**
 *
 * This activity interfaces with D2L. It has features to allow download
 * page sources and files from D2L. User D2L credentials must be set for these features
 * to work
 * 
 */
public class D2LSourceGetter {
    private static final String BAD_LOGIN_STRING = "Invalid Username\\/Password";
    private static final String LOGIN_URL = "http://learn.ou.edu/d2l/lp/auth/login/login.d2l";
    private String lastSource;
    
    public static enum SGError {
        BAD_CREDENTIALS, NO_CONNECTION, NO_CREDENTIALS, NO_ERROR, NO_DATA
    }
    
    private long bytesTransfered;
    private long totalDownloadSize;
    
    private class CountingFileOutputStream extends FileOutputStream {
        private ContentActivity.Download t;

        public CountingFileOutputStream(File file, boolean append, ContentActivity.Download t) throws FileNotFoundException {
            super(file, append);
            this.t = t;
            t.setTotalDownloadSize((int)totalDownloadSize);
        }

        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {
            super.write(buffer, offset, count);
            bytesTransfered += count;
            t.publicProgressUpdate((int)bytesTransfered);
        }


        @Override
        public void write(int oneByte) throws IOException {
            super.write(oneByte);
            bytesTransfered++;
            t.publicProgressUpdate((int)bytesTransfered);
        }
    }
    
    private List<NameValuePair> loginParams;
    private Boolean loggedIn;
    private HttpClient httpclient;
    private BufferedReader mReader;
    
    public D2LSourceGetter() {
        loggedIn = false;
        loginParams = new ArrayList<NameValuePair>();
        httpclient = new DefaultHttpClient();
        lastSource = "";
        bytesTransfered = 0L;
        totalDownloadSize = 0L;
    }
    
    public void setCredentials(String username, String password) {
        loginParams.clear();
        loginParams.add(new BasicNameValuePair("userName", username));
        loginParams.add(new BasicNameValuePair("password", password));
        loginParams.add(new BasicNameValuePair("Login", "Login"));
    }
    
    public Boolean credentialsSet() {
        if (loginParams.isEmpty())
            return false;
        return true;
    }
    
    public String getPulledSource()
    {
        String t = lastSource;
        //lastSource = null;
        return t;
    }
    
    public SGError pullSource(String url) {
        if (!loggedIn) {
            SGError loginError = login();
            if (loginError != SGError.NO_ERROR)
                return loginError;
        }
        
        try
        {
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            //OutputStream q = new OutputStream();
            //ssssresponse.getEntity().writeTo(q);
            
            mReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            lastSource = "";
            while ((line = mReader.readLine()) != null)
            {
                lastSource = lastSource + line;
            }
        } 
        catch (IOException ex)
        {
            return SGError.NO_CONNECTION;
        }
        
        //get da pages

        return SGError.NO_ERROR;
    }
    
    public SGError downloadFile(File file, String url, ContentActivity.Download t) {
        if (!loggedIn) {
            SGError loginError = login();
            if (loginError != SGError.NO_ERROR)
                return loginError;
        }
        Log.d("OU", "Using url: "+url);
        try
        {   
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                bytesTransfered = 0L;
                totalDownloadSize = entity.getContentLength();
                Log.d("OU", "Total file size: "+totalDownloadSize);
                CountingFileOutputStream fos = new CountingFileOutputStream(file, false, t);
                entity.writeTo(fos);
                fos.close();
            }
            else {
                Log.d("OU", "entity was null");
                return SGError.NO_DATA;
            }
        }
        catch (IOException ex) {
            Logger.getLogger(D2LSourceGetter.class.getName()).log(Level.SEVERE, null, ex);
            Log.e("OU", "There was an exception when downloading file", ex);
        }
        return SGError.NO_ERROR;
    }
    
    // Sets lastSource to whatever page login redirects to.
    // Should be d2l home if login is successful.
    public SGError login() {
        if (!credentialsSet())
            return SGError.NO_CREDENTIALS;
        lastSource="";
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(loginParams, "UTF-8");
            HttpPost httppost = new HttpPost(LOGIN_URL);
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);

            mReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = mReader.readLine()) != null)
            {
                lastSource = lastSource + line;
            }
        }
        /* Could not connect to D2L. */
        catch (IOException ex) {
            return SGError.NO_CONNECTION;
        }

        /* Bad Login. */
        if (lastSource.indexOf(BAD_LOGIN_STRING) > 0) {
            return SGError.BAD_CREDENTIALS;
        }
        return SGError.NO_ERROR;
    }
}
