package com.huawei.hms.novelreadingapp.ui.detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.huawei.hms.novelreadingapp.R;
import com.huawei.hms.novelreadingapp.adapter.ChapterAdapter;
import com.huawei.hms.novelreadingapp.adapter.NovelListAdapter;
import com.huawei.hms.novelreadingapp.model.Chapter;
import com.huawei.hms.novelreadingapp.model.Novel;
import com.huawei.hms.novelreadingapp.ui.home.HomeFragment;
import com.huawei.hms.novelreadingapp.ui.read.ReadActivity;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    TextView title, author, totalChapters;
    ImageView cover;
    Button readLatest;
    ImageButton back;
    RecyclerView recyclerView;
    ArrayList<Chapter> mChapters;
    private ChapterAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        matching();
        Intent intent = getIntent();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        recyclerView.setLayoutManager(verticalLayoutManager);

        String id = intent.getStringExtra("id");
        getNovel(id);
    }
    private void getNovel(String id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Novels").child(id);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Novel novel = snapshot.getValue(Novel.class);
                assert novel != null;
                novel.setId(snapshot.getKey());
                title.setText(novel.getName());
                author.setText(novel.getAuthor());
                totalChapters.setText(String.valueOf(novel.getChapter_quantity()));
                getChapters(novel.getId());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getChapters(String id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Chapter").child(id);
         mChapters = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChapters.clear();
                for (DataSnapshot dtShot : snapshot.getChildren()) {
                    Chapter chapter = dtShot.getValue(Chapter.class);
                    assert chapter != null;
                    chapter.setId(dtShot.getKey());
                    chapter.setNovelId(id);
                    mChapters.add(chapter);
                }
                readLatest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ReadActivity.class);

                        intent.putExtra("novelId",id);
                        intent.putExtra("id", mChapters.get(mChapters.size()-1).getId());
                        startActivity(intent);

                    }
                });

                adapter = new ChapterAdapter(getApplicationContext(), mChapters);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void matching(){
         title = findViewById(R.id.detail_tv_title);
         author = findViewById(R.id.detail_tv_author);
         totalChapters = findViewById(R.id.detail_tv_totalChapter);
         cover = findViewById(R.id.detail_iv_cover);
         readLatest = findViewById(R.id.detail_btn_readLatest);
         back = findViewById(R.id.detail_ib_back);
         recyclerView = findViewById(R.id.detail_rv_chapters);
    }

}