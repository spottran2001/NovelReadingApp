package com.huawei.hms.novelreadingapp.ui.wishlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.huawei.hms.novelreadingapp.adapter.WishlistAdapter;
import com.huawei.hms.novelreadingapp.databinding.FragmentWishlistBinding;
import com.huawei.hms.novelreadingapp.model.Novel;
import com.huawei.hms.novelreadingapp.model.Wishlist;
import com.huawei.hms.novelreadingapp.ui.auth.LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class WishlistFragment extends Fragment {

    private WishlistViewModel wishlistViewModel;
    private FragmentWishlistBinding binding;
    private List<Novel> mWishlist;
    private WishlistAdapter adapter;
    private RecyclerView recyclerView;
    private TextView items;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        wishlistViewModel =
                new ViewModelProvider( this ).get( WishlistViewModel.class );

        binding = FragmentWishlistBinding.inflate( inflater, container, false );
        View root = binding.getRoot();
        matching();

//        assert getArguments() != null;
//
//        String userID = getArguments().getString("userId");

        items = binding.wishlistTvItems;

        recyclerView = binding.wishlistRvProducts;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalLayoutManager);


        getWishlist(LoginActivity.getAccount().getOpenId());

        return root;
    }
    private void matching(){

    }
    private void getWishlist(String id){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Wishlist").child(id);

        List<HashMap<String,String>> wishlistOptions = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dtShot : snapshot.getChildren()) {
                    Wishlist wishlist = dtShot.getValue(Wishlist.class);
                    assert wishlist != null;
                    getNovels(wishlist.getNovel_id(), wishlist.getChapter_id());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void getNovels(String novel_id, String chapter_id){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Novels");

        mWishlist = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dtShot : snapshot.getChildren()) {
                    Novel novel = dtShot.getValue(Novel.class);
                    assert novel != null;
                    if(novel.getId().equals(novel_id)){
                        novel.setChapter_read(chapter_id);
                        mWishlist.add(novel);
                    }
                }
                adapter = new WishlistAdapter(getContext(),mWishlist, items);
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
}