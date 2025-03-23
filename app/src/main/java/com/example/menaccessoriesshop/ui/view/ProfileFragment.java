package com.example.menaccessoriesshop.ui.view;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.data.model.User;
import com.example.menaccessoriesshop.data.repository.UserRepository;
import com.example.menaccessoriesshop.data.repository.UserService;
import com.example.menaccessoriesshop.ui.viewmodel.ProfileViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private TextView tvFullname, tvEmail, tvPhone, tvAddress;
    private UserService userService;
    private ProfileViewModel mViewModel;
    Button btnLogout, btnEditProfile, btnOrderHistory;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvFullname = view.findViewById(R.id.tvFullname);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvAddress = view.findViewById(R.id.tvAddress);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnOrderHistory = view.findViewById(R.id.btnOrderHistory);
        btnEditProfile = view.findViewById(R.id.btnUpdateProfile);
        userService = UserRepository.getUserService();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", null);

        if (userID != null) {
            getUserInfo(userID);
        } else {
            Toast.makeText(getContext(), "User ID not found", Toast.LENGTH_SHORT).show();
        }
        btnOrderHistory.setOnClickListener(v -> {
            if (userID != null) {
                Intent intent = new Intent(getActivity(), UserOrderActivity.class);
                intent.putExtra("userID", userID); // Truyền userID vào Intent
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "User ID not found", Toast.LENGTH_SHORT).show();
            }
        });
        btnEditProfile.setOnClickListener(v -> {
            updateUserInformation(userID);
        });
        btnLogout.setOnClickListener(v -> {
            logout();
        });
    }
    private void updateUserInformation(String userID) {
        Call<User> call = userService.getUserById(userID);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    // Hiển thị thông tin người dùng trong các EditText
                    tvFullname.setText(user.getFullName());
                    tvEmail.setText(user.getEmail());
                    tvPhone.setText(String.valueOf(user.getPhone()));
                    tvAddress.setText(user.getAddress());

                    // Thêm sự kiện khi người dùng nhấn nút "Save"
                    btnEditProfile.setOnClickListener(v -> {
                        String fullname = tvFullname.getText().toString().trim();
                        String phoneStr = tvPhone.getText().toString().trim();
                        if (!fullname.matches("^[a-zA-Z\\s]+$")) {
                            Toast.makeText(getContext(), "Họ và tên chỉ được phép chứa chữ cái và khoảng trắng", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (phoneStr.isEmpty() || !phoneStr.matches("\\d+")) {
                            Toast.makeText(getContext(), "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        user.setFullName(tvFullname.getText().toString());
                        user.setEmail(tvEmail.getText().toString());
                        user.setPhone(Integer.parseInt(tvPhone.getText().toString()));
                        user.setAddress(tvAddress.getText().toString());

                        Call<User> updateCall = userService.updateUser(userID, user);
                        updateCall.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Toast.makeText(getContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();

                                    // Cập nhật lại thông tin trên giao diện
                                    tvFullname.setText(user.getFullName());
                                    tvEmail.setText(user.getEmail());
                                    tvPhone.setText(String.valueOf(user.getPhone()));
                                    tvAddress.setText(user.getAddress());
                                } else {
                                    Toast.makeText(getContext(), "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    });
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    String userID = sharedPreferences.getString("userID", null);
                    // Hiển thị dialog hoặc fragment để người dùng chỉnh sửa thông tin
                    getUserInfo(userID);
                } else
                    Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void getUserInfo(String userID) {
        Call<User> call = userService.getUserById(userID);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    tvFullname.setText(user.getFullName());
                    tvEmail.setText(user.getEmail());
                    tvPhone.setText(String.valueOf(user.getPhone()));
                    tvAddress.setText(user.getAddress());
                } else
                    Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logout() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(getContext(), "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}