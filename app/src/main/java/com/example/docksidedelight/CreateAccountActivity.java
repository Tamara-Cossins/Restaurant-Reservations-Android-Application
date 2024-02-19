package com.example.docksidedelight;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;

public class CreateAccountActivity extends AppCompatActivity {
    // Activity to create a new account, save details into shared preferences (local storage)

    // Temporary class to organise user data into a structured object, making it easier to serialise and de-serialise when retrieving from sharedprefs
    public class CreatedUser {
        private String email;
        private String password;
        private String customerName;
        private String customerPhoneNumber;

        // Constructor
        public CreatedUser(String email, String password, String customerName, String customerPhoneNumber) {
            this.email = email;
            this.password = password;
            this.customerName = customerName;
            this.customerPhoneNumber = customerPhoneNumber;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        EditText email_EditText = findViewById(R.id.input_email);
        EditText name_EditText = findViewById(R.id.input_name);
        EditText phone_EditText = findViewById(R.id.input_phone_number);
        EditText password_EditText = findViewById(R.id.input_password);
        Button create_account_btn = findViewById(R.id.create_account_button);

        // When create_account_button is clicked, it stores the data into local storage using sharedpreferences. Email as unique key, rest of details as JSON string
        create_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Takes all inputted data into variables
                String email = email_EditText.getText().toString();
                String name = name_EditText.getText().toString();
                String phone_number = phone_EditText.getText().toString();
                String password = password_EditText.getText().toString();

                // If all mandatory fields have inputted data, continue
                if (!email.isEmpty() && !name.isEmpty() && !phone_number.isEmpty() && !password.isEmpty()){
                    // Uses CreatedUser class to structure user details
                    CreatedUser user = new CreatedUser(email, password, name, phone_number);

                    // Serialise user details to JSON
                    Gson gson = new Gson();
                    String userDetails = gson.toJson(user);

                    // Save to sharedpreferences
                    SharedPreferences sharedpreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    // Saves details into local storage; unique email as key, userDetails (JSON string) as corresponding value
                    editor.putString(email, userDetails);
                    editor.apply();

                    // DIALOG TO COLLECT CONSENT FOR DATA COLLECTION

                    displayPrivacyPolicyConsentDialog();

                }

                // If any EditText fields are empty, a toast warns the user and prevents account creation
                else {
                    CharSequence text = "Please input all profile details";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(CreateAccountActivity.this, text, duration);
                    toast.show();
                }
            }
        });
    }

    private void displayPrivacyPolicyConsentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Privacy Policy Consent");

        String message = "Please read and accept our Privacy Policy and Terms of Use to continue. By accepting, you agree to the collection and use of your information in accordance with our policy.";
        builder.setMessage(message);

        // Accept button to agree to the privacy policy
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User accepted the policy, proceed with account creation or other actions
                // Upon creating the account and storing details into local storage, it navigates to DashboardActivity
                Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Decline button to disagree with the privacy policy
        builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                CharSequence text = "Account not created.";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(CreateAccountActivity.this, text, duration);
                toast.show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}