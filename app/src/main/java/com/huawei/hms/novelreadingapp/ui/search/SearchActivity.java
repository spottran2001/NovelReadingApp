package com.huawei.hms.novelreadingapp.ui.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.huawei.hms.novelreadingapp.R;
import com.huawei.hms.novelreadingapp.VNCharacterUtils;
import com.huawei.hms.novelreadingapp.adapter.NovelListAdapter;
import com.huawei.hms.novelreadingapp.adapter.SearchAdapter;
import com.huawei.hms.novelreadingapp.model.Novel;
import com.huawei.hms.novelreadingapp.ui.auth.LoginActivity;
import com.huawei.hms.novelreadingapp.ui.detail.DetailActivity;
import com.huawei.hms.novelreadingapp.ui.home.HomeFragment;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.OnNovelListener {
    SearchAdapter adapter;
    ArrayList<Novel> mNovels;
    ImageButton prevBtn;
    AutoCompleteTextView searchView;
    RecyclerView recyclerView;
    String keySearch;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        matching();
        recyclerView.setHasFixedSize(true);
        keySearch = getIntent().getStringExtra("keySearch");


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);

        FirebaseDatabase database = FirebaseDatabase.getInstance();


        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1);
        //get firebase
        //connect
        DatabaseReference myRef = database.getReference("Novels");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dtShot : snapshot.getChildren()) {
                    Novel novel = dtShot.getValue(Novel.class);
                    assert novel != null;
                    novel.setId(dtShot.getKey());
                    String name = VNCharacterUtils.removeAccent(novel.getName());
                    adapter.add(novel.getName());
                    adapter.add(name);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchView.setAdapter(adapter);

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                keySearch = searchView.getText().toString();
                getNovel(keySearch);

                searchView.setText("");

            }
        });

        searchView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                @SuppressLint("ClickableViewAccessibility") final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (searchView.getRight() - searchView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width() - 50) && !searchView.getText().toString().matches("")) {
                        keySearch = searchView.getText().toString();
                        getNovel(keySearch);

                        return true;

                    }
                }
                return false;
            }
        });
        getNovel(keySearch);

    }
    private void getNovel(String keySearch) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Novels");
        mNovels = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mNovels.clear();
                for (DataSnapshot dtShot : snapshot.getChildren()) {
                    Novel novel = dtShot.getValue(Novel.class);
                    assert novel != null;
                    if ( novel.getName().toLowerCase().contains(keySearch.toLowerCase()) || VNCharacterUtils.removeAccent(novel.getName().toLowerCase()).contains(keySearch.toLowerCase())){
                        novel.setId(dtShot.getKey());
                        mNovels.add(novel);
                    }
                }
                adapter = new SearchAdapter(getApplicationContext(), mNovels, SearchActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void matching() {
        prevBtn = (ImageButton) findViewById(R.id.search_ibtn_prev);
        title =(TextView) findViewById(R.id.search_title);
        searchView = findViewById(R.id.search_et_searchView);
        recyclerView = (RecyclerView) findViewById(R.id.search_rv_novels);

    }

    @Override
    public void onNovelClick(int position, View view, String id) {
        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}