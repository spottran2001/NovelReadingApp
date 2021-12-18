package com.huawei.hms.novelreadingapp.ui.home;

import android.content.Intent;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.huawei.hms.novelreadingapp.R;
import com.huawei.hms.novelreadingapp.adapter.NovelListAdapter;
import com.huawei.hms.novelreadingapp.databinding.FragmentHomeBinding;
import com.huawei.hms.novelreadingapp.model.Novel;
import com.huawei.hms.novelreadingapp.ui.detail.DetailActivity;

import java.util.ArrayList;

public class HomeFragment extends Fragment  implements NovelListAdapter.OnNovelListener {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    RecyclerView recyclerView;
    private ArrayList<Novel> mNovels;
    private NovelListAdapter adapter;

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
                    Novel product = dtShot.getValue(Novel.class);
                    assert product != null;
                    product.setId(dtShot.getKey());
                    mNovels.add(product);
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
}