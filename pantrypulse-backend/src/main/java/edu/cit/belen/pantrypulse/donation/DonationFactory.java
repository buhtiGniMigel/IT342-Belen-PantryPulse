package edu.cit.belen.pantrypulse.donation;

public class DonationFactory {
    private DonationFactory() {}

    public static Donation getDonation(String donationType) {
        if (donationType == null) return null;
        
        if (donationType.equalsIgnoreCase("FOOD")) {
            return new FoodDonation();
        } else if (donationType.equalsIgnoreCase("CASH")) {
            return new CashDonation();
        }
        return null;
    }
}