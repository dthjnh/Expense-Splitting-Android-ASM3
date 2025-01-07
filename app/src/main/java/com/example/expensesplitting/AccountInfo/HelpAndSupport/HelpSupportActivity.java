package com.example.expensesplitting.AccountInfo.HelpAndSupport;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.AccountInfo.AccountActivity;
import com.example.expensesplitting.ChatBox.UserChatActivity;
import com.example.expensesplitting.R;

public class HelpSupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help_support);

    }

    public void onBackPressed(View view) {
        startActivity(new Intent(this, AccountActivity.class));
    }

    public void privacyPolicy(View view) {
        startActivity(new Intent(this, PrivacyPolicyActivity.class));
    }

    public void termsOfService(View view) {
        startActivity(new Intent(this, TermsOfServiceActivity.class));
    }

    public void contactSupport(View view) {
        startActivity(new Intent(this, UserChatActivity.class));
    }
}

