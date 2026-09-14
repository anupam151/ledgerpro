package com.cluster.ledgerpro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddContactActivity extends AppCompatActivity {

    private TextInputEditText etContactName, etContactPhone;
    private MaterialButton btnSaveContact;
    private String userEmail;

    private final ActivityResultLauncher<Intent> contactPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri contactUri = result.getData().getData();
                    processPickedContact(contactUri);
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    launchContactPicker();
                } else {
                    Toast.makeText(this, "Permission denied to read contacts.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_contact);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainAddContact), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(0, insets.top, 0, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userEmail = currentUser.getEmail();
        }

        etContactName = findViewById(R.id.etContactName);
        etContactPhone = findViewById(R.id.etContactPhone);
        btnSaveContact = findViewById(R.id.btnSaveContact);
        MaterialCardView cardPickFromDevice = findViewById(R.id.cardPickFromDevice);

        findViewById(R.id.ivBackContact).setOnClickListener(v -> finish());
        cardPickFromDevice.setOnClickListener(v -> checkContactPermissionAndPick());
        btnSaveContact.setOnClickListener(v -> validateAndSaveContact());
    }

    private void checkContactPermissionAndPick() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            launchContactPicker();
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS);
        }
    }

    private void launchContactPicker() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        contactPickerLauncher.launch(contactPickerIntent);
    }

    @SuppressLint("Range")
    private void processPickedContact(Uri contactUri) {
        if (contactUri != null) {
            try (Cursor cursor = getContentResolver().query(contactUri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    if (number != null) {
                        number = number.replaceAll("[^0-9]", "");
                        if (number.length() > 10) {
                            number = number.substring(number.length() - 10);
                        }
                    }

                    etContactName.setText(name);
                    etContactPhone.setText(number);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to read contact.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String generateRandomAlphanumeric() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @SuppressLint("SetTextI18n")
    private void validateAndSaveContact() {
        String name = String.valueOf(etContactName.getText()).trim();
        String phone = String.valueOf(etContactPhone.getText()).trim();

        if (TextUtils.isEmpty(name)) {
            etContactName.setError("Required");
            return;
        }

        if (TextUtils.isEmpty(phone) || phone.length() != 10) {
            etContactPhone.setError("Enter valid 10-digit number");
            return;
        }

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Session error.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSaveContact.setEnabled(false);
        btnSaveContact.setText("Checking...");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Check if phone number already exists in Contacts collection
        db.collection("users").document(userEmail).collection("Contacts")
                .whereEqualTo("phone", phone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        etContactPhone.setError("A contact with this phone number already exists!");
                        etContactPhone.requestFocus();
                        btnSaveContact.setEnabled(true);
                        btnSaveContact.setText("Save Contact");
                    } else {
                        // Safe to save
                        btnSaveContact.setText("Saving...");
                        String firstName = name.split(" ")[0].toUpperCase().replaceAll("[^A-Z0-9]", "");
                        if (firstName.isEmpty()) firstName = "CONTACT";

                        String docId = String.format("%s_%s_%s", generateRandomAlphanumeric(), phone, firstName);

                        Map<String, Object> contactMap = new HashMap<>();
                        contactMap.put("name", name);
                        contactMap.put("phone", phone);
                        contactMap.put("is_self", false);
                        contactMap.put("total_payable", 0.0);
                        contactMap.put("total_receivable", 0.0);
                        contactMap.put("created_at", System.currentTimeMillis());

                        db.collection("users").document(userEmail)
                                .collection("Contacts").document(docId)
                                .set(contactMap)
                                .addOnCompleteListener(saveTask -> {
                                    if (saveTask.isSuccessful()) {
                                        Toast.makeText(this, "Contact Saved!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Failed to save contact.", Toast.LENGTH_SHORT).show();
                                        btnSaveContact.setEnabled(true);
                                        btnSaveContact.setText("Save Contact");
                                    }
                                });
                    }
                });
    }
}