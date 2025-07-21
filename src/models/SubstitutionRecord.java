package models;

import java.time.LocalDate;

public class SubstitutionRecord {
    private final int originalFoodId;
    private final int substituteFoodId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDate dateApplied;

    public SubstitutionRecord(int originalFoodId, int substituteFoodId, LocalDate startDate, LocalDate endDate, LocalDate dateApplied) {
        this.originalFoodId = originalFoodId;
        this.substituteFoodId = substituteFoodId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dateApplied = dateApplied;
    }

    public int getOriginalFoodId() {
        return originalFoodId;
    }

    public int getSubstituteFoodId() {
        return substituteFoodId;
    }

    public LocalDate getDateApplied() {
        return dateApplied;
    }
}
