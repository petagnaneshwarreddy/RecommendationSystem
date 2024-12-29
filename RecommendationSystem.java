import java.util.*;

public class RecommendationSystem {

    private static final Map<String, Map<String, Integer>> userItemRatings = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Welcome to the Recommendation System!");
        loadSampleData();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. View Recommendations");
            System.out.println("2. Add New User");
            System.out.println("3. Add Rating for Existing User");
            System.out.println("4. Display All Ratings");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter the user name for recommendations: ");
                    String user = scanner.nextLine();
                    if (userItemRatings.containsKey(user)) {
                        List<String> recommendations = getRecommendations(user);
                        System.out.println("Recommendations for " + user + ": " + recommendations);
                    } else {
                        System.out.println("User not found!");
                    }
                }
                case 2 -> addNewUser(scanner);
                case 3 -> addRating(scanner);
                case 4 -> displayAllRatings();
                case 5 -> {
                    System.out.println("Exiting the system. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void loadSampleData() {
        userItemRatings.put("Alice", Map.of("Item1", 5, "Item2", 3, "Item3", 4));
        userItemRatings.put("Bob", Map.of("Item1", 3, "Item2", 4, "Item4", 2));
        userItemRatings.put("Charlie", Map.of("Item2", 5, "Item3", 3, "Item4", 4));
        userItemRatings.put("David", Map.of("Item1", 4, "Item3", 5, "Item4", 3));
    }

    private static void addNewUser(Scanner scanner) {
        System.out.print("Enter the new user name: ");
        String newUser = scanner.nextLine();

        if (userItemRatings.containsKey(newUser)) {
            System.out.println("User already exists!");
            return;
        }

        Map<String, Integer> ratings = new HashMap<>();
        while (true) {
            System.out.print("Enter item name (or type 'done' to finish): ");
            String item = scanner.nextLine();
            if (item.equalsIgnoreCase("done")) break;

            System.out.print("Enter rating for " + item + " (1-5): ");
            int rating = scanner.nextInt();
            scanner.nextLine();
            ratings.put(item, rating);
        }
        userItemRatings.put(newUser, ratings);
        System.out.println("User " + newUser + " added successfully!");
    }

    private static void addRating(Scanner scanner) {
        System.out.print("Enter the user name: ");
        String user = scanner.nextLine();

        if (!userItemRatings.containsKey(user)) {
            System.out.println("User not found! Please add the user first.");
            return;
        }

        Map<String, Integer> ratings = userItemRatings.get(user);
        while (true) {
            System.out.print("Enter item name (or type 'done' to finish): ");
            String item = scanner.nextLine();
            if (item.equalsIgnoreCase("done")) break;

            System.out.print("Enter rating for " + item + " (1-5): ");
            int rating = scanner.nextInt();
            scanner.nextLine(); 
            ratings.put(item, rating);
        }
        System.out.println("Ratings updated successfully for user " + user + "!");
    }

    private static void displayAllRatings() {
        System.out.println("\nAll Ratings:");
        for (Map.Entry<String, Map<String, Integer>> entry : userItemRatings.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private static List<String> getRecommendations(String user) {
        Map<String, Integer> targetRatings = userItemRatings.get(user);
        Map<String, Double> recommendationScores = new HashMap<>();

        for (Map.Entry<String, Map<String, Integer>> entry : userItemRatings.entrySet()) {
            String otherUser = entry.getKey();
            Map<String, Integer> otherRatings = entry.getValue();

            if (otherUser.equals(user)) continue;

          
            double similarity = calculateSimilarity(targetRatings, otherRatings);

           
            for (Map.Entry<String, Integer> itemEntry : otherRatings.entrySet()) {
                String item = itemEntry.getKey();
                int rating = itemEntry.getValue();

                if (!targetRatings.containsKey(item)) {
                    recommendationScores.put(item, recommendationScores.getOrDefault(item, 0.0) + similarity * rating);
                }
            }
        }

        
        return recommendationScores.entrySet()
                .stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .toList();
    }

    private static double calculateSimilarity(Map<String, Integer> ratings1, Map<String, Integer> ratings2) {
        Set<String> commonItems = new HashSet<>(ratings1.keySet());
        commonItems.retainAll(ratings2.keySet());

        if (commonItems.isEmpty()) return 0.0;

       
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String item : commonItems) {
            int rating1 = ratings1.get(item);
            int rating2 = ratings2.get(item);
            dotProduct += rating1 * rating2;
            norm1 += Math.pow(rating1, 2);
            norm2 += Math.pow(rating2, 2);
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
