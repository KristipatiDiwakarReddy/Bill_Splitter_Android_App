package com.example.bs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private LinearLayout containerLayout;
    private EditText numOfPeopleEditText;
    private Button submitButton;

    private ArrayList<String> namesList;
    private ArrayList<Integer> spentList;
    private ArrayList<Integer> paidList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayout = findViewById(R.id.rel);
        containerLayout = findViewById(R.id.cont);
        numOfPeopleEditText = findViewById(R.id.people);
        submitButton = findViewById(R.id.submit);

        namesList = new ArrayList<>();
        spentList = new ArrayList<>();
        paidList = new ArrayList<>();

        submitButton.setVisibility(View.GONE);

        numOfPeopleEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    int numOfPeople;
                    try {
                        numOfPeople = Integer.parseInt(numOfPeopleEditText.getText().toString());
                    } catch (NumberFormatException e) {
                        numOfPeople = 0;
                    }

                    containerLayout.removeAllViews();

                    for (int i = 0; i < numOfPeople; i++) {
                        EditText namesEditText = new EditText(MainActivity.this);
                        EditText spentEditText = new EditText(MainActivity.this);
                        EditText paidEditText = new EditText(MainActivity.this);

                        namesEditText.setHint("Name of person "+(i+1));
                        spentEditText.setHint("Spent amount of person "+(i+1));
                        paidEditText.setHint("Paid amount of person "+(i+1));

                        spentEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        paidEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

                        containerLayout.addView(namesEditText);
                        containerLayout.addView(spentEditText);
                        containerLayout.addView(paidEditText);
                    }

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    if (numOfPeople > 1) {
                        submitButton.setVisibility(View.VISIBLE);
                    } else {
                        submitButton.setVisibility(View.GONE);
                    }

                    return true;
                }
                return false;
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namesList.clear();
                spentList.clear();
                paidList.clear();

                for (int i = 0; i < containerLayout.getChildCount(); i += 3) {
                    EditText namesEditText = (EditText) containerLayout.getChildAt(i);
                    EditText spentEditText = (EditText) containerLayout.getChildAt(i + 1);
                    EditText paidEditText = (EditText) containerLayout.getChildAt(i + 2);

                    String name = namesEditText.getText().toString().trim();
                    String spent = spentEditText.getText().toString().trim();
                    String paid = paidEditText.getText().toString().trim();

                    // Check if any field is empty
                    if (name.isEmpty() || spent.isEmpty() || paid.isEmpty()) {
                        // Display error message
                        Toast.makeText(MainActivity.this, "Please enter all values properly", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    namesList.add(namesEditText.getText().toString());
                    spentList.add(Integer.parseInt(spentEditText.getText().toString()));
                    paidList.add(Integer.parseInt(paidEditText.getText().toString()));
                }

                int numOfPeople = namesList.size();

                Splitter[] p = new Splitter[numOfPeople];
                int[] amount = new int[numOfPeople];
                int sumSpent = 0;
                int sumPaid = 0;

                for (int i = 0; i < numOfPeople; i++) {
                    p[i] = new Splitter();
                    p[i].name = namesList.get(i);
                    p[i].spent = spentList.get(i);
                    p[i].paid = paidList.get(i);
                    p[i].netAmount = p[i].paid - p[i].spent;

                    sumSpent += p[i].spent;
                    sumPaid += p[i].paid;
                }

                if (sumSpent != sumPaid) {
                    Log.d("MainActivity", "Wrong Input - Sum of Spent amount must be equal to Paid amount");
                    Toast.makeText(MainActivity.this, "Wrong Input - Sum of Spent amount must be equal to Paid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (int i = 0; i < numOfPeople; i++) {
                    amount[i] = p[i].netAmount;
                }

                ArrayList<String> transactions = new ArrayList<>();
                minCashFlowRec(amount, numOfPeople, p, transactions);

                // Pass transactions to TransactionActivity
                Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                intent.putStringArrayListExtra("transactions", transactions);
                startActivity(intent);
            }
        });
    }

    static int getMin(int arr[], int N) {
        int minInd = 0;
        for (int i = 1; i < N; i++)
            if (arr[i] < arr[minInd])
                minInd = i;
        return minInd;
    }

    static int getMax(int arr[], int N) {
        int maxInd = 0;
        for (int i = 1; i < N; i++)
            if (arr[i] > arr[maxInd])
                maxInd = i;
        return maxInd;
    }

    static int minOf2(int x, int y) {
        return (x < y) ? x : y;
    }

    static void minCashFlowRec(int amount[], int N, Splitter p[], ArrayList<String> transactions) {
        int mxCredit = getMax(amount, N);
        int mxDebit = getMin(amount, N);

        if (amount[mxCredit] == 0 && amount[mxDebit] == 0)
            return;

        int min = minOf2(-amount[mxDebit], amount[mxCredit]);
        amount[mxCredit] -= min;
        amount[mxDebit] += min;

        String transaction = p[mxDebit].name + " pays " + min + " to " + p[mxCredit].name;
        transactions.add(transaction);

        Log.d("MainActivity", transaction);

        minCashFlowRec(amount, N, p, transactions);
    }

    static class Splitter {
        String name;
        int spent;
        int paid;
        int netAmount;
    }
}