package com.example.expensesplitting.Database;

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

    /**
     * Divides the total amount equally among participants.
     */
    public void divideEqually() {
        splitDetails.clear();
        double equalShare = totalAmount / participants.size();
        for (Participant participant : participants) {
            splitDetails.put(participant.getName(), equalShare);
            participant.setAmount(equalShare);
        }
    }

    /**
     * Divides the total amount unequally (manual input for each participant).
     *
     * @param amounts A map containing the amounts assigned to each participant.
     */
    public void divideUnequally(Map<String, Double> amounts) {
        splitDetails.clear();
        for (Participant participant : participants) {
            double assignedAmount = amounts.getOrDefault(participant.getName(), 0.0);
            splitDetails.put(participant.getName(), assignedAmount);
            participant.setAmount(assignedAmount);
        }
    }

    /**
     * Calculates the balances for each participant.
     *
     * @return A map where the key is the participant's name and the value is the balance.
     */
    public Map<String, Double> calculateBalances(long groupId) {
        Map<String, Double> balances = new HashMap<>();
        double perPersonShare = totalAmount / participants.size();

        for (Participant participant : participants) {
            double paid = participant.getPaidAmount(); // Assume Participant class has a `getPaidAmount()` method
            double owes = perPersonShare - paid;
            balances.put(participant.getName(), owes);
        }

        return balances;
    }

    /**
     * Gets the split details (amounts for each participant).
     *
     * @return A map containing the split amounts for each participant.
     */
    public Map<String, Double> getSplitDetails() {
        return splitDetails;
    }
}

