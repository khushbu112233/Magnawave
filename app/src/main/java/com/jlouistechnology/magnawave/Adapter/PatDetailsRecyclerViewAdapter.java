package com.jlouistechnology.magnawave.Adapter;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.jlouistechnology.magnawave.Fonts.TextView_Regular;
import com.jlouistechnology.magnawave.Fonts.TextView_bld;
import com.jlouistechnology.magnawave.Fragment.AddNewPatFragment;
import com.jlouistechnology.magnawave.Interface.OnClickDeleteListener;
import com.jlouistechnology.magnawave.Interface.OnClickEditPatPointListener;
import com.jlouistechnology.magnawave.Interface.OnClickLinkedCurrentDeviceListener;
import com.jlouistechnology.magnawave.Model.Pat_details;
import com.jlouistechnology.magnawave.utils.CircularImageView;
import com.jlouistechnology.magnawave.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class PatDetailsRecyclerViewAdapter extends RecyclerSwipeAdapter<PatDetailsRecyclerViewAdapter.SimpleViewHolder> {


    private Context mContext;
    private ArrayList<Pat_details> pat_detail_list;
    Dialog mDialogRowBoardList;
    Dialog mDialogRowBoardList_camera;
    Dialog mDialogRowBoardList_image;
    int pos;
    SimpleViewHolder viewHolder_new;
    CircularImageView img_animal_part;
    OnClickLinkedCurrentDeviceListener onClickLinkedCurrentDeviceListener;
    OnClickDeleteListener onClickDeleteListener;
    OnClickEditPatPointListener onClickEditPatPointListener;


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


    public PatDetailsRecyclerViewAdapter(Context context, ArrayList<Pat_details> objects) {
        this.mContext = context;
        this.pat_detail_list = objects;

    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_animal_details, parent, false);
       /* StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());*/
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final Pat_details item = pat_detail_list.get(position);

        viewHolder_new=viewHolder;

        Log.e("onBindViewHolder","onBindViewHolder");

        // Drag From Left
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));
        // Drag From Right
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper));

        // viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        viewHolder.tvtitle.setText(pat_detail_list.get(position).getHeading());
        viewHolder.tvdesc.setText(pat_detail_list.get(position).getDescription());
        viewHolder.tv_count.setText(pat_detail_list.get(position).getId()+"");

        ViewTreeObserver vto =  viewHolder.tvdesc.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    viewHolder.tvdesc.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    viewHolder.tvdesc.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int width  =  viewHolder.tvdesc.getMeasuredWidth();
                int height =  viewHolder.tvdesc.getMeasuredHeight();
                Log.e("height",height+"");
                viewHolder.view.getLayoutParams().height = height;
                viewHolder.view.requestLayout();

            }
        });

        if(pat_detail_list.size()==1 || position==pat_detail_list.size()-1)
        {
            viewHolder.view.setVisibility(View.GONE);
        }
        else {
            viewHolder.view.setVisibility(View.VISIBLE);
        }

        if(position==0)
        {
            viewHolder.view_top.setVisibility(View.GONE);
        }
        else {

            viewHolder.view_top.setVisibility(View.VISIBLE);
        }

        viewHolder.text_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext,"pos:"+position,Toast.LENGTH_LONG).show();
                onClickEditPatPointListener.OnClickEditPatPointListener(position);
            }
        });

        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                Log.e("onClose","onClose");
                if(position==0)
                {
                    viewHolder.view_top.setVisibility(View.GONE);
                }
                else {
                    viewHolder.view_top.setVisibility(View.VISIBLE);
                }
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                Log.e("onUpdate","onUpdate");
                viewHolder.view_top.setVisibility(View.GONE);
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                Log.e("onStartOpen","onStartOpen");

            }

            @Override
            public void onOpen(SwipeLayout layout) {

                viewHolder.view_top.setVisibility(View.GONE);
                Log.e("onOpen","onOpen");
                //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

                Log.e("onStartClose","onStartClose");


            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.

                Log.e("onHandRelease","onHandRelease");
            }
        });
        viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewHolder.tv_view_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pat_detail_list.get(position).getBmp_image()!=null) {
                    showInputDialog_image(position);
                }
                else {
                    Toast.makeText(mContext, R.string.Image_is_not_available,Toast.LENGTH_LONG).show();
                }
            }
        });

        viewHolder.txtdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

                alertDialogBuilder.setMessage("Are you sure you want to remove this item?");
                // set positive button: Yes message
                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {

                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        onClickDeleteListener.OnClickDeleteListener(position);




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


        viewHolder.txtedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                onClickLinkedCurrentDeviceListener.OnClickLinkedCurrentDeviceListener( AddNewPatFragment.pat_detailsArrayList.get(position).getHeading(), AddNewPatFragment.pat_detailsArrayList.get(position).getDescription(),position, AddNewPatFragment.pat_detailsArrayList.get(position).getBmp_image());

                //showInputDialog(pat_detail_list.get(position).getHeading(),pat_detail_list.get(position).getDescription(),position,pat_detail_list.get(position).getBmp_image());
            }
        });


        // mItemManger is member in RecyclerSwipeAdapter Class
        mItemManger.bindView(viewHolder.itemView, position);

    }



    public void showInputDialog_image(final int pos) {

        mDialogRowBoardList_image = new Dialog(mContext);
        mDialogRowBoardList_image.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogRowBoardList_image.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialogRowBoardList_image.setContentView(R.layout.image_list_dialod_layout);
        mDialogRowBoardList_image.setCancelable(false);
        mWindow = mDialogRowBoardList_image.getWindow();
        mLayoutParams = mWindow.getAttributes();
        mLayoutParams.gravity = Gravity.CENTER;
        mWindow.setAttributes(mLayoutParams);
        final ImageView img_animal_part = (ImageView) mDialogRowBoardList_image.findViewById(R.id.img_animal_part);
        final TextView_bld txtcancel = (TextView_bld) mDialogRowBoardList_image.findViewById(R.id.cancel);

        img_animal_part.setImageBitmap(pat_detail_list.get(pos).getBmp_image());


        txtcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("size", AddNewPatFragment.pat_detailsArrayList.size()+"--");

                mDialogRowBoardList_image.dismiss();
            }
        });



        mDialogRowBoardList_image.show();
    }

    @Override
    public int getItemCount() {
        return pat_detail_list.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    //  ViewHolder Class

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView_bld tvtitle;
        TextView_bld txtdelete;
        TextView_bld txtedit;
        TextView_Regular tvdesc;
        TextView_Regular tv_view_image;
        TextView_bld tv_count;
        RelativeLayout text_layout;
        View view;
        View view_top;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvtitle = (TextView_bld) itemView.findViewById(R.id.tvtitle);
            txtdelete = (TextView_bld) itemView.findViewById(R.id.txtdelete);
            txtedit = (TextView_bld) itemView.findViewById(R.id.txtedit);
            tvdesc = (TextView_Regular) itemView.findViewById(R.id.tv_desc);
            tv_view_image= (TextView_Regular) itemView.findViewById(R.id.tv_view_image);
            tv_count = (TextView_bld) itemView.findViewById(R.id.tvcount);
            view = (View) itemView.findViewById(R.id.view);
            view_top = (View) itemView.findViewById(R.id.view_top);
            text_layout=(RelativeLayout) itemView.findViewById(R.id.text_layout);
        }
    }


    protected void showInputDialog_camera() {

        mDialogRowBoardList_camera = new Dialog(mContext);
        mDialogRowBoardList_camera.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogRowBoardList_camera.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialogRowBoardList_camera.setContentView(R.layout.photo_dialod_layout);
        mDialogRowBoardList_camera.setCancelable(false);
        mWindow = mDialogRowBoardList_camera.getWindow();
        mLayoutParams = mWindow.getAttributes();
        mLayoutParams.gravity = Gravity.BOTTOM;
        mWindow.setAttributes(mLayoutParams);
        final TextView_Regular txtgallery = (TextView_Regular) mDialogRowBoardList_camera.findViewById(R.id.txtgallery);
        final TextView_Regular txtcamera= (TextView_Regular) mDialogRowBoardList_camera.findViewById(R.id.txtcamera);
        final TextView_bld cancel = (TextView_bld) mDialogRowBoardList_camera.findViewById(R.id.cancel);

        txtgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ((FragmentActivity)mContext).requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        ((FragmentActivity)mContext).startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                SELECT_FILE);
                        mDialogRowBoardList_camera.dismiss();
                    }
                } else {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    ((FragmentActivity)mContext).startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                    mDialogRowBoardList_camera.dismiss();
                }
            }
        });

        txtcamera.setOnClickListener(new View.OnClickListener() {

                                         @Override
                                         public void onClick(View v) {


                                             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                 if (mContext.checkSelfPermission(Manifest.permission.CAMERA)
                                                         != PackageManager.PERMISSION_GRANTED) {
                                                     ((FragmentActivity)mContext).requestPermissions(new String[]{Manifest.permission.CAMERA},
                                                             MY_REQUEST_CODE);
                                                 } else {
                                                     Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                     fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                                                     Log.v("fileUri",fileUri+"--");
                                                     intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                                     // start the image capture Intent
                                                     ((FragmentActivity)mContext).startActivityForResult(intent, REQUEST_CAMERA);
                                                     mDialogRowBoardList_camera.dismiss();
                                                 }
                                             } else {
                                                 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                 fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                                                 Log.v("fileUri",fileUri+"--");
                                                 intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                                 // start the image capture Intent
                                                 ((FragmentActivity)mContext).startActivityForResult(intent, REQUEST_CAMERA);
                                                 mDialogRowBoardList_camera.dismiss();
                                             }


                                         }
                                     }
        );
        cancel.setOnClickListener(new View.OnClickListener()

                                  {
                                      @Override
                                      public void onClick(View v) {

                                          mDialogRowBoardList_camera.dismiss();
                                      }
                                  }

        );


        mDialogRowBoardList_camera.show();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == ((FragmentActivity)mContext).RESULT_OK) {
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
                    Cursor cursor = ((FragmentActivity)mContext).managedQuery(selectedImageUri, projection, null, null,
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

    public void onClickLinkedCurrentDeviceListener(OnClickLinkedCurrentDeviceListener onClickLinkedCurrentDeviceListener) {
        this.onClickLinkedCurrentDeviceListener = onClickLinkedCurrentDeviceListener;
    }
    public void onClickDeleteListener(OnClickDeleteListener onClickDeleteListener) {
        this.onClickDeleteListener = onClickDeleteListener;
    }
    public void onClickEditPatPointListener(OnClickEditPatPointListener onClickEditPatPointListener) {
        this.onClickEditPatPointListener = onClickEditPatPointListener;
    }
}
