package com.cluster.ledgerpro;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import yuku.ambilwarna.AmbilWarnaDialog;

@SuppressLint("SetTextI18n")
public class AddCardActivity extends AppCompatActivity {

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

    private TextInputEditText etCardName, etLast4Digits, etTotalLimit, etBillingDay, etCashbackRates;
    private MaterialAutoCompleteTextView spinBankName, spinCardType;
    private TextInputLayout tilBankName, tilCardType, tilCashbackRates;
    private MaterialCardView cardColorPreview;
    private MaterialSwitch switchCashback;
    private MaterialButton btnSaveCard;
    private TextView tvActivityTitle;

    private final int[] currentColor = {Color.parseColor("#192033")};
    private final int defaultColor = Color.parseColor("#192033");

    private String editCardId = null;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Force Light Theme to fix AmbilWarnaDialog dark background issue
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_card);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainAddCard), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(0, insets.top, 0, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userEmail = currentUser.getEmail();
        }

        initializeViews();
        setupDropdowns();
        checkForEditMode();
        setupClickListeners();
    }

    private void initializeViews() {
        tvActivityTitle = findViewById(R.id.tvActivityTitle);
        etCardName = findViewById(R.id.etCardName);
        etLast4Digits = findViewById(R.id.etLast4Digits);
        etTotalLimit = findViewById(R.id.etTotalLimit);
        etBillingDay = findViewById(R.id.etBillingDay);

        etCashbackRates = findViewById(R.id.etCashbackRates);
        tilCashbackRates = findViewById(R.id.tilCashbackRates);

        spinBankName = findViewById(R.id.spinBankName);
        tilBankName = findViewById(R.id.tilBankName);

        spinCardType = findViewById(R.id.spinCardType);
        tilCardType = findViewById(R.id.tilCardType);

        cardColorPreview = findViewById(R.id.cardColorPreview);
        switchCashback = findViewById(R.id.switchCashback);
        btnSaveCard = findViewById(R.id.btnSaveCard);

        tilBankName.setEndIconMode(TextInputLayout.END_ICON_NONE);
        tilCardType.setEndIconMode(TextInputLayout.END_ICON_NONE);

        switchCashback.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tilCashbackRates.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked && TextUtils.isEmpty(etCashbackRates.getText())) {
                etCashbackRates.setText("1, 2, 5");
            }
        });
    }

    private void checkForEditMode() {
        if (getIntent() != null && getIntent().hasExtra("CARD_ID")) {
            editCardId = getIntent().getStringExtra("CARD_ID");
            tvActivityTitle.setText("Edit Credit Card");
            btnSaveCard.setText("Update Card");

            tilBankName.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
            tilBankName.setEndIconTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#667085")));

            tilCardType.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
            tilCardType.setEndIconTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#667085")));

            etCardName.setText(getIntent().getStringExtra("CARD_NAME"));
            spinBankName.setText(getIntent().getStringExtra("BANK_NAME"), false);
            spinCardType.setText(getIntent().getStringExtra("CARD_TYPE"), false);
            etLast4Digits.setText(getIntent().getStringExtra("LAST4"));

            double limit = getIntent().getDoubleExtra("TOTAL_LIMIT", 0.0);
            etTotalLimit.setText(limit > 0 ? String.valueOf((int) limit) : "");

            int bDay = getIntent().getIntExtra("BILLING_DAY", 1);
            etBillingDay.setText(String.valueOf(bDay));

            double[] ratesArray = getIntent().getDoubleArrayExtra("CASHBACK_RATES");
            if (ratesArray != null && ratesArray.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ratesArray.length; i++) {
                    if (ratesArray[i] == (long) ratesArray[i]) {
                        sb.append(String.format(Locale.getDefault(), "%d", (long) ratesArray[i]));
                    } else {
                        sb.append(ratesArray[i]);
                    }
                    if (i < ratesArray.length - 1) sb.append(", ");
                }
                etCashbackRates.setText(sb.toString());
            }

            boolean isCb = getIntent().getBooleanExtra("IS_CASHBACK", false);
            switchCashback.setChecked(isCb);
            tilCashbackRates.setVisibility(isCb ? View.VISIBLE : View.GONE);

            try {
                String themeColorStr = getIntent().getStringExtra("THEME_COLOR");
                if (themeColorStr != null && !themeColorStr.isEmpty()) {
                    currentColor[0] = Integer.parseInt(themeColorStr);
                    cardColorPreview.setCardBackgroundColor(currentColor[0]);
                }
            } catch (Exception ignored) {}
        } else {
            etCardName.requestFocus();
            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), etCardName);
            controller.show(WindowInsetsCompat.Type.ime());
        }
    }

    private void setupDropdowns() {
        GradientDrawable roundedDropdownBackground = new GradientDrawable();
        roundedDropdownBackground.setColor(Color.WHITE);
        float cornerRadius = 12 * getResources().getDisplayMetrics().density;
        roundedDropdownBackground.setCornerRadius(cornerRadius);

        spinBankName.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        spinBankName.setAdapter(getBankArrayAdapter());
        spinBankName.setDropDownBackgroundDrawable(roundedDropdownBackground);

        spinBankName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (spinBankName.hasFocus() && s.length() > 0) spinBankName.showDropDown();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        spinCardType.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        String[] cardTypes = new String[]{"RuPay", "Visa", "MasterCard", "Amex", "Discover", "Other"};
        ArrayAdapter<String> cardTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cardTypes);
        spinCardType.setAdapter(cardTypeAdapter);
        spinCardType.setDropDownBackgroundDrawable(roundedDropdownBackground);

        spinCardType.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (spinCardType.hasFocus() && s.length() > 0) spinCardType.showDropDown();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupClickListeners() {
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnClearAllFields).setOnClickListener(v -> clearAllFields());

        findViewById(R.id.btnResetColor).setOnClickListener(v -> {
            currentColor[0] = defaultColor;
            cardColorPreview.setCardBackgroundColor(defaultColor);
        });

        cardColorPreview.setOnClickListener(v -> {
            AmbilWarnaDialog colorPickerDialog = new AmbilWarnaDialog(this, currentColor[0], new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override public void onCancel(AmbilWarnaDialog dialog) {}
                @Override public void onOk(AmbilWarnaDialog dialog, int color) {
                    currentColor[0] = color;
                    cardColorPreview.setCardBackgroundColor(color);
                }
            });
            colorPickerDialog.show();
        });

        btnSaveCard.setOnClickListener(v -> validateAndSaveCard());
    }

    private void clearAllFields() {
        etCardName.setText("");
        spinBankName.setText("", false);
        spinCardType.setText("", false);
        etLast4Digits.setText("");
        etTotalLimit.setText("");
        etBillingDay.setText("");
        etCashbackRates.setText("");
        switchCashback.setChecked(false);
        tilCashbackRates.setVisibility(View.GONE);

        currentColor[0] = defaultColor;
        cardColorPreview.setCardBackgroundColor(defaultColor);

        etCardName.setError(null);
        spinBankName.setError(null);
        spinCardType.setError(null);
        etLast4Digits.setError(null);
        etTotalLimit.setError(null);
        etBillingDay.setError(null);
        etCashbackRates.setError(null);

        etCardName.requestFocus();
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

    private void validateAndSaveCard() {
        String cardName = String.valueOf(etCardName.getText()).trim();
        String bankName = String.valueOf(spinBankName.getText()).trim();
        String cardType = String.valueOf(spinCardType.getText()).trim();
        String last4 = String.valueOf(etLast4Digits.getText()).trim();
        String limitStr = String.valueOf(etTotalLimit.getText()).trim();
        String billingDayStr = String.valueOf(etBillingDay.getText()).trim();
        boolean isCashback = switchCashback.isChecked();
        String ratesStr = String.valueOf(etCashbackRates.getText()).trim();

        if (TextUtils.isEmpty(cardName)) { etCardName.setError("Required"); return; }
        if (TextUtils.isEmpty(bankName)) { spinBankName.setError("Required"); return; }

        boolean isValidBank = false;
        String matchedBankName = bankName;
        for (String validBank : BANK_INITIALS_MAP.keySet()) {
            if (validBank.equalsIgnoreCase(bankName)) {
                isValidBank = true;
                matchedBankName = validBank;
                break;
            }
        }

        if (!isValidBank) {
            spinBankName.setError("Please select a valid bank from the list");
            spinBankName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cardType)) { spinCardType.setError("Required"); return; }

        String[] validCardTypes = new String[]{"RuPay", "Visa", "MasterCard", "Amex", "Discover", "Other"};
        boolean isValidCardType = false;
        for (String type : validCardTypes) {
            if (type.equalsIgnoreCase(cardType)) {
                isValidCardType = true;
                break;
            }
        }

        if (!isValidCardType) {
            spinCardType.setError("Please select a valid card type from the list");
            spinCardType.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(last4) || last4.length() != 4) { etLast4Digits.setError("Enter 4 digits"); return; }
        if (TextUtils.isEmpty(limitStr)) { etTotalLimit.setError("Required"); return; }
        if (TextUtils.isEmpty(billingDayStr)) { etBillingDay.setError("Required"); return; }

        double totalLimit = Double.parseDouble(limitStr);
        int billingDay = Integer.parseInt(billingDayStr);
        if (billingDay < 1 || billingDay > 31) {
            etBillingDay.setError("Enter 1-31");
            return;
        }

        List<Double> ratesList = new ArrayList<>();
        if (isCashback) {
            if (TextUtils.isEmpty(ratesStr)) {
                etCashbackRates.setError("Enter at least one percentage");
                etCashbackRates.requestFocus();
                return;
            }

            String[] parts = ratesStr.split(",");
            boolean hasDuplicate = false;

            for (String part : parts) {
                try {
                    double val = Double.parseDouble(part.replace("%", "").trim());
                    if (val > 0) {
                        if (ratesList.contains(val)) {
                            hasDuplicate = true;
                            break;
                        } else {
                            ratesList.add(val);
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }

            if (hasDuplicate) {
                etCashbackRates.setError("Duplicate percentages are not allowed");
                etCashbackRates.requestFocus();
                return;
            }

            if (ratesList.isEmpty()) {
                etCashbackRates.setError("Enter valid numbers (e.g., 1, 2, 5)");
                etCashbackRates.requestFocus();
                return;
            }

            Collections.sort(ratesList);
        }

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Error: User session not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String cardIdToSave;
        if (editCardId != null) {
            cardIdToSave = editCardId;
        } else {
            String shortName = BANK_INITIALS_MAP.get(matchedBankName);
            if (shortName == null) {
                shortName = matchedBankName.length() >= 3 ? matchedBankName.substring(0, 3) : matchedBankName;
            }
            cardIdToSave = String.format("%s_%s_%s", generateRandomAlphanumeric(), shortName, last4).toUpperCase();
        }

        Map<String, Object> cardMap = new HashMap<>();
        cardMap.put("bank_name", matchedBankName);
        cardMap.put("card_name", cardName);
        cardMap.put("network", cardType);
        cardMap.put("account_id", last4);
        cardMap.put("credit_limit", totalLimit);
        cardMap.put("current_balance", 0.0);
        cardMap.put("billing_day", billingDay);
        cardMap.put("theme_color", currentColor[0]);
        cardMap.put("cashback_enabled", isCashback);
        cardMap.put("cashback_rates", ratesList);

        if (editCardId == null) {
            cardMap.put("created_at", System.currentTimeMillis());
        }

        btnSaveCard.setEnabled(false);
        btnSaveCard.setText(editCardId != null ? "Updating..." : "Saving...");

        db.collection("users").document(userEmail).collection("Credit Cards").document(cardIdToSave)
                .set(cardMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, editCardId != null ? "Card Updated!" : "Card Saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error occurred";
                        Toast.makeText(this, "Failed to save: " + errorMessage, Toast.LENGTH_SHORT).show();
                        btnSaveCard.setEnabled(true);
                        btnSaveCard.setText(editCardId != null ? "Update Card" : "Save Card");
                    }
                });
    }

    private ArrayAdapter<String> getBankArrayAdapter() {
        List<String> bankList = new ArrayList<>(BANK_INITIALS_MAP.keySet());

        return new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(bankList)) {
            @NonNull
            @Override
            public Filter getFilter() {
                return new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results = new FilterResults();
                        if (constraint == null || constraint.length() == 0) {
                            results.values = bankList;
                            results.count = bankList.size();
                        } else {
                            List<String> filteredList = new ArrayList<>();
                            String filterPattern = constraint.toString().toLowerCase().trim();
                            for (String bank : bankList) {
                                if (bank.toLowerCase().contains(filterPattern)) {
                                    filteredList.add(bank);
                                }
                            }
                            results.values = filteredList;
                            results.count = filteredList.size();
                        }
                        return results;
                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        clear();
                        if (results.values != null) addAll((List<String>) results.values);
                        notifyDataSetChanged();
                    }
                };
            }
        };
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof android.widget.EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    boolean clickedAnotherEditText = false;
                    int[] editIds = {
                            R.id.etCardName, R.id.spinBankName, R.id.spinCardType,
                            R.id.etLast4Digits, R.id.etTotalLimit, R.id.etBillingDay, R.id.etCashbackRates
                    };
                    Rect rect = new Rect();
                    for (int id : editIds) {
                        View editView = findViewById(id);
                        if (editView != null && editView.isShown()) {
                            editView.getGlobalVisibleRect(rect);
                            if (rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                                clickedAnotherEditText = true;
                                break;
                            }
                        }
                    }
                    if (!clickedAnotherEditText) {
                        v.clearFocus();
                        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), v);
                        controller.hide(WindowInsetsCompat.Type.ime());
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}