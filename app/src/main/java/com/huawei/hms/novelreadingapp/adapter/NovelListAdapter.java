package com.huawei.hms.novelreadingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.novelreadingapp.R;
import com.huawei.hms.novelreadingapp.model.Novel;

import java.util.List;

public class NovelListAdapter extends RecyclerView.Adapter<NovelListAdapter.ViewHolder>{


    private Context context;
    private static List<Novel> mNovelList;
    private NovelListAdapter.OnNovelListener mOnNovelListener;


    public NovelListAdapter(Context context, List<Novel> mNovelList, NovelListAdapter.OnNovelListener OnNovelListener){
        this.context = context;
        this.mNovelList = mNovelList;
        this.mOnNovelListener = OnNovelListener;
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
        OnNovelListener onNovelListener;
        public ViewHolder(@NonNull View itemView, OnNovelListener mOnNovelListener) {
            super(itemView);
            cover = itemView.findViewById(R.id.novel_iv_novel);
            title = itemView.findViewById(R.id.novel_tv_title);
            author  = itemView.findViewById(R.id.novel_tv_author);
            this.onNovelListener = mOnNovelListener;
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
}
