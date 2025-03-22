package example;

import dao.GenericDAO;
import dao.QueryCondition;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Example demonstrating how to use the improved GenericDAO implementation.
 */
public class DAOExample {

    public static void main(String[] args) {
        // Initialize Hibernate SessionFactory
        SessionFactory sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
        
        try {
            // Create a DAO for User entities
            GenericDAO<User, Long> userDao = new GenericDAO<>(User.class, sessionFactory);
            
            // Create example
            User newUser = new User("johndoe", "John", "Doe", "john@example.com");
            try {
                userDao.create(newUser);
                System.out.println("User created: " + userDao.toJson(newUser));
            } catch (GenericDAO.DAOException e) {
                System.err.println("Error creating user: " + e.getMessage());
            }
            
            // Find by ID example
            try {
                Optional<User> userById = userDao.findById(1L);
                userById.ifPresent(user -> System.out.println("Found user: " + userDao.toJson(user)));
            } catch (GenericDAO.DAOException e) {
                System.err.println("Error finding user: " + e.getMessage());
            }
            
            // Update example
            try {
                Optional<User> foundUser = userDao.findById(1L);
                if (foundUser.isPresent()) {
                    User user = foundUser.get();
                    user.setEmail("john.doe@example.com");
                    userDao.update(user);
                    System.out.println("User updated: " + userDao.toJson(user));
                }
            } catch (GenericDAO.DAOException e) {
                System.err.println("Error updating user: " + e.getMessage());
            }
            
            // Find by field example
            try {
                List<User> usersWithEmail = userDao.findByField("email", "john.doe@example.com");
                System.out.println("Users with email john.doe@example.com: " + userDao.toJson(usersWithEmail));
            } catch (GenericDAO.DAOException e) {
                System.err.println("Error finding users by email: " + e.getMessage());
            }
            
            // Search with criteria example
            try {
                Map<String, Object> criteria = new HashMap<>();
                criteria.put("firstName", "John");
                criteria.put("active", true);
                
                List<User> searchResults = userDao.search(criteria, "lastName", true, 0, 10);
                System.out.println("Search results: " + userDao.toJson(searchResults));
            } catch (GenericDAO.DAOException e) {
                System.err.println("Error searching users: " + e.getMessage());
            }
            
            // Using QueryCondition for complex queries
            QueryCondition nameCondition = QueryCondition.like("firstName", "Jo%");
            QueryCondition dateCondition = QueryCondition.dateBetween("creationDate", 
                    new Date(System.currentTimeMillis() - 30*24*60*60*1000L), new Date());
            System.out.println("Complex query conditions created: " + 
                    Arrays.asList(nameCondition, dateCondition));
            
            // Delete example
            try {
                boolean deleted = userDao.deleteById(1L);
                System.out.println("User deleted: " + deleted);
            } catch (GenericDAO.DAOException e) {
                System.err.println("Error deleting user: " + e.getMessage());
            }
            
        } finally {
            // Clean up resources
            sessionFactory.close();
        }
    }
    
    // Example entity class
    public static class User {
        private Long id;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private boolean active;
        private Date creationDate;
        
        public User() {
            this.active = true;
            this.creationDate = new Date();
        }
        
        public User(String username, String firstName, String lastName, String email) {
            this();
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        
        public Date getCreationDate() { return creationDate; }
        public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }
        
        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", email='" + email + '\'' +
                    ", active=" + active +
                    ", creationDate=" + creationDate +
                    '}';
        }
    }
}
