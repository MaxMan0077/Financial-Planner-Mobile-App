package com.example.financialplanner02;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerUsernameEditText;
    private EditText registerPasswordEditText;
    private TextView errorTextView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);

        registerUsernameEditText = findViewById(R.id.registerUsernameEditText);
        registerPasswordEditText = findViewById(R.id.registerPasswordEditText);
        errorTextView = findViewById(R.id.errorTextView);
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = registerUsernameEditText.getText().toString().trim();
                String password = registerPasswordEditText.getText().toString().trim();

                if (databaseHelper.usernameExists(username)) {
                    showError("Error: Username already exists. Please choose another one.");
                } else if (isValidUsername(username) && isValidPassword(password)) {
                    User user = new User(username, password);
                    long userId = databaseHelper.addUser(user.getUsername(), user.getPassword());
                    if (userId > -1) {
                        Toast.makeText(RegisterActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        showError("Error: Failed to create account. Please try again.");
                    }
                } else {
                    showError("Error: Password must be more than 8 characters long, and contain both letters and numbers.");
                }
            }
        });

    }

    private boolean isValidUsername(String username) {
        return !TextUtils.isEmpty(username) && !databaseHelper.checkUserExists(username);
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        Pattern letterPattern = Pattern.compile("[a-zA-Z]");
        Pattern digitPattern = Pattern.compile("[0-9]");

        return letterPattern.matcher(password).find() && digitPattern.matcher(password).find();
    }

    private void showError(String errorMessage) {
        errorTextView.setText(errorMessage);
    }

    private void showSuccessAndRedirect() {
        Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
