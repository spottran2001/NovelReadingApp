package com.huawei.hms.novelreadingapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import com.huawei.hms.novelreadingapp.ui.read.ReadActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder>{

    private Context context;
    private static List<Chapter> mChapters;
    private ChapterAdapter.OnChapterListener mOnChapterListener;
    private String novelId;
    private int size;

    public ChapterAdapter(Context context, List<Chapter> mChapters, ChapterAdapter.OnChapterListener onChapterListener, String novel_id){
        this.context = context;
        this.mChapters = mChapters;
        this.mOnChapterListener = onChapterListener;
        this.novelId = novel_id;
    }


    @NonNull
    @Override
    public ChapterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false);

        return new ChapterAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterAdapter.ViewHolder holder, int position) {
        size = mChapters.size();
        Chapter chapter = mChapters.get(holder.getAdapterPosition());
        String title = chapter.getChapter() + ". " + chapter.getTitle();
        holder.title.setText(title);
        holder.title.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReadActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("chapterId", chapter.getId());
                intent.putExtra( "novelId", novelId);
                intent.putExtra( "size", size);
                context.startActivity(intent);
            }
        } );
        // lay wishlist
        //getUserWishlist(fUser.getUid(), product, holder.heart);


        holder.heart.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {

                boolean condition = holder.heart.getDrawable().getConstantState() == Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.ic_heart)).getConstantState();

                if(getDeviceName().contentEquals("Samsung"))
                {  Integer resource = (Integer) holder.heart.getTag();
                    boolean samsungCont = resource == R.drawable.ic_heart;
                    if (samsungCont)
                    {
                        holder.heart.setImageResource(R.drawable.ic_heart_activated);
                        //String idUser = fUser.getUid();
                        //addProductWishlist(idUser,productOptions.get(holder.getAdapterPosition()));
                        holder.heart.setTag(R.drawable.ic_heart_activated);

                    }else{
                        holder.heart.setImageResource(R.drawable.ic_heart);
                        //String idUser = fUser.getUid();
                        //delProductWishlist(idUser,product.getProduct_id());
                        holder.heart.setTag(R.drawable.ic_heart);

                    }
                }else{
                    if (condition)
                    {
                        holder.heart.setImageResource(R.drawable.ic_heart_activated);
                        //String idUser = fUser.getUid();
                        //addProductWishlist(idUser,productOptions.get(holder.getAdapterPosition()));
                        holder.heart.setTag(R.drawable.ic_heart_activated);

                    }else{
                        holder.heart.setImageResource(R.drawable.ic_heart);
                        //String idUser = fUser.getUid();
                        //delProductWishlist(idUser,product.getProduct_id());
                        holder.heart.setTag(R.drawable.ic_heart);

                    }
                }


            }});

    }
    private void getUserWishlist(String user_id, Chapter chapter , ImageButton heart){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("wishlist/"+user_id);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> listWishlist = new ArrayList<String>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    listWishlist.add(item.getKey());
                }
                for (int i =0 ; i < listWishlist.size();i++) {
                    // so sanh id cua san pham (cua minh la chapter) voi id trong list nay
                    if (chapter.getId().equals(listWishlist.get(i))) {
                        heart.setImageResource(R.drawable.ic_heart_activated);
                        heart.setTag(R.drawable.ic_heart_activated);
                    } else {
                        heart.setTag(R.drawable.ic_heart);

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer);
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
    private void addProductWishlist(String idUser, HashMap<String, String> product){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("wishlist/"+idUser);
        myRef.child(Objects.requireNonNull(product.get("productId"))).child("product_id").setValue(product.get("productId"));
        myRef.child(Objects.requireNonNull(product.get("productId"))).child("product_size").setValue(product.get("size"));
        myRef.child(Objects.requireNonNull(product.get("productId"))).child("product_color").setValue(product.get("color"));

    }

    private void delProductWishlist(String idUser,String idProduct){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("wishlist/"+idUser);
        myRef.child(idProduct).removeValue();

    }
    @Override
    public int getItemCount() {
        return mChapters ==null? 0: mChapters.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button title;
        ImageButton heart;
        ChapterAdapter.OnChapterListener onChapterListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.chapter_btn_chapter);
            heart = itemView.findViewById(R.id.chapter_btn_heart);
            this.onChapterListener = mOnChapterListener;
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String id = mChapters.get(position).getId();
            onChapterListener.onChapterClick(position,v,id);
        }
    }
    public interface OnChapterListener{
        void onChapterClick(int position, View view, String id);
    }
    private void loadImage(ImageView image, String imageName){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(imageName);
        try {
            File file = File.createTempFile("tmp",".jpg");
            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    BitmapDrawable ob = new BitmapDrawable(bitmap);

                    image.setBackground(ob);
                    image.setVisibility(View.VISIBLE);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
