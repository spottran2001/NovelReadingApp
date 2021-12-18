package com.huawei.hms.novelreadingapp.ui.read;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.huawei.hms.novelreadingapp.R;
import com.huawei.hms.novelreadingapp.model.Chapter;
import com.huawei.hms.novelreadingapp.model.Novel;

import java.util.ArrayList;

public class ReadActivity extends AppCompatActivity {
    TextView title, content;
    Button previous,previous2, next,next2;
    Spinner spinner,spinner2;
    ImageButton back;
    ArrayList<Chapter> mChapters;
    private String currentChapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        matching();
        Intent intent = getIntent();
        String novelId = intent.getStringExtra("novelId");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        String id = intent.getStringExtra("id");
        currentChapter= id;
        getNovel(novelId,currentChapter);
    }
    private void getNovel(String novelId, String chapterId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Novels").child(novelId);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Novel novel = snapshot.getValue(Novel.class);
                assert novel != null;
                novel.setId(snapshot.getKey());
                getChapters(novel.getId());
                getChapter(novel.getId(),chapterId);

                title.setText(novel.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getChapters(String id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Chapter").child(id);
        ArrayList<String> chapters = new ArrayList<>();
        mChapters = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dtShot : snapshot.getChildren()) {
                    Chapter chapter = dtShot.getValue(Chapter.class);
                    assert chapter != null;
                    chapter.setId(dtShot.getKey());
                    chapter.setNovelId(id);
                    mChapters.add(chapter);
                    chapters.add("Chapter "+String.valueOf(chapter.getChapter()));
                }
                previous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int i = 0 ; i< mChapters.size() ; i ++){
                            if(mChapters.get(i).getId().equals(currentChapter)){
                                if(i-1 >= 0){
                                    currentChapter =  mChapters.get(i-1).getId();
                                    break;
                                }
                            }
                        }

                    }
                });
                previous2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int i = 0 ; i< mChapters.size() ; i ++){
                            if(mChapters.get(i).getId().equals(currentChapter)){
                                if(i-1 >= 0){
                                    currentChapter =  mChapters.get(i-1).getId();
                                    break;
                                }
                            }
                        }

                    }
                });
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int i = 0 ; i< mChapters.size() ; i ++){
                            if(mChapters.get(i).getId().equals(currentChapter)){
                                if(i+1 < mChapters.size()){
                                    currentChapter =  mChapters.get(i+1).getId();
                                    break;
                                }
                            }
                        }

                    }
                });
                next2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int i = 0 ; i< mChapters.size() ; i ++){
                            if(mChapters.get(i).getId().equals(currentChapter)){
                                if(i+1 < mChapters.size()){
                                    currentChapter =  mChapters.get(i+1).getId();
                                    break;
                                }
                            }
                        }

                    }
                });
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item, chapters);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner2.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String text = parent.getItemAtPosition(position).toString();
                        for (DataSnapshot dtShot : snapshot.getChildren()) {
                            Chapter chapter = dtShot.getValue(Chapter.class);
                            assert chapter != null;
                            chapter.setId(dtShot.getKey());
                            if(chapter.getChapter() == (Integer.parseInt(text.substring(7)))){
                                currentChapter = chapter.getId();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // sometimes you need nothing here
                    }
                });
                spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String text = parent.getItemAtPosition(position).toString();
                        for (DataSnapshot dtShot : snapshot.getChildren()) {
                            Chapter chapter = dtShot.getValue(Chapter.class);
                            assert chapter != null;
                            chapter.setId(dtShot.getKey());
                            if(chapter.getChapter() == (Integer.parseInt(text.substring(7)))){
                                currentChapter = chapter.getId();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // sometimes you need nothing here
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getChapter(String novelId, String id){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Chapter").child(novelId).child(id);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chapter chapter = snapshot.getValue(Chapter.class);
                assert chapter != null;
                chapter.setId(snapshot.getKey());

                content.setText(chapter.getContent());
                currentChapter = chapter.getId();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void matching(){
        back= findViewById(R.id.read_ib_back);
        title = findViewById(R.id.read_tv_title);
        content = findViewById(R.id.read_tv_content);
        previous = findViewById(R.id.read_btn_previous);
        previous2 = findViewById(R.id.read_btn_previous_2);
        next = findViewById(R.id.read_btn_next);
        next2 = findViewById(R.id.read_btn_next_2);
        spinner = findViewById(R.id.read_spinner_chapter);
        spinner2 = findViewById(R.id.read_spinner_chapter_2);
    }

}