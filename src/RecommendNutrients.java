import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RecommendNutrients implements RecommendationInterface {

	@Override
	public Optional<Map<String, Double>> findForUser(UserProfile user) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}
	
}
