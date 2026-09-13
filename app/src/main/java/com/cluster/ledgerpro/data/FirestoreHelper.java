package com.cluster.ledgerpro.data;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class FirestoreHelper {

    private static final String TAG = "FirestoreHelper";
    private final FirebaseFirestore db;
    private final String userId; // This now stores the Email instead of the UID

    public interface FirestoreCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().getEmail() != null) {
            // Changed to use getEmail() instead of getUid()
            this.userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        } else {
            this.userId = "";
            Log.e(TAG, "FirestoreHelper initialized without an authenticated user!");
        }
    }

    public void saveAccount(String accountId, String name, String type, double creditLimit,
                            String statementDate, String dueDate, FirestoreCallback<Void> callback) {
        if (userId.isEmpty()) {
            callback.onError(new IllegalStateException("User not authenticated"));
            return;
        }

        Map<String, Object> accountData = new HashMap<>();
        accountData.put("account_id", accountId);
        accountData.put("name", name);
        accountData.put("type", type);
        accountData.put("credit_limit", creditLimit);
        accountData.put("statement_date", statementDate);
        accountData.put("due_date", dueDate);
        accountData.put("current_balance", 0.0);

        db.collection("users").document(userId)
                .collection("accounts").document(accountId)
                .set(accountData)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    public void saveContact(String contactId, String name, double initialNetBalance, FirestoreCallback<Void> callback) {
        if (userId.isEmpty()) {
            callback.onError(new IllegalStateException("User not authenticated"));
            return;
        }

        Map<String, Object> contactData = new HashMap<>();
        contactData.put("contact_id", contactId);
        contactData.put("name", name);
        contactData.put("net_balance", initialNetBalance);

        db.collection("users").document(userId)
                .collection("contacts").document(contactId)
                .set(contactData)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    public void createTransaction(String txId, String merchant, double totalAmount, String accountId,
                                  String masterStatus, List<Map<String, Object>> initialLifecycle,
                                  FirestoreCallback<Void> callback) {
        if (userId.isEmpty()) {
            callback.onError(new IllegalStateException("User not authenticated"));
            return;
        }

        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("tx_id", txId);
        transactionData.put("merchant", merchant);
        transactionData.put("total_amount", totalAmount);
        transactionData.put("account_id", accountId);
        transactionData.put("master_status", masterStatus);
        transactionData.put("lifecycle", initialLifecycle);
        transactionData.put("created_at", System.currentTimeMillis());

        db.collection("users").document(userId)
                .collection("transactions").document(txId)
                .set(transactionData)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    @SuppressWarnings("unchecked")
    public void appendTransactionLifecycle(String txId, Map<String, Object> newLifecycleSubEntry,
                                           String updatedMasterStatus, FirestoreCallback<Void> callback) {
        if (userId.isEmpty()) {
            callback.onError(new IllegalStateException("User not authenticated"));
            return;
        }

        DocumentReference txRef = db.collection("users").document(userId)
                .collection("transactions").document(txId);

        db.runTransaction(snapshotTransaction -> {
                    DocumentSnapshot snapshot = snapshotTransaction.get(txRef);
                    Object rawLifecycle = snapshot.get("lifecycle");
                    List<Map<String, Object>> lifecycle;

                    if (rawLifecycle instanceof List) {
                        lifecycle = (List<Map<String, Object>>) rawLifecycle;
                    } else {
                        lifecycle = new ArrayList<>();
                    }

                    lifecycle.add(newLifecycleSubEntry);

                    snapshotTransaction.update(txRef, "lifecycle", lifecycle);
                    snapshotTransaction.update(txRef, "master_status", updatedMasterStatus);
                    return null;
                }).addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }
}