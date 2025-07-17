import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
public interface RecommendationInterface {
	Optional<Map<String, Double>> findForUser(UserProfile user);
}
