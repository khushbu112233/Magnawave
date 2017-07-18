package com.jlouistechnology.magnawave.Webservice_list;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import com.jlouistechnology.magnawave.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.HttpGet;
import com.loopj.android.http.RequestParams;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class WebService {

    public static ProgressDialog mProgressDialog;
    static String json_login = "";


    //live
    public static String BASE_URL = "https://www.magnawavepemf.com/mobileAPI/api/v1/";
    //  public static String BASE_URL ="http://192.168.1.38/magnawave/api/v1/";

    public static String LOGIN=BASE_URL+"login";

    public static String ADDCLIENT=BASE_URL+"client/add";

    public static String EDITCLIENT=BASE_URL+"client/edit";

    public static String CLIENTS=BASE_URL+"clients";

    public static String ADDPAT=BASE_URL+"client/pet/add";


    public static String EDITPET=BASE_URL+"pet/details/add_edit";

    public static String EDITPAT=BASE_URL+"client/pet/edit";

    public static String LOGOUT=BASE_URL+"logout";

    public static String LISTPATDETAILS=BASE_URL+"client/pet/list/";

    public static String LISTRECENTRECORD=BASE_URL+"clients/filter/";

    public static String GUIDELINECATEGORIES=BASE_URL+"guidelinecategories";

    public static String GUIDELINEDETAIL=BASE_URL+"guidelinearticles/";

    public static String REGITER = BASE_URL+"register";

    public static String FORGOT = BASE_URL+"forgot";

    public static String CLIENT_DELETE = BASE_URL+"client/delete/";

    public static String CLIENT_PET_DELETE = BASE_URL+"client/pet/delete/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.v("URL", url);
        Log.v("RequestParams", params.toString());
        client.get(url, params, responseHandler);

    }
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.v("URL", url);
        Log.v("RequestParams_post", params.toString());
        client.post(url, params, responseHandler);
    }

    public static void showProgress(Context context) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();
        try {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mProgressDialog.show();
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setContentView(R.layout.progressdialog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissProgress() {
        if (mProgressDialog != null) {
            try {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static String GetData(String url, String value) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            Log.e("value",""+url);
            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization","Bearer "+ value);
            HttpResponse httpResponse = httpclient.execute(get);


            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;


    }

    public static String PostData(String url, String value) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            Log.e("value",""+value);
            HttpPost get = new HttpPost(url);
            get.setHeader("Authorization","Bearer "+ value);
            HttpResponse httpResponse = httpclient.execute(get);


            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;


    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public static String PostData(String[] valuse, String[] values, String url) {
        String s="";
        try
        {
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(url);

            List<cz.msebera.android.httpclient.NameValuePair> list=new ArrayList<cz.msebera.android.httpclient.NameValuePair>();
            for (int i =0;i<valuse.length;i++) {
                list.add(new cz.msebera.android.httpclient.message.BasicNameValuePair(values[i], valuse[i]));
                Log.v("values",values[i]+"--"+valuse[i]);
            }

            //  httpPost.addHeader("Content-Type","application/x-www-form-urlencoded");
            httpPost.setEntity(new cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity(list));
            HttpResponse httpResponse=  httpClient.execute(httpPost);

            cz.msebera.android.httpclient.HttpEntity httpEntity=httpResponse.getEntity();
            s= readResponse(httpResponse);
            Log.e("s",""+s);
        }
        catch(Exception exception)  {}
        return s;


    }

    public static String readResponse(HttpResponse res) {
        InputStream is=null;
        String return_text="";
        try {
            is=res.getEntity().getContent();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is));
            String line="";
            StringBuffer sb=new StringBuffer();
            while ((line=bufferedReader.readLine())!=null)
            {
                sb.append(line);
            }
            return_text=sb.toString();
        } catch (Exception e)
        {

        }
        return return_text;

    }

}
