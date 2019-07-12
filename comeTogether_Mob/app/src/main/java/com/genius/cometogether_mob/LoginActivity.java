package com.genius.cometogether_mob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    EditText edit_text_email,edit_text_password;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // initialize UI
        initUI();
    }

    private void initUI()
    {
        // Set a listener on buttons
        findViewById(R.id.button_register_student).setOnClickListener(this);
        findViewById(R.id.button_register_organi).setOnClickListener(this);
        findViewById(R.id.button_sign_in).setOnClickListener(this);
        progressBar = findViewById(R.id.progressbar);
        edit_text_email = findViewById(R.id.edit_text_email);
        edit_text_password = findViewById(R.id.edit_text_password);
    }

    @Override
    public void onClick(View view)
    {
        Intent intent = new Intent();

        switch (view.getId())
        {
            // Student registration
            case R.id.button_register_student:
            {
                intent.setClass(getApplicationContext(), StudentRegActivity.class);
                startActivity(intent);
                break;
            }

            // Organization registration
            case R.id.button_register_organi:
            {
                intent.setClass(getApplicationContext(), OrganizationRegActivity.class);
                startActivity(intent);
                break;
            }

            // Sign in
            case R.id.button_sign_in:
            {
                userLogin();
                break;
            }

        }
    }

    private void userLogin()
    {
        String email = edit_text_email.getText().toString().trim();
        String password = edit_text_password.getText().toString().trim();

        if (email.isEmpty())
        {
            edit_text_email.setError("Email address is required!");
            edit_text_email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            edit_text_email.setError("Please enter a valid email address");
            edit_text_email.requestFocus();
            return;
        }

        if (password.isEmpty())
        {
            edit_text_password.setError("Password is required!");
            edit_text_password.requestFocus();
            return;
        }

        if (password.length() < 6)
        {
            edit_text_password.setError("Minimum length of password should be 6.");
            edit_text_password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful())
                {
                    Intent intent = new Intent(LoginActivity.this, ExploreActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
