package com.huawei.hms.novelreadingapp.ui.read;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    ImageButton dropdown1, dropdown2;
    Spinner spinner,spinner2;
    ImageButton back;
    ArrayList<Chapter> mChapters;
    private String currentChapter;
    private int size;
    private String chapterId, novelId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        matching();
        Intent intent = getIntent();
        novelId = intent.getStringExtra("novelId");
        chapterId = intent.getStringExtra("chapterId");
        size = intent.getIntExtra( "size",0);
        String abc = novelId + chapterId + size;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


        getNovel(novelId,chapterId);
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
                        previousChapter(chapterId, size);

                    }
                });
                previous2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        previousChapter(chapterId, size);
                    }
                });
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextChapter(chapterId, size);

                    }
                });
                next2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextChapter(chapterId, size);

                    }
                });
                dropdown1.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        spinner.performClick();
                    }
                } );
                dropdown2.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        spinner2.performClick();
                    }
                } );
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item, chapters);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner2.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int text = Integer.parseInt(parent.getItemAtPosition(position).toString().substring(8)) ;
                        if (text <= size){
                            chapterId = "C" + text;
                            getChapter(novelId, chapterId);
                            spinner2.setSelection(text - 1);
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
                        int text = Integer.parseInt( parent.getItemAtPosition(position).toString().substring(8) ) ;
                        if (text <= size){
                            chapterId = "C" + text;
                            getChapter(novelId, chapterId);
                            spinner.setSelection(text - 1);
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
                String[] text = chapter.getContent().split( "  ");
                String contents = "";
                for (int i = 0; i < text.length; i ++){
                    contents += text[i] + "\n\n";
                }
                content.setText(contents);
                currentChapter = chapter.getId();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void nextChapter(String chapter_id, int size){
        int chapter = Integer.valueOf(chapter_id.substring(1));
        if (chapter < size){
            chapterId = "C"+ (chapter + 1);
            getChapter(novelId, chapterId);
        }else{
            if (chapter == size){
                chapterId = "C"+ (chapter + 1);
            }
            content.setText("Chapter is incoming, please comeback in the other time");
        }

    }

    private void previousChapter(String chapter_id, int size){
        int chapter = Integer.valueOf(chapter_id.substring(1));
        if (chapter - 1 > 0){
            chapterId = "C" + (chapter - 1);
            getChapter(novelId, chapterId);
        }
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
        dropdown1 = findViewById(R.id.read_ib_drop);
        dropdown2 = findViewById(R.id.read_ib_drop_2);
    }

}