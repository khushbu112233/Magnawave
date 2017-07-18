package com.jlouistechnology.magnawave.Fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jlouistechnology.magnawave.Activity.DashboardActivity;
import com.jlouistechnology.magnawave.Adapter.PatDetailsRecyclerViewAdapter;
import com.jlouistechnology.magnawave.Fonts.Edittext_Regular;
import com.jlouistechnology.magnawave.Fonts.TextView_Regular;
import com.jlouistechnology.magnawave.Fonts.TextView_bld;
import com.jlouistechnology.magnawave.Interface.OnClickDeleteListener;
import com.jlouistechnology.magnawave.Interface.OnClickLinkedCurrentDeviceListener;
import com.jlouistechnology.magnawave.Model.Pat_details;
import com.jlouistechnology.magnawave.R;
import com.jlouistechnology.magnawave.Webservice_list.WebService;
import com.jlouistechnology.magnawave.databinding.FragmentAddNewPatBinding;
import com.jlouistechnology.magnawave.utils.CircularImageView;
import com.jlouistechnology.magnawave.utils.ConnectionDetector;
import com.jlouistechnology.magnawave.utils.Constants;
import com.jlouistechnology.magnawave.utils.Pref;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by aipxperts on 9/5/17.
 */
public class AddNewPatFragment_Old extends Fragment {

    FragmentAddNewPatBinding mBinding;
    View rootView;
    Context context;
    public Dialog mDialogRowBoardList;
    Dialog mDialogRowBoardList_add;
    Dialog mDialogRowBoardList_edit;
    int no_of_notes=1;
    public static PatDetailsRecyclerViewAdapter adapter;
    public static ArrayList<Pat_details> pat_detailsArrayList=new ArrayList<>();
    public static RecyclerView recycler_view_pat_details;
    public static final int FLIP_VERTICAL = 1;
    public static final int FLIP_HORIZONTAL = 2;
    int height,width;
    int count=1;;
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
    CircularImageView img_animal_part;
    String resultstring;
    File animal_front_file;
    File animal_rotate_file;
    ConnectionDetector cd;
    Bitmap simple_image;
    Bitmap flip_image=null;
    int default_image_status=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /**
         * Binding all xml views
         */
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_new_pat, container, false);

        rootView = mBinding.getRoot();
        context = getActivity();
        cd=new ConnectionDetector(context);
        pat_detailsArrayList.clear();
        setdate();
        setanimal_default();
        mBinding.txtClientName.setText(Pref.getValue(context,"client_fname","")+" "+ Pref.getValue(context,"client_lname",""));
        recycler_view_pat_details=(RecyclerView)rootView.findViewById(R.id.recycler_view_pat_details);

        set_simple_and_flip_bitmap();
        Pref.setValue(context,"is_flip_image","false");
        height=mBinding.imageFrameLayout.getHeight();
        width=mBinding.imageFrameLayout.getWidth();


        BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPat.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        simple_image=bitmap;

        mBinding.imageFrameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPat.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                if(bitmap!=null) {
                    showInputDialog(context, (int) event.getX(), (int) event.getY());
                }
                else {
                    Toast.makeText(context,"Please add image by click on camera icon!",Toast.LENGTH_LONG).show();
                }


                return false;
            }
        });

        mBinding.imageFrameLayoutFlip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                showInputDialog(context,(int) event.getX(),(int) event.getY());


                return false;
            }
        });

        mBinding.imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pref.setValue(context,"animal_image","true");
                //if(pat_detailsArrayList.size()==0) {
                showInputDialog_camera();
                //}
            }
        });

        mBinding.txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setMessage("Are you sure you want to go back?");
                // set positive button: Yes message
                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {

                        getActivity().getSupportFragmentManager().popBackStack();

                    }
                });
                // set negative button: No message
                alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel the alert box and put a Toast to the user
                        dialog.cancel();

                    }
                });
                // set neutral button: Exit the app message

                AlertDialog alertDialog = alertDialogBuilder.create();
                // show alert
                alertDialog.show();

            }
        });

        mBinding.imgFlip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(default_image_status==0)
                {
                    mBinding.imageFrameLayoutFlip.removeAllViews();
                    mBinding.imageFrameLayoutFlip.addView(mBinding.imgPatFlip);

                    Log.e("is_flip_image","yes");
                    Pref.setValue(context,"is_flip_image","true");
                   /* if(mBinding.imgpAT.getDrawable()!=null) {
                        if(flip_image==null) {
                            BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPat.getDrawable();
                            Bitmap bitmap = drawable.getBitmap();
                            //simple_image=bitmap;
                            if (bitmap != null) {

                                flip_image = flipImage(bitmap, 2);
                                Log.e("is_flip_image", "values:" + flip_image);
                            }
                        }
                    }
                    mBinding.imgPatFlip.setImageBitmap(flip_image);
                    default_image_status=1;*/
                    if(mBinding.imgPatFlip.getDrawable()==null) {
                        if(flip_image==null) {
                            BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPat.getDrawable();
                            Bitmap bitmap = drawable.getBitmap();
                            //simple_image=bitmap;
                            if (bitmap != null) {

                                flip_image = flipImage(bitmap, 2);
                                Log.e("is_flip_image", "values:" + flip_image);
                            }
                        }
                        mBinding.imgPatFlip.setImageBitmap(flip_image);
                    }
                    else {

                    }

                    default_image_status=1;
                    if(pat_detailsArrayList.size()>0) {
                        for (int i = 0; i < pat_detailsArrayList.size(); i++) {
                            if (pat_detailsArrayList.get(i).getIs_flip_image().equals(Pref.getValue(context, "is_flip_image", ""))) {

                                final float inPixels = getActivity().getResources().getDimension(R.dimen.count_size);
                                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) inPixels, (int) inPixels);
                                TextView iv = new TextView(context);
                                lp.setMargins(pat_detailsArrayList.get(i).getX(), pat_detailsArrayList.get(i).getY(), 0, 0);
                                iv.setLayoutParams(lp);
                                iv.setText((pat_detailsArrayList.get(i).getId()) + "");
                                iv.setGravity(Gravity.CENTER);
                                iv.setTextColor(Color.parseColor("#ffffff"));
                                count++;
                                iv.setBackground(getResources().getDrawable(
                                        R.drawable.circle_white_border));
                                mBinding.imageFrameLayoutFlip.addView(iv);
                            }
                        }

                    }

                    mBinding.imageFrameLayoutFlip.setVisibility(View.VISIBLE);
                    mBinding.imageFrameLayout.setVisibility(View.GONE);

                }
                else {
                    BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPat.getDrawable();
                    Bitmap simple_image = drawable.getBitmap();
                    Log.e("is_flip_image","no");
                    Pref.setValue(context,"is_flip_image","false");
                    mBinding.imageFrameLayout.removeAllViews();
                    mBinding.imageFrameLayout.addView(mBinding.imgPat);
                    default_image_status=0;
                    mBinding.imgPat.setImageBitmap(simple_image);
                    if(pat_detailsArrayList.size()>0) {
                        for (int i = 0; i < pat_detailsArrayList.size(); i++) {
                            if (pat_detailsArrayList.get(i).getIs_flip_image().equals(Pref.getValue(context, "is_flip_image", ""))) {
                                final float inPixels = getActivity().getResources().getDimension(R.dimen.count_size);
                                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) inPixels, (int) inPixels);
                                TextView iv = new TextView(context);
                                lp.setMargins(pat_detailsArrayList.get(i).getX(), pat_detailsArrayList.get(i).getY(), 0, 0);
                                iv.setLayoutParams(lp);
                                iv.setText((pat_detailsArrayList.get(i).getId()) + "");
                                iv.setGravity(Gravity.CENTER);
                                iv.setTextColor(Color.parseColor("#ffffff"));
                                count++;
                                iv.setBackground(getResources().getDrawable(
                                        R.drawable.circle_white_border));
                                mBinding.imageFrameLayout.addView(iv);
                            }
                        }

                    }
                    mBinding.imageFrameLayoutFlip.setVisibility(View.GONE);
                    mBinding.imageFrameLayout.setVisibility(View.VISIBLE);

                }

                return false;
            }
        });

        mBinding.txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(mBinding.edtPatName.getText().toString().trim().length()==0)
                {
                    mBinding.edtPatName.setError(getString(R.string.Pat_name_is_required));
                }
                else {

                    Pat_details[] pat_detailses = new Pat_details[pat_detailsArrayList.size()];
                    for (int i = 0; i < pat_detailsArrayList.size(); i++) {

                        pat_detailses[i] = new Pat_details();
                        pat_detailses[i].setId(pat_detailsArrayList.get(i).getId());
                        pat_detailses[i].setHeading(pat_detailsArrayList.get(i).getHeading());
                        pat_detailses[i].setDescription(pat_detailsArrayList.get(i).getDescription());
                        pat_detailses[i].setBmp_image(pat_detailsArrayList.get(i).getBmp_image());
                        Log.e("bmp_image",pat_detailsArrayList.get(i).getBmp_image()+"");
                        if(pat_detailsArrayList.get(i).getBmp_image()!=null) {
                            pat_detailses[i].setFile(bitmapToFile(pat_detailsArrayList.get(i).getBmp_image()));
                        }

                        pat_detailsArrayList.set(i, pat_detailses[i]);

                    }
                    animal_front_file=null;


                    mBinding.txtSave.setEnabled(false);

                    BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPat.getDrawable();
                    Bitmap bitmap_new = drawable.getBitmap();

                    if(mBinding.imageFrameLayout!=null && mBinding.imageFrameLayout.getHeight()>0) {
                        Bitmap bitmap = setsimple_merge_image(mBinding.imageFrameLayout);
                        if (bitmap != null) {

                            Bitmap bmpnew = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
                            Log.e("responsimage", "front" + bmpnew);
                            animal_front_file = bitmapToFile(bmpnew);
                        }
                    }
                    animal_rotate_file=null;

                    if(mBinding.imageFrameLayoutFlip!=null && mBinding.imageFrameLayoutFlip.getHeight()>0) {
                        Bitmap bitmap_flip_new = setsimple_merge_image(mBinding.imageFrameLayoutFlip);
                        if (bitmap_flip_new != null) {
                            Bitmap bmpnewf = Bitmap.createScaledBitmap(bitmap_flip_new, 300, 300, false);
                            Log.e("responsimage", "flip" + bmpnewf);

                            animal_rotate_file = bitmapToFileFlip(bmpnewf);
                        }
                    }
                    Log.e("responsimage", "before "+animal_front_file + " animal_rotate_file " + animal_rotate_file);



                    if (cd.isConnectingToInternet()) {

                        new ExecuteTask().execute(WebService.ADDPAT);
                    } else {
                        cd.showToast(context, R.string.NO_INTERNET_CONNECTION);

                    }
                }



            }
        });
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        return rootView;
    }


    public void set_points_on_image()
    {

        if(default_image_status==1)
        {
            BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPatFlip.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            mBinding.imageFrameLayoutFlip.removeAllViews();
            mBinding.imageFrameLayoutFlip.addView(mBinding.imgPatFlip);
            mBinding.imgPatFlip.setImageBitmap(bitmap);

            Log.e("is_flip_image","yes");
            Pref.setValue(context,"is_flip_image","true");
           /* if(mBinding.imgPat.getDrawable()!=null) {
                if(flip_image==null) {
                    BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPat.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    //simple_image=bitmap;
                    if (bitmap != null) {

                        flip_image = flipImage(bitmap, 2);
                        Log.e("is_flip_image", "values:" + flip_image);
                    }
                }
            }
            mBinding.imgPatFlip.setImageBitmap(flip_image);*/
            default_image_status=1;
            if(pat_detailsArrayList.size()>0) {
                for (int i = 0; i < pat_detailsArrayList.size(); i++) {
                    if (pat_detailsArrayList.get(i).getIs_flip_image().equals(Pref.getValue(context, "is_flip_image", ""))) {

                        final float inPixels = getActivity().getResources().getDimension(R.dimen.count_size);
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) inPixels, (int) inPixels);
                        TextView iv = new TextView(context);
                        lp.setMargins(pat_detailsArrayList.get(i).getX(), pat_detailsArrayList.get(i).getY(), 0, 0);
                        iv.setLayoutParams(lp);
                        iv.setText((pat_detailsArrayList.get(i).getId()) + "");
                        iv.setGravity(Gravity.CENTER);
                        iv.setTextColor(Color.parseColor("#ffffff"));
                        count++;
                        iv.setBackground(getResources().getDrawable(
                                R.drawable.circle_white_border));
                        mBinding.imageFrameLayoutFlip.addView(iv);
                    }
                }

            }

            mBinding.imageFrameLayoutFlip.setVisibility(View.VISIBLE);
            mBinding.imageFrameLayout.setVisibility(View.GONE);

        }
        else {

            BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPat.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Log.e("is_flip_image","no");
            Pref.setValue(context,"is_flip_image","false");
            mBinding.imageFrameLayout.removeAllViews();
            mBinding.imageFrameLayout.addView(mBinding.imgPat);
            default_image_status=0;
            mBinding.imgPat.setImageBitmap(bitmap);
            if(pat_detailsArrayList.size()>0) {
                for (int i = 0; i < pat_detailsArrayList.size(); i++) {
                    if (pat_detailsArrayList.get(i).getIs_flip_image().equals(Pref.getValue(context, "is_flip_image", ""))) {
                        final float inPixels = getActivity().getResources().getDimension(R.dimen.count_size);
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) inPixels, (int) inPixels);
                        TextView iv = new TextView(context);
                        lp.setMargins(pat_detailsArrayList.get(i).getX(), pat_detailsArrayList.get(i).getY(), 0, 0);
                        iv.setLayoutParams(lp);
                        iv.setText((pat_detailsArrayList.get(i).getId()) + "");
                        iv.setGravity(Gravity.CENTER);
                        iv.setTextColor(Color.parseColor("#ffffff"));
                        count++;
                        iv.setBackground(getResources().getDrawable(
                                R.drawable.circle_white_border));
                        mBinding.imageFrameLayout.addView(iv);
                    }
                }

            }
            mBinding.imageFrameLayoutFlip.setVisibility(View.GONE);
            mBinding.imageFrameLayout.setVisibility(View.VISIBLE);

        }

    }

    public void set_simple_and_flip_bitmap()
    {
        if(mBinding.imgPat.getDrawable()!=null) {
            BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPat.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            if(bitmap!=null) {
                mBinding.imgPatFlip.setImageBitmap(flipImage(bitmap, 2));

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DashboardActivity.visible_back();
        default_image_status=0;

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
            String file_name="image_"+String.valueOf(timestemp)+".png";
            FileOutputStream fos = context.openFileOutput(file_name, Context.MODE_PRIVATE);
            fos.write(bArr);
            fos.flush();
            fos.close();
            File mFile = new File(context.getFilesDir().getAbsolutePath(),file_name);
            return mFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public File bitmapToFileFlip(Bitmap bmp) {
        try {
            final int REQUIRED_SIZE = 200;
            ByteArrayOutputStream bos = new ByteArrayOutputStream(REQUIRED_SIZE);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bArr = bos.toByteArray();
            bos.flush();
            bos.close();
            long timestemp=(System.currentTimeMillis()/100);
            String file_name="image_flip"+String.valueOf(timestemp)+".png";
            FileOutputStream fos = context.openFileOutput(file_name, Context.MODE_PRIVATE);
            fos.write(bArr);
            fos.flush();
            fos.close();
            File mFile = new File(context.getFilesDir().getAbsolutePath(),file_name);
            return mFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setanimal_default()
    {
        if(Pref.getValue(context,"selected_animal","").equals("horse"))
        {
            mBinding.imgPat.setImageResource(R.mipmap.horse_new2);
        }
        else  if(Pref.getValue(context,"selected_animal","").equals("cat"))
        {
            mBinding.imgPat.setImageResource(R.mipmap.cat_new2);
        }
        else  if(Pref.getValue(context,"selected_animal","").equals("dog"))
        {
            mBinding.imgPat.setImageResource(R.mipmap.dog_new2);
        }
        else  if(Pref.getValue(context,"selected_animal","").equals("general"))
        {
            mBinding.imgPat.setImageResource(R.mipmap.general_new);
        }
    }

    public  void showInputDialog_camera() {

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

                    if(Pref.getValue(context,"animal_image","").equals("true")) {
                        /* Pref.setValue(context,"is_flip_image","false");
                    default_image_status=0;
                    simple_image=bmpnew;
                    mBinding.imageFrameLayout.removeAllViews();
                    mBinding.imageFrameLayout.addView(mBinding.imgPat);
                    mBinding.imgPat.setImageBitmap(bmpnew);
                    flip_image=null;
                    mBinding.imageFrameLayout.setVisibility(View.VISIBLE);
                    mBinding.imageFrameLayoutFlip.setVisibility(View.GONE);*/
                        if(default_image_status==1)
                        {
                            Pref.setValue(context,"is_flip_image","true");
                            default_image_status=1;
                            simple_image=bmpnew;
                            mBinding.imageFrameLayoutFlip.removeAllViews();
                            mBinding.imageFrameLayoutFlip.addView(mBinding.imgPatFlip);
                            mBinding.imgPatFlip.setImageBitmap(bmpnew);
                            // flip_image=null;
                            mBinding.imageFrameLayoutFlip.setVisibility(View.VISIBLE);
                            mBinding.imageFrameLayout.setVisibility(View.GONE);
                        }
                        else {
                            Pref.setValue(context,"is_flip_image","false");
                            default_image_status=0;
                            simple_image=bmpnew;
                            mBinding.imageFrameLayout.removeAllViews();
                            mBinding.imageFrameLayout.addView(mBinding.imgPat);
                            mBinding.imgPat.setImageBitmap(bmpnew);
                            flip_image=null;
                            mBinding.imageFrameLayout.setVisibility(View.VISIBLE);
                            mBinding.imageFrameLayoutFlip.setVisibility(View.GONE);
                        }
                        set_points_on_image();

                        //set_simple_and_flip_bitmap();
                    }
                    else {
                        img_animal_part.setImageBitmap(rotatedBitmap);
                    }

                    // file1= bitmapToFile(bmpnew);

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

                if(Pref.getValue(context,"animal_image","").equals("true")) {
                   /* Pref.setValue(context,"is_flip_image","false");
                    default_image_status=0;
                    simple_image=bmpnew;
                    mBinding.imageFrameLayout.removeAllViews();
                    mBinding.imageFrameLayout.addView(mBinding.imgPat);
                    mBinding.imgPat.setImageBitmap(bmpnew);
                    flip_image=null;
                    mBinding.imageFrameLayout.setVisibility(View.VISIBLE);
                    mBinding.imageFrameLayoutFlip.setVisibility(View.GONE);*/
                    if(default_image_status==1)
                    {
                        Pref.setValue(context,"is_flip_image","true");
                        default_image_status=1;
                        simple_image=bmpnew;
                        mBinding.imageFrameLayoutFlip.removeAllViews();
                        mBinding.imageFrameLayoutFlip.addView(mBinding.imgPatFlip);
                        mBinding.imgPatFlip.setImageBitmap(bmpnew);
                        // flip_image=null;
                        mBinding.imageFrameLayoutFlip.setVisibility(View.VISIBLE);
                        mBinding.imageFrameLayout.setVisibility(View.GONE);
                    }
                    else {
                        Pref.setValue(context,"is_flip_image","false");
                        default_image_status=0;
                        simple_image=bmpnew;
                        mBinding.imageFrameLayout.removeAllViews();
                        mBinding.imageFrameLayout.addView(mBinding.imgPat);
                        mBinding.imgPat.setImageBitmap(bmpnew);
                        flip_image=null;
                        mBinding.imageFrameLayout.setVisibility(View.VISIBLE);
                        mBinding.imageFrameLayoutFlip.setVisibility(View.GONE);
                    }

                    set_points_on_image();

                    //set_simple_and_flip_bitmap();
                }
                else {
                    img_animal_part.setImageBitmap(bmpnew);
                }

                //  file1=bitmapToFile(bmpnew);

            }

        }
    }

    public void setdate()
    {
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String currentDateandTime = sdf.format(new Date());
        mBinding.txtDate.setText(currentDateandTime);
    }


    public void showInputDialog(String heading, String desc, final int pos,Bitmap bmp_image) {

        mDialogRowBoardList_edit = new Dialog(context);
        mDialogRowBoardList_edit.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogRowBoardList_edit.getWindow().setBackgroundDrawableResource(android.R.color.white);
        mDialogRowBoardList_edit.setContentView(R.layout.edit_body_part_pat_dialod_layout);
        mDialogRowBoardList_edit.setCancelable(false);
        mWindow = mDialogRowBoardList_edit.getWindow();
        mLayoutParams = mWindow.getAttributes();
        mLayoutParams.gravity = Gravity.CENTER;
        mWindow.setAttributes(mLayoutParams);
        final TextView_Regular txt_organ_name = (TextView_Regular) mDialogRowBoardList_edit.findViewById(R.id.txt_organ_name);
        final TextView_Regular txt_organ_desc = (TextView_Regular) mDialogRowBoardList_edit.findViewById(R.id.txt_organ_desc);
        final Edittext_Regular edt_organ_name = (Edittext_Regular) mDialogRowBoardList_edit.findViewById(R.id.edt_organ_name);
        final Edittext_Regular edt_organ_desc = (Edittext_Regular) mDialogRowBoardList_edit.findViewById(R.id.edt_organ_desc);
        img_animal_part=(CircularImageView) mDialogRowBoardList_edit.findViewById(R.id.img_animal_part);
        final TextView_bld txtadd = (TextView_bld) mDialogRowBoardList_edit.findViewById(R.id.txtadd);
        final TextView_bld txtcancel = (TextView_bld) mDialogRowBoardList_edit.findViewById(R.id.txtcancel);


        Constants.setTextWatcher(context,edt_organ_name,txt_organ_name);
        Constants.setTextWatcher(context,edt_organ_desc,txt_organ_desc);
        if(bmp_image!=null) {
            img_animal_part.setImageBitmap(bmp_image);
            img_animal_part.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        edt_organ_name.setText(heading);
        edt_organ_desc.setText(desc);

        img_animal_part.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pref.setValue(context,"animal_image","false");
                showInputDialog_camera();
            }
        });

        txtcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("size", AddNewPatFragment_Old.pat_detailsArrayList.size()+"--");


                mDialogRowBoardList_edit.dismiss();
            }
        });

        txtadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                int err_count=0;
                if(edt_organ_name.getText().toString().trim().length()==0)
                {
                    edt_organ_name.setError(context.getString(R.string.Organ_Name_is_required));
                    err_count=1;
                }
                if(edt_organ_desc.getText().toString().trim().length()==0)
                {
                    edt_organ_desc.setError(context.getString(R.string.Organ_description_is_required));
                    err_count=1;
                }
                if(err_count==0) {

                    Log.v("size", AddNewPatFragment_Old.pat_detailsArrayList.size()+"--");

                    if(AddNewPatFragment_Old.pat_detailsArrayList.size()>0)
                    {
                        for (int i = 0; i< AddNewPatFragment_Old.pat_detailsArrayList.size(); i++)
                        {
                            Log.v("size",i+"--"+pos+"----"+ AddNewPatFragment_Old.pat_detailsArrayList.get(i).getId());
                            if(i==pos)
                            {
                                Pat_details[] pat_detailses=new Pat_details[1];
                                pat_detailses[0]=new Pat_details();
                                pat_detailses[0].setId(AddNewPatFragment_Old.pat_detailsArrayList.get(i).getId());
                                pat_detailses[0].setX(AddNewPatFragment_Old.pat_detailsArrayList.get(i).getX());
                                pat_detailses[0].setY(AddNewPatFragment_Old.pat_detailsArrayList.get(i).getY());
                                pat_detailses[0].setIs_flip_image(AddNewPatFragment_Old.pat_detailsArrayList.get(i).getIs_flip_image());
                                BitmapDrawable drawable = (BitmapDrawable) img_animal_part.getDrawable();
                                if(img_animal_part.getDrawable()!=null) {
                                    Bitmap bitmap = drawable.getBitmap();
                                    pat_detailses[0].setBmp_image(bitmap);
                                }
                                else {
                                    pat_detailses[0].setBmp_image(null);
                                    pat_detailses[0].setFile(null);
                                }
                                pat_detailses[0].setHeading(edt_organ_name.getText().toString());
                                pat_detailses[0].setDescription(edt_organ_desc.getText().toString());
                                AddNewPatFragment_Old.pat_detailsArrayList.set(i,pat_detailses[0]);
                                AddNewPatFragment_Old.adapter.notifyDataSetChanged();

                                break;
                            }
                        }
                    }

                    mDialogRowBoardList_edit.dismiss();
                }
            }
        });

        mDialogRowBoardList_edit.show();
    }

    protected void showInputDialog(final Context context, final int x, final int y) {

        mDialogRowBoardList_add = new Dialog(context);
        mDialogRowBoardList_add.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogRowBoardList_add.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialogRowBoardList_add.setContentView(R.layout.input_body_part_pat_dialod_layout);
        mDialogRowBoardList_add.setCancelable(true);
        mWindow = mDialogRowBoardList_add.getWindow();
        mLayoutParams = mWindow.getAttributes();
        mLayoutParams.gravity = Gravity.CENTER;
        mWindow.setAttributes(mLayoutParams);
        final TextView_Regular txt_organ_name = (TextView_Regular) mDialogRowBoardList_add.findViewById(R.id.txt_organ_name);
        final TextView_Regular txt_organ_desc = (TextView_Regular) mDialogRowBoardList_add.findViewById(R.id.txt_organ_desc);
        final Edittext_Regular edt_organ_name = (Edittext_Regular) mDialogRowBoardList_add.findViewById(R.id.edt_organ_name);
        final Edittext_Regular edt_organ_desc = (Edittext_Regular) mDialogRowBoardList_add.findViewById(R.id.edt_organ_desc);
        img_animal_part=(CircularImageView) mDialogRowBoardList_add.findViewById(R.id.img_animal_part);
        final TextView_bld txtadd = (TextView_bld) mDialogRowBoardList_add.findViewById(R.id.txtadd);
        final TextView_bld txtcancel = (TextView_bld) mDialogRowBoardList_add.findViewById(R.id.txtcancel);
        Constants.setTextWatcher(context,edt_organ_name,txt_organ_name);
        Constants.setTextWatcher(context,edt_organ_desc,txt_organ_desc);

        img_animal_part.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pref.setValue(context,"animal_image","false");
                showInputDialog_camera();
            }
        });

        txtcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogRowBoardList_add.dismiss();

            }
        });

        txtadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // View view = getCurrentFocus();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                int err_count=0;

                if(edt_organ_name.getText().toString().trim().length()==0)
                {
                    edt_organ_name.setError(getString(R.string.Organ_Name_is_required));
                    err_count=1;
                }
                if(edt_organ_desc.getText().toString().trim().length()==0)
                {
                    edt_organ_desc.setError(getString(R.string.Organ_description_is_required));
                    err_count=1;
                }
                if(err_count==0) {

                    //add count on image
                    final float inPixels = getActivity().getResources().getDimension(R.dimen.count_size);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) inPixels, (int) inPixels);
                    TextView iv = new TextView(context);
                    lp.setMargins(x , y , 0, 0);
                    iv.setLayoutParams(lp);
                    iv.setText((pat_detailsArrayList.size()+1)+"");
                    iv.setGravity(Gravity.CENTER);
                    iv.setTextColor(Color.parseColor("#ffffff"));
                    count++;
                    iv.setBackground(getResources().getDrawable(
                            R.drawable.circle_white_border));

                    if(Pref.getValue(context,"is_flip_image","").equals("false")) {
                        mBinding.imageFrameLayout.addView(iv);
                    }
                    else {
                        mBinding.imageFrameLayoutFlip.addView(iv);
                    }


                    Pat_details[] pat_detailses=new Pat_details[1];
                    pat_detailses[0]=new Pat_details();
                    pat_detailses[0].setId(pat_detailsArrayList.size()+1);
                    pat_detailses[0].setX(x);
                    pat_detailses[0].setY(y);
                    BitmapDrawable drawable = (BitmapDrawable) img_animal_part.getDrawable();
                    if(drawable!=null) {
                        Bitmap bitmap = drawable.getBitmap();
                        pat_detailses[0].setBmp_image(bitmap);
                    }
                    else {
                        pat_detailses[0].setBmp_image(null);
                        pat_detailses[0].setFile(null);
                    }
                    //   no_of_notes++;
                    pat_detailses[0].setHeading(edt_organ_name.getText().toString());
                    pat_detailses[0].setDescription(edt_organ_desc.getText().toString());
                    pat_detailses[0].setIs_flip_image(Pref.getValue(context,"is_flip_image",""));
                    pat_detailsArrayList.add(pat_detailses[0]);
                    recycler_view_pat_details.setLayoutManager(new LinearLayoutManager(context));
                    adapter=new PatDetailsRecyclerViewAdapter(context,pat_detailsArrayList);
                    recycler_view_pat_details.setAdapter(adapter);
                    adapter.onClickDeleteListener(onClickDeleteListener);
                    adapter.onClickLinkedCurrentDeviceListener(onClickLinkedCurrentDeviceListener);
                    mDialogRowBoardList_add.dismiss();
                }
            }
        });

        mDialogRowBoardList_add.show();
    }

    public Bitmap flipImage(Bitmap src, int type) {
        // create new matrix for transformation
        Matrix matrix = new Matrix();
        // if vertical
        if(type == FLIP_VERTICAL) {
            // y = y * -1
            matrix.preScale(1.0f, -1.0f);
        }
        // if horizonal
        else if(type == FLIP_HORIZONTAL) {
            // x = x * -1
            matrix.preScale(-1.0f, 1.0f);
            // unknown type
        } else {
            return null;
        }

        // return transformed image
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    OnClickLinkedCurrentDeviceListener onClickLinkedCurrentDeviceListener=new OnClickLinkedCurrentDeviceListener() {
        @Override
        public void OnClickLinkedCurrentDeviceListener(String heading, String desc, final int pos,Bitmap bmp_image) {

            showInputDialog(heading,desc,pos,bmp_image);
        }
    };

    OnClickDeleteListener onClickDeleteListener=new OnClickDeleteListener() {
        @Override
        public void OnClickDeleteListener(int pos) {
            if(AddNewPatFragment_Old.pat_detailsArrayList.size()>0)
            {
                for (int i = 0; i< AddNewPatFragment_Old.pat_detailsArrayList.size(); i++) {

                    if (i == pos) {
                        AddNewPatFragment_Old.pat_detailsArrayList.remove(i);
                        break;
                    }
                }
                for (int i = 0; i< AddNewPatFragment_Old.pat_detailsArrayList.size(); i++)
                {
                    // notifyDataSetChanged();
                    for(int k = 0; k< AddNewPatFragment_Old.pat_detailsArrayList.size(); k++)
                    {
                        Pat_details[] pat_detailses=new Pat_details[1];
                        pat_detailses[0]=new Pat_details();
                        pat_detailses[0].setId(k+1);
                        pat_detailses[0].setX(pat_detailsArrayList.get(k).getX());
                        pat_detailses[0].setY(pat_detailsArrayList.get(k).getY());
                        if(AddNewPatFragment_Old.pat_detailsArrayList.get(k).getBmp_image()!=null) {

                            pat_detailses[0].setBmp_image(AddNewPatFragment_Old.pat_detailsArrayList.get(k).getBmp_image());
                        }
                        else {
                            pat_detailses[0].setBmp_image(null);
                        }
                        pat_detailses[0].setHeading(AddNewPatFragment_Old.pat_detailsArrayList.get(k).getHeading());
                        pat_detailses[0].setDescription(AddNewPatFragment_Old.pat_detailsArrayList.get(k).getDescription());
                        pat_detailses[0].setIs_flip_image(pat_detailsArrayList.get(k).getIs_flip_image());
                        AddNewPatFragment_Old.pat_detailsArrayList.set(k,pat_detailses[0]);
                        // AddNewPatFragment.adapter.notifyDataSetChanged();
                    }

                }

                AddNewPatFragment_Old.adapter=new PatDetailsRecyclerViewAdapter(context, AddNewPatFragment_Old.pat_detailsArrayList);
                AddNewPatFragment_Old.recycler_view_pat_details.setAdapter( AddNewPatFragment_Old.adapter);
                AddNewPatFragment_Old.adapter.onClickLinkedCurrentDeviceListener(onClickLinkedCurrentDeviceListener);
                AddNewPatFragment_Old.adapter.onClickDeleteListener(onClickDeleteListener);




                if(Pref.getValue(context,"is_flip_image","").equals("true"))
                {
                    BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPatFlip.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    mBinding.imageFrameLayoutFlip.removeAllViews();
                    mBinding.imageFrameLayoutFlip.addView(mBinding.imgPatFlip);
                    mBinding.imgPatFlip.setImageBitmap(bitmap);
                }
                else {
                    BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPat.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    mBinding.imageFrameLayout.removeAllViews();
                    mBinding.imageFrameLayout.addView(mBinding.imgPat);
                    mBinding.imgPat.setImageBitmap(bitmap);
                }

                for(int i=0;i<pat_detailsArrayList.size();i++)
                {
                    if(pat_detailsArrayList.get(i).getIs_flip_image().equals(Pref.getValue(context,"is_flip_image","")))
                    {
                        final float inPixels = getActivity().getResources().getDimension(R.dimen.count_size);
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) inPixels, (int) inPixels);
                        TextView iv = new TextView(context);
                        lp.setMargins(pat_detailsArrayList.get(i).getX(), pat_detailsArrayList.get(i).getY(), 0, 0);
                        iv.setLayoutParams(lp);
                        iv.setText((pat_detailsArrayList.get(i).getId()) + "");
                        iv.setGravity(Gravity.CENTER);
                        iv.setTextColor(Color.parseColor("#ffffff"));
                        count++;
                        iv.setBackground(getResources().getDrawable(
                                R.drawable.circle_white_border));
                        if(Pref.getValue(context,"is_flip_image","").equals("true")) {
                            mBinding.imageFrameLayoutFlip.addView(iv);
                        }
                        else {
                            mBinding.imageFrameLayout.addView(iv);
                        }
                    }
                }
            }
        }
    };



    public class ExecuteTask extends
            AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            WebService.showProgress(context);
        }

        @Override
        protected String doInBackground(String... urls) {

            return executeMultipartPost(urls[0], Pref.getValue(context, "token",""));

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

                    }


                }
                else {
                    Toast.makeText(context,json2.getString("message"),Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


        public String executeMultipartPost(String url, String token) {


            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost poster = new HttpPost(url);

                // File image = new File(imgPath);  //get the actual file from the device
                Log.v("responsimage", "file&token:" +url+ " "+token+"\n"+animal_front_file);
                //  poster.setHeader("Content-Type","multipart/form-data");
                poster.setHeader("Authorization","Bearer "+ token);
                poster.addHeader("Accept", "application/json");

               /* entity.addPart("Content-Type",new StringBody("multipart/form-data"));
                entity.addPart("Authorization", new StringBody("Bearer "+token));*/
                Log.e("responsimage", "animal_front_file "+animal_front_file + " animal_rotate_file " + animal_rotate_file);
                MultipartEntity entity = new MultipartEntity(org.apache.http.entity.mime.HttpMultipartMode.BROWSER_COMPATIBLE);
                if(animal_front_file!=null) {
                    entity.addPart("pet_img", new org.apache.http.entity.mime.content.FileBody(animal_front_file));
                }
                if(animal_rotate_file!=null) {
                    entity.addPart("pet_swip_img", new org.apache.http.entity.mime.content.FileBody(animal_rotate_file));
                }
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                Log.e("timestamp",ts+"");
                entity.addPart("created", new StringBody(ts));
                entity.addPart("client_id", new StringBody(Pref.getValue(context,"client_id","")));
                entity.addPart("cat_name", new StringBody(Pref.getValue(context,"selected_animal","")));
                entity.addPart("pet_name", new StringBody(mBinding.edtPatName.getText().toString()));
                //Log.e("request",ts+" "+Pref.getValue(context,"client_id","")+" "+Pref.getValue(context,"selected_animal","")+" "+mBinding.edtPatName.getText().toString());
                for(int i=0;i<pat_detailsArrayList.size();i++) {
                    if(pat_detailsArrayList.get(i).getFile()!=null) {
                        entity.addPart("bp_img" + i, new org.apache.http.entity.mime.content.FileBody(pat_detailsArrayList.get(i).getFile()));
                    }
                    entity.addPart("bp_title"+i, new StringBody(pat_detailsArrayList.get(i).getHeading()));
                    entity.addPart("bp_desc"+i, new StringBody(pat_detailsArrayList.get(i).getDescription()));

                }
                poster.setEntity(entity);

                Log.e("responsimage", "request:" + entity+" ");
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

    public Bitmap setsimple_merge_image(View view)
    {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


}