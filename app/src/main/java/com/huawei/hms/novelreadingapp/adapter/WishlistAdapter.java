package com.huawei.hms.novelreadingapp.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.huawei.hms.novelreadingapp.R;
import com.huawei.hms.novelreadingapp.model.Chapter;
import com.huawei.hms.novelreadingapp.model.Novel;
import com.huawei.hms.novelreadingapp.ui.auth.LoginActivity;
import com.huawei.hms.novelreadingapp.ui.read.ReadActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder>{

    private Context context;
    private  List<Novel> mWishlist;
    private int totalCount = 0;
    private TextView items;
    private List<Chapter>mChapters;

    public WishlistAdapter(Context context, List<Novel> mWishlist, TextView items) {
        this.context = context;
        this.mWishlist = mWishlist;
        this.items = items;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_wishlist, parent, false);
        return new ViewHolder(view);
    }




    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Novel novel = mWishlist.get(holder.getAdapterPosition());



        loadImage(holder.image,novel.getImage());

        totalCount= mWishlist.size();
        items.setText(totalCount > 1 ? totalCount + " Novels": totalCount + " Novel");


        holder.name.setText(novel.getName());

        holder.author.setText(novel.getAuthor());
        getChapters(novel.getId(), novel, holder.tv_chapter, holder.read,holder.remove,holder.getAdapterPosition() );



    }

    private void getChapters(String id, Novel novel, TextView tv_chapter, Button read, ImageButton remove,int position) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Chapter").child(id);
        mChapters = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChapters.clear();
                String currentChapter = "";

                for (DataSnapshot dtShot : snapshot.getChildren()) {
                    Chapter chapter = dtShot.getValue(Chapter.class);
                    assert chapter != null;
                    chapter.setId(dtShot.getKey());
                    chapter.setNovelId(id);
                    mChapters.add(chapter);
                    if (chapter.getId().equals(novel.getChapter_read())){
                        currentChapter= chapter.getId();
                        tv_chapter.setText(chapter.getChapter());
                    }
                }
                String finalCurrentChapter = currentChapter;
                read.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context.getApplicationContext(), ReadActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("novelId",id);
                        intent.putExtra("chapterId", finalCurrentChapter);
                        intent.putExtra("size", mChapters.size());
                        context.startActivity(intent);
                    }
                } );


                String finalCurrentChapter1 = currentChapter;
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new AlertDialog.Builder(context)
                                .setTitle("Warning")
                                .setMessage("Do you want to delete this?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        delProductWishlist(LoginActivity.getAccount().getOpenId(),novel.getId() + finalCurrentChapter1,position);

                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();


                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void delProductWishlist(String idUser, String idProduct, int position){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Wishlist/"+idUser);
        myRef.child(idProduct).removeValue();
        if(!mWishlist.isEmpty()&& mWishlist.size() > position) {
            mWishlist.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
        if(mWishlist.isEmpty()){
            mWishlist.clear();
            items.setText("0 ITEM");
        }
    }


    private void loadImage(ImageView image, String imageName){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(imageName);
        try {
            File file = File.createTempFile("tmp",".png");
            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    BitmapDrawable ob = new BitmapDrawable(bitmap);

                    image.setBackground(ob);


                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public int getItemCount() {
        return mWishlist ==null? 0: mWishlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name,author,totalChapter;
        Button read;
        ImageButton remove;
        TextView tv_chapter;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image =(ImageView) itemView.findViewById(R.id.wishlist_iv_cover);
            name = itemView.findViewById(R.id.wishlist_tv_title);
            author = itemView.findViewById(R.id.wishlist_tv_author);

            read = itemView.findViewById(R.id.wishList_btn_read);
            remove = itemView.findViewById(R.id.wishlist_ibtn_remove);
            tv_chapter = itemView.findViewById(R.id.wishlist_tv_dropDownSize);

        }
    }
}
