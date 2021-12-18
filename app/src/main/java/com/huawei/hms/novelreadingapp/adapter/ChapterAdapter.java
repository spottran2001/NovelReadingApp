package com.huawei.hms.novelreadingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.novelreadingapp.R;
import com.huawei.hms.novelreadingapp.model.Chapter;
import com.huawei.hms.novelreadingapp.model.Novel;
import com.huawei.hms.novelreadingapp.ui.read.ReadActivity;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder>{

    private Context context;
    private static List<Chapter> mChapters;

    public ChapterAdapter(Context context, List<Chapter> mChapters){
        this.context = context;
        this.mChapters = mChapters;
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
        Chapter chapter = mChapters.get(holder.getAdapterPosition());
        String title = chapter.getChapter() + " " + chapter.getTitle();
        holder.title.setText(title);
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReadActivity.class);

                intent.putExtra("novelId", chapter.getNovelId());
                intent.putExtra("id", chapter.getId());

                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mChapters ==null? 0: mChapters.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        Button title;
        ImageButton save;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.chapter_btn_chapter);
            save = itemView.findViewById(R.id.chapter_btn_heart);

        }
    }
}
