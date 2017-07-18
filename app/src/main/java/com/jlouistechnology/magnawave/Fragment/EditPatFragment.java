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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jlouistechnology.magnawave.Activity.DashboardActivity;
import com.jlouistechnology.magnawave.Activity.MainActivity;
import com.jlouistechnology.magnawave.Adapter.EditPatDetailsInfoRecyclerViewAdapter;
import com.jlouistechnology.magnawave.Adapter.PatDetailsInfoRecyclerViewAdapter;
import com.jlouistechnology.magnawave.Adapter.PatDetailsRecyclerViewAdapter;
import com.jlouistechnology.magnawave.Fonts.Edittext_Regular;
import com.jlouistechnology.magnawave.Fonts.TextView_Regular;
import com.jlouistechnology.magnawave.Fonts.TextView_bld;
import com.jlouistechnology.magnawave.Interface.OnClickDeleteListener;
import com.jlouistechnology.magnawave.Interface.OnClickEditListener;
import com.jlouistechnology.magnawave.Interface.OnClickEditPatPointListener;
import com.jlouistechnology.magnawave.Interface.OnClickLinkedCurrentDeviceListener;
import com.jlouistechnology.magnawave.Model.Pat_client_details;
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

import pl.polidea.view.ZoomView;

/**
 * Created by aipxperts on 9/5/17.
 */
public class EditPatFragment extends Fragment {

    FragmentAddNewPatBinding mBinding;
    View rootView;
    Context context;
    public Dialog mDialogRowBoardList;
    Dialog mDialogRowBoardList_add;
    Dialog mDialogRowBoardList_edit;
    int no_of_notes=1;
    public static  EditPatDetailsInfoRecyclerViewAdapter  adapter;
    public static ArrayList<Pat_details> pat_detailsArrayList=new ArrayList<>();
    public static RecyclerView recycler_view_pat_details;
    ArrayList<Pat_client_details> pat_client_detailsArrayList=new ArrayList<>();
    ArrayList<Pat_client_details> pat_client_detailsArrayList_temp=new ArrayList<>();
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


    /**
     * for image dialog
     */
    Dialog mDialogRowBoardList_image;
    ImageView img_save;
    ZoomView zoomView;
    LinearLayout main_container;
    ImageView image;
    FrameLayout frameLayout_image;
    View v;
    int temp=0;
    int touch_status=0;
    float xCoOrdinate, yCoOrdinate;
    String prev_event="";
    String curr_event="";
    int prev_y=0;
    int curr_y=0;
    int x_latest=0;
    int y_latest=0;
    int is_flip_default=0;
    Bitmap default_image_with_flip;
    Bitmap default_image_without_flip;
    ImageView img_back;
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
        mBinding.imgCamera.setVisibility(View.GONE);
        mBinding.edtPatName.setText(MainActivity.capitalize(Pref.getValue(context,"pat_name","")));

        setdate();
        setanimal_default();
        setadapter();
        mBinding.txtClientName.setText(Pref.getValue(context,"client_fname","")+" "+ Pref.getValue(context,"client_lname",""));
        recycler_view_pat_details=(RecyclerView)rootView.findViewById(R.id.recycler_view_pat_details);

        // set_simple_and_flip_bitmap();
        Pref.setValue(context,"is_flip_image","false");
        height=mBinding.imageFrameLayout.getHeight();
        width=mBinding.imageFrameLayout.getWidth();

        mBinding.imageFrameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPat.getDrawable();
                if(drawable!=null) {
                    Bitmap bitmap = drawable.getBitmap();
                    if (bitmap != null) {
                        /**
                         * THis function is use for to add points on it.
                         */
                        showInputDialog_image(context, default_image_without_flip, "false","1");
                    } else {
                        Toast.makeText(context, "Please add image by click on camera icon!", Toast.LENGTH_LONG).show();
                    }
                }

                return false;
            }
        });

        mBinding.imageFrameLayoutFlip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //   showInputDialog(context,(int) event.getX(),(int) event.getY());

                BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPatFlip.getDrawable();
                if(drawable!=null) {
                    Bitmap bitmap = drawable.getBitmap();
                    if (bitmap != null) {
                        /**
                         * THis function is use for to add points on it.
                         */
                        showInputDialog_image(context, default_image_with_flip, "true","1");
                    } else {
                        Toast.makeText(context, "Please add image by click on camera icon!", Toast.LENGTH_LONG).show();
                    }
                }
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

        mBinding.imgFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(is_flip_default==0)
                {
                    is_flip_default=1;
                    mBinding.imageFrameLayout.setVisibility(View.GONE);
                    mBinding.imageFrameLayoutFlip.setVisibility(View.VISIBLE);
                    mBinding.imageFrameLayoutFlip.removeAllViews();
                    mBinding.imageFrameLayoutFlip.addView(mBinding.imgPatFlip);
                    if(Pref.getValue(context,"pat_swipe_image","").length()>0)
                    {
                        Glide.with(context).load(Pref.getValue(context,"pat_swipe_image","")).asBitmap().fitCenter().into(mBinding.imgPatFlip);
                    }
                    else {
                        mBinding.imgPatFlip.setImageBitmap(default_image_with_flip);
                    }


                    Pref.setValue(context,"is_flip_image","true");
                    if(pat_client_detailsArrayList.size()>0)
                    {
                        for(int i=0;i<pat_client_detailsArrayList.size();i++)
                        {
                            Log.e("is_flip_image",pat_client_detailsArrayList.get(i).getIs_flip_image()+"");
                            if(pat_client_detailsArrayList.get(i).getIs_flip_image().equals("1"))
                            {
                                if (Pref.getValue(context, "set_flip_image", "").equals("0")) {
                                    HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, pat_client_detailsArrayList.get(i).getX(), Integer.parseInt(Pref.getValue(context, "screen_width_old", "")), mBinding.imgPatFlip, pat_client_detailsArrayList.get(i).getY(), Integer.parseInt(Pref.getValue(context, "screen_height_old", "")), mBinding.imageFrameLayoutFlip, pat_client_detailsArrayList.get(i).getPat_detail_id(), "false");
                                }
                                else {
                                    HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, pat_client_detailsArrayList.get(i).getX(), Integer.parseInt(Pref.getValue(context, "screen_width", "")), mBinding.imgPatFlip, pat_client_detailsArrayList.get(i).getY(), Integer.parseInt(Pref.getValue(context, "screen_height", "")), mBinding.imageFrameLayoutFlip, pat_client_detailsArrayList.get(i).getPat_detail_id(), "false");

                                }
                            }



                        }
                    }
                }
                else {

                    is_flip_default=0;
                    mBinding.imageFrameLayout.setVisibility(View.VISIBLE);
                    mBinding.imageFrameLayoutFlip.setVisibility(View.GONE);
                    mBinding.imageFrameLayout.removeAllViews();
                    mBinding.imageFrameLayout.addView(mBinding.imgPat);
                    Pref.setValue(context,"is_flip_image","false");
                    Glide.with(context).load(Pref.getValue(context,"pat_image","")).asBitmap().fitCenter().into(mBinding.imgPat);

                    if(pat_client_detailsArrayList.size()>0)
                    {
                        for(int i=0;i<pat_client_detailsArrayList.size();i++)
                        {
                            Log.e("is_flip_image",pat_client_detailsArrayList.get(i).getIs_flip_image()+"");
                            if(pat_client_detailsArrayList.get(i).getIs_flip_image().equals("0"))
                            {

                                if (Pref.getValue(context, "set_front_image", "").equals("0"))
                                {
                                        HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, pat_client_detailsArrayList.get(i).getX(), Integer.parseInt(Pref.getValue(context, "screen_width_old", "")), mBinding.imgPat, pat_client_detailsArrayList.get(i).getY(), Integer.parseInt(Pref.getValue(context, "screen_height_old", "")), mBinding.imageFrameLayout, pat_client_detailsArrayList.get(i).getPat_detail_id(), "false");
                                }
                                else {
                                    HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, pat_client_detailsArrayList.get(i).getX(), Integer.parseInt(Pref.getValue(context, "screen_width", "")), mBinding.imgPat, pat_client_detailsArrayList.get(i).getY(), Integer.parseInt(Pref.getValue(context, "screen_height", "")), mBinding.imageFrameLayout, pat_client_detailsArrayList.get(i).getPat_detail_id(), "false");

                                }
                            }


                        }
                    }
                }

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
                    showInputDialog_image(context, default_image_without_flip, "false","0");

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms

                            int is_call_api = 0;
                            if (pat_client_detailsArrayList.size() > 0) {
                                is_call_api = 0;

                                for (int i = 0; i < pat_client_detailsArrayList.size(); i++) {

                                    if (pat_client_detailsArrayList.get(i).getBp_title().equals("")) {
                                        is_call_api = 1;
                                        break;
                                    }
                                }
                            }

                            if (is_call_api == 0) {
                            Log.e("list_size",pat_client_detailsArrayList.size()+"-----");
                            Pat_client_details[] pat_detailses = new Pat_client_details[pat_client_detailsArrayList.size()];
                            for (int i = 0; i < pat_client_detailsArrayList.size(); i++) {

                                pat_detailses[i] = new Pat_client_details();
                                pat_detailses[i].setPat_main_id(pat_client_detailsArrayList.get(i).getPat_main_id());
                                pat_detailses[i].setPat_detail_id(pat_client_detailsArrayList.get(i).getPat_detail_id());
                                pat_detailses[i].setBp_title(pat_client_detailsArrayList.get(i).getBp_title());
                                pat_detailses[i].setBp_desc(pat_client_detailsArrayList.get(i).getBp_desc());
                                pat_detailses[i].setBp_img(pat_client_detailsArrayList.get(i).getBp_img());
                                pat_detailses[i].setId(pat_client_detailsArrayList.get(i).getId());
                                pat_detailses[i].setX(pat_client_detailsArrayList.get(i).getX());
                                pat_detailses[i].setY(pat_client_detailsArrayList.get(i).getY());
                                pat_detailses[i].setIs_flip_image(pat_client_detailsArrayList.get(i).getIs_flip_image());
                                if(pat_client_detailsArrayList.get(i).getPet_img()!=null) {
                                    pat_detailses[i].setImg_file(bitmapToFile_detail(pat_client_detailsArrayList.get(i).getPet_img()));
                                }
                                pat_client_detailsArrayList.set(i, pat_detailses[i]);

                            }

                            animal_front_file=null;
                            mBinding.txtSave.setEnabled(false);

                            BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPat.getDrawable();
                            Bitmap bitmap_new = drawable.getBitmap();

                            if(mBinding.imageFrameLayout!=null && mBinding.imageFrameLayout.getHeight()>0) {
                                Bitmap bitmap = setsimple_merge_image(mBinding.imageFrameLayout);
                                if (bitmap != null) {

                                    Bitmap bmpnew = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
                                    animal_front_file = bitmapToFile(default_image_without_flip);
                                }
                            }
                            animal_rotate_file=null;
                            if(mBinding.imageFrameLayoutFlip!=null && mBinding.imageFrameLayoutFlip.getHeight()>0) {
                                Bitmap bitmap_flip_new = setsimple_merge_image(mBinding.imageFrameLayoutFlip);
                                if (bitmap_flip_new != null) {
                                    Bitmap bmpnewf = Bitmap.createScaledBitmap(bitmap_flip_new, 300, 300, false);
                                    animal_rotate_file = bitmapToFileFlip(default_image_with_flip);
                                }
                            }

                            if (cd.isConnectingToInternet()) {

                                new ExecuteTask().execute(WebService.EDITPET);
                            } else {
                                cd.showToast(context, R.string.NO_INTERNET_CONNECTION);

                            }
                            }
                            else {

                                Toast.makeText(context,"First fill all the detail information or delete it!",Toast.LENGTH_LONG).show();
                            }
                        }
                    }, 100);


                }
            }
        });
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        return rootView;
    }

    protected void showInputDialog_image(final Context context, final Bitmap bmp, final String is_flip, final String is_close) {

        mDialogRowBoardList_image = new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mDialogRowBoardList_image.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogRowBoardList_image.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialogRowBoardList_image.setContentView(R.layout.activity_zoom_image);
        mDialogRowBoardList_image.setCancelable(true);
        mWindow = mDialogRowBoardList_image.getWindow();
        mLayoutParams = mWindow.getAttributes();
        mLayoutParams.gravity = Gravity.CENTER;
        mWindow.setAttributes(mLayoutParams);
        v = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.zoomableview, null, false);
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        zoomView = new ZoomView(context);
        zoomView.setMaxZoom(1.5f);
        Log.e("zoom", zoomView.getMaxZoom() + "");
        zoomView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        zoomView.addView(v);
        img_save=(ImageView)mDialogRowBoardList_image.findViewById(R.id.img_save);
        main_container = (LinearLayout)mDialogRowBoardList_image.findViewById(R.id.main_container);
        image = (ImageView) v.findViewById(R.id.image);
        img_back=(ImageView)mDialogRowBoardList_image.findViewById(R.id.img_back);
        frameLayout_image = (FrameLayout) v.findViewById(R.id.frame_layout);
        main_container.addView(zoomView);
        Log.e("data_w_h",Pref.getValue(context,"screen_width","")+" "+Pref.getValue(context,"screen_height",""));
        pat_client_detailsArrayList_temp.clear();


        if(is_flip.equals("true")) {
            Log.e("default_image_with_flip",default_image_with_flip+"-------");
            image.setImageBitmap(default_image_with_flip);
            if (pat_client_detailsArrayList.size() > 0) {
                int set_flag = 0;

                for (int i = 0; i < pat_client_detailsArrayList.size(); i++) {

                    if (pat_client_detailsArrayList.get(i).getIs_flip_image().equals("1")) {

                        if (Pref.getValue(context, "set_flip_image", "").equals("0")) {
                            if(pat_client_detailsArrayList.get(i).getIs_change_point().equals("0")) {

                                set_flag = 1;
                                ViewTreeObserver vto = image.getViewTreeObserver();
                                final int finalI = i;
                                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        image.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                        int x_new = 0;
                                        long x_temp = 0;
                                        int x_new1 = 0;
                                        int x1 = 0;

                                        x_temp = ((rootView.getWidth()) * pat_client_detailsArrayList.get(finalI).getX());
                                        x_new = (int) (x_temp / Integer.parseInt(Pref.getValue(context, "screen_width_old", "")));
                                        x1 = (((int) image.getRight() - (int) image.getLeft()) * x_new) / (rootView.getWidth());
                                        Log.e("data--------new:", "x_old:" + pat_client_detailsArrayList.get(finalI).getX());

                                        pat_client_detailsArrayList.get(finalI).setX(x1);

                                        int y_new = 0;
                                        int y1;
                                        y_new = ((rootView.getHeight()) * pat_client_detailsArrayList.get(finalI).getY()) / Integer.parseInt(Pref.getValue(context, "screen_height_old", ""));
                                        y1 = (((int) image.getBottom()) * y_new) / (rootView.getHeight());


                                        pat_client_detailsArrayList.get(finalI).setY(y1);
                                        pat_client_detailsArrayList.get(finalI).setIs_change_point("1");



                                        Log.e("data-------after", pat_client_detailsArrayList.get(finalI).getX() + " " + pat_client_detailsArrayList.get(finalI).getY() + " " + finalI);
                                        HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, (int) pat_client_detailsArrayList.get(finalI).getX(), rootView.getWidth(), image, (int) pat_client_detailsArrayList.get(finalI).getY(), rootView.getHeight(), frameLayout_image, pat_client_detailsArrayList.get(finalI).getPat_detail_id() + "", "true");


                                    }
                                });
                            }
                            else {
                                HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, (int) pat_client_detailsArrayList.get(i).getX(), rootView.getWidth(), image, (int) pat_client_detailsArrayList.get(i).getY(), rootView.getHeight(), frameLayout_image, pat_client_detailsArrayList.get(i).getPat_detail_id() + "", "true");

                            }


                        } else {
                            HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, (int) pat_client_detailsArrayList.get(i).getX(), rootView.getWidth(), image, (int) pat_client_detailsArrayList.get(i).getY(), rootView.getHeight(), frameLayout_image, pat_client_detailsArrayList.get(i).getPat_detail_id() + "", "true");


                        }

                    }

                }

                if (set_flag == 1) {
                    Pref.setValue(context, "set_flip_image", "1");
                }


            }
        }
        else {

            int set_flag=0;
            image.setImageBitmap(default_image_without_flip);
            if(pat_client_detailsArrayList.size()>0)
            {
                for(int i=0;i<pat_client_detailsArrayList.size();i++)
                {

                    if(pat_client_detailsArrayList.get(i).getIs_flip_image().equals("0") ) {

                        if(Pref.getValue(context,"set_front_image","").equals("0"))
                        {
                            if(pat_client_detailsArrayList.get(i).getIs_change_point().equals("0")) {
                                set_flag = 1;
                                ViewTreeObserver vto = image.getViewTreeObserver();
                                final int finalI = i;
                                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        image.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                        int x_new = 0;
                                        long x_temp = 0;
                                        int x_new1 = 0;

                                        int x1 = 0;

                                        x_temp = ((rootView.getWidth()) * pat_client_detailsArrayList.get(finalI).getX());
                                        x_new = (int) (x_temp / Integer.parseInt(Pref.getValue(context, "screen_width_old", "")));
                                        x1 = (((int) image.getRight() - (int) image.getLeft()) * x_new) / (rootView.getWidth());
                                        pat_client_detailsArrayList.get(finalI).setX(x1);
                                        int y_new = 0;
                                        int y1;
                                        y_new = ((rootView.getHeight()) * pat_client_detailsArrayList.get(finalI).getY()) / Integer.parseInt(Pref.getValue(context, "screen_height_old", ""));
                                        y1 = (((int) image.getBottom()) * y_new) / (rootView.getHeight());
                                        pat_client_detailsArrayList.get(finalI).setY(y1);
                                        pat_client_detailsArrayList.get(finalI).setIs_change_point("1");

                                       /* if(Integer.parseInt(Pref.getValue(context, "screen_width_old", ""))<rootView.getWidth())
                                        {

                                            HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, (int) pat_client_detailsArrayList.get(finalI).getX()-45, rootView.getWidth(), image, (int) pat_client_detailsArrayList.get(finalI).getY()-45, rootView.getHeight(), frameLayout_image, pat_client_detailsArrayList.get(finalI).getPat_detail_id() + "", "true");

                                        }
                                        else  if(Integer.parseInt(Pref.getValue(context, "screen_width_old", ""))>rootView.getWidth()) {

                                            HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, (int) pat_client_detailsArrayList.get(finalI).getX()-25, rootView.getWidth(), image, (int) pat_client_detailsArrayList.get(finalI).getY()-25, rootView.getHeight(), frameLayout_image, pat_client_detailsArrayList.get(finalI).getPat_detail_id() + "", "true");

                                        }
                                        else {*/
                                            HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, (int) pat_client_detailsArrayList.get(finalI).getX(), rootView.getWidth(), image, (int) pat_client_detailsArrayList.get(finalI).getY(), rootView.getHeight(), frameLayout_image, pat_client_detailsArrayList.get(finalI).getPat_detail_id() + "", "true");

                                       // }


                                    }
                                });
                            }
                            else {
                                HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, (int) pat_client_detailsArrayList.get(i).getX(), rootView.getWidth(), image, (int) pat_client_detailsArrayList.get(i).getY(), rootView.getHeight(), frameLayout_image, pat_client_detailsArrayList.get(i).getPat_detail_id() + "", "true");
                            }



                        }
                        else {
                            HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, (int) pat_client_detailsArrayList.get(i).getX(), rootView.getWidth(), image, (int) pat_client_detailsArrayList.get(i).getY(), rootView.getHeight(), frameLayout_image, pat_client_detailsArrayList.get(i).getPat_detail_id() + "", "true");
                        }
                    }

                }

                if(set_flag==1)
                {
                    Pref.setValue(context,"set_front_image","1");
                }


            }
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                if(is_close.equals("0"))
                {
                    mDialogRowBoardList_image.dismiss();
                    showInputDialog_image(context, default_image_with_flip, "true","2");
                }
                if(is_close.equals("2"))
                {
                    mDialogRowBoardList_image.dismiss();

                }
            }
        }, 10);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogRowBoardList_image.dismiss();
            }
        });

        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(pat_client_detailsArrayList_temp.size()>0)
                {
                    pat_client_detailsArrayList.addAll(pat_client_detailsArrayList_temp);
                    recycler_view_pat_details.setLayoutManager(new LinearLayoutManager(context));
                    adapter=new EditPatDetailsInfoRecyclerViewAdapter(context,pat_client_detailsArrayList);
                    recycler_view_pat_details.setAdapter(adapter);
                    adapter.onClickDeleteListener(onClickDeleteListener);
                    adapter.onClickEditListener(onClickEditListener);
                    adapter.onClickEditPatPointListener(onClickEditPatPointListener);
                }



                Pref.setValue(context, "screen_width", rootView.getWidth() + "");
                Pref.setValue(context, "screen_height", rootView.getHeight() + "");
                /**
                 * THis logic is use to make one image with all points add on it.
                 */
                //Bitmap bitmap = setsimple_merge_image(frameLayout_image);
                // mBinding.imgPat.setImageBitmap(bitmap);
                if(is_flip.equals("true")) {
                    mBinding.imageFrameLayoutFlip.removeAllViews();
                    mBinding.imageFrameLayoutFlip.addView(mBinding.imgPatFlip);
                    mBinding.imgPatFlip.setImageBitmap(default_image_with_flip);
                    mBinding.imageFrameLayoutFlip.setVisibility(View.VISIBLE);
                    mBinding.imageFrameLayout.setVisibility(View.GONE);
                    set_point_on_screen("true");
                }
                else {
                    mBinding.imageFrameLayout.removeAllViews();
                    mBinding.imageFrameLayout.addView(mBinding.imgPat);
                    mBinding.imgPat.setImageBitmap(default_image_without_flip);
                    mBinding.imageFrameLayoutFlip.setVisibility(View.GONE);
                    mBinding.imageFrameLayout.setVisibility(View.VISIBLE);
                    set_point_on_screen("false");
                }



                mDialogRowBoardList_image.dismiss();



            }
        });

        zoomView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                /**
                 * To add points on image there is following condition should be check
                 * 1) temp must be 0 because in this senario I check when action down & up at that time y axis must be same or deffrence between them must be less then 5 then only can place point
                 * 2) current touch event must be single
                 * 3) previous touch event also must be single then only point can locate.
                 */

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    temp = 0;
                    prev_y = (int) event.getY();
                    Log.e("action", "ACTION_DOWN" + " " + event.getX() + " " + event.getY());
                    Log.e("actionmy", "ACTION_down :----------" + event.getX() + " " + event.getY() + " " + prev_y + " " + temp);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    temp = 2;

                    if (prev_y == (int) event.getY()) {
                        temp = 0;
                        Log.e("actionmy", "ACTION_UP1 :********" + event.getX() + " " + event.getY() + " " + prev_y + " " + temp);
                    } else if ((prev_y - (int) event.getY() < 5) && ((prev_y - (int) event.getY()) > -5)) {
                        temp = 0;
                        Log.e("actionmy", "ACTION_UP2 :********" + event.getX() + " " + event.getY() + " " + prev_y + " " + temp + ":::::::::" + (prev_y - (int) event.getY()));
                    } else if ((int) event.getY() - prev_y < 5 && (int) event.getY() - prev_y > -5) {
                        temp = 0;
                        Log.e("actionmy", "ACTION_UP3 :********" + event.getX() + " " + event.getY() + " " + prev_y + " " + temp + ":::::::::" + ((int) event.getY() - prev_y));
                    } else {
                        temp = 1;
                        Log.e("actionmy", "ACTION_UP4 :********" + event.getX() + " " + event.getY() + " " + prev_y + " " + temp);
                    }
                    //prev_y=0;
                    if (temp == 0) {
                        if (event.getPointerCount() == 1) {


                            if (prev_event.equals("single")) {

                                /**
                                 * This function is use to display point on image
                                 */

                                Bitmap bmp_image;

                                if(is_flip.equals("true")) {

                                    bmp_image=default_image_with_flip;
                                }
                                else {
                                    bmp_image=default_image_without_flip;
                                }

                                if(bmp_image!=null) {
                                    locate_point_on_image(context, (int) event.getX() - 45, (int) event.getY() - 45);
                                }
                                else {
                                    image.setImageBitmap(bmp_image);
                                }

                            }
                        }

                    }
                    // first finger lifted

                }
                if (event.getAction() == MotionEvent.ACTION_POINTER_UP) {
                    // second finger lifted
                    Log.e("action", "ACTION_POINTER_UP");
                }
                if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
                    // first and second finger down
                    Log.e("action", "ACTION_POINTER_DOWN");
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.e("action", "ACTION_MOVE-----------" + prev_y);


                    Log.e("action", "ACTION_MOVE" + event.getX() + " " + event.getY() + " " + prev_y);
                }

                if (event.getPointerCount() > 1) {
                    Log.d("action", "Multitouch event" + event.getPointerCount());
                    prev_event = "multi";

                } else {
                    // Single touch event
                    Log.d("action", "Single touch event" + event.getPointerCount());
                    prev_event = "single";

                }

                return false;
            }
        });



        mDialogRowBoardList_image.show();
    }


    protected void showInputDialog_image_edit_point(final Context context,final String is_flip, final int x, final int y, final int pos, final int id) {

        mDialogRowBoardList_image = new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mDialogRowBoardList_image.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogRowBoardList_image.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialogRowBoardList_image.setContentView(R.layout.activity_zoom_image);
        mDialogRowBoardList_image.setCancelable(true);
        mWindow = mDialogRowBoardList_image.getWindow();
        mLayoutParams = mWindow.getAttributes();
        mLayoutParams.gravity = Gravity.CENTER;
        mWindow.setAttributes(mLayoutParams);
        v = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.zoomableview, null, false);
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        zoomView = new ZoomView(context);
        zoomView.setMaxZoom(1.5f);
        Log.e("zoom", zoomView.getMaxZoom() + "");
        zoomView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        zoomView.addView(v);
        img_save=(ImageView)mDialogRowBoardList_image.findViewById(R.id.img_save);
        main_container = (LinearLayout)mDialogRowBoardList_image.findViewById(R.id.main_container);
        image = (ImageView) v.findViewById(R.id.image);
        img_back=(ImageView)mDialogRowBoardList_image.findViewById(R.id.img_back);
        frameLayout_image = (FrameLayout) v.findViewById(R.id.frame_layout);
        main_container.addView(zoomView);

        if(is_flip.equals("1")) {
            image.setImageBitmap(default_image_with_flip);
            Log.e("x:", x + " " + y + "----------");

            for (int i = 0; i < pat_client_detailsArrayList.size(); i++) {

                if (pat_client_detailsArrayList.get(i).getIs_flip_image().equals("1")) {

                    if (i == pos) {

                        if (pat_client_detailsArrayList.get(i).getIs_change_point().equals("0")) {

                            ViewTreeObserver vto = image.getViewTreeObserver();
                            final int finalI = i;
                            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    image.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                    int x_new = 0;
                                    long x_temp = 0;
                                    int x_new1 = 0;
                                    int x1 = 0;

                                    x_temp = ((rootView.getWidth()) * pat_client_detailsArrayList.get(finalI).getX());
                                    x_new = (int) (x_temp / Integer.parseInt(Pref.getValue(context, "screen_width_old", "")));
                                    x1 = (((int) image.getRight() - (int) image.getLeft()) * x_new) / (rootView.getWidth());
                                    Log.e("data--------new:", "x_old:" + pat_client_detailsArrayList.get(finalI).getX());

                                    pat_client_detailsArrayList.get(finalI).setX(x1);

                                    int y_new = 0;
                                    int y1;
                                    y_new = ((rootView.getHeight()) * pat_client_detailsArrayList.get(finalI).getY()) / Integer.parseInt(Pref.getValue(context, "screen_height_old", ""));
                                    y1 = (((int) image.getBottom()) * y_new) / (rootView.getHeight());


                                    pat_client_detailsArrayList.get(finalI).setY(y1);
                                    pat_client_detailsArrayList.get(finalI).setIs_change_point("1");


                                    Log.e("data-------after", pat_client_detailsArrayList.get(finalI).getX() + " " + pat_client_detailsArrayList.get(finalI).getY() + " " + finalI);

                                    update_point_on_image(context, (int) pat_client_detailsArrayList.get(finalI).getX(), (int) pat_client_detailsArrayList.get(finalI).getY(), id, pos);


                                }
                            });
                            break;
                        }
                        else {
                            update_point_on_image(context, x, y, id, pos);
                            break;
                        }
                    }
                }
            }
        }
        else {

            image.setImageBitmap(default_image_without_flip);
            for (int i = 0; i < pat_client_detailsArrayList.size(); i++) {

                if (pat_client_detailsArrayList.get(i).getIs_flip_image().equals("0")) {

                    if (i == pos) {

                        if (pat_client_detailsArrayList.get(i).getIs_change_point().equals("0")) {

                            ViewTreeObserver vto = image.getViewTreeObserver();
                            final int finalI = i;
                            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    image.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                    int x_new = 0;
                                    long x_temp = 0;
                                    int x_new1 = 0;
                                    int x1 = 0;

                                    x_temp = ((rootView.getWidth()) * pat_client_detailsArrayList.get(finalI).getX());
                                    x_new = (int) (x_temp / Integer.parseInt(Pref.getValue(context, "screen_width_old", "")));
                                    x1 = (((int) image.getRight() - (int) image.getLeft()) * x_new) / (rootView.getWidth());
                                    Log.e("data--------new:", "x_old:" + pat_client_detailsArrayList.get(finalI).getX());

                                    pat_client_detailsArrayList.get(finalI).setX(x1);

                                    int y_new = 0;
                                    int y1;
                                    y_new = ((rootView.getHeight()) * pat_client_detailsArrayList.get(finalI).getY()) / Integer.parseInt(Pref.getValue(context, "screen_height_old", ""));
                                    y1 = (((int) image.getBottom()) * y_new) / (rootView.getHeight());


                                    pat_client_detailsArrayList.get(finalI).setY(y1);
                                    pat_client_detailsArrayList.get(finalI).setIs_change_point("1");


                                    Log.e("data-------after", pat_client_detailsArrayList.get(finalI).getX() + " " + pat_client_detailsArrayList.get(finalI).getY() + " " + finalI);

                                    update_point_on_image(context, (int) pat_client_detailsArrayList.get(finalI).getX(), (int) pat_client_detailsArrayList.get(finalI).getY(), id, pos);


                                }
                            });
                            break;
                        }
                        else {
                            update_point_on_image(context, x, y, id, pos);
                            break;
                        }
                    }
                }
            }


        }
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogRowBoardList_image.dismiss();
            }
        });


        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 /**
                 * THis logic is use to make one image with all points add on it.
                 */
                //Bitmap bitmap = setsimple_merge_image(frameLayout_image);
                // mBinding.imgPat.setImageBitmap(bitmap);

                /**
                 * This logic is use to add points in to the list & set adapter
                 */

                for(int i=0;i<pat_client_detailsArrayList.size();i++)
                {
                    if(i==pos)
                    {
                        pat_client_detailsArrayList.get(i).setX(x_latest);
                        pat_client_detailsArrayList.get(i).setY(y_latest);
                        break;
                    }
                }


                recycler_view_pat_details.setLayoutManager(new LinearLayoutManager(context));
                adapter=new EditPatDetailsInfoRecyclerViewAdapter(context,pat_client_detailsArrayList);
                recycler_view_pat_details.setAdapter(adapter);
                adapter.onClickDeleteListener(onClickDeleteListener);
                adapter.onClickEditListener(onClickEditListener);
                adapter.onClickEditPatPointListener(onClickEditPatPointListener);

                if(is_flip.equals("1")) {
                    mBinding.imageFrameLayoutFlip.removeAllViews();
                    mBinding.imageFrameLayoutFlip.addView(mBinding.imgPatFlip);
                    mBinding.imgPatFlip.setImageBitmap(default_image_with_flip);
                    mBinding.imageFrameLayoutFlip.setVisibility(View.VISIBLE);
                    mBinding.imageFrameLayout.setVisibility(View.GONE);
                    set_point_on_screen_main("true");
                    set_point_on_screen_main("false");
                }
                else {
                    mBinding.imageFrameLayout.removeAllViews();
                    mBinding.imageFrameLayout.addView(mBinding.imgPat);
                    mBinding.imgPat.setImageBitmap(default_image_without_flip);
                    mBinding.imageFrameLayoutFlip.setVisibility(View.GONE);
                    mBinding.imageFrameLayout.setVisibility(View.VISIBLE);
                    set_point_on_screen_main("true");
                    set_point_on_screen_main("false");
                }

                mDialogRowBoardList_image.dismiss();


            }
        });

        zoomView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                /**
                 * To add points on image there is following condition should be check
                 * 1) temp must be 0 because in this senario I check when action down & up at that time y axis must be same or deffrence between them must be less then 5 then only can place point
                 * 2) current touch event must be single
                 * 3) previous touch event also must be single then only point can locate.
                 */

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    temp = 0;
                    prev_y = (int) event.getY();
                    Log.e("action", "ACTION_DOWN" + " " + event.getX() + " " + event.getY());
                    Log.e("actionmy", "ACTION_down :----------" + event.getX() + " " + event.getY() + " " + prev_y + " " + temp);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    temp = 2;

                    if (prev_y == (int) event.getY()) {
                        temp = 0;
                        Log.e("actionmy", "ACTION_UP1 :********" + event.getX() + " " + event.getY() + " " + prev_y + " " + temp);
                    } else if ((prev_y - (int) event.getY() < 5) && ((prev_y - (int) event.getY()) > -5)) {
                        temp = 0;
                        Log.e("actionmy", "ACTION_UP2 :********" + event.getX() + " " + event.getY() + " " + prev_y + " " + temp + ":::::::::" + (prev_y - (int) event.getY()));
                    } else if ((int) event.getY() - prev_y < 5 && (int) event.getY() - prev_y > -5) {
                        temp = 0;
                        Log.e("actionmy", "ACTION_UP3 :********" + event.getX() + " " + event.getY() + " " + prev_y + " " + temp + ":::::::::" + ((int) event.getY() - prev_y));
                    } else {
                        temp = 1;
                        Log.e("actionmy", "ACTION_UP4 :********" + event.getX() + " " + event.getY() + " " + prev_y + " " + temp);
                    }
                    //prev_y=0;
                    if (temp == 0) {
                        if (event.getPointerCount() == 1) {


                            if (prev_event.equals("single")) {

                                /**
                                 * This function is use to display point on image
                                 */
                                frameLayout_image.removeAllViews();
                                frameLayout_image.addView(image);
                                if(is_flip.equals("1"))
                                {
                                    image.setImageBitmap(default_image_with_flip);



                                }
                                else {

                                    image.setImageBitmap(default_image_without_flip);


                                }
                                Log.e("x:",(int)event.getX()+" "+(int)event.getY()+"**************");
                                update_point_on_image(context,(int)event.getX()-45,(int)event.getY()-45,id,pos);


                            }
                        }

                    }
                    // first finger lifted

                }
                if (event.getAction() == MotionEvent.ACTION_POINTER_UP) {
                    // second finger lifted
                    Log.e("action", "ACTION_POINTER_UP");
                }
                if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
                    // first and second finger down
                    Log.e("action", "ACTION_POINTER_DOWN");
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.e("action", "ACTION_MOVE-----------" + prev_y);


                    Log.e("action", "ACTION_MOVE" + event.getX() + " " + event.getY() + " " + prev_y);
                }

                if (event.getPointerCount() > 1) {
                    Log.d("action", "Multitouch event" + event.getPointerCount());
                    prev_event = "multi";

                } else {
                    // Single touch event
                    Log.d("action", "Single touch event" + event.getPointerCount());
                    prev_event = "single";

                }

                return false;
            }
        });



        mDialogRowBoardList_image.show();
    }


    public void update_point_on_image(Context context,int x,int y,int id,int pos)
    {

        /**
         *  THis logic is use to add points
         */
        x_latest=x;
        y_latest=y;
        float inPixels=0;
        if(rootView.getWidth()>1500)
        {
            inPixels = getActivity().getResources().getDimension(R.dimen.count_size_very_large);
        }
        else {
            inPixels = getActivity().getResources().getDimension(R.dimen.count_size);
        }
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) inPixels, (int) inPixels);
        TextView iv = new TextView(context);
        lp.setMargins(x , y , 0, 0);
        iv.setLayoutParams(lp);
        iv.setText(id+"");
        iv.setGravity(Gravity.CENTER);
        iv.setTextColor(Color.parseColor("#ffffff"));
        count++;
        iv.setBackground(getResources().getDrawable(
                R.drawable.circle_white_border));

        /**
         * This logic is use for add points on simple image or flip image
         */


        if(iv.getParent()!=null)
            ((ViewGroup)iv.getParent()).removeView(iv); // <- fix
        frameLayout_image.addView(iv);





    }


    public void locate_point_on_image(Context context,int x,int y)
    {

        /**
         *  THis logic is use to add points
         */
        x_latest=x;
        y_latest=y;
        float inPixels=0;
        if(rootView.getWidth()>1500)
        {
            inPixels = getActivity().getResources().getDimension(R.dimen.count_size_very_large);
        }
        else {
            inPixels = getActivity().getResources().getDimension(R.dimen.count_size);
        }
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) inPixels, (int) inPixels);
        TextView iv = new TextView(context);
        lp.setMargins(x , y , 0, 0);
        iv.setLayoutParams(lp);
        iv.setText((pat_client_detailsArrayList.size()+1)+"");
        iv.setGravity(Gravity.CENTER);
        iv.setTextColor(Color.parseColor("#ffffff"));
        count++;
        iv.setBackground(getResources().getDrawable(
                R.drawable.circle_white_border));

        /**
         * This logic is use for add points on simple image or flip image
         */

        if(Pref.getValue(context,"is_flip_image","").equals("false")) {

            if(iv.getParent()!=null)
                ((ViewGroup)iv.getParent()).removeView(iv); // <- fix
            frameLayout_image.addView(iv);

        }
        else {
            if(iv.getParent()!=null)
                ((ViewGroup)iv.getParent()).removeView(iv); // <- fix
            frameLayout_image.addView(iv);
        }


        /**
         * This logic is use to add points in to the list & set adapter
         */


        Pat_client_details[] pat_detailses=new Pat_client_details[1];
        pat_detailses[0]=new Pat_client_details();
        pat_detailses[0].setPat_main_id("");
        pat_detailses[0].setPat_detail_id(pat_client_detailsArrayList.size()+1+pat_client_detailsArrayList_temp.size()+"");
            pat_detailses[0].setId(pat_client_detailsArrayList.size()+1+pat_client_detailsArrayList_temp.size()+"");
        pat_detailses[0].setX(x);
        pat_detailses[0].setY(y);
        pat_detailses[0].setBp_img("");
        pat_detailses[0].setPet_img(null);
        pat_detailses[0].setImg_file(null);
        pat_detailses[0].setBp_title("");
        pat_detailses[0].setBp_desc("");
        pat_detailses[0].setIs_change_point("1");
        if((Pref.getValue(context,"is_flip_image","").equals("true")))
        {
            pat_detailses[0].setIs_flip_image("1");
        }
        else{
            pat_detailses[0].setIs_flip_image("0");
         }

        pat_client_detailsArrayList_temp.add(pat_detailses[0]);

    }



    public void set_point_on_screen(String is_flip)
    {
        Log.e("size_data",pat_client_detailsArrayList.size()+"---");
        if(pat_client_detailsArrayList.size()>0) {
            if(is_flip.equals("true"))
            {
                for (int i = 0; i < pat_client_detailsArrayList.size(); i++) {
                    if(pat_client_detailsArrayList.get(i).getIs_flip_image().equals("1")) {

                        HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, (int) pat_client_detailsArrayList.get(i).getX(), Integer.parseInt(Pref.getValue(context,"screen_width","")), mBinding.imgPatFlip, (int) pat_client_detailsArrayList.get(i).getY(),Integer.parseInt(Pref.getValue(context,"screen_height","")), mBinding.imageFrameLayoutFlip, pat_client_detailsArrayList.get(i).getPat_detail_id() + "", "false");

                    }
                }
            }
            else {
                for (int i = 0; i < pat_client_detailsArrayList.size(); i++) {
                    if(pat_client_detailsArrayList.get(i).getIs_flip_image().equals("0")) {

                        HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, (int) pat_client_detailsArrayList.get(i).getX(),Integer.parseInt(Pref.getValue(context,"screen_width","")), mBinding.imgPat, (int) pat_client_detailsArrayList.get(i).getY(),Integer.parseInt(Pref.getValue(context,"screen_height","")), mBinding.imageFrameLayout, pat_client_detailsArrayList.get(i).getPat_detail_id() + "", "false");
                    }
                }
            }

        }
    }

    public void set_point_on_screen_main(String is_flip)
    {
        Log.e("size_data",pat_client_detailsArrayList.size()+"---");
        if(pat_client_detailsArrayList.size()>0) {
            if(is_flip.equals("true"))
            {
                for (int i = 0; i < pat_client_detailsArrayList.size(); i++) {
                    if(pat_client_detailsArrayList.get(i).getIs_flip_image().equals("1")) {

                        if(pat_client_detailsArrayList.get(i).getIs_change_point().equals("1")) {
                            HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, (int) pat_client_detailsArrayList.get(i).getX(), rootView.getWidth(), mBinding.imgPatFlip, (int) pat_client_detailsArrayList.get(i).getY(), rootView.getHeight(), mBinding.imageFrameLayoutFlip, pat_client_detailsArrayList.get(i).getPat_detail_id() + "", "false");
                        }
                        else {
                            HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, (int) pat_client_detailsArrayList.get(i).getX(), Integer.parseInt(Pref.getValue(context, "screen_width_old", "")), mBinding.imgPatFlip, (int) pat_client_detailsArrayList.get(i).getY(), Integer.parseInt(Pref.getValue(context, "screen_height_old", "")), mBinding.imageFrameLayoutFlip, pat_client_detailsArrayList.get(i).getPat_detail_id() + "", "false");

                        }
                    }
                }
            }
            else {
                for (int i = 0; i < pat_client_detailsArrayList.size(); i++) {
                    if(pat_client_detailsArrayList.get(i).getIs_flip_image().equals("0")) {
                        if(pat_client_detailsArrayList.get(i).getIs_change_point().equals("1")) {

                            HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, (int) pat_client_detailsArrayList.get(i).getX(), rootView.getWidth(), mBinding.imgPat, (int) pat_client_detailsArrayList.get(i).getY(), rootView.getHeight(), mBinding.imageFrameLayout, pat_client_detailsArrayList.get(i).getPat_detail_id() + "", "false");
                        }
                        else {
                            HomeFragment.find_x_y_according_to_the_screen(getActivity(), rootView, (int) pat_client_detailsArrayList.get(i).getX(), Integer.parseInt(Pref.getValue(context, "screen_width_old", "")), mBinding.imgPat, (int) pat_client_detailsArrayList.get(i).getY(), Integer.parseInt(Pref.getValue(context, "screen_height_old", "")), mBinding.imageFrameLayout, pat_client_detailsArrayList.get(i).getPat_detail_id() + "", "false");

                        }
                    }
                }
            }

        }
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
                        pat_client_detailses[0].setIs_change_point("0");
                        Log.e("get_pet_details",ClientdetailFragment.client_detailsArrayList.get(i).getPat_client_detailsArrayList().get(j).getBp_img()+"");
                        pat_client_detailsArrayList.add(pat_client_detailses[0]);
                    }

                }


                break;
            }

        }
        Log.e("size_data",pat_client_detailsArrayList.size()+"");
        if(pat_client_detailsArrayList.size()>0)
        {
            for(int i=0;i<pat_client_detailsArrayList.size();i++)
            {
                Log.e("is_flip_image",pat_client_detailsArrayList.get(i).getIs_flip_image()+"");
                if(pat_client_detailsArrayList.get(i).getIs_flip_image().equals("1"))
                {
                    HomeFragment.find_x_y_according_to_the_screen(getActivity(),rootView,pat_client_detailsArrayList.get(i).getX(),Integer.parseInt(Pref.getValue(context,"screen_width","")),mBinding.imgPatFlip,pat_client_detailsArrayList.get(i).getY(),Integer.parseInt(Pref.getValue(context,"screen_height","")),mBinding.imageFrameLayoutFlip,pat_client_detailsArrayList.get(i).getPat_detail_id(),"false");

                }
                else {
                    HomeFragment.find_x_y_according_to_the_screen(getActivity(),rootView,pat_client_detailsArrayList.get(i).getX(),Integer.parseInt(Pref.getValue(context,"screen_width","")),mBinding.imgPat,pat_client_detailsArrayList.get(i).getY(),Integer.parseInt(Pref.getValue(context,"screen_height","")),mBinding.imageFrameLayout,pat_client_detailsArrayList.get(i).getPat_detail_id(),"false");

                }


            }
        }
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.recyclerViewPatDetails.setLayoutManager(llm);
        adapter=new EditPatDetailsInfoRecyclerViewAdapter(context,pat_client_detailsArrayList);
        mBinding.recyclerViewPatDetails.setAdapter(adapter);
        adapter.onClickEditListener(onClickEditListener);
        adapter.onClickDeleteListener(onClickDeleteListener);
        adapter.onClickEditPatPointListener(onClickEditPatPointListener);

    }

    OnClickEditListener onClickEditListener=new OnClickEditListener() {
        @Override
        public void onClickEditListener(String bp_title, String bp_desc, int position, Bitmap bp_img) {

            showInputDialog(bp_title,bp_desc,position,bp_img);
        }

    };
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
            if(pat_client_detailsArrayList.size()>0) {
                for (int i = 0; i < pat_client_detailsArrayList.size(); i++) {
                    if (pat_client_detailsArrayList.get(i).getIs_flip_image().equals(Pref.getValue(context, "is_flip_image", ""))) {

                        final float inPixels = getActivity().getResources().getDimension(R.dimen.count_size);
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) inPixels, (int) inPixels);
                        TextView iv = new TextView(context);
                        lp.setMargins(pat_client_detailsArrayList.get(i).getX(), pat_client_detailsArrayList.get(i).getY(), 0, 0);
                        iv.setLayoutParams(lp);
                        iv.setText((pat_client_detailsArrayList.get(i).getId()) + "");
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
            if(pat_client_detailsArrayList.size()>0) {
                for (int i = 0; i < pat_client_detailsArrayList.size(); i++) {
                    if (pat_client_detailsArrayList.get(i).getIs_flip_image().equals(Pref.getValue(context, "is_flip_image", ""))) {
                        final float inPixels = getActivity().getResources().getDimension(R.dimen.count_size);
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) inPixels, (int) inPixels);
                        TextView iv = new TextView(context);
                        lp.setMargins(pat_client_detailsArrayList.get(i).getX(), pat_client_detailsArrayList.get(i).getY(), 0, 0);
                        iv.setLayoutParams(lp);
                        iv.setText((pat_client_detailsArrayList.get(i).getId()) + "");
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
        Pref.setValue(context,"from_edit","1");

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

    public File bitmapToFile_detail(Bitmap bmp) {
        try {
            final int REQUIRED_SIZE = 200;
            ByteArrayOutputStream bos = new ByteArrayOutputStream(REQUIRED_SIZE);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bArr = bos.toByteArray();
            bos.flush();
            bos.close();
            long timestemp=(System.currentTimeMillis()/100);
            String file_name="image_detail"+String.valueOf(timestemp)+".png";
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


        if(Pref.getValue(context,"pat_image","").length()>0)
        {

            Glide.with(context).load(Pref.getValue(context,"pat_image","")).asBitmap().into(new BitmapImageViewTarget(mBinding.imgPat) {
                @Override
                protected void setResource(Bitmap resource) {
                    simple_image=resource;
                    default_image_without_flip=simple_image;
                    mBinding.imgPat.setImageBitmap(default_image_without_flip);
                }
            });


        }

        Log.e("pat_type_flip",Pref.getValue(context,"pat_type_flip","")+"---");
        if(Pref.getValue(context,"pat_swipe_image","").length()>0)
        {
            Glide.with(context).load(Pref.getValue(context,"pat_swipe_image","")).asBitmap().into(new BitmapImageViewTarget(mBinding.imgPatFlip) {
                @Override
                protected void setResource(Bitmap resource) {
                    simple_image=resource;
                    default_image_with_flip=resource;
                    mBinding.imgPatFlip.setImageBitmap(default_image_with_flip);
                }
            });

        }
        else{
            if(Pref.getValue(context,"pat_type_flip","").equals("horse"))
            {

                Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.horse_brown600);

                //mBinding.imgPatFlip.setImageBitmap(flipImage(bitmap,2));
                default_image_with_flip=flipImage(bitmap,2);
                mBinding.imgPatFlip.setImageBitmap(default_image_with_flip);
            }
            else if(Pref.getValue(context,"pat_type_flip","").equals("cat"))
            {

                Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.cat_600px);

              //  mBinding.imgPatFlip.setImageBitmap(flipImage(bitmap,2));
                default_image_with_flip=flipImage(bitmap,2);
                mBinding.imgPatFlip.setImageBitmap(default_image_with_flip);
            }
            else if(Pref.getValue(context,"pat_type_flip","").equals("dog"))
            {

                Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.dog_600px);

               // mBinding.imgPatFlip.setImageBitmap(flipImage(bitmap,2));
                default_image_with_flip=flipImage(bitmap,2);
                mBinding.imgPatFlip.setImageBitmap(default_image_with_flip);
            }
            else if(Pref.getValue(context,"pat_type_flip","").equals("general"))
            {

                Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.general_new);

              //  mBinding.imgPatFlip.setImageBitmap(flipImage(bitmap,2));
                default_image_with_flip=flipImage(bitmap,2);
                default_image_without_flip=bitmap;
                mBinding.imgPat.setImageBitmap(default_image_without_flip);
                mBinding.imgPatFlip.setImageBitmap(default_image_with_flip);
            }
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
                            default_image_with_flip=bmpnew;
                            mBinding.imgPatFlip.setImageBitmap(default_image_with_flip);
                            set_point_on_screen("true");

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
                            default_image_without_flip=bmpnew;
                            mBinding.imgPat.setImageBitmap(default_image_without_flip);
                            set_point_on_screen("false");

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
                        default_image_with_flip=bmpnew;
                        mBinding.imgPatFlip.setImageBitmap(default_image_with_flip);
                        set_point_on_screen("true");
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
                        default_image_without_flip=bmpnew;
                        mBinding.imgPat.setImageBitmap(default_image_without_flip);
                        set_point_on_screen("false");
                        flip_image=null;
                        mBinding.imageFrameLayout.setVisibility(View.VISIBLE);
                        mBinding.imageFrameLayoutFlip.setVisibility(View.GONE);
                    }

                    // set_points_on_image();

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
                                pat_detailses[0].setPat_main_id(pat_client_detailsArrayList.get(i).getPat_main_id());
                                pat_detailses[0].setId(pat_client_detailsArrayList.get(i).getId());
                                pat_detailses[0].setPat_detail_id(pat_client_detailsArrayList.get(i).getPat_detail_id());
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
                                pat_detailses[0].setX(pat_client_detailsArrayList.get(i).getX());
                                pat_detailses[0].setY(pat_client_detailsArrayList.get(i).getY());
                                pat_detailses[0].setIs_flip_image(pat_client_detailsArrayList.get(i).getIs_flip_image());
                                pat_detailses[0].setIs_change_point(pat_client_detailsArrayList.get(i).getIs_change_point());
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
                    iv.setText((pat_client_detailsArrayList.size()+1)+"");
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

                    Pat_client_details[] pat_client_detailses = new Pat_client_details[1];
                    pat_client_detailses[0] = new Pat_client_details();
                    pat_client_detailses[0].setPat_main_id("");
                    pat_client_detailses[0].setId(String.valueOf(pat_client_detailsArrayList.size()+1));
                    pat_client_detailses[0].setPat_detail_id(String.valueOf(pat_client_detailsArrayList.size()+1));
                    pat_client_detailses[0].setBp_title(edt_organ_name.getText().toString());
                    pat_client_detailses[0].setBp_desc(edt_organ_desc.getText().toString());
                    pat_client_detailses[0].setBp_img("");
                    pat_client_detailses[0].setX(x);
                    pat_client_detailses[0].setY(y);
                    pat_client_detailses[0].setIs_flip_image(Pref.getValue(context,"is_flip_image",""));
                    BitmapDrawable drawable = (BitmapDrawable) img_animal_part.getDrawable();
                    if(drawable!=null) {
                        Bitmap bitmap = drawable.getBitmap();
                        pat_client_detailses[0].setPet_img(bitmap);
                    }
                    else {
                        pat_client_detailses[0].setPet_img(null);
                        pat_client_detailses[0].setImg_file(null);
                    }
                    pat_client_detailsArrayList.add(pat_client_detailses[0]);


                    recycler_view_pat_details.setLayoutManager(new LinearLayoutManager(context));
                    adapter=new EditPatDetailsInfoRecyclerViewAdapter(context,pat_client_detailsArrayList);
                    mBinding.recyclerViewPatDetails.setAdapter(adapter);
                    adapter.onClickEditListener(onClickEditListener);
                    adapter.onClickDeleteListener(onClickDeleteListener);
                    adapter.onClickEditPatPointListener(onClickEditPatPointListener);

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
            if(pat_client_detailsArrayList.size()>0)
            {
                for (int i = 0; i<pat_client_detailsArrayList.size(); i++) {

                    if (i == pos) {
                        pat_client_detailsArrayList.remove(i);
                        break;
                    }
                }
                // for (int i = 0; i< pat_client_detailsArrayList.size(); i++)
                //  {
                // notifyDataSetChanged();
                for(int k = 0; k< pat_client_detailsArrayList.size(); k++)
                {
                    Pat_client_details[] pat_client_detailses = new Pat_client_details[1];
                    pat_client_detailses[0] = new Pat_client_details();
                    pat_client_detailses[0].setPat_main_id(pat_client_detailsArrayList.get(k).getPat_main_id());
                    pat_client_detailses[0].setId(k+1+"");
                    pat_client_detailses[0].setPat_detail_id(k+1+"");
                    pat_client_detailses[0].setBp_title(pat_client_detailsArrayList.get(k).getBp_title());
                    pat_client_detailses[0].setBp_desc(pat_client_detailsArrayList.get(k).getBp_desc());
                    pat_client_detailses[0].setBp_img(pat_client_detailsArrayList.get(k).getBp_img());
                    pat_client_detailses[0].setX(pat_client_detailsArrayList.get(k).getX());
                    pat_client_detailses[0].setY(pat_client_detailsArrayList.get(k).getY());
                    pat_client_detailses[0].setIs_flip_image(pat_client_detailsArrayList.get(k).getIs_flip_image());
                    pat_client_detailses[0].setPet_img(pat_client_detailsArrayList.get(k).getPet_img());
                    pat_client_detailses[0].setImg_file(pat_client_detailsArrayList.get(k).getImg_file());
                    pat_client_detailses[0].setIs_change_point(pat_client_detailsArrayList.get(k).getIs_change_point());
                    pat_client_detailsArrayList.set(k,pat_client_detailses[0]);
                    adapter.notifyDataSetChanged();
                }

                // }

                recycler_view_pat_details.setLayoutManager(new LinearLayoutManager(context));
                adapter=new EditPatDetailsInfoRecyclerViewAdapter(context,pat_client_detailsArrayList);
                mBinding.recyclerViewPatDetails.setAdapter(adapter);
                adapter.onClickEditListener(onClickEditListener);
                adapter.onClickDeleteListener(onClickDeleteListener);
                adapter.onClickEditPatPointListener(onClickEditPatPointListener);



                if(Pref.getValue(context,"is_flip_image","").equals("true"))
                {
                    BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPatFlip.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    mBinding.imageFrameLayoutFlip.removeAllViews();
                    mBinding.imageFrameLayoutFlip.addView(mBinding.imgPatFlip);
                    mBinding.imgPatFlip.setImageBitmap(bitmap);
                    set_point_on_screen("true");
                }
                else {
                    // BitmapDrawable drawable = (BitmapDrawable) mBinding.imgPat.getDrawable();
                    // Bitmap bitmap = drawable.getBitmap();
                    mBinding.imageFrameLayout.removeAllViews();
                    mBinding.imageFrameLayout.addView(mBinding.imgPat);
                    mBinding.imgPat.setImageBitmap(default_image_without_flip);
                    set_point_on_screen("false");

                }


            }
        }
    };

    OnClickEditPatPointListener onClickEditPatPointListener=new OnClickEditPatPointListener() {
        @Override
        public void OnClickEditPatPointListener(int pos) {
            int x=0;
            int y=0;
            String is_flip;
            int id;

            if(pat_client_detailsArrayList.size()>0) {
                for (int i = 0; i < pat_client_detailsArrayList.size(); i++) {

                    if (i == pos) {

                        x=pat_client_detailsArrayList.get(i).getX();
                        y=pat_client_detailsArrayList.get(i).getY();
                        is_flip=pat_client_detailsArrayList.get(i).getIs_flip_image();
                        id=Integer.parseInt(pat_client_detailsArrayList.get(i).getPat_detail_id());
                        showInputDialog_image_edit_point(context,is_flip,x,y,pos,id);

                        break;
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
                entity.addPart("Authorization", new StringBody("Bearer "+token));
*/

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
                entity.addPart("pet_id", new StringBody(Pref.getValue(context,"pat_id","")));
                entity.addPart("client_id", new StringBody(Pref.getValue(context,"client_id","")));
                entity.addPart("cat_name", new StringBody(Pref.getValue(context,"selected_animal","")));
                entity.addPart("pet_name", new StringBody(mBinding.edtPatName.getText().toString()));
                entity.addPart("width", new StringBody(rootView.getWidth()+""));
                entity.addPart("height", new StringBody(rootView.getHeight()+""));
                //Log.e("request",ts+" "+Pref.getValue(context,"client_id","")+" "+Pref.getValue(context,"selected_animal","")+" "+mBinding.edtPatName.getText().toString());

                Log.e("list_size",pat_client_detailsArrayList.size()+"--");
                for(int i=0;i<pat_client_detailsArrayList.size();i++) {

                    Log.e("send_data",pat_client_detailsArrayList.get(i).getImg_file()+" "+pat_client_detailsArrayList.get(i).getBp_title()+" "+pat_client_detailsArrayList.get(i).getBp_desc()+" "+pat_client_detailsArrayList.get(i).getPat_main_id());

                    if(pat_client_detailsArrayList.get(i).getImg_file()!=null) {
                        entity.addPart("bp_img"+ i, new org.apache.http.entity.mime.content.FileBody(pat_client_detailsArrayList.get(i).getImg_file()));
                    }
                    entity.addPart("bp_title"+i, new StringBody(pat_client_detailsArrayList.get(i).getBp_title()));
                    entity.addPart("bp_desc"+i, new StringBody(pat_client_detailsArrayList.get(i).getBp_desc()));
                    entity.addPart("pat_detail_id"+i, new StringBody(pat_client_detailsArrayList.get(i).getPat_main_id()));
                    entity.addPart("x"+i,new StringBody(pat_client_detailsArrayList.get(i).getX()+""));
                    entity.addPart("y"+i,new StringBody(pat_client_detailsArrayList.get(i).getY()+""));
                   Log.e("is_flip","i"+i+" "+pat_client_detailsArrayList.get(i).getIs_flip_image());
                    if(pat_client_detailsArrayList.get(i).getIs_flip_image().equals("1"))
                    {
                        entity.addPart("is_flip"+i,new StringBody("1"));
                    }
                    else {
                        entity.addPart("is_flip"+i,new StringBody("0"));
                    }



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
