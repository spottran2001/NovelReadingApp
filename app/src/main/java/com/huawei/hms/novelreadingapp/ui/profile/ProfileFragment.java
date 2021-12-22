package com.huawei.hms.novelreadingapp.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.huawei.hms.novelreadingapp.databinding.FragmentProfileBinding;
import com.huawei.hms.novelreadingapp.ui.auth.LoginActivity;
import com.huawei.hms.support.account.result.AuthAccount;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    ImageView profile_photo;
    TextView tv_detail;
    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider( this ).get( ProfileViewModel.class );

        binding = FragmentProfileBinding.inflate( inflater, container, false );
        View root = binding.getRoot();


//        Intent intent = getActivity().getIntent();
//        String email = intent.getStringExtra("email");
//        String name = intent.getStringExtra("name");

        AuthAccount account = LoginActivity.getAccount();
        String avt = account.getAvatarUriString();
        String email = account.getEmail();
        String name = account.getDisplayName();
        String userID = account.getOpenId();


        tv_detail = binding.tvProfileUserName;
        tv_detail.setText("Hi, "+name);
        profile_photo = binding.profileIvAvatar;
        Glide.with(ProfileFragment.this)
                .load(getActivity().getIntent().getStringExtra("avt"))
                .into(profile_photo);
        LoginActivity login = new LoginActivity();
        binding.profileBtnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.signOut();
                Intent intent = new Intent(requireContext(),LoginActivity.class);
                requireContext().startActivity(intent);
                requireActivity().finish();
            }
        });
        binding.profileBtnCancelAuthorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.cancelAuthorization();
                Intent intent = new Intent(requireContext(),LoginActivity.class);
                requireContext().startActivity(intent);
                requireActivity().finish();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}