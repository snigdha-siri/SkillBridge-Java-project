import java.util.*;

public class SkillBridge {

    static Scanner sc = new Scanner(System.in);

    static ArrayList<Candidate> candidates = new ArrayList<>();
    static ArrayList<Opportunity> opportunities = new ArrayList<>();

    // Candidate Class
    static class Candidate {

        String name;
        TreeSet<String> skills = new TreeSet<>();

        Candidate(String name, String skillsInput) {

            this.name = name;

            for (String skill : skillsInput.split(",")) {

                if (!skill.trim().isEmpty()) {
                    skills.add(skill.trim().toLowerCase());
                }
            }
        }
    }

    // Opportunity Class
    static class Opportunity {

        String title;
        String company;
        TreeSet<String> requiredSkills = new TreeSet<>();

        Opportunity(String title, String company, String skillsInput) {

            this.title = title;
            this.company = company;

            for (String skill : skillsInput.split(",")) {

                if (!skill.trim().isEmpty()) {
                    requiredSkills.add(skill.trim().toLowerCase());
                }
            }
        }
    }

    // Main Method
    public static void main(String[] args) {

        loadSampleOpportunities();

        System.out.println("======================================");
        System.out.println("       WELCOME TO SKILLBRIDGE");
        System.out.println(" Skill-to-Opportunity Matching Platform");
        System.out.println("======================================");

        int choice;

        do {

            showMenu();

            choice = readInteger("Enter your choice: ");

            switch (choice) {

                case 1:
                    addCandidate();
                    break;

                case 2:
                    addOpportunity();
                    break;

                case 3:
                    findMatches();
                    break;

                case 4:
                    viewAllCandidates();
                    break;

                case 5:
                    viewAllOpportunities();
                    break;

                case 6:
                    System.out.println("\nThank you for using SkillBridge!");
                    System.out.println("Keep learning and improving your skills.");
                    break;

                default:
                    System.out.println("Invalid choice. Enter a number from 1 to 6.");
            }

        } while (choice != 6);

        sc.close();
    }

    // Display Main Menu
    static void showMenu() {

        System.out.println("\n========== SKILLBRIDGE MAIN MENU ==========");
        System.out.println("1. Add Candidate");
        System.out.println("2. Add Opportunity");
        System.out.println("3. Find Matches");
        System.out.println("4. View All Candidates");
        System.out.println("5. View All Opportunities");
        System.out.println("6. Exit");
        System.out.println("===========================================");
    }

    // Read Integer Safely
    static int readInteger(String message) {

        while (true) {

            System.out.print(message);

            String input = sc.nextLine().trim();

            try {
                return Integer.parseInt(input);
            }

            catch (NumberFormatException e) {

                System.out.println("Please enter a valid number.");
            }
        }
    }

    // Add Candidate
    static void addCandidate() {

        System.out.println("\n========== ADD CANDIDATE ==========");

        System.out.print("Enter candidate name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter skills separated by commas: ");
        String skills = sc.nextLine().trim();

        if (name.isEmpty() || skills.isEmpty()) {

            System.out.println("Name and skills cannot be empty.");
            return;
        }

        Candidate candidate = new Candidate(name, skills);

        if (candidate.skills.isEmpty()) {

            System.out.println("Please enter at least one skill.");
            return;
        }

        candidates.add(candidate);

        System.out.println("Candidate added successfully!");
    }

    // Add Opportunity
    static void addOpportunity() {

        System.out.println("\n========== ADD OPPORTUNITY ==========");

        System.out.print("Enter opportunity title: ");
        String title = sc.nextLine().trim();

        System.out.print("Enter company name: ");
        String company = sc.nextLine().trim();

        System.out.print("Enter required skills separated by commas: ");
        String skills = sc.nextLine().trim();

        if (title.isEmpty() || company.isEmpty() || skills.isEmpty()) {

            System.out.println("All fields are required.");
            return;
        }

        Opportunity opportunity =
                new Opportunity(title, company, skills);

        if (opportunity.requiredSkills.isEmpty()) {

            System.out.println("Please enter at least one required skill.");
            return;
        }

        opportunities.add(opportunity);

        System.out.println("Opportunity added successfully!");
    }

    // Find Matching Opportunities
    static void findMatches() {

        if (candidates.isEmpty()) {

            System.out.println("\nNo candidates available. Add a candidate first.");
            return;
        }

        if (opportunities.isEmpty()) {

            System.out.println("\nNo opportunities available.");
            return;
        }

        viewAllCandidates();

        int id = readInteger("\nEnter candidate ID: ");

        if (id < 1 || id > candidates.size()) {

            System.out.println("Invalid candidate ID.");
            return;
        }

        Candidate candidate = candidates.get(id - 1);

        // Copy opportunities for ranking
        ArrayList<Opportunity> ranked =
                new ArrayList<>(opportunities);

        // Sort by matching percentage
        ranked.sort((a, b) ->
                Double.compare(
                        calculateMatch(candidate, b),
                        calculateMatch(candidate, a)
                )
        );

        System.out.println("\n========== MATCHING RESULTS ==========");

        System.out.println("Candidate: " + candidate.name);
        System.out.println("Skills: " + candidate.skills);

        // Display each opportunity
        for (Opportunity opportunity : ranked) {

            // Find matched skills
            TreeSet<String> matched =
                    new TreeSet<>(candidate.skills);

            matched.retainAll(opportunity.requiredSkills);

            // Find missing skills
            TreeSet<String> missing =
                    new TreeSet<>(opportunity.requiredSkills);

            missing.removeAll(candidate.skills);

            // Calculate percentage
            double percentage =
                    calculateMatch(candidate, opportunity);

            System.out.println("\n--------------------------------------");

            System.out.println("Opportunity: " + opportunity.title);
            System.out.println("Company: " + opportunity.company);

            System.out.printf(
                    "Match Percentage: %.2f%%\n",
                    percentage
            );

            System.out.println("Matched Skills: " + matched);
            System.out.println("Missing Skills: " + missing);

            // Learning Recommendations
            System.out.println("\nLearning Recommendations:");

            if (missing.isEmpty()) {

                System.out.println("Great! You have all the required skills.");
            }

            else {

                int count = 1;

                for (String skill : missing) {

                    System.out.println(
                            "\n" + count + ". " + skill.toUpperCase()
                    );

                    System.out.println(
                            "   Suggestion: " + getLearningSuggestion(skill)
                    );

                    count++;
                }
            }
        }

        System.out.println("\n======================================");
        System.out.println("Higher matching percentages are shown first.");
        System.out.println("A skill match does not guarantee job selection.");
    }

    // Calculate Matching Percentage
    static double calculateMatch(
            Candidate candidate, Opportunity opportunity) {

        if (opportunity.requiredSkills.isEmpty()) {
            return 0.0;
        }

        TreeSet<String> matched =
                new TreeSet<>(candidate.skills);

        matched.retainAll(opportunity.requiredSkills);

        return matched.size() * 100.0
                / opportunity.requiredSkills.size();
    }

    // View All Candidates
    static void viewAllCandidates() {

        System.out.println("\n========== ALL CANDIDATES ==========");

        if (candidates.isEmpty()) {

            System.out.println("No candidates added yet.");
            return;
        }

        for (int i = 0; i < candidates.size(); i++) {

            Candidate candidate = candidates.get(i);

            System.out.println("\nCandidate ID: " + (i + 1));
            System.out.println("Name: " + candidate.name);
            System.out.println("Skills: " + candidate.skills);
        }
    }

    // View All Opportunities
    static void viewAllOpportunities() {

        System.out.println("\n========== ALL OPPORTUNITIES ==========");

        if (opportunities.isEmpty()) {

            System.out.println("No opportunities available.");
            return;
        }

        for (int i = 0; i < opportunities.size(); i++) {

            Opportunity opportunity = opportunities.get(i);

            System.out.println("\nOpportunity ID: " + (i + 1));
            System.out.println("Title: " + opportunity.title);
            System.out.println("Company: " + opportunity.company);

            System.out.println(
                    "Required Skills: " + opportunity.requiredSkills
            );
        }
    }

    // Learning Suggestion Method
    static String getLearningSuggestion(String skill) {

        switch (skill.toLowerCase()) {

            case "java":
                return "Learn Java basics, OOP, collections and build projects.";

            case "sql":
                return "Practise SELECT, WHERE, JOIN and GROUP BY queries.";

            case "html":
                return "Learn HTML tags, forms, links and page structure.";

            case "css":
                return "Learn styling, colours, layouts and responsive design.";

            case "python":
                return "Learn variables, conditions, loops and functions.";

            case "git":
                return "Practise repositories, commits and version control.";

            case "excel":
                return "Practise formulas, sorting, filtering and charts.";

            case "oop":
                return "Learn classes, objects, inheritance and polymorphism.";

            case "problem solving":
                return "Practise programming problems and basic algorithms.";

            case "ui ux design":
                return "Learn wireframes, user research and usability principles.";

            case "javascript":
                return "Learn variables, functions, loops and DOM basics.";

            default:
                return "Find beginner lessons, practise exercises and build a small project.";
        }
    }

    // Load Sample Opportunities
    static void loadSampleOpportunities() {

        opportunities.add(new Opportunity(
                "Java Developer Intern",
                "Sample Company A",
                "Java,OOP,SQL,Git"
        ));

        opportunities.add(new Opportunity(
                "Data Analyst Intern",
                "Sample Company B",
                "SQL,Excel,Python"
        ));

        opportunities.add(new Opportunity(
                "Software Developer Intern",
                "Sample Company C",
                "Java,OOP,Problem Solving"
        ));

        opportunities.add(new Opportunity(
                "Web Development Intern",
                "Sample Company D",
                "HTML,CSS,JavaScript"
        ));

        opportunities.add(new Opportunity(
                "UI/UX Design Intern",
                "Sample Company E",
                "UI UX Design,HTML,CSS"
        ));
    }
}