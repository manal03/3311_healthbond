package UnitTests;

import models.FoodGroupData;
import models.UserProfile;
import org.junit.jupiter.api.Test;
import services.FoodGroupService;
import services.strategies.CFG2007AlignmentStrategy;
import services.strategies.CFG2019AlignmentStrategy;

import static org.junit.jupiter.api.Assertions.*;

public class FoodGroupServiceTest {

    @Test
    public void testCFG2019AlignmentScore() {
        UserProfile user = new UserProfile(1, "1990-01-01", 70, 175, "male", "Test User", "metric");

        FoodGroupData perfectData = new FoodGroupData(40, 10, 25, 25, 0);

        FoodGroupService.setAlignmentStrategy(new CFG2019AlignmentStrategy());

        double score = FoodGroupService.calculateAlignmentScore(perfectData, user);

        assertTrue(score >= 95.0);
        assertEquals("Canada's Food Guide 2019", FoodGroupService.getGuidelineName());
        assertFalse(FoodGroupService.includesDairy());
    }

    @Test
    public void testCFG2007AlignmentScore() {
        UserProfile user = new UserProfile(1, "1990-01-01", 70, 175, "male", "Test User", "metric");

        FoodGroupData goodData = new FoodGroupData(35, 15, 25, 20, 5);

        FoodGroupService.setAlignmentStrategy(new CFG2007AlignmentStrategy());

        double score = FoodGroupService.calculateAlignmentScore(goodData, user);

        assertTrue(score >= 85.0);
        assertEquals("Canada's Food Guide 2007", FoodGroupService.getGuidelineName());
        assertTrue(FoodGroupService.includesDairy());
    }

    @Test
    public void testEmptyFoodData() {
        UserProfile user = new UserProfile(1, "1990-01-01", 70, 175, "male", "Test User", "metric");

        FoodGroupData emptyData = new FoodGroupData();

        FoodGroupService.setAlignmentStrategy(new CFG2019AlignmentStrategy());
        double score2019 = FoodGroupService.calculateAlignmentScore(emptyData, user);
        assertEquals(0.0, score2019);

        FoodGroupService.setAlignmentStrategy(new CFG2007AlignmentStrategy());
        double score2007 = FoodGroupService.calculateAlignmentScore(emptyData, user);
        assertEquals(0.0, score2007);
    }
}