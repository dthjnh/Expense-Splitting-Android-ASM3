package com.example.expensesplitting.AccountInfo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private TextView privacyPolicyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        privacyPolicyText = findViewById(R.id.privacy_policy);

        setPrivacyPolicyContent();
    }

    private void setPrivacyPolicyContent() {
        String content = "1. Information We Collect:\n" +
                "• We collect user-provided information for account creation and bill management.\n" +
                "• Automatically collected data includes device information, usage patterns, and location (with user consent).\n\n" +
                "2. How We Use Your Information:\n" +
                "• Personal information is used for account management, bill-splitting calculations, and communication.\n" +
                "• Non-personal data helps us improve our services, analyze trends, and enhance user experience.\n\n" +
                "3. Data Security:\n" +
                "• We employ industry-standard measures to safeguard user data.\n" +
                "• We do not sell or share personal information with third parties for marketing purposes.\n\n" +
                "4. Third-Party Services:\n" +
                "• Splitify may integrate with third-party services for specific functionalities.\n" +
                "• Users are encouraged to review the privacy policies of linked services.";

        SpannableString spannableString = new SpannableString(content);

        setBoldSpan(spannableString, "1. Information We Collect:");
        setBoldSpan(spannableString, "2. How We Use Your Information:");
        setBoldSpan(spannableString, "3. Data Security:");
        setBoldSpan(spannableString, "4. Third-Party Services:");

        privacyPolicyText.setText(spannableString);
    }

    private void setBoldSpan(SpannableString spannableString, String sectionTitle) {
        int start = spannableString.toString().indexOf(sectionTitle);
        int end = start + sectionTitle.length();
        if (start >= 0) {
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public void onBackPressed(View view) {
        startActivity(new Intent(this, HelpSupportActivity.class));
    }
}