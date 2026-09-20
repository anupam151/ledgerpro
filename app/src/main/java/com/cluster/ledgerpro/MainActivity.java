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
import androidx.core.view.WindowInsetsControllerCompat;
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
import com.google.firebase.firestore.ListenerRegistration;
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

    // Firestore Listener Registrations for lifecycle management
    private ListenerRegistration cardsListener;
    private ListenerRegistration contactsListener;
    private ListenerRegistration transactionsListener;

    /**
     * Called when the activity is first created. Initializes the UI, sets up edge-to-edge
     * layout, loads user data from Firebase, and configures event listeners.
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        WindowInsetsControllerCompat windowController = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowController.setAppearanceLightStatusBars(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
                Glide.with(this).load(photoUrl).placeholder(android.R.drawable.ic_menu_camera).into(imgProfile);
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
            ensureMyLedgerContactExists();
        }
    }

    // =========================================================================
    // LIFECYCLE MANAGEMENT FOR REAL-TIME FIREBASE SYNC
    // =========================================================================

    @Override
    protected void onResume() {
        super.onResume();
        if (userEmail != null && !userEmail.isEmpty()) {
            loadFirestoreData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove listeners when leaving to prevent memory leaks and duplicate updates
        if (cardsListener != null) cardsListener.remove();
        if (contactsListener != null) contactsListener.remove();
        if (transactionsListener != null) transactionsListener.remove();
    }

    /**
     * Checks if the default "My Ledger" contact exists. If not, silently creates it.
     */
    private void ensureMyLedgerContactExists() {
        if (userEmail == null) return;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String tempFirstName = "USER";
        if (currentUser != null && currentUser.getDisplayName() != null) {
            tempFirstName = currentUser.getDisplayName().split(" ")[0].toUpperCase().replaceAll("[^A-Z0-9]", "");
            if (tempFirstName.isEmpty()) tempFirstName = "USER";
        }
        final String firstName = tempFirstName;

        db.collection("users").document(userEmail).collection("Contacts")
                .whereEqualTo("is_self", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        String randomStr = String.format(Locale.getDefault(), "%03d", new java.util.Random().nextInt(1000));
                        String docId = String.format("%s_0000000000_%s", randomStr, firstName);
                        Map<String, Object> myLedgerMap = new HashMap<>();
                        myLedgerMap.put("name", "My Ledger");
                        myLedgerMap.put("phone", "Self");
                        myLedgerMap.put("is_self", true);
                        myLedgerMap.put("total_payable", 0.0);
                        myLedgerMap.put("total_receivable", 0.0);
                        myLedgerMap.put("created_at", 0);
                        db.collection("users").document(userEmail).collection("Contacts").document(docId).set(myLedgerMap);
                    }
                });
    }

    /**
     * Initializes all RecyclerViews for Cards, Transactions, and Contacts.
     */
    private void setupRecyclerViews() {
        tvEmptyCards = findViewById(R.id.tv_empty_cards);
        tvEmptyTransactions = findViewById(R.id.tv_empty_transactions);
        tvEmptyCreditCards = findViewById(R.id.tv_empty_credit_cards);
        tvEmptyContacts = findViewById(R.id.tv_empty_contacts);

        rvLinkedCards = findViewById(R.id.rv_linked_cards);
        rvLinkedCards.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        cardAdapter = new CardAdapter();
        rvLinkedCards.setAdapter(cardAdapter);

        rvRecentTransactions = findViewById(R.id.rv_recent_transactions);
        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        transactionAdapter = new TransactionAdapter();
        rvRecentTransactions.setAdapter(transactionAdapter);

        rvAccountsCreditCards = findViewById(R.id.rv_accounts_credit_cards);
        rvAccountsCreditCards.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        accountsCardAdapter = new AccountsCardAdapter();
        rvAccountsCreditCards.setAdapter(accountsCardAdapter);

        rvAccountsContacts = findViewById(R.id.rv_accounts_contacts);
        rvAccountsContacts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        contactAdapter = new ContactAdapter();
        rvAccountsContacts.setAdapter(contactAdapter);
    }

    /**
     * Attaches real-time SnapshotListeners to Firestore collections.
     */
    private void loadFirestoreData() {
        if (userEmail == null) return;

        if (cardsListener != null) cardsListener.remove();
        if (contactsListener != null) contactsListener.remove();
        if (transactionsListener != null) transactionsListener.remove();

        cardsListener = db.collection("users").document(userEmail).collection("Credit Cards")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Credit Cards listen failed", e);
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

        contactsListener = db.collection("users").document(userEmail).collection("Contacts")
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Contacts listen failed", e);
                        return;
                    }
                    if (snapshots != null && !snapshots.isEmpty()) {
                        List<DocumentSnapshot> sortedContacts = processAndSortContacts(snapshots.getDocuments());
                        rvAccountsContacts.setVisibility(View.VISIBLE);
                        tvEmptyContacts.setVisibility(View.GONE);
                        contactAdapter.setContacts(sortedContacts);
                    } else {
                        rvAccountsContacts.setVisibility(View.GONE);
                        tvEmptyContacts.setVisibility(View.VISIBLE);
                        contactAdapter.setContacts(new ArrayList<>());
                    }
                });

        transactionsListener = db.collection("users").document(userEmail).collection("transactions")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Transactions listen failed", e);
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
     * Processes a list of contact documents, sorts them in memory, and ensures
     * that "My Ledger" (is_self = true) always stays pinned at the very top.
     */
    private List<DocumentSnapshot> processAndSortContacts(List<DocumentSnapshot> rawDocs) {
        List<DocumentSnapshot> docList = new ArrayList<>(rawDocs);

        docList.sort((d1, d2) -> {
            Boolean self1 = d1.getBoolean("is_self");
            Boolean self2 = d2.getBoolean("is_self");
            boolean s1 = self1 != null && self1;
            boolean s2 = self2 != null && self2;
            if (s1 && !s2) return -1;
            if (!s1 && s2) return 1;
            String n1 = d1.getString("name");
            String n2 = d2.getString("name");
            return (n1 != null ? n1 : "").compareToIgnoreCase(n2 != null ? n2 : "");
        });

        return docList;
    }

    // =========================================================================
    // DIALOGS & EDIT/DELETE HANDLERS
    // =========================================================================

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

    @SuppressLint("SetTextI18n")
    private void showContactOptionsDialog(DocumentSnapshot doc) {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_card_options);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvTitle = dialog.findViewById(R.id.tv_dialog_title);
        TextView tvEdit = dialog.findViewById(R.id.tv_edit_card);
        TextView tvDelete = dialog.findViewById(R.id.tv_delete_card);

        tvTitle.setText(doc.getString("name"));
        tvEdit.setText("Edit Contact");
        tvDelete.setText("Delete Contact");

        tvEdit.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(this, "Edit Contact functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        tvDelete.setOnClickListener(v -> {
            dialog.dismiss();
            confirmDeleteContact(doc);
        });

        dialog.show();
    }

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

    private void showCustomDeleteDialog(String title, String message, Runnable onDeleteAction) {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_confirm_delete);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvTitle = dialog.findViewById(R.id.tv_delete_title);
        TextView tvMessage = dialog.findViewById(R.id.tv_delete_message);
        View btnCancel = dialog.findViewById(R.id.btn_cancel_delete);
        View btnDelete = dialog.findViewById(R.id.btn_confirm_delete);

        tvTitle.setText(title);
        tvMessage.setText(message);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnDelete.setOnClickListener(v -> {
            dialog.dismiss();
            onDeleteAction.run();
        });

        dialog.show();
    }

    private void confirmDeleteCard(DocumentSnapshot doc) {
        showCustomDeleteDialog(
                "Delete Credit Card",
                "Are you sure you want to delete this credit card? This action cannot be undone.",
                () -> db.collection("users").document(userEmail).collection("Credit Cards")
                        .document(doc.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Credit Card deleted", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show())
        );
    }

    private void confirmDeleteContact(DocumentSnapshot doc) {
        showCustomDeleteDialog(
                "Delete Contact",
                "Are you sure you want to delete this contact? This action cannot be undone.",
                () -> db.collection("users").document(userEmail).collection("Contacts")
                        .document(doc.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show())
        );
    }

    // =========================================================================
    // NAVIGATION & TAB SETUP
    // =========================================================================

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
        androidx.viewpager2.widget.ViewPager2 viewPager = findViewById(R.id.view_pager_accounts);

        View sectionCreditCards = findViewById(R.id.section_credit_cards);
        View sectionContacts = findViewById(R.id.section_contacts);

        if (sectionCreditCards.getParent() != null) {
            ((ViewGroup) sectionCreditCards.getParent()).removeAllViews();
        }

        viewPager.setAdapter(new RecyclerView.Adapter<>() {
            private final View[] pages = new View[]{sectionCreditCards, sectionContacts};

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                android.widget.FrameLayout frameLayout = new android.widget.FrameLayout(parent.getContext());
                frameLayout.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                return new RecyclerView.ViewHolder(frameLayout) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                android.widget.FrameLayout frameLayout = (android.widget.FrameLayout) holder.itemView;
                frameLayout.removeAllViews();

                View view = pages[position];
                if (view.getParent() != null) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
                frameLayout.addView(view);
            }

            @Override
            public int getItemCount() {
                return pages.length;
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }
        });

        new com.google.android.material.tabs.TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Credit Cards");
                    } else {
                        tab.setText("Contacts");
                    }
                }
        ).attach();
    }

    private void setupClickListeners() {
        findViewById(R.id.card_all_cards).setOnClickListener(v -> {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            bottomNav.setSelectedItemId(R.id.nav_accounts);

            // Directly control ViewPager2 instead of TabLayout
            androidx.viewpager2.widget.ViewPager2 viewPager = findViewById(R.id.view_pager_accounts);
            viewPager.setCurrentItem(0, false);
        });

        findViewById(R.id.card_all_contacts).setOnClickListener(v -> {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            bottomNav.setSelectedItemId(R.id.nav_accounts);

            // Directly control ViewPager2 instead of TabLayout
            androidx.viewpager2.widget.ViewPager2 viewPager = findViewById(R.id.view_pager_accounts);
            viewPager.setCurrentItem(1, false);
        });

        findViewById(R.id.card_add_card).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.card_add_contact).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.card_utility_emi_calculator).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmiCalculatorActivity.class);
            startActivity(intent);
        });
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

            if (themeColor != null) {
                holder.tvCardName.setTextColor(themeColor.intValue());
            } else {
                holder.tvCardName.setTextColor(0xFF192033);
            }

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

            if (themeColor != null) {
                holder.tvCardName.setTextColor(themeColor.intValue());
            } else {
                holder.tvCardName.setTextColor(0xFF192033);
            }

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

    private class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
        private List<DocumentSnapshot> contactList = new ArrayList<>();

        @SuppressLint("NotifyDataSetChanged")
        public void setContacts(List<DocumentSnapshot> contacts) {
            this.contactList = contacts;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_card, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
            DocumentSnapshot doc = contactList.get(position);
            String name = doc.getString("name");
            holder.tvContactName.setText(name);
            holder.tvContactPhone.setText(doc.getString("phone"));

            Boolean isSelf = doc.getBoolean("is_self");
            if (isSelf != null && isSelf) {
                holder.imgContactIcon.setColorFilter(android.graphics.Color.parseColor("#4CAF50"));
                holder.itemView.setOnLongClickListener(null);
            } else {
                holder.imgContactIcon.setColorFilter(android.graphics.Color.parseColor("#192033"));
                holder.itemView.setOnLongClickListener(v -> {
                    showContactOptionsDialog(doc);
                    return true;
                });
            }

            holder.itemView.setOnClickListener(v -> Toast.makeText(MainActivity.this, "Opening Contact Ledger for " + name, Toast.LENGTH_SHORT).show());
        }

        @Override
        public int getItemCount() { return contactList.size(); }

        class ContactViewHolder extends RecyclerView.ViewHolder {
            TextView tvContactName, tvContactPhone; ImageView imgContactIcon;
            public ContactViewHolder(@NonNull View itemView) {
                super(itemView);
                tvContactName = itemView.findViewById(R.id.tv_contact_name);
                tvContactPhone = itemView.findViewById(R.id.tv_contact_phone);
                imgContactIcon = itemView.findViewById(R.id.img_contact_icon);
            }
        }
    }
}