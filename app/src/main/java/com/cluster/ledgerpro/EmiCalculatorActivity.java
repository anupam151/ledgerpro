package com.cluster.ledgerpro;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class EmiCalculatorActivity extends AppCompatActivity {

    // --- UI Component Declarations ---
    private TextInputEditText etPrincipal, etInterestRate, etTenure, etProcessingFee, etPrivilegeCharge;
    private TextInputEditText etTxnDate, etBillDate;

    private LinearLayout layoutProRataDates;
    private SwitchMaterial switchProRata;
    private MaterialButton btnCalculate;

    private MaterialCardView cardSummary, cardTable;
    private TextView tvSummary;
    private TableLayout tableAmortization;
    private ImageView btnBack;
    private NestedScrollView scrollView;

    // --- Formatters & Date Storage ---
    private final DecimalFormat currencyFormat = new DecimalFormat("₹#,##0.00");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private Calendar calTxnDate = null;
    private Calendar calBillDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_emi_calculator);

        WindowInsetsControllerCompat windowController = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowController.setAppearanceLightStatusBars(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        scrollView = findViewById(R.id.scroll_view);
        etPrincipal = findViewById(R.id.etPrincipal);
        etInterestRate = findViewById(R.id.etInterestRate);
        etTenure = findViewById(R.id.etTenure);
        etProcessingFee = findViewById(R.id.etProcessingFee);
        etPrivilegeCharge = findViewById(R.id.etPrivilegeCharge);

        etTxnDate = findViewById(R.id.etTxnDate);
        etBillDate = findViewById(R.id.etBillDate);
        layoutProRataDates = findViewById(R.id.layoutProRataDates);

        switchProRata = findViewById(R.id.switchProRata);
        btnCalculate = findViewById(R.id.btnCalculate);
        cardSummary = findViewById(R.id.cardSummary);
        cardTable = findViewById(R.id.cardTable);
        tvSummary = findViewById(R.id.tvSummary);
        tableAmortization = findViewById(R.id.tableAmortization);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        switchProRata.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutProRataDates.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                etTxnDate.setText("");
                etBillDate.setText("");
                calTxnDate = null;
                calBillDate = null;
            }
        });

        etTxnDate.setOnClickListener(v -> showDatePicker(true));
        etBillDate.setOnClickListener(v -> showDatePicker(false));
        btnCalculate.setOnClickListener(v -> calculateBreakdown());
    }

    private void showDatePicker(boolean isTxnDate) {
        Calendar defaultDate = Calendar.getInstance();
        if (isTxnDate && calTxnDate != null) defaultDate = calTxnDate;
        if (!isTxnDate && calBillDate != null) defaultDate = calBillDate;

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth, 0, 0, 0);
            selectedDate.set(Calendar.MILLISECOND, 0);

            String formattedDate = dateFormat.format(selectedDate.getTime());

            if (isTxnDate) {
                calTxnDate = selectedDate;
                etTxnDate.setText(formattedDate);
            } else {
                calBillDate = selectedDate;
                etBillDate.setText(formattedDate);
            }
        }, defaultDate.get(Calendar.YEAR), defaultDate.get(Calendar.MONTH), defaultDate.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private double round(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @SuppressLint("SetTextI18n")
    private void calculateBreakdown() {
        String principalStr = String.valueOf(etPrincipal.getText()).trim();
        String rateStr = String.valueOf(etInterestRate.getText()).trim();
        String tenureStr = String.valueOf(etTenure.getText()).trim();
        String pfStr = String.valueOf(etProcessingFee.getText()).trim();
        String privStr = String.valueOf(etPrivilegeCharge.getText()).trim();

        if (TextUtils.isEmpty(principalStr) || TextUtils.isEmpty(rateStr) || TextUtils.isEmpty(tenureStr)) {
            Toast.makeText(this, "Principal, Rate, and Tenure are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- UX Polish: Hide keyboard and remove cursor focus from inputs ---
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            currentFocus.clearFocus();
        }

        double principal = Double.parseDouble(principalStr);
        double annualRate = Double.parseDouble(rateStr);
        int months = Integer.parseInt(tenureStr);
        double processingFee = TextUtils.isEmpty(pfStr) ? 0.0 : Double.parseDouble(pfStr);
        double totalPrivilege = TextUtils.isEmpty(privStr) ? 0.0 : Double.parseDouble(privStr);

        double monthlyPrivilege = round(totalPrivilege / months);

        double monthlyRate = (annualRate / 100.0) / 12.0;
        double dailyRate = (annualRate / 100.0) / 360.0;

        double standardEmi;
        if (annualRate > 0) {
            standardEmi = (principal * monthlyRate * Math.pow(1 + monthlyRate, months))
                    / (Math.pow(1 + monthlyRate, months) - 1);
        } else {
            standardEmi = principal / months;
        }

        long activeDays = 0;
        if (switchProRata.isChecked()) {
            if (calTxnDate == null || calBillDate == null) {
                Toast.makeText(this, "Please select both dates for pro rata math.", Toast.LENGTH_SHORT).show();
                return;
            }

            long diffInMillis = calBillDate.getTimeInMillis() - calTxnDate.getTimeInMillis();
            long daysToBill = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

            if (daysToBill < 0) {
                Toast.makeText(this, "First Bill Date cannot be before the Transaction Date.", Toast.LENGTH_SHORT).show();
                return;
            }
            activeDays = daysToBill + 20;
        }

        tableAmortization.removeAllViews();
        addTableRow(true, "#", "Date", "Principal (P)", "Interest(I)", "Total (P+I)", "GST", "Other Charges", "Total");

        double balance = principal;
        double totalInterestAccumulated = 0;
        double totalGstAccumulated = 0;
        double proRataInterestLog = 0;

        for (int i = 1; i <= months; i++) {
            double standardInterest = round(balance * monthlyRate);

            double currentPrincipal = (i == months) ? balance : round(standardEmi - standardInterest);
            double currentInterest;

            if (i == 1 && switchProRata.isChecked()) {
                currentInterest = round(balance * dailyRate * activeDays);
                proRataInterestLog = currentInterest;
            } else {
                currentInterest = standardInterest;
            }

            double baseTotal = currentPrincipal + currentInterest;
            double gstOnInterest = round(currentInterest * 0.18);

            double actualMonthlyPrivilege = (i == months) ? round(totalPrivilege - (monthlyPrivilege * (months - 1))) : monthlyPrivilege;
            double totalChargedToContact = baseTotal + gstOnInterest + actualMonthlyPrivilege;

            Calendar emiCal;
            if (calBillDate != null) {
                emiCal = (Calendar) calBillDate.clone();
                emiCal.add(Calendar.MONTH, i - 1);
            } else {
                emiCal = Calendar.getInstance();
                emiCal.add(Calendar.MONTH, i);
            }
            String rowDateStr = dateFormat.format(emiCal.getTime());

            balance = round(balance - currentPrincipal);
            if (balance < 0) balance = 0;

            totalInterestAccumulated += currentInterest;
            totalGstAccumulated += gstOnInterest;

            addTableRow(false,
                    String.valueOf(i),
                    rowDateStr,
                    currencyFormat.format(currentPrincipal),
                    currencyFormat.format(currentInterest),
                    currencyFormat.format(baseTotal),
                    currencyFormat.format(gstOnInterest),
                    currencyFormat.format(actualMonthlyPrivilege),
                    currencyFormat.format(totalChargedToContact)
            );
        }

        // --- Summary Card Update ---
        double pfGst = round(processingFee * 0.18);
        double totalPf = processingFee + pfGst;
        double totalPayableToBank = principal + totalInterestAccumulated + totalGstAccumulated + totalPf;
        double totalRecoveredFromContact = totalPayableToBank + totalPrivilege;

        StringBuilder summary = new StringBuilder();
        summary.append("<b>Total Principal:</b> ").append(currencyFormat.format(principal)).append("<br/>");
        summary.append("<b>Total Bank Interest:</b> ").append(currencyFormat.format(totalInterestAccumulated)).append("<br/>");
        summary.append("<b>Total GST (18% on Interest):</b> ").append(currencyFormat.format(totalGstAccumulated)).append("<br/><br/>");

        if (processingFee > 0) {
            summary.append("<b>PF (Base):</b> ").append(currencyFormat.format(processingFee))
                    .append(" | <b>PF GST:</b> ").append(currencyFormat.format(pfGst))
                    .append(" | <b>Total PF:</b> ").append(currencyFormat.format(totalPf)).append("<br/><br/>");
        }

        if (switchProRata.isChecked()) {
            summary.append("<b>Pro rata (").append(activeDays).append(" Days) Included in Month 1:</b><br/>");
            summary.append("Interest: ").append(currencyFormat.format(proRataInterestLog))
                    .append(" | GST: ").append(currencyFormat.format(round(proRataInterestLog * 0.18))).append("<br/><br/>");
        }

        summary.append("<b>Total Bank Liability:</b> ").append(currencyFormat.format(totalPayableToBank)).append("<br/>");
        summary.append("<b>Total Contact Liability (w/ Privilege):</b> <font color='#192033'><b>")
                .append(currencyFormat.format(totalRecoveredFromContact)).append("</b></font>");

        tvSummary.setText(android.text.Html.fromHtml(summary.toString(), android.text.Html.FROM_HTML_MODE_COMPACT));
        cardSummary.setVisibility(View.VISIBLE);
        cardTable.setVisibility(View.VISIBLE);

        // --- UX Polish: Smooth scroll to the summary/table section ---
        scrollView.postDelayed(() -> scrollView.smoothScrollTo(0, cardSummary.getTop()), 150);
    }

    private void addTableRow(boolean isHeader, String... values) {
        TableRow row = new TableRow(this);
        row.setPadding(0, 8, 0, 8);

        for (String value : values) {
            TextView tv = new TextView(this);
            tv.setText(value);
            tv.setPadding(16, 12, 16, 12);
            tv.setGravity(Gravity.CENTER);

            tv.setTextColor(isHeader ? Color.WHITE : Color.parseColor("#333333"));
            tv.setTextSize(isHeader ? 14 : 13);

            if (isHeader) {
                tv.setTypeface(null, Typeface.BOLD);
                tv.setBackgroundColor(Color.parseColor("#192033"));
            } else {
                tv.setBackgroundResource(R.drawable.border_bottom);
            }
            row.addView(tv);
        }
        tableAmortization.addView(row);
    }
}