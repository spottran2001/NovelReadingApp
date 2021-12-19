package com.huawei.hms.novelreadingapp.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.huawei.hms.novelreadingapp.ui.detail.DetailActivity;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder>{

    private Context context;
    private  List<Novel> mWishlist;
    private List<HashMap<String,String>> wishlistOptions;
    private int totalCount = 0;
    private TextView totalProducts;
    private List<Chapter>mChapters;

    public WishlistAdapter(Context context, List<Novel> mWishlist, List<HashMap<String,String>> wishlistOptions) {
        this.context = context;
        this.mWishlist = mWishlist;
        this.wishlistOptions = wishlistOptions;
        this.totalProducts = totalProducts;

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



        loadImage(holder.image,novel.getImage(),holder);

        totalCount= wishlistOptions.size();
        totalProducts.setText(totalCount > 1 ? totalCount + " ITEMS": totalCount + " ITEM");


        holder.name.setText(novel.getName());

        holder.totalChapter.setText(novel.getChapter_quantity());

        holder.read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chuyển sang đọc
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(context)
                        .setTitle("Warning")
                        .setMessage("Do you want to delete this?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //delProductWishlist(idUser,product.getProduct_id(),holder.getAdapterPosition());

                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
        });

        getChapters(novel.getId(),holder);



    }
    private void getChapters(String id, ViewHolder holder) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Chapter").child(id);
        ArrayList<String> chapters = new ArrayList<>();

        mChapters = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChapters.clear();
                for (DataSnapshot dtShot : snapshot.getChildren()) {
                    Chapter chapter = dtShot.getValue(Chapter.class);
                    assert chapter != null;
                    chapter.setId(dtShot.getKey());
                    chapter.setNovelId(id);
                    mChapters.add(chapter);
                    chapters.add("Chapter "+ String.valueOf(chapter.getChapter()));
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_dropdown_item,chapters);


                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                holder.chapterSpinner.setAdapter(adapter);

                //get chapter
                for(int i = 0 ; i < chapters.size(); i ++){
                    String temp = wishlistOptions.get(holder.getAdapterPosition()).get("product_size");
                    if(chapters.get(i).equals(temp)){
                        holder.chapterSpinner.setSelection(i);
                    }
                }
                holder.chapterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String text = parent.getItemAtPosition(position).toString();
                        for (DataSnapshot dtShot : snapshot.getChildren()) {
                            Chapter chapter = dtShot.getValue(Chapter.class);
                            assert chapter != null;
                            chapter.setId(dtShot.getKey());
                            if(chapter.getChapter().equals(text.substring(7))){
                                // lưu vào wishlist
                                break;
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // sometimes you need nothing here
                    }
                });

                holder.dropDown.setOnClickListener(new  View.OnClickListener(){
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View v) {
                        holder.chapterSpinner.performClick();
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
        DatabaseReference myRef = database.getReference("wishlist/"+idUser);
        myRef.child(idProduct).removeValue();
        if(!mWishlist.isEmpty()&& mWishlist.size() > position) {
            mWishlist.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
        if(mWishlist.isEmpty()){
            mWishlist.clear();
            totalProducts.setText("0 ITEM");
        }
    }

    private void saveEdit(String productId, String size ){
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("wishlist/"+ FirebaseAuth.getInstance().getCurrentUser().getUid() + "/"+ productId);
        //myRef.child("product_size").setValue(size);
        //lưu khi đổi wishlist
    }

    private void addProductCart(String idUser,String idProduct,int amount, String color,String size){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("cart");
        String idColor = color.substring(1);
        myRef.child(idUser).child("product").child(idProduct+idColor).child("productId").setValue(idProduct);
        myRef.child(idUser).child("product").child(idProduct+idColor).child("amount").setValue(amount);
        myRef.child(idUser).child("product").child(idProduct+idColor).child("color").setValue(color);
        myRef.child(idUser).child("product").child(idProduct+idColor).child("size").setValue(size);
    }


    private void loadImage(ImageView image, String imageName, ViewHolder holder){
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
        ImageButton remove,dropDown;
        Spinner chapterSpinner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image =(ImageView) itemView.findViewById(R.id.wishlist_iv_cover);
            name = itemView.findViewById(R.id.wishlist_tv_title);
            author = itemView.findViewById(R.id.wishlist_tv_author);
            totalChapter = itemView.findViewById(R.id.wishlist_tv_totalChapter);
            chapterSpinner = (Spinner) itemView.findViewById(R.id.wishlist_spinner_dropDownSize);
            read = itemView.findViewById(R.id.wishList_btn_read);
            remove = itemView.findViewById(R.id.wishlist_ibtn_remove);
            dropDown = itemView.findViewById(R.id.wishlist_btn_dropDown);
        }
    }
}
