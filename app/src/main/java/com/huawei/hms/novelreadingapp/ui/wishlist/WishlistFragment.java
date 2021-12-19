package com.huawei.hms.novelreadingapp.ui.wishlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.huawei.hms.novelreadingapp.adapter.ChapterAdapter;
import com.huawei.hms.novelreadingapp.adapter.WishlistAdapter;
import com.huawei.hms.novelreadingapp.databinding.FragmentWishlistBinding;
import com.huawei.hms.novelreadingapp.model.Chapter;
import com.huawei.hms.novelreadingapp.model.Novel;
import com.huawei.hms.novelreadingapp.ui.detail.DetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WishlistFragment extends Fragment {

    private WishlistViewModel wishlistViewModel;
    private FragmentWishlistBinding binding;
    private List<Novel> mWishlist;
    private WishlistAdapter adapter;
    private RecyclerView recyclerView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        wishlistViewModel =
                new ViewModelProvider( this ).get( WishlistViewModel.class );

        binding = FragmentWishlistBinding.inflate( inflater, container, false );
        View root = binding.getRoot();
        matching();


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalLayoutManager);
        // cần id người dùng
        String id = "1";
        getWishlist(id);

        return root;
    }
    private void matching(){

    }
    private void getWishlist(String id){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Wishlist").child(id);
        mWishlist = new ArrayList<>();
        List<HashMap<String,String>> wishlistOptions = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mWishlist.clear();
                for (DataSnapshot dtShot : snapshot.getChildren()) {
                    Novel novel = dtShot.getValue(Novel.class);
                    assert novel != null;
                    novel.setId(dtShot.getKey());
                    novel.setId(id);
                    mWishlist.add(novel);
                }
                adapter = new WishlistAdapter(getContext(), mWishlist,wishlistOptions);
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