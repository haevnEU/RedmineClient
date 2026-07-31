# Redmine Client

This library is a basic client for interacting with Redmine's REST API. 
It provides a simple interface to perform CRUD operations on Redmine resources such as issues, projects, users, and more.

# Example Usage

```java
public static void main(String[] args) {
    final RedmineClient client = new RedmineClient("https://your-redmine-url.com", "your-api-key");
    try {
        final List<Issue> myIssues = client.getMyAssignedIssues(QueryParams.values());
        for (final Issue issue : myIssues) {
            System.out.printf("[%d] %s (Status: %s)%n", issue.id(), issue.subject(),
                issue.status() != null ? issue.status().name() : "N/A");

            if (issue.assignedTo() != null) {
                System.out.println("   Zugewiesen an: " + issue.assignedTo().name());
            }

            if (issue.attachments() != null && !issue.attachments().isEmpty()) {
                System.out.println("   Dateianhänge: " + issue.attachments().size());
            }
        }
    } catch (RedmineException e) {
        throw new RuntimeException(e);
    }    
}
````