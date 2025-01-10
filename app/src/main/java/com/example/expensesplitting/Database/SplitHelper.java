package com.example.expensesplitting.Database;

import android.util.Log;

import com.example.expensesplitting.Group.Participant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplitHelper {

    private List<Participant> participants;
    private Map<String, Double> splitDetails; // Store the amount each participant owes
    private double totalAmount;

    public SplitHelper(List<Participant> participants, double totalAmount) {
        this.participants = participants;
        this.totalAmount = totalAmount;
        this.splitDetails = new HashMap<>();
    }


    public void divideEqually() {
        splitDetails.clear();
        double equalShare = totalAmount / participants.size();
        for (Participant participant : participants) {
            splitDetails.put(participant.getName(), equalShare);
            participant.setAmount(equalShare);
        }
    }

    public void divideUnequally(Map<String, Double> amounts) {
        if (amounts == null || amounts.isEmpty()) {
            Log.e("SplitHelper", "Amounts map is null or empty. Cannot perform unequal split.");
            return;
        }

        splitDetails.clear();
        for (Participant participant : participants) {
            double assignedAmount = amounts.getOrDefault(participant.getName(), 0.0);
            splitDetails.put(participant.getName(), assignedAmount);
            participant.setAmount(assignedAmount);
        }
        Log.d("SplitHelper", "Unequal Split Details Updated: " + splitDetails);
    }

    public Map<String, Double> calculateBalances(long groupId) {
        Map<String, Double> balances = new HashMap<>();
        double perPersonShare = totalAmount / participants.size();

        for (Participant participant : participants) {
            double paid = participant.getPaidAmount();
            double owes = perPersonShare - paid;
            balances.put(participant.getName(), owes);
        }

        return balances;
    }

    public Map<String, Double> getSplitDetails() {
        return splitDetails;
    }
}