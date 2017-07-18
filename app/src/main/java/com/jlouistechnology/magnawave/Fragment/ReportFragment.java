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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jlouistechnology.magnawave.Activity.DashboardActivity;
import com.jlouistechnology.magnawave.Activity.MainActivity;
import com.jlouistechnology.magnawave.Adapter.PatDetailsInfoRecyclerViewAdapter;
import com.jlouistechnology.magnawave.Fonts.Edittext_Regular;
import com.jlouistechnology.magnawave.Fonts.TextView_Regular;
import com.jlouistechnology.magnawave.Fonts.TextView_bld;
import com.jlouistechnology.magnawave.Interface.OnClickEditListener;
import com.jlouistechnology.magnawave.Model.Pat_client_details;
import com.jlouistechnology.magnawave.Webservice_list.WebService;
import com.jlouistechnology.magnawave.utils.CircularImageView;
import com.jlouistechnology.magnawave.utils.ConnectionDetector;
import com.jlouistechnology.magnawave.utils.Constants;
import com.jlouistechnology.magnawave.utils.Pref;
import com.jlouistechnology.magnawave.R;
import com.jlouistechnology.magnawave.databinding.FragmentPatDetailsBinding;

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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by aipxperts on 9/5/17.
 */

public class ReportFragment extends Fragment {

    FragmentPatDetailsBinding mBinding;
    View rootView;
    Context context;
    Dialog mDialogRowBoardList_edit;
    Dialog mDialogRowBoardList;
    PatDetailsInfoRecyclerViewAdapter adapter;
    ArrayList<Pat_client_details> pat_client_detailsArrayList=new ArrayList<>();
    public static final int FLIP_VERTICAL = 1;
    public static final int FLIP_HORIZONTAL = 2;
    String data_sent="";
    ArrayList<Uri> uris = new ArrayList<Uri>();
    String resultstring;
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
    CircularImageView img_animal_part;
    int is_flip_default=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /**
         * Binding all xml views
         */

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_pat_details, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        cd=new ConnectionDetector(getActivity());
        setdata();
        setadapter();
        mBinding.txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((Integer.parseInt(Pref.getValue(context,"screen_width",""))!=rootView.getWidth()) && Integer.parseInt(Pref.getValue(context,"screen_height",""))!=rootView.getHeight())
                {
                    Pref.setValue(context,"set_front_image","0");
                    Pref.setValue(context,"set_flip_image","0");

                }
                else {
                    Pref.setValue(context,"set_front_image","1");
                    Pref.setValue(context,"set_flip_image","1");
                }

                EditPatFragment fragment = new EditPatFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).addToBackStack(null).commit();

            }
        });

        mBinding.imgFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(is_flip_default==0)
                {
                    is_flip_default=1;
                    mBinding.frameImgPat.setVisibility(View.GONE);
                    mBinding.frameImgPatRotated.setVisibility(View.VISIBLE);
                    mBinding.frameImgPatRotated.removeAllViews();
                    mBinding.frameImgPatRotated.addView(mBinding.imgPatRotated);
                    Glide.with(context).load(Pref.getValue(context,"pat_swipe_image","")).asBitmap().fitCenter().into(mBinding.imgPatRotated);


                    if(pat_client_detailsArrayList.size()>0)
                    {
                        for(int i=0;i<pat_client_detailsArrayList.size();i++)
                        {
                            Log.e("is_flip_image",pat_client_detailsArrayList.get(i).getIs_flip_image()+"");
                            if(pat_client_detailsArrayList.get(i).getIs_flip_image().equals("1"))
                            {
                                HomeFragment.find_x_y_according_to_the_screen(getActivity(),rootView,pat_client_detailsArrayList.get(i).getX(),Integer.parseInt(Pref.getValue(context,"screen_width","")),mBinding.imgPatRotated,pat_client_detailsArrayList.get(i).getY(),Integer.parseInt(Pref.getValue(context,"screen_height","")),mBinding.frameImgPatRotated,pat_client_detailsArrayList.get(i).getPat_detail_id(),"false");

                            }



                        }
                    }
                }
                else {

                    is_flip_default=0;
                    mBinding.frameImgPat.setVisibility(View.VISIBLE);
                    mBinding.frameImgPatRotated.setVisibility(View.GONE);
                    mBinding.frameImgPat.removeAllViews();
                    mBinding.frameImgPat.addView(mBinding.imgPat);
                    Glide.with(context).load(Pref.getValue(context,"pat_image","")).asBitmap().fitCenter().into(mBinding.imgPat);

                    if(pat_client_detailsArrayList.size()>0)
                    {
                        for(int i=0;i<pat_client_detailsArrayList.size();i++)
                        {
                            Log.e("is_flip_image",pat_client_detailsArrayList.get(i).getIs_flip_image()+"");
                            if(pat_client_detailsArrayList.get(i).getIs_flip_image().equals("0"))
                            {
                                HomeFragment.find_x_y_according_to_the_screen(getActivity(),rootView,pat_client_detailsArrayList.get(i).getX(),Integer.parseInt(Pref.getValue(context,"screen_width","")),mBinding.imgPat,pat_client_detailsArrayList.get(i).getY(),Integer.parseInt(Pref.getValue(context,"screen_height","")),mBinding.frameImgPat,pat_client_detailsArrayList.get(i).getPat_detail_id(),"false");

                            }


                        }
                    }
                }

            }
        });

        mBinding.txtEmailReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uris = new ArrayList<Uri>();
                if(mBinding.imgPat.getDrawable()!=null) {
                Bitmap bitmap = ((BitmapDrawable) mBinding.imgPat.getDrawable()).getBitmap();
                    Bitmap bmp_merge=setsimple_merge_image(mBinding.frameImgPat);


                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bmp_merge.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    File f = new File(Environment.getExternalStorageDirectory() + File.separator + "animal_image.jpg");
                    try {
                        f.createNewFile();
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(bytes.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    uris.add(Uri.parse("file:///sdcard/animal_image.jpg"));

                }
                if(mBinding.imgPatRotated.getDrawable()!=null) {

                    Bitmap bitmap_flip = ((BitmapDrawable) mBinding.imgPatRotated.getDrawable()).getBitmap();
                    Bitmap bmp_merge_flip=setsimple_merge_image(mBinding.frameImgPatRotated);

                    ByteArrayOutputStream bytes_FLIP = new ByteArrayOutputStream();

                    bmp_merge_flip.compress(Bitmap.CompressFormat.JPEG, 100, bytes_FLIP);

                    File f_flip = new File(Environment.getExternalStorageDirectory() + File.separator + "animal_image_flip.jpg");
                    try {
                        f_flip.createNewFile();
                        FileOutputStream fo_flip = new FileOutputStream(f_flip);
                        fo_flip.write(bytes_FLIP.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    uris.add(Uri.parse("file:///sdcard/animal_image_flip.jpg"));

                }
                Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                intent.setType("image/jpeg");
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {Pref.getValue(context, "client_email", "")});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Share Report");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                data_sent="Client Name:"+ Pref.getValue(context,"client_fname","")+" "+ Pref.getValue(context,"client_lname","")+"\n";
                data_sent=data_sent+"Pat Name:"+ Pref.getValue(context,"pat_name","")+"\n\n";
                data_sent=data_sent+"Pat Details"+"\n\n";
                for(int i=0;i<pat_client_detailsArrayList.size();i++) {

                    data_sent=data_sent+"Title:"+pat_client_detailsArrayList.get(i).getBp_title()+"\n"+"Description:"+pat_client_detailsArrayList.get(i).getBp_desc()+"\n"+"Image Url:"+pat_client_detailsArrayList.get(i).getBp_img()+"\n\n";
                }
                intent.putExtra(Intent.EXTRA_TEXT, data_sent);
                startActivity(Intent.createChooser(intent, "Share Report via:"));
            }
        });

        mBinding.txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pat_client_details[] pat_detailses = new Pat_client_details[pat_client_detailsArrayList.size()];
                    for (int i = 0; i < pat_client_detailsArrayList.size(); i++) {

                        pat_detailses[i]=new Pat_client_details();
                        pat_detailses[i].setBp_title(pat_client_detailsArrayList.get(i).getBp_title());
                        pat_detailses[i].setBp_desc(pat_client_detailsArrayList.get(i).getBp_desc());
                        pat_detailses[i].setPet_img(pat_client_detailsArrayList.get(i).getPet_img());
                        pat_detailses[i].setBp_img("");
                        pat_detailses[i].setPat_detail_id(pat_client_detailsArrayList.get(i).getPat_detail_id());
                        pat_detailses[i].setPat_main_id(pat_client_detailsArrayList.get(i).getPat_main_id());
                        if(pat_client_detailsArrayList.get(i).getPet_img()!=null) {
                            pat_detailses[i].setImg_file(bitmapToFile(pat_client_detailsArrayList.get(i).getPet_img()));
                        }
                        pat_client_detailsArrayList.set(i,pat_detailses[i]);
                    }

                    if (cd.isConnectingToInternet()) {

                        new ExecuteTask().execute(WebService.EDITPAT);
                    } else {
                        cd.showToast(context, R.string.NO_INTERNET_CONNECTION);

                    }
            }
        });

        return rootView;
    }
    public void setdata()
    {
        mBinding.txtClientName.setText(MainActivity.capitalize(Pref.getValue(context,"client_fname","")+" "+ Pref.getValue(context,"client_lname","")));
        mBinding.txtPatName.setText(MainActivity.capitalize(Pref.getValue(context,"pat_name","")));
        mBinding.txtDate.setText(setdate(Long.parseLong(Pref.getValue(context,"created",""))));

        Glide.with(context).load(Pref.getValue(context,"pat_image","")).asBitmap().fitCenter().into(mBinding.imgPat);
        Glide.with(context).load(Pref.getValue(context,"pat_swipe_image","")).asBitmap().fitCenter().into(mBinding.imgPatRotated);
        mBinding.imgPat.setImageResource(R.mipmap.general_new);

        if(Pref.getValue(context,"pat_image","").length()==0)
        {
            mBinding.imgPat.setVisibility(View.GONE);
        }
        if(Pref.getValue(context,"pat_swipe_image","").length()==0)
        {
            mBinding.imgPatRotated.setVisibility(View.GONE);
            mBinding.imgFlip.setVisibility(View.GONE);

        }
       // mBinding.imgPatRotated.setVisibility(View.GONE);

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
    public void setadapter()
    {

        for(int i = 0; i< ClientdetailFragment.client_detailsArrayList.size(); i++)
        {
            if(ClientdetailFragment.client_detailsArrayList.get(i).getPet_id().equals(Pref.getValue(context,"pat_id","")))
            {

                if(ClientdetailFragment.client_detailsArrayList.get(i).getPat_client_detailsArrayList().size()>0)
                {
                    pat_client_detailsArrayList.clear();
                    for(int j = 0; j< ClientdetailFragment.client_detailsArrayList.get(i).getPat_client_detailsArrayList().size(); j++) {
                        Pat_client_details[] pat_client_detailses = new Pat_client_details[1];
                        pat_client_detailses[0] = new Pat_client_details();
                        pat_client_detailses[0].setPat_main_id(ClientdetailFragment.client_detailsArrayList.get(i).getPat_client_detailsArrayList().get(j).getPat_main_id());
                        pat_client_detailses[0].setPat_detail_id(String.valueOf(j + 1));
                        pat_client_detailses[0].setBp_title(ClientdetailFragment.client_detailsArrayList.get(i).getPat_client_detailsArrayList().get(j).getBp_title());
                        pat_client_detailses[0].setBp_desc(ClientdetailFragment.client_detailsArrayList.get(i).getPat_client_detailsArrayList().get(j).getBp_desc());
                        pat_client_detailses[0].setBp_img(ClientdetailFragment.client_detailsArrayList.get(i).getPat_client_detailsArrayList().get(j).getBp_img());
                        pat_client_detailses[0].setX(ClientdetailFragment.client_detailsArrayList.get(i).getPat_client_detailsArrayList().get(j).getX());
                        pat_client_detailses[0].setY(ClientdetailFragment.client_detailsArrayList.get(i).getPat_client_detailsArrayList().get(j).getY());
                        pat_client_detailses[0].setIs_flip_image(ClientdetailFragment.client_detailsArrayList.get(i).getPat_client_detailsArrayList().get(j).getIs_flip_image());

                        Log.e("get_pet_details",ClientdetailFragment.client_detailsArrayList.get(i).getPat_client_detailsArrayList().get(j).getBp_img()+"");
                        pat_client_detailsArrayList.add(pat_client_detailses[0]);
                    }

                }
                Log.e("size_data",pat_client_detailsArrayList.size()+"");

                break;
            }

        }

        if(pat_client_detailsArrayList.size()>0)
        {
            for(int i=0;i<pat_client_detailsArrayList.size();i++)
            {
                Log.e("is_flip_image",pat_client_detailsArrayList.get(i).getIs_flip_image()+"");
                if(pat_client_detailsArrayList.get(i).getIs_flip_image().equals("1"))
                {
                    HomeFragment.find_x_y_according_to_the_screen(getActivity(),rootView,pat_client_detailsArrayList.get(i).getX(),Integer.parseInt(Pref.getValue(context,"screen_width","")),mBinding.imgPatRotated,pat_client_detailsArrayList.get(i).getY(),Integer.parseInt(Pref.getValue(context,"screen_height","")),mBinding.frameImgPatRotated,pat_client_detailsArrayList.get(i).getPat_detail_id(),"false");

                }
                else {
                    HomeFragment.find_x_y_according_to_the_screen(getActivity(),rootView,pat_client_detailsArrayList.get(i).getX(),Integer.parseInt(Pref.getValue(context,"screen_width","")),mBinding.imgPat,pat_client_detailsArrayList.get(i).getY(),Integer.parseInt(Pref.getValue(context,"screen_height","")),mBinding.frameImgPat,pat_client_detailsArrayList.get(i).getPat_detail_id(),"false");

                }
            }
        }
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.recyclerViewPatDetails.setLayoutManager(llm);
        adapter=new PatDetailsInfoRecyclerViewAdapter(context,pat_client_detailsArrayList);
        mBinding.recyclerViewPatDetails.setAdapter(adapter);
        adapter.onClickEditListener(onClickEditListener);
    }

    public static CharSequence setdate(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp*1000);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        return sdf.format(d);
    }

    @Override
    public void onResume() {
        super.onResume();
        DashboardActivity.visible_back();
        if(Pref.getValue(context,"from_edit","").equals("1"))
        {
            getActivity().getSupportFragmentManager().popBackStack();
        }

    }


    OnClickEditListener onClickEditListener=new OnClickEditListener() {
        @Override
        public void onClickEditListener(String bp_title, String bp_desc, int position, Bitmap bp_img) {

            showInputDialog(bp_title,bp_desc,position,bp_img);
        }

    };



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

                showInputDialog_camera();
            }
        });

        txtcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



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


                    if(pat_client_detailsArrayList.size()>0)
                    {
                        for (int i = 0; i< pat_client_detailsArrayList.size(); i++)
                        {

                            if(i==pos)
                            {
                               Pat_client_details[] pat_detailses=new Pat_client_details[1];
                                pat_detailses[0]=new Pat_client_details();
                                pat_detailses[0].setBp_title(edt_organ_name.getText().toString());
                                pat_detailses[0].setBp_desc(edt_organ_desc.getText().toString());

                                    BitmapDrawable drawable = (BitmapDrawable) img_animal_part.getDrawable();
                                if(drawable!=null) {
                                    Bitmap bitmap = drawable.getBitmap();
                                    pat_detailses[0].setPet_img(bitmap);
                                }
                                else {
                                    pat_detailses[0].setPet_img(null);
                                }

                                pat_detailses[0].setBp_img("");
                                pat_detailses[0].setPat_detail_id(pat_client_detailsArrayList.get(i).getPat_detail_id());
                                pat_detailses[0].setPat_main_id(pat_client_detailsArrayList.get(i).getPat_main_id());
                                pat_client_detailsArrayList.set(i,pat_detailses[0]);
                                adapter.notifyDataSetChanged();

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

                        img_animal_part.setImageBitmap(rotatedBitmap);


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

                    img_animal_part.setImageBitmap(bmpnew);


                //  file1=bitmapToFile(bmpnew);

            }

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

                poster.setHeader("Authorization","Bearer "+ token);
                poster.addHeader("Accept", "application/json");

                MultipartEntity entity = new MultipartEntity(org.apache.http.entity.mime.HttpMultipartMode.BROWSER_COMPATIBLE);

                //Log.e("request",ts+" "+Pref.getValue(context,"client_id","")+" "+Pref.getValue(context,"selected_animal","")+" "+mBinding.edtPatName.getText().toString());
                for(int i=0;i<pat_client_detailsArrayList.size();i++) {

                    Log.e("send_data",pat_client_detailsArrayList.get(i).getImg_file()+" "+pat_client_detailsArrayList.get(i).getBp_title()+" "+pat_client_detailsArrayList.get(i).getBp_desc()+" "+pat_client_detailsArrayList.get(i).getPat_main_id());

                    if(pat_client_detailsArrayList.get(i).getImg_file()!=null) {
                       entity.addPart("bp_img"+ i, new org.apache.http.entity.mime.content.FileBody(pat_client_detailsArrayList.get(i).getImg_file()));
                    }
                    entity.addPart("bp_title"+i, new StringBody(pat_client_detailsArrayList.get(i).getBp_title()));
                    entity.addPart("bp_desc"+i, new StringBody(pat_client_detailsArrayList.get(i).getBp_desc()));
                    entity.addPart("pat_detail_id"+i, new StringBody(pat_client_detailsArrayList.get(i).getPat_main_id()));


                }
                poster.setEntity(entity);

             //   Log.e("responsimage", "request:" + entity.getContent()+" ");
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
