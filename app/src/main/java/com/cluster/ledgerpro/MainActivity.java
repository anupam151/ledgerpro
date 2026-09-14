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
import androidx.appcompat.app.AlertDialog;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // =========================================================================
    // STATIC MAPPINGS (FULL BANK SHORT NAME LIST)
    // =========================================================================
    private static final Map<String, String> BANK_INITIALS_MAP = new HashMap<>();

    static {
        BANK_INITIALS_MAP.put("Bank of Baroda", "BOB");
        BANK_INITIALS_MAP.put("Bank of India", "BOI");
        BANK_INITIALS_MAP.put("Bank of Maharashtra", "BOM");
        BANK_INITIALS_MAP.put("Canara Bank", "CAN");
        BANK_INITIALS_MAP.put("Central Bank of India", "CBI");
        BANK_INITIALS_MAP.put("Indian Bank", "IB");
        BANK_INITIALS_MAP.put("Indian Overseas Bank", "IOB");
        BANK_INITIALS_MAP.put("Punjab National Bank", "PNB");
        BANK_INITIALS_MAP.put("Punjab & Sind Bank", "PSB");
        BANK_INITIALS_MAP.put("State Bank of India", "SBI");
        BANK_INITIALS_MAP.put("UCO Bank", "UCO");
        BANK_INITIALS_MAP.put("Union Bank of India", "UBI");
        BANK_INITIALS_MAP.put("Axis Bank", "AXIS");
        BANK_INITIALS_MAP.put("Bandhan Bank", "BANDHAN");
        BANK_INITIALS_MAP.put("CSB Bank", "CSB");
        BANK_INITIALS_MAP.put("City Union Bank", "CUB");
        BANK_INITIALS_MAP.put("DCB Bank", "DCB");
        BANK_INITIALS_MAP.put("Dhanlaxmi Bank", "DLB");
        BANK_INITIALS_MAP.put("Federal Bank", "FED");
        BANK_INITIALS_MAP.put("HDFC Bank", "HDFC");
        BANK_INITIALS_MAP.put("ICICI Bank Limited", "ICICI");
        BANK_INITIALS_MAP.put("ICICI Bank", "ICICI");
        BANK_INITIALS_MAP.put("IDBI Bank", "IDBI");
        BANK_INITIALS_MAP.put("IDFC FIRST Bank", "IDFC");
        BANK_INITIALS_MAP.put("IndusInd Bank", "IND");
        BANK_INITIALS_MAP.put("Jammu & Kashmir Bank", "J&K");
        BANK_INITIALS_MAP.put("Karnataka Bank", "KBL");
        BANK_INITIALS_MAP.put("Karur Vysya Bank", "KVB");
        BANK_INITIALS_MAP.put("Kotak Mahindra Bank", "KOTAK");
        BANK_INITIALS_MAP.put("Nainital Bank", "NB");
        BANK_INITIALS_MAP.put("RBL Bank", "RBL");
        BANK_INITIALS_MAP.put("South Indian Bank", "SIB");
        BANK_INITIALS_MAP.put("Tamilnad Mercantile Bank", "TMB");
        BANK_INITIALS_MAP.put("YES Bank", "YES");
        BANK_INITIALS_MAP.put("AU Small Finance Bank", "AUSFB");
        BANK_INITIALS_MAP.put("Capital Small Finance Bank", "CSFB");
        BANK_INITIALS_MAP.put("Equitas Small Finance Bank", "ESFB");
        BANK_INITIALS_MAP.put("ESAF Small Finance Bank", "ESFB");
        BANK_INITIALS_MAP.put("Fincare Small Finance Bank", "FSFB");
        BANK_INITIALS_MAP.put("Jana Small Finance Bank", "JSFB");
        BANK_INITIALS_MAP.put("North East Small Finance Bank", "NESFB");
        BANK_INITIALS_MAP.put("Shivalik Small Finance Bank", "SSFB");
        BANK_INITIALS_MAP.put("Suryoday Small Finance Bank", "SSFB");
        BANK_INITIALS_MAP.put("Ujjivan Small Finance Bank", "USFB");
        BANK_INITIALS_MAP.put("Unity Small Finance Bank", "USFB");
        BANK_INITIALS_MAP.put("Utkarsh Small Finance Bank", "USFB");
        BANK_INITIALS_MAP.put("Airtel Payments Bank", "APBL");
        BANK_INITIALS_MAP.put("Fino Payments Bank", "FPB");
        BANK_INITIALS_MAP.put("India Post Payments Bank", "IPPB");
        BANK_INITIALS_MAP.put("Jio Payments Bank", "JPB");
        BANK_INITIALS_MAP.put("NSDL Payments Bank", "NPB");
        BANK_INITIALS_MAP.put("Paytm Payments Bank", "PPBL");
        BANK_INITIALS_MAP.put("Baroda Gujarat Gramin Bank", "BGGB");
        BANK_INITIALS_MAP.put("Baroda Rajasthan Kshetriya Gramin Bank", "BRKGB");
        BANK_INITIALS_MAP.put("Baroda U.P. Bank", "BUPB");
        BANK_INITIALS_MAP.put("Kerala Gramin Bank", "KGB");
        BANK_INITIALS_MAP.put("Cosmos Co-operative Bank", "CCB");
        BANK_INITIALS_MAP.put("Saraswat Co-operative Bank", "SCB");
        BANK_INITIALS_MAP.put("SVC Co-operative Bank", "SVC");
        BANK_INITIALS_MAP.put("American Express", "AMEX");
        BANK_INITIALS_MAP.put("Bank of America", "BOA");
        BANK_INITIALS_MAP.put("Barclays Bank", "BARB");
        BANK_INITIALS_MAP.put("BNP Paribas", "BNP");
        BANK_INITIALS_MAP.put("Citibank", "CITI");
        BANK_INITIALS_MAP.put("DBS Bank", "DBS");
        BANK_INITIALS_MAP.put("Deutsche Bank", "DB");
        BANK_INITIALS_MAP.put("First Abu Dhabi Bank", "FAB");
        BANK_INITIALS_MAP.put("HSBC Bank", "HSBC");
        BANK_INITIALS_MAP.put("Qatar National Bank", "QNB");
        BANK_INITIALS_MAP.put("SBM Bank India", "SBM");
        BANK_INITIALS_MAP.put("Standard Chartered Bank", "SCB");
        BANK_INITIALS_MAP.put("Standard Chartered", "SCB");
    }

    // UI Components for Lists and Empty States
    private RecyclerView rvLinkedCards;
    private RecyclerView rvRecentTransactions;
    private RecyclerView rvAccountsCreditCards;
    private RecyclerView rvAccountsContacts;
    private TextView tvEmptyCards;
    private TextView tvEmptyTransactions;
    private TextView tvEmptyCreditCards;
    private TextView tvEmptyContacts;

    // Custom Adapters for RecyclerViews
    private CardAdapter cardAdapter;
    private AccountsCardAdapter accountsCardAdapter;
    private TransactionAdapter transactionAdapter;
    private ContactAdapter contactAdapter;

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

        if (currentUser != null) {
            userEmail = currentUser.getEmail();

            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                displayName = currentUser.getDisplayName().split(" ")[0];
            }

            if (currentUser.getPhotoUrl() != null) {
                String photoUrl = currentUser.getPhotoUrl().toString();
                photoUrl = photoUrl.replace("s96-c", "s400-c");

                Glide.with(this)
                        .load(photoUrl)
                        .placeholder(android.R.drawable.ic_menu_camera)
                        .into(imgProfile);
            }
        }

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        String timeGreeting = "Good Evening";
        if (hour >= 4 && hour < 12) {
            timeGreeting = "Good Morning";
        } else if (hour >= 12 && hour < 17) {
            timeGreeting = "Good Afternoon";
        }
        tvGreeting.setText(timeGreeting + ", " + displayName + "!");

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        tvDate.setText(sdf.format(new Date()));

        setupRecyclerViews();
        setupClickListeners();
        setupBottomNavigation();
        setupAccountsTabSwitching();

        if (userEmail != null && !userEmail.isEmpty()) {
            loadFirestoreData();
        }
    }

    /**
     * Initializes the RecyclerViews for Cards, Transactions, Credit Cards, and Contacts.
     */
    private void setupRecyclerViews() {
        tvEmptyCards = findViewById(R.id.tv_empty_cards);
        tvEmptyTransactions = findViewById(R.id.tv_empty_transactions);
        tvEmptyCreditCards = findViewById(R.id.tv_empty_credit_cards);
        tvEmptyContacts = findViewById(R.id.tv_empty_contacts);

        // Horizontal scrolling configuration (Home screen)
        rvLinkedCards = findViewById(R.id.rv_linked_cards);
        rvLinkedCards.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        cardAdapter = new CardAdapter();
        rvLinkedCards.setAdapter(cardAdapter);

        // Vertical scrolling configuration (Home screen transactions)
        rvRecentTransactions = findViewById(R.id.rv_recent_transactions);
        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        transactionAdapter = new TransactionAdapter();
        rvRecentTransactions.setAdapter(transactionAdapter);

        // Accounts Tab - Vertical Credit Cards List (New Adapter)
        rvAccountsCreditCards = findViewById(R.id.rv_accounts_credit_cards);
        rvAccountsCreditCards.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        accountsCardAdapter = new AccountsCardAdapter();
        rvAccountsCreditCards.setAdapter(accountsCardAdapter);

        // Accounts Tab - Vertical Contacts List
        rvAccountsContacts = findViewById(R.id.rv_accounts_contacts);
        rvAccountsContacts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        contactAdapter = new ContactAdapter();
        rvAccountsContacts.setAdapter(contactAdapter);
    }

    /**
     * Attaches real-time SnapshotListeners to Firestore collections.
     */
    private void loadFirestoreData() {
        db.collection("users").document(userEmail).collection("Credit Cards")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Listen failed for Credit Cards.", e);
                        return;
                    }
                    if (snapshots != null && !snapshots.isEmpty()) {
                        rvLinkedCards.setVisibility(View.VISIBLE);
                        tvEmptyCards.setVisibility(View.GONE);
                        cardAdapter.setCards(snapshots.getDocuments());

                        rvAccountsCreditCards.setVisibility(View.VISIBLE);
                        tvEmptyCreditCards.setVisibility(View.GONE);
                        accountsCardAdapter.setCards(snapshots.getDocuments());
                    } else {
                        rvLinkedCards.setVisibility(View.GONE);
                        tvEmptyCards.setVisibility(View.VISIBLE);
                        cardAdapter.setCards(new ArrayList<>());

                        rvAccountsCreditCards.setVisibility(View.GONE);
                        tvEmptyCreditCards.setVisibility(View.VISIBLE);
                        accountsCardAdapter.setCards(new ArrayList<>());
                    }
                });

        db.collection("users").document(userEmail).collection("contacts")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;
                    if (snapshots != null && !snapshots.isEmpty()) {
                        rvAccountsContacts.setVisibility(View.VISIBLE);
                        tvEmptyContacts.setVisibility(View.GONE);
                        contactAdapter.setContacts(snapshots.getDocuments());
                    } else {
                        rvAccountsContacts.setVisibility(View.GONE);
                        tvEmptyContacts.setVisibility(View.VISIBLE);
                        contactAdapter.setContacts(new ArrayList<>());
                    }
                });

        db.collection("users").document(userEmail).collection("transactions")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;
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
     * Triggers a custom rounded-corner dialog displaying "Edit" and "Delete" options for a selected card.
     */
    private void showCardOptionsDialog(DocumentSnapshot doc) {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_card_options);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvTitle = dialog.findViewById(R.id.tv_dialog_title);
        TextView tvEdit = dialog.findViewById(R.id.tv_edit_card);
        TextView tvDelete = dialog.findViewById(R.id.tv_delete_card);

        tvTitle.setText(doc.getString("card_name"));

        tvEdit.setOnClickListener(v -> {
            dialog.dismiss();
            launchEditCardActivity(doc);
        });

        tvDelete.setOnClickListener(v -> {
            dialog.dismiss();
            confirmDeleteCard(doc);
        });

        dialog.show();
    }

    /**
     * Packages the Firestore document data into an Intent and launches AddCardActivity in edit mode.
     */
    private void launchEditCardActivity(DocumentSnapshot doc) {
        Intent intent = new Intent(this, AddCardActivity.class);
        intent.putExtra("CARD_ID", doc.getId());
        intent.putExtra("BANK_NAME", doc.getString("bank_name"));
        intent.putExtra("CARD_NAME", doc.getString("card_name"));
        intent.putExtra("CARD_TYPE", doc.getString("network"));
        intent.putExtra("LAST4", doc.getString("account_id"));
        intent.putExtra("TOTAL_LIMIT", doc.getDouble("credit_limit"));

        Long billingDay = doc.getLong("billing_day");
        intent.putExtra("BILLING_DAY", billingDay != null ? billingDay.intValue() : 1);

        Boolean isCb = doc.getBoolean("cashback_enabled");
        intent.putExtra("IS_CASHBACK", isCb != null ? isCb : false);

        Object ratesObj = doc.get("cashback_rates");
        if (ratesObj instanceof List) {
            List<?> ratesList = (List<?>) ratesObj;
            double[] ratesArr = new double[ratesList.size()];
            for (int i = 0; i < ratesList.size(); i++) {
                ratesArr[i] = ((Number) ratesList.get(i)).doubleValue();
            }
            intent.putExtra("CASHBACK_RATES", ratesArr);
        }

        Long themeColor = doc.getLong("theme_color");
        if (themeColor != null) {
            intent.putExtra("THEME_COLOR", String.valueOf(themeColor));
        }

        startActivity(intent);
    }

    /**
     * Shows a confirmation dialog before permanently removing the card document from Firestore.
     */
    private void confirmDeleteCard(DocumentSnapshot doc) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Credit Card")
                .setMessage("Are you sure you want to delete this credit card? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> db.collection("users").document(userEmail).collection("Credit Cards")
                        .document(doc.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Credit Card deleted", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * SPA Architecture: Manages navigation entirely within this activity.
     */
    @SuppressLint("SetTextI18n")
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        TextView tvAppTitle = findViewById(R.id.tv_app_title);
        View layoutHome = findViewById(R.id.layout_home);
        View layoutAccounts = findViewById(R.id.layout_accounts);
        View layoutUtilities = findViewById(R.id.layout_utilities);
        View layoutSettings = findViewById(R.id.layout_settings);

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

    private void setupAccountsTabSwitching() {
        com.google.android.material.tabs.TabLayout tabLayout = findViewById(R.id.tab_layout_accounts);
        View sectionCreditCards = findViewById(R.id.section_credit_cards);
        View sectionContacts = findViewById(R.id.section_contacts);

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
            @Override public void onTabUnselected(com.google.android.material.tabs.TabLayout.Tab tab) {}
            @Override public void onTabReselected(com.google.android.material.tabs.TabLayout.Tab tab) {}
        });
    }

    private void setupClickListeners() {
        // --- Home Screen Action Clicks ---

        // Navigate to Accounts -> Credit Cards Tab
        findViewById(R.id.card_all_cards).setOnClickListener(v -> {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            bottomNav.setSelectedItemId(R.id.nav_accounts);

            com.google.android.material.tabs.TabLayout tabLayout = findViewById(R.id.tab_layout_accounts);
            com.google.android.material.tabs.TabLayout.Tab creditCardsTab = tabLayout.getTabAt(0);
            if (creditCardsTab != null) {
                creditCardsTab.select();
            }
        });

        // Navigate to Accounts -> Contacts Tab
        findViewById(R.id.card_all_contacts).setOnClickListener(v -> {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            bottomNav.setSelectedItemId(R.id.nav_accounts);

            com.google.android.material.tabs.TabLayout tabLayout = findViewById(R.id.tab_layout_accounts);
            com.google.android.material.tabs.TabLayout.Tab contactsTab = tabLayout.getTabAt(1);
            if (contactsTab != null) {
                contactsTab.select();
            }
        });

        // Launch AddCardActivity
        findViewById(R.id.card_add_card).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.card_add_contact).setOnClickListener(v -> Toast.makeText(this, "Opening Add Contact Form", Toast.LENGTH_SHORT).show());

        // --- Utility Screen Action Clicks ---
        findViewById(R.id.card_utility_emi_calculator).setOnClickListener(v -> Toast.makeText(this, "Opening EMI Calculator...", Toast.LENGTH_SHORT).show());

        // --- Settings Screen Action Clicks ---
        findViewById(R.id.card_setting_account).setOnClickListener(v -> Toast.makeText(this, "Opening Account Settings...", Toast.LENGTH_SHORT).show());
        findViewById(R.id.card_setting_profile).setOnClickListener(v -> Toast.makeText(this, "Opening Profile Settings...", Toast.LENGTH_SHORT).show());
        findViewById(R.id.card_setting_logout).setOnClickListener(v -> logoutAndClearSession());
    }

    @SuppressWarnings("deprecation")
    private void logoutAndClearSession() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {});

        try {
            File cacheDir = getCacheDir();
            deleteDir(cacheDir);
        } catch (Exception e) {
            Log.e(TAG, "Error clearing application cache directory", e);
        }
        try {
            FirebaseFirestore.getInstance().clearPersistence();
        } catch (Exception e) {
            Log.e(TAG, "Error clearing Firestore persistence", e);
        }

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) return false;
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
     * Adapter for the horizontal credit card layout on the Home screen dashboard.
     */
    private class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
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

            String fullBankName = doc.getString("bank_name");
            String cardName = doc.getString("card_name");
            String network = doc.getString("network");
            String accountId = doc.getString("account_id");
            Double creditLimit = doc.getDouble("credit_limit");
            Double currentBalance = doc.getDouble("current_balance");
            Long themeColor = doc.getLong("theme_color");

            // Apply dynamic Theme Color strictly to the text name on the white card
            if (themeColor != null) {
                holder.tvCardName.setTextColor(themeColor.intValue());
            } else {
                holder.tvCardName.setTextColor(0xFF192033);
            }

            // Execute Map matching logic for abbreviation
            String shortBankName = "BANK";
            if (fullBankName != null) {
                if (BANK_INITIALS_MAP.containsKey(fullBankName)) {
                    shortBankName = BANK_INITIALS_MAP.get(fullBankName);
                } else {
                    shortBankName = fullBankName.length() >= 4 ? fullBankName.substring(0, 4).toUpperCase() : fullBankName.toUpperCase();
                }
            }

            holder.tvBankName.setText(shortBankName);
            holder.tvCardName.setText(cardName != null ? cardName : "");
            holder.tvCardNetwork.setText(network != null ? network.toUpperCase() : "VISA");

            String maskId = accountId != null && accountId.length() >= 4 ? accountId.substring(accountId.length() - 4) : "1234";
            holder.tvCardNumber.setText(maskId);

            holder.tvCardLimit.setText(creditLimit != null ? currencyFormat.format(creditLimit) : "₹0.00");
            holder.tvCardDue.setText(currentBalance != null ? currencyFormat.format(currentBalance) : "₹0.00");

            holder.itemView.setOnClickListener(v -> Toast.makeText(MainActivity.this, "Opening Ledger for " + cardName, Toast.LENGTH_SHORT).show());
            holder.itemView.setOnLongClickListener(v -> {
                showCardOptionsDialog(doc);
                return true;
            });
        }

        @Override
        public int getItemCount() { return cardList.size(); }

        class CardViewHolder extends RecyclerView.ViewHolder {
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
     * Adapter exclusively used for the vertical credit card list in the Accounts tab.
     */
    private class AccountsCardAdapter extends RecyclerView.Adapter<AccountsCardAdapter.AccountsCardViewHolder> {
        private List<DocumentSnapshot> cardList = new ArrayList<>();
        private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale.Builder().setLanguage("en").setRegion("IN").build());

        @SuppressLint("NotifyDataSetChanged")
        public void setCards(List<DocumentSnapshot> cards) {
            this.cardList = cards;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AccountsCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AccountsCardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_card, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull AccountsCardViewHolder holder, int position) {
            DocumentSnapshot doc = cardList.get(position);

            String fullBankName = doc.getString("bank_name");
            String cardName = doc.getString("card_name");
            String accountId = doc.getString("account_id");
            Double creditLimit = doc.getDouble("credit_limit");
            Long themeColor = doc.getLong("theme_color");

            // Apply dynamic Theme Color strictly to the text name
            if (themeColor != null) {
                holder.tvCardName.setTextColor(themeColor.intValue());
            } else {
                holder.tvCardName.setTextColor(0xFF192033);
            }

            // Map Matching
            String shortBankName = "BANK";
            if (fullBankName != null) {
                if (BANK_INITIALS_MAP.containsKey(fullBankName)) {
                    shortBankName = BANK_INITIALS_MAP.get(fullBankName);
                } else {
                    shortBankName = fullBankName.length() >= 4 ? fullBankName.substring(0, 4).toUpperCase() : fullBankName.toUpperCase();
                }
            }

            holder.tvBankName.setText(shortBankName);
            holder.tvCardName.setText(cardName != null ? cardName : "");

            String maskId = accountId != null && accountId.length() >= 4 ? accountId.substring(accountId.length() - 4) : "1234";
            holder.tvCardNumber.setText("•••• " + maskId);
            holder.tvCardLimit.setText(creditLimit != null ? currencyFormat.format(creditLimit) : "₹0.00");

            holder.itemView.setOnClickListener(v -> Toast.makeText(MainActivity.this, "Opening Ledger for " + cardName, Toast.LENGTH_SHORT).show());
            holder.itemView.setOnLongClickListener(v -> {
                showCardOptionsDialog(doc);
                return true;
            });
        }

        @Override
        public int getItemCount() { return cardList.size(); }

        class AccountsCardViewHolder extends RecyclerView.ViewHolder {
            TextView tvBankName, tvCardName, tvCardNumber, tvCardLimit;
            public AccountsCardViewHolder(@NonNull View itemView) {
                super(itemView);
                tvBankName = itemView.findViewById(R.id.tv_acc_bank_name);
                tvCardName = itemView.findViewById(R.id.tv_acc_card_name);
                tvCardNumber = itemView.findViewById(R.id.tv_acc_card_number);
                tvCardLimit = itemView.findViewById(R.id.tv_acc_card_limit);
            }
        }
    }

    // Existing Transaction Adapter
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
            holder.tvTitle.setText(doc.getString("merchant"));
            Long createdAt = doc.getLong("created_at");
            holder.tvDate.setText(createdAt != null ? dateFormat.format(new Date(createdAt)) : "Unknown Date");

            Double amount = doc.getDouble("total_amount");
            if (amount != null) {
                holder.tvAmount.setText(currencyFormat.format(amount));
                holder.tvAmount.setTextColor(amount < 0 ? 0xFFD32F2F : 0xFF4CAF50);
            }
            holder.imgIcon.setImageResource(android.R.drawable.ic_menu_sort_by_size);
        }

        @Override
        public int getItemCount() { return transactionList.size(); }

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

    // Existing Contact Adapter
    private static class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
        private List<DocumentSnapshot> contactList = new ArrayList<>();

        @SuppressLint("NotifyDataSetChanged")
        public void setContacts(List<DocumentSnapshot> contacts) {
            this.contactList = contacts;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_transaction, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
            DocumentSnapshot doc = contactList.get(position);
            holder.tvTitle.setText(doc.getString("name"));
            holder.tvDate.setText(doc.getString("phone"));
            holder.tvAmount.setText("");
            holder.imgIcon.setImageResource(android.R.drawable.ic_menu_my_calendar);
        }

        @Override
        public int getItemCount() { return contactList.size(); }

        static class ContactViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDate, tvAmount; ImageView imgIcon;
            public ContactViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_transaction_title);
                tvDate = itemView.findViewById(R.id.tv_transaction_date);
                tvAmount = itemView.findViewById(R.id.tv_transaction_amount);
                imgIcon = itemView.findViewById(R.id.img_transaction_icon);
            }
        }
    }
}