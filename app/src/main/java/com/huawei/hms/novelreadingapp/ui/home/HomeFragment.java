package com.huawei.hms.novelreadingapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.huawei.hms.ads.InterstitialAd;
import com.huawei.hms.novelreadingapp.adapter.NovelListAdapter;
import com.huawei.hms.novelreadingapp.databinding.FragmentHomeBinding;
import com.huawei.hms.novelreadingapp.model.Novel;
import com.huawei.hms.novelreadingapp.ui.detail.DetailActivity;

import java.util.ArrayList;
import java.util.Random;

public class HomeFragment extends Fragment  implements NovelListAdapter.OnNovelListener {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    RecyclerView recyclerView;
    private ArrayList<Novel> mNovels;
    private NovelListAdapter adapter;
    private InterstitialAd interstitialAd;

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

        getProduct();


        //ads-kit
//        HwAds.init(getActivity());
//        loadInterstitialAd();





        return root;
    }
    private void getProduct() {
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