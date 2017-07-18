package com.jlouistechnology.magnawave.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jlouistechnology.magnawave.Activity.DashboardActivity;
import com.jlouistechnology.magnawave.Fonts.TextView_Regular;
import com.jlouistechnology.magnawave.Fonts.TextView_bld;
import com.jlouistechnology.magnawave.Webservice_list.WebService;
import com.jlouistechnology.magnawave.utils.ConnectionDetector;
import com.jlouistechnology.magnawave.utils.Constants;
import com.jlouistechnology.magnawave.utils.Pref;
import com.jlouistechnology.magnawave.R;
import com.jlouistechnology.magnawave.databinding.FragmentUpdateClientBinding;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by aipxperts on 9/5/17.
 */

public class EditClientFragment extends Fragment {

    FragmentUpdateClientBinding mBinding;
    View rootView;
    Context context;
    Dialog mDialogRowBoardList;
    ConnectionDetector cd;
    /**
     * To get photo
     */
    private Window mWindow;
    private WindowManager.LayoutParams mLayoutParams;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MY_REQUEST_CODE = 100;
    public static Uri fileUri;
    int REQUEST_CAMERA = 0;
    int SELECT_FILE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    Bitmap rotatedBitmap;
    File file1;
    String resultstring;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /**
         * Binding all xml views
         */
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_update_client, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        cd=new ConnectionDetector(context);
        set_material_design();
        setdata();
        mBinding.imgClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });
        mBinding.txtdone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                int err_cnt=0;

                if(mBinding.edtFirstname.getText().toString().isEmpty())
                {
                    mBinding.edtFirstname.setError(getString(R.string.First_name_is_required));
                    err_cnt++;
                }
                if(mBinding.edtLastname.getText().toString().isEmpty())
                {
                    mBinding.edtLastname.setError(getString(R.string.Last_name_is_required));
                    err_cnt++;
                }
                /*if(mBinding.edtCompanyname.getText().toString().isEmpty())
                {
                    mBinding.edtCompanyname.setError(getString(R.string.Company_is_required));
                    err_cnt++;
                }*/
                if(mBinding.edtEmail.getText().toString().isEmpty())
                {
                    mBinding.edtEmail.setError(getString(R.string.email_is_required));
                    err_cnt++;
                }
                else {

                    if (!Patterns.EMAIL_ADDRESS.matcher(
                            mBinding.edtEmail.getText().toString()).matches()) {
                        mBinding.edtEmail.setError(getString(R.string.invalid_email));
                        err_cnt++;
                    }
                }
               /* if(mBinding.edtAddress.getText().toString().isEmpty())
                {
                    mBinding.edtAddress.setError(getString(R.string.address_is_required));
                    err_cnt++;
                }*/
                if(mBinding.edtPhone.getText().toString().isEmpty())
                {
                    mBinding.edtPhone.setError(getString(R.string.phone_no_is_required));
                    err_cnt++;
                }
                else {

                    if(mBinding.edtPhone.getText().toString().length()<10 || mBinding.edtPhone.getText().toString().length()>13)
                    {
                        mBinding.edtPhone.setError(getString(R.string.invalid_phone));
                        err_cnt++;
                    }
                }
                if(err_cnt==0) {
                    if(cd.isConnectingToInternet()) {

                        new ExecuteTask().execute(WebService.EDITCLIENT);
                    }else
                    {
                        cd.showToast(context, R.string.NO_INTERNET_CONNECTION);

                    }
                }

            }
        });
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        DashboardActivity.visible_back();

    }

    public void setdata()
    {
        mBinding.edtCompanyname.setText(Pref.getValue(context,"client_company_name",""));
        mBinding.edtEmail.setText(Pref.getValue(context,"client_email",""));
        mBinding.edtPhone.setText(Pref.getValue(context,"client_pno",""));
        mBinding.edtFirstname.setText(Pref.getValue(context,"client_fname",""));
        mBinding.edtLastname.setText(Pref.getValue(context,"client_lname",""));
        mBinding.edtAddress.setText(Pref.getValue(context,"client_address",""));
        if(!Pref.getValue(context,"client_profile","").equals("")) {
            Glide.with(context).load(Pref.getValue(context, "client_profile", "")).asBitmap().into(new BitmapImageViewTarget(mBinding.imgClient) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    mBinding.imgClient.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
    }

    public void set_material_design()
    {
        Constants.setTextWatcher(context, mBinding.edtFirstname,mBinding.txtFirstname);
        Constants.setTextWatcher(context,mBinding.edtLastname,mBinding.txtLastname);
        Constants.setTextWatcher(context, mBinding.edtCompanyname,mBinding.txtCompanyname);
        Constants.setTextWatcher(context,mBinding.edtEmail,mBinding.txtEmail);
        Constants.setTextWatcher(context, mBinding.edtPhone,mBinding.txtPhone);
        Constants.setTextWatcher(context, mBinding.edtAddress,mBinding.txtAddress);


    }

    protected void showInputDialog() {

        mDialogRowBoardList = new Dialog(context);
        mDialogRowBoardList.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogRowBoardList.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialogRowBoardList.setContentView(R.layout.photo_dialod_layout);
        mDialogRowBoardList.setCancelable(false);
        mWindow = mDialogRowBoardList.getWindow();
        mLayoutParams = mWindow.getAttributes();
        mLayoutParams.gravity = Gravity.BOTTOM;
        mWindow.setAttributes(mLayoutParams);
        final TextView_Regular txtgallery = (TextView_Regular) mDialogRowBoardList.findViewById(R.id.txtgallery);
        final TextView_Regular txtcamera= (TextView_Regular) mDialogRowBoardList.findViewById(R.id.txtcamera);
        final TextView_bld cancel = (TextView_bld) mDialogRowBoardList.findViewById(R.id.cancel);

        txtgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                SELECT_FILE);
                        mDialogRowBoardList.dismiss();
                    }
                } else {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                    mDialogRowBoardList.dismiss();
                }
            }
        });

        txtcamera.setOnClickListener(new View.OnClickListener() {

                                         @Override
                                         public void onClick(View v) {


                                             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                 if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                                                         != PackageManager.PERMISSION_GRANTED) {
                                                     getActivity().requestPermissions(new String[]{Manifest.permission.CAMERA},
                                                             MY_REQUEST_CODE);
                                                 } else {
                                                     Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                     fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                                                     Log.v("fileUri",fileUri+"--");
                                                     intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                                     // start the image capture Intent
                                                     startActivityForResult(intent, REQUEST_CAMERA);
                                                     mDialogRowBoardList.dismiss();
                                                 }
                                             } else {
                                                 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                 fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                                                 Log.v("fileUri",fileUri+"--");
                                                 intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                                 // start the image capture Intent
                                                 startActivityForResult(intent, REQUEST_CAMERA);
                                                 mDialogRowBoardList.dismiss();
                                             }


                                         }
                                     }
        );
        cancel.setOnClickListener(new View.OnClickListener()

                                  {
                                      @Override
                                      public void onClick(View v) {

                                          mDialogRowBoardList.dismiss();
                                      }
                                  }

        );


        mDialogRowBoardList.show();
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }  else {
            return null;
        }

        return mediaFile;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ((FragmentActivity)context).RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail=null;
                Bitmap rotatedBitmap = null;
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    // downsizing image as it throws OutOfMemory Exception for larger
                    // images
                    options.inSampleSize = 8;

                    final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                            options);

                    if (bitmap.getWidth() > bitmap.getHeight()) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight(), matrix, true);
                    }
                    else {
                        rotatedBitmap=bitmap;
                    }

                    // rotatedBitmap=bitmap;
                    Log.v("rotatedBitmap",rotatedBitmap+"--");

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    Bitmap b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    Bitmap bmpnew=Bitmap.createScaledBitmap(b, 500, 500, false);
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(context.getResources(), bmpnew);
                    circularBitmapDrawable.setCircular(true);
                    mBinding.imgClient.setImageDrawable(circularBitmapDrawable);
                    file1= bitmapToFile(bmpnew);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                if(selectedImageUri.toString().startsWith("content")) {
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    Cursor cursor = ((FragmentActivity)context).managedQuery(selectedImageUri, projection, null, null,
                            null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    String selectedImagePath = cursor.getString(column_index);
                    Bitmap bm;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(selectedImagePath, options);
                    final int REQUIRED_SIZE = 200;
                    int scale = 1;
                    while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                            && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                        scale *= 2;
                    options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;
                    bm = BitmapFactory.decodeFile(selectedImagePath, options);

                    rotatedBitmap=bm;

                    if (bm.getWidth() > bm.getHeight()) {
                        Matrix matrix = new Matrix();
                        //matrix.postRotate(90);
                        rotatedBitmap = Bitmap.createBitmap(bm, 0, 0,bm.getWidth(),bm.getHeight(), matrix, true);
                    }
                }
                else {

                    String file_path=data.getData().toString();
                    Log.e("rotatedBitmap","---------------"+file_path.substring(5,file_path.length()));
                    File imgFile = new  File(file_path.substring(5,file_path.length()));
                    Log.e("rotatedBitmap","---------------"+imgFile+"--"+imgFile.exists());
                    if(imgFile.exists()) {
                        rotatedBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        Log.e("rotatedBitmap",rotatedBitmap+"---");
                    }
                }


                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Bitmap b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                Bitmap bmpnew=Bitmap.createScaledBitmap(b, 500, 500, false);


                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), bmpnew);
                circularBitmapDrawable.setCircular(true);
                mBinding.imgClient.setImageDrawable(circularBitmapDrawable);
               file1=bitmapToFile(bmpnew);


            }

        }
    }
    public File bitmapToFile(Bitmap bmp) {
        try {
            final int REQUIRED_SIZE = 200;
            ByteArrayOutputStream bos = new ByteArrayOutputStream(REQUIRED_SIZE);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bArr = bos.toByteArray();
            bos.flush();
            bos.close();
            long timestemp=(System.currentTimeMillis()/100);
            String file_name="profile_"+String.valueOf(timestemp)+".png";
            FileOutputStream fos = context.openFileOutput(file_name, Context.MODE_PRIVATE);
            fos.write(bArr);
            fos.flush();
            fos.close();
            File mFile = new File(context.getFilesDir().getAbsolutePath(), file_name);
            return mFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class ExecuteTask extends
            AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(context);
        }

        @Override
        protected String doInBackground(String... urls) {

            return executeMultipartPost(urls[0],file1, Pref.getValue(context, "token",""));

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            try {
                WebService.dismissProgress();

                Log.e("result",result+"");
                JSONObject json;
                json = new JSONObject(result);
                JSONObject json2=json.getJSONObject("success");
                if(json2.getString("status").equals("true"))
                {
                    if(json2.getString("response_code").equals("200")) {

                        Toast.makeText(context,json2.getString("message"),Toast.LENGTH_LONG).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                        getActivity().getSupportFragmentManager().popBackStack();

                    }


                }
                else {
                    Toast.makeText(context,json2.getString("message"),Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


        public String executeMultipartPost(String url, File file, String token) {


            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost poster = new HttpPost(url);

                // File image = new File(imgPath);  //get the actual file from the device
                Log.v("responsimage", "file&token:" + file+" "+token);
                //  poster.setHeader("Content-Type","multipart/form-data");
                poster.setHeader("Authorization","Bearer "+ token);
                poster.addHeader("Accept", "application/json");

               /* entity.addPart("Content-Type",new StringBody("multipart/form-data"));
                entity.addPart("Authorization", new StringBody("Bearer "+token));
*/
                MultipartEntity entity = new MultipartEntity(org.apache.http.entity.mime.HttpMultipartMode.BROWSER_COMPATIBLE);
                if(file!=null) {
                    entity.addPart("profile", new org.apache.http.entity.mime.content.FileBody(file));
                }
                entity.addPart("fname", new StringBody(mBinding.edtFirstname.getText().toString()));
                entity.addPart("lname", new StringBody(mBinding.edtLastname.getText().toString()));
                entity.addPart("company", new StringBody(mBinding.edtCompanyname.getText().toString()));
                entity.addPart("email", new StringBody(mBinding.edtEmail.getText().toString()));
                entity.addPart("address", new StringBody(mBinding.edtAddress.getText().toString()));
                entity.addPart("contact", new StringBody(mBinding.edtPhone.getText().toString()));
                entity.addPart("client_id",new StringBody(Pref.getValue(context,"client_id","")));
                poster.setEntity(entity);

                //Log.e("responsimage", "request:" + entity.getContent());
                client.execute(poster, new ResponseHandler<Object>() {
                    public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                        HttpEntity respEntity = response.getEntity();
                        String responseString = EntityUtils.toString(respEntity);
                        // do something with the response string
                        resultstring=responseString.toString();
                        Log.e("responseimage", responseString.toString());
                        return resultstring;
                    }
                });
            } catch (Exception e) {
                //do something with the error
            }


            return resultstring;
        }
        public   String readResponse(HttpResponse res) {
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
}
