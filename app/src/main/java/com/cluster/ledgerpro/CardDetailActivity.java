package com.cluster.ledgerpro;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.NumberFormat;
import java.util.Locale;

public class CardDetailActivity extends AppCompatActivity {

    private static final String TAG = "CardDetailActivity";
    private String cardId;

    // Firebase
    private FirebaseFirestore db;
    private String userEmail;
    private ListenerRegistration cardListener;

    // Formatting
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale.Builder().setLanguage("en").setRegion("IN").build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge drawing
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_card_detail);

        // Retrieve passed Card ID from calling intent
        cardId = getIntent().getStringExtra("CARD_ID");

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userEmail = currentUser.getEmail();
        }

        // Set status bar icons to dark appearance
        WindowInsetsControllerCompat windowController = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowController.setAppearanceLightStatusBars(true);

        // Apply window insets to avoid system bar overlap
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        initializeViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userEmail != null && cardId != null) {
            loadCardData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove listener to prevent memory leaks when activity is not visible
        if (cardListener != null) {
            cardListener.remove();
        }
    }

    /**
     * Binds XML layout elements to Java instances.
     */
    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Loading..."); // Temporary title until data loads
        }
    }

    /**
     * Configures click listeners for navigation cards and the professional FAB transaction menu.
     */
    private void setupListeners() {
        // Toolbar back navigation button
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Vertical Navigation Cards
        MaterialCardView cardNavUnbilled = findViewById(R.id.cardNavUnbilled);
        MaterialCardView cardNavEmi = findViewById(R.id.cardNavEmi);
        MaterialCardView cardNavLastStmt = findViewById(R.id.cardNavLastStmt);
        MaterialCardView cardNavPastStmt = findViewById(R.id.cardNavPastStmt);

        cardNavUnbilled.setOnClickListener(v -> Toast.makeText(this, "Unbilled UI pending for Card: " + cardId, Toast.LENGTH_SHORT).show());
        cardNavEmi.setOnClickListener(v -> Toast.makeText(this, "Active EMIs UI pending for Card: " + cardId, Toast.LENGTH_SHORT).show());
        cardNavLastStmt.setOnClickListener(v -> Toast.makeText(this, "Last Statement UI pending for Card: " + cardId, Toast.LENGTH_SHORT).show());
        cardNavPastStmt.setOnClickListener(v -> Toast.makeText(this, "Past Statements UI pending for Card: " + cardId, Toast.LENGTH_SHORT).show());

        // Floating Action Button -> Opens Professional Custom Bottom Sheet Dialog
        FloatingActionButton fabAction = findViewById(R.id.fabAction);
        fabAction.setOnClickListener(v -> {
            com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(CardDetailActivity.this);

            // FIX: Passed a root ViewGroup (android.R.id.content) and 'false' to resolve the layout inflation warning
            View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_card_actions, findViewById(android.R.id.content), false);
            bottomSheetDialog.setContentView(sheetView);

            // Bind individual transaction options from the bottom sheet
            sheetView.findViewById(R.id.actionAddExpense).setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
                Toast.makeText(this, "Opening Add Expense for Card ID: " + cardId, Toast.LENGTH_SHORT).show();
                // TODO: Launch AddExpenseActivity here
            });

            sheetView.findViewById(R.id.actionAddPayment).setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
                Toast.makeText(this, "Opening Add Payment for Card ID: " + cardId, Toast.LENGTH_SHORT).show();
                // TODO: Launch AddPaymentActivity here
            });

            sheetView.findViewById(R.id.actionAddCharges).setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
                Toast.makeText(this, "Opening Add Charges for Card ID: " + cardId, Toast.LENGTH_SHORT).show();
                // TODO: Launch AddChargesActivity here
            });

            sheetView.findViewById(R.id.actionAddRefund).setOnClickListener(view -> {
                bottomSheetDialog.dismiss();
                Toast.makeText(this, "Opening Add Refund for Card ID: " + cardId, Toast.LENGTH_SHORT).show();
                // TODO: Launch AddRefundActivity here
            });

            bottomSheetDialog.show();
        });
    }

    /**
     * Fetches the specific card document from Firestore in real-time.
     */
    @SuppressLint("SetTextI18n")
    private void loadCardData() {
        cardListener = db.collection("users").document(userEmail).collection("Credit Cards")
                .document(cardId)
                .addSnapshotListener((doc, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error fetching card details", e);
                        Toast.makeText(CardDetailActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (doc != null && doc.exists()) {
                        // Extract values
                        String cardName = doc.getString("card_name");
                        Double creditLimit = doc.getDouble("credit_limit");

                        // Update Toolbar Title with the Card Name
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(cardName != null ? cardName : "Card Details");
                        }

                        // Update Total Limit TextView
                        TextView tvTotalLimit = findViewById(R.id.tvTotalLimit);
                        if (creditLimit != null) {
                            tvTotalLimit.setText(currencyFormat.format(creditLimit));
                        } else {
                            tvTotalLimit.setText("₹0.00");
                        }

                    } else {
                        Toast.makeText(CardDetailActivity.this, "Card not found", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity if the card was deleted elsewhere
                    }
                });
    }
}