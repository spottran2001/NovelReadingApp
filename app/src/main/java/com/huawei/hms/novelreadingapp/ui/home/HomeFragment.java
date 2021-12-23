package com.huawei.hms.novelreadingapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.InterstitialAd;
import com.huawei.hms.novelreadingapp.VNCharacterUtils;
import com.huawei.hms.novelreadingapp.adapter.NovelListAdapter;
import com.huawei.hms.novelreadingapp.databinding.FragmentHomeBinding;
import com.huawei.hms.novelreadingapp.model.Novel;
import com.huawei.hms.novelreadingapp.ui.detail.DetailActivity;
import com.huawei.hms.novelreadingapp.ui.search.SearchActivity;

import java.util.ArrayList;
import java.util.Random;

public class HomeFragment extends Fragment  implements NovelListAdapter.OnNovelListener {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    RecyclerView recyclerView;
    private ArrayList<Novel> mNovels;
    private NovelListAdapter adapter;
    private InterstitialAd interstitialAd;
    private String userId =null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider( this ).get( HomeViewModel.class );

        binding = FragmentHomeBinding.inflate( inflater, container, false );
        View root = binding.getRoot();

        recyclerView = binding.homeRvNovels;
        //recycler view
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

//        assert        () != null;
//        userId= getArguments().getString("userId");

        final AutoCompleteTextView search = binding.homeTvSearch;
        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1);

        //connect
        FirebaseDatabase database = FirebaseDatabase.getInstance();

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

        search.setAdapter(adapter);

        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra("keySearch", search.getText().toString());
                startActivity(intent);
                search.setText("");

            }
        });
        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (search.getRight() - search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width() - 50) && !search.getText().toString().matches("")) {

                        @SuppressLint("ClickableViewAccessibility") Intent intent = new Intent(getContext(), SearchActivity.class);
                        intent.putExtra("keySearch", search.getText().toString());
                        startActivity(intent);

                        return true;

                    }
                }
                return false;
            }
        });




        getNovel();


        //ads-kit
        HwAds.init(getActivity());
        loadInterstitialAd();





        return root;
    }
    private void getNovel() {
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
                    novel.setId(dtShot.getKey());
                    mNovels.add(novel);
                }
                adapter = new NovelListAdapter(getContext(), mNovels, HomeFragment.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onNovelClick(int position, View view, String id) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("email", userId);
        startActivity(intent);
    }

    private void loadInterstitialAd(){
        interstitialAd = new InterstitialAd(getActivity());
        interstitialAd.setAdId(getAdID());
        interstitialAd.setAdListener(adListener);

        AdParam adParam = new AdParam.Builder().build();
        interstitialAd.loadAd(adParam);
    }

    private String getAdID(){
        Random rand = new Random();
        int int_rand = rand.nextInt(2);
        if (int_rand == 0){
            return  "teste9ih9j0rc3";
        }
        else {
            return "testb4znbuh3n2";
        }
    }

    private AdListener adListener = new AdListener(){
        @Override
        public void onAdLoaded(){
            super.onAdLoaded();
            Toast.makeText(getContext(),"Ad loaded",Toast.LENGTH_SHORT).show();

            showInTerstitial();
        }

        @Override
        public void onAdFailed(int err){
            Toast.makeText(getContext(),"Ad load failed with error code: "+ err,
                            Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdClosed(){
            super.onAdClosed();
        }

        @Override
        public void onAdClicked(){
            super.onAdClicked();
        }

        @Override
        public void onAdOpened(){
            super.onAdOpened();
        }
    };

    private void showInTerstitial(){
        if (interstitialAd != null && interstitialAd.isLoaded()){
            interstitialAd.show();
        }else {
            Toast.makeText(getContext(),"Ad did not load", Toast.LENGTH_SHORT).show();
        }
    }

}