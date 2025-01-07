package com.example.expensesplitting.AccountInfo.HelpAndSupport;

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

public class TermsOfServiceActivity extends AppCompatActivity {

    private TextView termsOfServiceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_service);

        termsOfServiceText = findViewById(R.id.terms_of_service_text);

        setTermsOfServiceContent();
    }

    private void setTermsOfServiceContent() {
        String content = "1. Acceptance of Terms:\n" +
                "By using Splitify, you agree to comply with these terms and our privacy policy.\n\n" +
                "2. User Responsibilities:\n" +
                "Users are responsible for the accuracy of information provided and maintaining the confidentiality of their account.\n\n" +
                "3. Bill Management:\n" +
                "• Splitify calculates bill splits based on user input.\n" +
                "• Users are responsible for reviewing and confirming expenses.\n\n" +
                "4. Premium Subscriptions:\n" +
                "• Premium features are subject to the selected subscription plan.\n" +
                "• Users can cancel subscriptions at any time, and benefits continue until the subscription period ends.\n\n" +
                "5. Termination of Service:\n" +
                "• We reserve the right to terminate or suspend accounts for violation of terms or misuse of the app.";

        SpannableString spannableString = new SpannableString(content);

        setBoldSpan(spannableString, "1. Acceptance of Terms:");
        setBoldSpan(spannableString, "2. User Responsibilities:");
        setBoldSpan(spannableString, "3. Bill Management:");
        setBoldSpan(spannableString, "4. Premium Subscriptions:");
        setBoldSpan(spannableString, "5. Termination of Service:");

        termsOfServiceText.setText(spannableString);
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