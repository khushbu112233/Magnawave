package com.jlouistechnology.magnawave.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.jlouistechnology.magnawave.Activity.MainActivity;
import com.jlouistechnology.magnawave.Fonts.TextView_Regular;
import com.jlouistechnology.magnawave.Fonts.TextView_bld;
import com.jlouistechnology.magnawave.Fragment.AddNewPatFragment;
import com.jlouistechnology.magnawave.Interface.OnClickEditListener;
import com.jlouistechnology.magnawave.Interface.OnClickEditPatPointListener;
import com.jlouistechnology.magnawave.Interface.OnClickLinkedCurrentDeviceListener;
import com.jlouistechnology.magnawave.Model.Pat_client_details;
import com.jlouistechnology.magnawave.utils.CircularImageView;
import com.jlouistechnology.magnawave.R;

import java.util.ArrayList;

public class PatDetailsInfoRecyclerViewAdapter extends RecyclerSwipeAdapter<PatDetailsInfoRecyclerViewAdapter.SimpleViewHolder> {


    private Context mContext;
    private ArrayList<Pat_client_details> pat_detail_list;
    Dialog mDialogRowBoardList;
    Dialog mDialogRowBoardList_camera;
    Dialog mDialogRowBoardList_image;
    int pos;
    SimpleViewHolder viewHolder_new;
    CircularImageView img_animal_part;
    OnClickEditListener onClickEditListener;

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

    public PatDetailsInfoRecyclerViewAdapter(Context context, ArrayList<Pat_client_details> objects) {
        this.mContext = context;
        this.pat_detail_list = objects;

    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_animal_details_report, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final Pat_client_details item = pat_detail_list.get(position);

        viewHolder_new=viewHolder;

        Log.e("onBindViewHolder","onBindViewHolder");


        // viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        viewHolder.tvtitle.setText(pat_detail_list.get(position).getBp_title());
        viewHolder.tvdesc.setText(MainActivity.capitalize(pat_detail_list.get(position).getBp_desc()));
        viewHolder.tv_count.setText(pat_detail_list.get(position).getPat_detail_id()+"");
        viewHolder.swipeLayout.setSwipeEnabled(false);
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));
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

        Log.e("pat_detail_list",pat_detail_list.get(position).getBp_img()+" "+pat_detail_list.get(position).getPet_img()+" "+position);


        if(!pat_detail_list.get(position).getBp_img().equals("")) {


            Glide.with(mContext)
                    .load(pat_detail_list.get(position).getBp_img())
                    .asBitmap()

                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            Log.v("glide","onLoadStarted");

                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {

                            Log.v("glide","onLoadFailed");

                        }

                        @Override
                        public void onResourceReady(final Bitmap icon1, GlideAnimation<? super Bitmap> glideAnimation) {
                            Log.v("glide","onResourceReady");
                            viewHolder.img_pet.setImageBitmap(icon1);
                            pat_detail_list.get(position).setPet_img(icon1);


                        }

                        @Override
                        public void onLoadCleared(Drawable placeholder) {
                            Log.v("glide","onLoadCleared");

                        }
                        @Override
                        public void setRequest(Request request) {

                            Log.v("glide","setRequest");
                        }

                        @Override
                        public Request getRequest() {
                            Log.v("glide","getRequest");

                            return null;
                        }

                        @Override
                        public void onStart() {

                            Log.v("glide","onStart");
                        }

                        @Override
                        public void onStop() {
                            Log.v("glide","onStop");
                        }

                        @Override
                        public void onDestroy() {
                            Log.v("glide","onDestroy");
                        }
                    });
        }
        else if(pat_detail_list.get(position).getPet_img()!=null){
            viewHolder.img_pet.setImageBitmap(pat_detail_list.get(position).getPet_img());
        }


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

        viewHolder.tv_view_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("pat_detail_list",pat_detail_list.get(position).getBp_img()+" "+pat_detail_list.get(position).getPet_img()+" "+position);

                if(!pat_detail_list.get(position).getBp_img().equals(""))
                {
                    showInputDialog_image(position);
                }
                else if(pat_detail_list.get(position).getPet_img()!=null) {

                    showInputDialog_image(position);
                }
                else {
                    Toast.makeText(mContext, R.string.Image_is_not_available,Toast.LENGTH_LONG).show();
                }
            }
        });
        viewHolder.txtedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                if(!pat_detail_list.get(position).getBp_img().equals(""))
                {
                    onClickEditListener.onClickEditListener(pat_detail_list.get(position).getBp_title(),pat_detail_list.get(position).getBp_desc(),position,pat_detail_list.get(position).getPet_img());

                }
                else if(pat_detail_list.get(position).getPet_img()!=null){
                    onClickEditListener.onClickEditListener(pat_detail_list.get(position).getBp_title(),pat_detail_list.get(position).getBp_desc(),position,pat_detail_list.get(position).getPet_img());

                }
                else {

                    onClickEditListener.onClickEditListener(pat_detail_list.get(position).getBp_title(),pat_detail_list.get(position).getBp_desc(),position,null);
                }
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

        if(!pat_detail_list.get(pos).getBp_img().equals(""))
        {
            Glide.with(mContext).load(pat_detail_list.get(pos).getBp_img()).asBitmap().centerCrop().into(img_animal_part);
        }
        else if(pat_detail_list.get(pos).getPet_img()!=null) {
            img_animal_part.setImageBitmap(pat_detail_list.get(pos).getPet_img());
        }

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
        ImageView img_pet;
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
            img_pet=(ImageView)itemView.findViewById(R.id.img_pet);
        }
    }

    public void onClickEditListener(OnClickEditListener onClickEditListener) {
        this.onClickEditListener = onClickEditListener;
    }

}
