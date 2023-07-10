package com.example.bs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class TransactionActivity extends AppCompatActivity {
    private TextView transactionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        transactionTextView = findViewById(R.id.transactionTextView);

        // Retrieve the transactions from the intent
        ArrayList<String> transactions = getIntent().getStringArrayListExtra("transactions");

        // Display the transactions
        StringBuilder stringBuilder = new StringBuilder();
        for (String transaction : transactions) {
            stringBuilder.append(transaction).append("\n");
        }

        transactionTextView.setText(stringBuilder.toString());
    }

    public void go_back(View view) {

        Intent intent = new Intent(TransactionActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
