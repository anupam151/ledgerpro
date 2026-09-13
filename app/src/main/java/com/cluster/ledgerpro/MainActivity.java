package com.cluster.ledgerpro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // UI Components for Lists and Empty States
    private RecyclerView rvLinkedCards;
    private RecyclerView rvRecentTransactions;
    private TextView tvEmptyCards;
    private TextView tvEmptyTransactions;

    // Custom Adapters for RecyclerViews
    private CardAdapter cardAdapter;
    private TransactionAdapter transactionAdapter;

    // Firebase instances
    private FirebaseFirestore db;
    private String userEmail;

    /**
     * Called when the activity is first created. Initializes the UI, sets up edge-to-edge
     * layout, loads user data from Firebase, and configures event listeners.
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable modern edge-to-edge UI
        EdgeToEdge.enable(this);
        // Using activity_main directly as the SPA container layout
        setContentView(R.layout.activity_main);

        // Apply Window Insets to prevent UI from hiding behind system bars (status bar/nav bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize top bar UI Elements
        TextView tvGreeting = findViewById(R.id.tv_greeting);
        TextView tvDate = findViewById(R.id.tv_date);
        ImageView imgProfile = findViewById(R.id.img_profile);

        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String displayName = "User";

        // Extract Google Account details if user is successfully authenticated
        if (currentUser != null) {
            userEmail = currentUser.getEmail();

            // Extract the first name from the Google Account
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                displayName = currentUser.getDisplayName().split(" ")[0];
            }

            // Fetch and upgrade Google Profile Picture to High Resolution (400px)
            if (currentUser.getPhotoUrl() != null) {
                String photoUrl = currentUser.getPhotoUrl().toString();
                photoUrl = photoUrl.replace("s96-c", "s400-c");

                Glide.with(this)
                        .load(photoUrl)
                        .placeholder(android.R.drawable.ic_menu_camera)
                        .into(imgProfile);
            }
        }

        // Calculate time of day for a dynamic, personalized greeting
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        String timeGreeting = "Good Evening";
        if (hour >= 4 && hour < 12) {
            timeGreeting = "Good Morning";
        } else if (hour >= 12 && hour < 17) {
            timeGreeting = "Good Afternoon";
        }
        tvGreeting.setText(timeGreeting + ", " + displayName + "!");

        // Set current live device date formatted for India locale
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        tvDate.setText(sdf.format(new Date()));

        // Setup layouts, listeners, and navigation
        setupRecyclerViews();
        setupClickListeners();
        setupBottomNavigation();
        setupAccountsTabSwitching();

        // Load dynamic data from Firestore only if userEmail is valid
        if (userEmail != null && !userEmail.isEmpty()) {
            loadFirestoreData();
        }
    }

    /**
     * Initializes the RecyclerViews for Cards and Transactions, sets their scroll direction,
     * and links the custom adapters.
     */
    private void setupRecyclerViews() {
        tvEmptyCards = findViewById(R.id.tv_empty_cards);
        tvEmptyTransactions = findViewById(R.id.tv_empty_transactions);

        // Cards List: Horizontal scrolling configuration
        rvLinkedCards = findViewById(R.id.rv_linked_cards);
        rvLinkedCards.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        cardAdapter = new CardAdapter();
        rvLinkedCards.setAdapter(cardAdapter);

        // Transactions List: Vertical scrolling configuration
        rvRecentTransactions = findViewById(R.id.rv_recent_transactions);
        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        transactionAdapter = new TransactionAdapter();
        rvRecentTransactions.setAdapter(transactionAdapter);
    }

    /**
     * Attaches real-time SnapshotListeners to Firestore collections. This ensures the
     * dashboard updates instantly if data changes in the cloud, without needing a refresh.
     */
    private void loadFirestoreData() {
        // Listen for linked Accounts/Cards
        db.collection("users").document(userEmail).collection("accounts")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Listen failed for accounts.", e);
                        return;
                    }
                    if (snapshots != null && !snapshots.isEmpty()) {
                        rvLinkedCards.setVisibility(View.VISIBLE);
                        tvEmptyCards.setVisibility(View.GONE);
                        cardAdapter.setCards(snapshots.getDocuments());
                    } else {
                        rvLinkedCards.setVisibility(View.GONE);
                        tvEmptyCards.setVisibility(View.VISIBLE);
                        cardAdapter.setCards(new ArrayList<>());
                    }
                });

        // Listen for Transactions, ordered chronologically descending
        db.collection("users").document(userEmail).collection("transactions")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Listen failed for transactions.", e);
                        return;
                    }
                    if (snapshots != null && !snapshots.isEmpty()) {
                        rvRecentTransactions.setVisibility(View.VISIBLE);
                        tvEmptyTransactions.setVisibility(View.GONE);
                        transactionAdapter.setTransactions(snapshots.getDocuments());
                    } else {
                        rvRecentTransactions.setVisibility(View.GONE);
                        tvEmptyTransactions.setVisibility(View.VISIBLE);
                        transactionAdapter.setTransactions(new ArrayList<>());
                    }
                });
    }

    /**
     * SPA Architecture: Manages navigation entirely within this activity.
     * Toggling layout visibility ensures the bottom navigation bar never unloads,
     * maintaining a smooth, uninterrupted single-page experience.
     */
    @SuppressLint("SetTextI18n")
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        TextView tvAppTitle = findViewById(R.id.tv_app_title);
        View layoutHome = findViewById(R.id.layout_home);
        View layoutAccounts = findViewById(R.id.layout_accounts);
        View layoutUtilities = findViewById(R.id.layout_utilities);
        View layoutSettings = findViewById(R.id.layout_settings);

        // Highlight Home upon initial launch
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                tvAppTitle.setText("LedgerPro");
                layoutHome.setVisibility(View.VISIBLE);
                layoutAccounts.setVisibility(View.GONE);
                layoutUtilities.setVisibility(View.GONE);
                layoutSettings.setVisibility(View.GONE);
                return true;
            }
            if (itemId == R.id.nav_accounts) {
                tvAppTitle.setText("Accounts");
                layoutHome.setVisibility(View.GONE);
                layoutAccounts.setVisibility(View.VISIBLE);
                layoutUtilities.setVisibility(View.GONE);
                layoutSettings.setVisibility(View.GONE);
                return true;
            }
            if (itemId == R.id.nav_utilities) {
                tvAppTitle.setText("Utilities");
                layoutHome.setVisibility(View.GONE);
                layoutAccounts.setVisibility(View.GONE);
                layoutUtilities.setVisibility(View.VISIBLE);
                layoutSettings.setVisibility(View.GONE);
                return true;
            }
            if (itemId == R.id.nav_settings) {
                tvAppTitle.setText("Settings");
                layoutHome.setVisibility(View.GONE);
                layoutAccounts.setVisibility(View.GONE);
                layoutUtilities.setVisibility(View.GONE);
                layoutSettings.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });
    }

    /**
     * Sets up the TabLayout for "Credit Cards" and "Contacts" with a bottom indicator style.
     */
    private void setupAccountsTabSwitching() {
        com.google.android.material.tabs.TabLayout tabLayout = findViewById(R.id.tab_layout_accounts);
        View sectionCreditCards = findViewById(R.id.section_credit_cards);
        View sectionContacts = findViewById(R.id.section_contacts);

        // Clear and add tabs if not already added
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("Credit Cards"));
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));

        tabLayout.addOnTabSelectedListener(new com.google.android.material.tabs.TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(com.google.android.material.tabs.TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    sectionCreditCards.setVisibility(View.VISIBLE);
                    sectionContacts.setVisibility(View.GONE);
                } else if (tab.getPosition() == 1) {
                    sectionCreditCards.setVisibility(View.GONE);
                    sectionContacts.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(com.google.android.material.tabs.TabLayout.Tab tab) {
                // No-op
            }

            @Override
            public void onTabReselected(com.google.android.material.tabs.TabLayout.Tab tab) {
                // No-op
            }
        });
    }
    private void setupClickListeners() {
        // --- Home Screen Action Clicks ---
        findViewById(R.id.card_all_cards).setOnClickListener(v -> Toast.makeText(this, "Loading Linked Cards...", Toast.LENGTH_SHORT).show());
        findViewById(R.id.card_all_contacts).setOnClickListener(v -> Toast.makeText(this, "Loading Payees...", Toast.LENGTH_SHORT).show());
        findViewById(R.id.card_add_card).setOnClickListener(v -> Toast.makeText(this, "Opening Add Card Form", Toast.LENGTH_SHORT).show());
        findViewById(R.id.card_add_contact).setOnClickListener(v -> Toast.makeText(this, "Opening Add Contact Form", Toast.LENGTH_SHORT).show());

        // --- Utility Screen Action Clicks ---
        findViewById(R.id.card_utility_emi_calculator).setOnClickListener(v -> Toast.makeText(this, "Opening EMI Calculator...", Toast.LENGTH_SHORT).show());

        // --- Settings Screen Action Clicks ---
        findViewById(R.id.card_setting_account).setOnClickListener(v -> Toast.makeText(this, "Opening Account Settings...", Toast.LENGTH_SHORT).show());
        findViewById(R.id.card_setting_profile).setOnClickListener(v -> Toast.makeText(this, "Opening Profile Settings...", Toast.LENGTH_SHORT).show());

        // Trigger complete logout procedure including Google Account picker reset and local cache clearing
        findViewById(R.id.card_setting_logout).setOnClickListener(v -> logoutAndClearSession());
    }

    /**
     * Signs the user out of Firebase Auth, clears Google Sign-In state to force the
     * account picker on the next login, wipes local cache & Firestore persistence,
     * and redirects to LoginActivity.
     */
    @SuppressWarnings("deprecation")
    private void logoutAndClearSession() {
        // 1. Sign out from Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // 2. Sign out from GoogleSignInClient so the Google account chooser appears on next login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // Optional: revokeAccess() can be called here if needed
        });

        // 3. Clear local application cache directory
        try {
            File cacheDir = getCacheDir();
            deleteDir(cacheDir);
        } catch (Exception e) {
            Log.e(TAG, "Error clearing application cache directory", e);
        }

        // 4. Clear local Firestore database persistence
        try {
            FirebaseFirestore.getInstance().clearPersistence();
        } catch (Exception e) {
            Log.e(TAG, "Error clearing Firestore persistence", e);
        }

        // 5. Notify user and redirect to LoginActivity, clearing task backstack
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Recursively deletes files and folders inside the provided directory.
     *
     * @param dir The file or directory to delete
     * @return true if deletion succeeded
     */
    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    // =========================================================================
    // RECYCLER VIEW ADAPTERS
    // =========================================================================

    /**
     * Adapter for processing and displaying user accounts inside the horizontal RecyclerView.
     */
    private static class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
        private List<DocumentSnapshot> cardList = new ArrayList<>();

        private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale.Builder().setLanguage("en").setRegion("IN").build());

        @SuppressLint("NotifyDataSetChanged")
        public void setCards(List<DocumentSnapshot> cards) {
            this.cardList = cards;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_linked_card, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
            DocumentSnapshot doc = cardList.get(position);

            String bankName = doc.getString("bank_name");
            String cardName = doc.getString("card_name");
            String network = doc.getString("network");
            String accountId = doc.getString("account_id");
            Double creditLimit = doc.getDouble("credit_limit");
            Double currentBalance = doc.getDouble("current_balance");

            // Fallback parsing for legacy Firestore document structures
            if (bankName == null) {
                String oldName = doc.getString("name");
                if (oldName != null && oldName.contains(" ")) {
                    String[] parts = oldName.split(" ", 2);
                    bankName = parts[0];
                    cardName = parts.length > 1 ? parts[1] : "Card";
                } else {
                    bankName = oldName != null ? oldName : "Unknown Bank";
                    cardName = "Card";
                }
            }

            holder.tvBankName.setText(bankName);
            holder.tvCardName.setText(cardName != null ? cardName : "");

            if (network != null && !network.isEmpty()) {
                holder.tvCardNetwork.setText(network);
            } else {
                String oldType = doc.getString("type");
                holder.tvCardNetwork.setText(oldType != null ? oldType.replace("_", " ").toUpperCase() : "Visa / Mastercard");
            }

            String maskId = accountId != null && accountId.length() >= 4 ? accountId.substring(accountId.length() - 4) : "1234";
            holder.tvCardNumber.setText("**** **** **** " + maskId);
            holder.tvCardLimit.setText(creditLimit != null ? currencyFormat.format(creditLimit) : "₹0.00");
            holder.tvCardDue.setText(currentBalance != null ? currencyFormat.format(currentBalance) : "₹0.00");
        }

        @Override
        public int getItemCount() {
            return cardList.size();
        }

        static class CardViewHolder extends RecyclerView.ViewHolder {
            TextView tvBankName, tvCardName, tvCardNetwork, tvCardNumber, tvCardLimit, tvCardDue;
            public CardViewHolder(@NonNull View itemView) {
                super(itemView);
                tvBankName = itemView.findViewById(R.id.tv_bank_name);
                tvCardName = itemView.findViewById(R.id.tv_card_name);
                tvCardNetwork = itemView.findViewById(R.id.tv_card_network);
                tvCardNumber = itemView.findViewById(R.id.tv_card_number);
                tvCardLimit = itemView.findViewById(R.id.tv_card_limit);
                tvCardDue = itemView.findViewById(R.id.tv_card_due);
            }
        }
    }

    /**
     * Adapter for processing and displaying recent user transactions inside the vertical RecyclerView.
     */
    private static class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
        private List<DocumentSnapshot> transactionList = new ArrayList<>();
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale.Builder().setLanguage("en").setRegion("IN").build());

        @SuppressLint("NotifyDataSetChanged")
        public void setTransactions(List<DocumentSnapshot> transactions) {
            this.transactionList = transactions;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TransactionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_transaction, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
            DocumentSnapshot doc = transactionList.get(position);
            String merchant = doc.getString("merchant");
            Double amount = doc.getDouble("total_amount");
            Long createdAt = doc.getLong("created_at");

            holder.tvTitle.setText(merchant != null ? merchant : "Unknown Merchant");

            if (createdAt != null) {
                holder.tvDate.setText(dateFormat.format(new Date(createdAt)));
            } else {
                holder.tvDate.setText("Unknown Date");
            }

            if (amount != null) {
                holder.tvAmount.setText(currencyFormat.format(amount));
                if (amount < 0) {
                    holder.tvAmount.setTextColor(0xFFD32F2F); // Material Red for expenses
                } else {
                    holder.tvAmount.setTextColor(0xFF4CAF50); // Material Green for incomes
                }
            } else {
                holder.tvAmount.setText("₹0.00");
            }

            holder.imgIcon.setImageResource(android.R.drawable.ic_menu_sort_by_size);
        }

        @Override
        public int getItemCount() {
            return transactionList.size();
        }

        static class TransactionViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDate, tvAmount; ImageView imgIcon;
            public TransactionViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_transaction_title);
                tvDate = itemView.findViewById(R.id.tv_transaction_date);
                tvAmount = itemView.findViewById(R.id.tv_transaction_amount);
                imgIcon = itemView.findViewById(R.id.img_transaction_icon);
            }
        }
    }
}