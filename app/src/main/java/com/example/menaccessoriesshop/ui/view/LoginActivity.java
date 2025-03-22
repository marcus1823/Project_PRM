package com.example.menaccessoriesshop.ui.view;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.data.model.User;
import com.example.menaccessoriesshop.data.repository.UserRepository;
import com.example.menaccessoriesshop.data.repository.UserService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin, tvSignUp;
    private UserService userService;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private ImageButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        userService = UserRepository.getUserService();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        //google
        signInButton = findViewById(R.id.sign_in_button);
        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);


            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);


                if (account != null) {

                    updateUI(account);
                }
            } catch (ApiException e) {
                Log.w("Google Sign In", "Sign-in failed", e);
                Toast.makeText(this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            String displayName = account.getDisplayName();
            String email = account.getEmail();

            checkEmailExists(email, user -> {
                if (user != null) {
                    getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().putString("userID", user.getId()).apply();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    intent.putExtra("user_name", displayName);
                    intent.putExtra("user_email", email);
                    startActivity(intent);
                }
            });
        } else {
            Log.d("Google Sign-In", "No user is signed in.");
        }
    }

    private void checkEmailExists(String email, OnUserCheckListener listener) {
        userService.getUserByEmail(email).enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(Call<User[]> call, Response<User[]> response) {
                if (response.isSuccessful() && response.body() != null && response.body().length > 0) {
                    listener.onCheckResult(response.body()[0]); // Trả về user đầu tiên tìm thấy
                } else {
                    listener.onCheckResult(null); // Không tìm thấy user
                }
            }

            @Override
            public void onFailure(Call<User[]> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                listener.onCheckResult(null);
            }
        });
    }

    // Tạo interface mới để trả về User thay vì boolean
    interface OnUserCheckListener {
        void onCheckResult(User user);
    }




    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<User[]> call = userService.getAllUsers();
        call.enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(Call<User[]> call, Response<User[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (User user : response.body()) {
                        if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().putString("userID", user.getId()).apply();
                            Intent intent;
                            if ("admin".equals(user.getRole())) {
                                intent = new Intent(LoginActivity.this, ManageStoreActivity.class);
                            } else if("user".equals(user.getRole())) {
                                intent = new Intent(LoginActivity.this, MainActivity.class);
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid account role", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            startActivity(intent);
                            finish();
                            return;
                        }
                    }
                    Toast.makeText(LoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User[]> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
