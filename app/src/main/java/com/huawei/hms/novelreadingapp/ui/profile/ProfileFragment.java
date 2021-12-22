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

import com.huawei.hms.novelreadingapp.databinding.FragmentProfileBinding;
import com.huawei.hms.novelreadingapp.ui.auth.LoginActivity;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    TextView tv_detail;
    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider( this ).get( ProfileViewModel.class );

        binding = FragmentProfileBinding.inflate( inflater, container, false );
        View root = binding.getRoot();


        assert getArguments() != null;
        String avt = getArguments().getString("avt");
        String email = getArguments().getString("email");
        String name = getArguments().getString("name");
//        String userID = getArguments().getString("userId");


        tv_detail = binding.tvProfileUserName;
        tv_detail.setText("Hi, "+name+"\n"+email);

        new DownloadImageTask((ImageView) binding.profileIvAvatar)
                .execute(avt);
        LoginActivity login = new LoginActivity();
        binding.profileBtnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.signOut();
                Intent intent = new Intent(requireContext(),LoginActivity.class);
                requireContext().startActivity(intent);
                requireActivity().finish();
            }
        });
        binding.profileBtnCancelAuthorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.cancelAuthorization();
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