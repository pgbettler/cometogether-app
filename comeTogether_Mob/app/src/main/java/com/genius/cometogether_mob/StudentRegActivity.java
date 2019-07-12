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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentRegActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressBar progressBar;
    EditText edit_text_email, edit_text_password, edit_text_firstName, edit_text_lastName;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_reg);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI
        initUI();
    }

    private void initUI()
    {
        findViewById(R.id.btn_back_to_login).setOnClickListener(this);
        findViewById(R.id.btn_create_std_acct).setOnClickListener(this);
        progressBar = findViewById(R.id.progressbar);
        edit_text_email = findViewById(R.id.edit_text_email);
        edit_text_password = findViewById(R.id.edit_text_password);
        edit_text_firstName = findViewById(R.id.edit_text_firstName);
        edit_text_lastName = findViewById(R.id.edit_text_lastName);
    }

    @Override
    public void onClick(View view)
    {
        Intent intent = new Intent();

        switch(view.getId())
        {
            case R.id.btn_back_to_login:
            {
                intent.setClass(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.btn_create_std_acct:
            {
                registerStudent();
                break;
            }
        }

    }

    private void registerStudent()
    {
        String email = edit_text_email.getText().toString().trim();
        String password = edit_text_password.getText().toString().trim();
        String firstName = edit_text_firstName.getText().toString().trim();
        String lastName = edit_text_lastName.getText().toString().trim();

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

        if (firstName.isEmpty())
        {
            edit_text_firstName.setError("Please enter your first name.");
            edit_text_firstName.requestFocus();
            return;
        }

        if (lastName.isEmpty())
        {
            edit_text_lastName.setError("Please enter your last name.");
            edit_text_lastName.requestFocus();
            return;
        }

        // Store to user collection
        final CollectionReference dbStudent = db.collection("users");
        // Set up constructor
        final User student = new User(
                "Student",
                firstName,
                lastName,
                "",
                ""
        );

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful())
                {
                    // Retrieve the User UID
                    String userId = mAuth.getUid();
                    // Store this student to "users" collection using this user's UID
                    dbStudent.document(userId).set(student);

                    Toast.makeText(getApplicationContext(), "Student successfully registered", Toast.LENGTH_LONG).show();
                }
                else
                {
                    // Account already registered
                    if (task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(getApplicationContext(), "This account already registered.", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Registration failed.", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

    }
}
