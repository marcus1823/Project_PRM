package com.example.menaccessoriesshop.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.data.model.User;
import com.example.menaccessoriesshop.data.repository.UserRepository;
import com.example.menaccessoriesshop.data.repository.UserService;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText edtFullname, edtEmail, edtPassword, edtPhone, edtAddress;
    private Button btnSignUp, tvSignUp;

    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edtFullname = findViewById(R.id.edtFullname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvSignUp = findViewById(R.id.tvSignUp);

        userService = UserRepository.getUserService();
        Intent intent = getIntent();
        if (intent != null) {
            String receivedName = intent.getStringExtra("user_name");
            String receivedEmail = intent.getStringExtra("user_email");

            if (receivedName != null && receivedEmail != null) {
                edtFullname.setText(receivedName);
                edtEmail.setText(receivedEmail);
                edtFullname.setEnabled(false);
                edtEmail.setEnabled(false);
            }
        }
        btnSignUp.setOnClickListener(v -> validateAndSignUp());
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void validateAndSignUp() {
        String fullname = edtFullname.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();


        // Kiểm tra input không để trống
        if (TextUtils.isEmpty(fullname) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng email hợp lệ
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra số điện thoại hợp lệ (ít nhất 10 số)
        if (phone.length() < 10 || !phone.matches("\\d+")) {
            Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra email đã tồn tại chưa
        checkEmailExists(email, () -> {
            // Nếu email chưa tồn tại, tiến hành đăng ký
            registerUser(fullname, email, password, phone, address);
        });
    }

    private void checkEmailExists(String email, Runnable onSuccess) {
        userService.getUserByEmail(email).enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(Call<User[]> call, Response<User[]> response) {
                if (response.isSuccessful() && response.body() != null && response.body().length > 0) {
                    // Email đã được đăng ký
                    Toast.makeText(SignupActivity.this, "Email đã được đăng ký trước, hãy thử lại", Toast.LENGTH_SHORT).show();
                } else {
                    // Email chưa có, tiếp tục đăng ký
                    onSuccess.run();
                }
            }

            @Override
            public void onFailure(Call<User[]> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                Log.e("SignUp", "Lỗi kiểm tra email", t);
            }
        });
    }

    private void registerUser(String fullname, String email, String password, String phone, String address) {
        String userId = UUID.randomUUID().toString();
        User newUser = new User(userId, fullname, email, password, Long.parseLong(phone), address, "user");

        userService.createUser(newUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                Log.e("SignUp", "Lỗi đăng ký", t);
            }
        });
    }
}
