package com.huawei.hms.novelreadingapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.huawei.hms.novelreadingapp.R;
import com.huawei.hms.novelreadingapp.model.Novel;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class NovelListAdapter extends RecyclerView.Adapter<NovelListAdapter.ViewHolder>{


    private Context context;
    private static List<Novel> mNovelList;
    private NovelListAdapter.OnNovelListener mOnNovelListener;
    private String novelId;


    public NovelListAdapter(Context context, List<Novel> mNovelList, NovelListAdapter.OnNovelListener onNovelListener){
        this.context = context;
        this.mNovelList = mNovelList;
        this.mOnNovelListener = onNovelListener;
    }

    @NonNull
    @Override
    public NovelListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_novel, parent, false);

        return new NovelListAdapter.ViewHolder(view,mOnNovelListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NovelListAdapter.ViewHolder holder, int position) {

        final Novel novel = mNovelList.get(position);

        String imageName = novel.getImage();
        loadImage(holder.cover,imageName);

        holder.title.setText(novel.getName());
        holder.author.setText(novel.getAuthor());


    }

    @Override
    public int getItemCount() {
        return mNovelList ==null? 0: mNovelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView cover;
        TextView title, author;
        NovelListAdapter.OnNovelListener onNovelListener;
        public ViewHolder(@NonNull View itemView, OnNovelListener mOnNovelListener) {
            super(itemView);
            cover = itemView.findViewById(R.id.novel_iv_novel);
            title = itemView.findViewById(R.id.novel_tv_title);
            author  = itemView.findViewById(R.id.novel_tv_author);
            this.onNovelListener = mOnNovelListener;
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String id = mNovelList.get(position).getId();
            onNovelListener.onNovelClick(position,v,id);
        }
    }
    public interface OnNovelListener{
        void onNovelClick(int position, View view, String id);
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
