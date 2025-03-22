# GenericDAO Documentation

## Overview

`GenericDAO` is a flexible, type-safe Data Access Object implementation that provides comprehensive CRUD (Create, Read, Update, Delete) operations for entity objects using Hibernate ORM. This class serves as a reusable foundation for database interactions across different entity types in your application.

## Features

- Type-safe generic implementation supporting any entity type
- Comprehensive CRUD operations
- Advanced search capabilities with criteria-based filtering
- Pagination support
- Sorting functionality 
- JSON serialization of entities
- Exception handling with custom `DAOException`
- Date handling utilities
- Named query execution support
- Logging for all database operations

## Class Definition

```java
public class GenericDAO<T, ID extends Serializable>
```

### Type Parameters

- `T` - The entity type this DAO manages
- `ID` - The type of the entity's primary key (must implement `Serializable`)

## Constructor

```java
public GenericDAO(Class<T> entityClass, SessionFactory sessionFactory)
```

Creates a new instance of `GenericDAO` for the specified entity class.

### Parameters

- `entityClass` - The class object of the entity this DAO will manage
- `sessionFactory` - The Hibernate SessionFactory to use for database operations

## Core CRUD Operations

### Create

```java
public T create(T entity) throws DAOException
```

Persists a new entity to the database.

#### Parameters
- `entity` - The entity to persist

#### Returns
- The persisted entity with potentially generated ID

#### Exceptions
- `DAOException` - If an error occurs during the operation

---

### Read

```java
public Optional<T> findById(ID id) throws DAOException
```

Retrieves an entity by its ID.

#### Parameters
- `id` - The ID of the entity to retrieve

#### Returns
- An `Optional` containing the entity if found, empty otherwise

#### Exceptions
- `DAOException` - If an error occurs during the operation

---

```java
public List<T> findAll() throws DAOException
```

Retrieves all entities of the managed type.

#### Returns
- A list of all entities, or an empty list if none exist

#### Exceptions
- `DAOException` - If an error occurs during the operation

---

### Update

```java
public T update(T entity) throws DAOException
```

Updates an existing entity in the database.

#### Parameters
- `entity` - The entity to update

#### Returns
- The updated entity

#### Exceptions
- `DAOException` - If an error occurs during the operation

---

### Delete

```java
public void delete(T entity) throws DAOException
```

Deletes an entity from the database.

#### Parameters
- `entity` - The entity to delete

#### Exceptions
- `DAOException` - If an error occurs during the operation

---

```java
public boolean deleteById(ID id) throws DAOException
```

Deletes an entity by its ID.

#### Parameters
- `id` - The ID of the entity to delete

#### Returns
- `true` if the entity was deleted, `false` if no entity with the given ID exists

#### Exceptions
- `DAOException` - If an error occurs during the operation

## Additional Query Methods

### Field-Based Queries

```java
public List<T> findByField(String fieldName, Object value) throws DAOException
```

Finds entities by a specific field value.

#### Parameters
- `fieldName` - The name of the field to match
- `value` - The value to match

#### Returns
- A list of matching entities, or an empty list if none match

#### Exceptions
- `DAOException` - If an error occurs during the operation

---

```java
public Optional<T> findUniqueByField(String fieldName, Object value) throws DAOException
```

Finds a unique entity by a specific field value.

#### Parameters
- `fieldName` - The name of the field to match
- `value` - The value to match

#### Returns
- An `Optional` containing the entity if found, empty otherwise

#### Exceptions
- `DAOException` - If an error occurs during the operation or multiple entities are found

### Named Queries

```java
public List<T> executeNamedQuery(String queryName, Map<String, Object> parameters) throws DAOException
```

Executes a named query with parameters.

#### Parameters
- `queryName` - The name of the query to execute
- `parameters` - Parameters to bind to the query

#### Returns
- A list of results, or an empty list if no results

#### Exceptions
- `DAOException` - If an error occurs during the operation

### Advanced Search

```java
public List<T> search(Map<String, Object> criteria, String sortBy, boolean ascending, 
                      int offset, int limit) throws DAOException
```

Searches for entities using multiple criteria with pagination.

#### Parameters
- `criteria` - A map of field names and their values to match
- `sortBy` - The field to sort by
- `ascending` - Whether to sort in ascending order
- `offset` - The starting index of results
- `limit` - The maximum number of results to return

#### Returns
- A list of entities matching the criteria

#### Exceptions
- `DAOException` - If an error occurs during the operation

### Date-Based Queries

```java
public List<T> findCreatedToday(String dateField) throws DAOException
```

Finds entities created today.

#### Parameters
- `dateField` - The name of the date field to check

#### Returns
- A list of entities created today

#### Exceptions
- `DAOException` - If an error occurs during the operation

## Utility Methods

### Count Entities

```java
public long count() throws DAOException
```

Counts all entities of the managed type.

#### Returns
- The total count of entities

#### Exceptions
- `DAOException` - If an error occurs during the operation

### JSON Conversion

```java
public String toJson(T entity)
```

Converts an entity to its JSON representation.

#### Parameters
- `entity` - The entity to convert

#### Returns
- A JSON string representing the entity

---

```java
public String toJson(List<T> entities)
```

Converts a list of entities to its JSON representation.

#### Parameters
- `entities` - The list of entities to convert

#### Returns
- A JSON string representing the list of entities

### Date Parsing

```java
protected Date parseDate(String dateString, String format) throws ParseException
```

Converts a date string to a Date object.

#### Parameters
- `dateString` - The date string to convert
- `format` - The format of the date string

#### Returns
- The converted Date object

#### Exceptions
- `ParseException` - If the date string cannot be parsed

### Error Handling

```java
protected String createErrorResponse(int errorCode, String errorMessage)
```

Creates an error response in JSON format.

#### Parameters
- `errorCode` - The error code
- `errorMessage` - The error message

#### Returns
- A JSON string representing the error

## Exception Handling

The DAO implements a custom exception class for handling database operation failures:

```java
public static class DAOException extends Exception {
    public DAOException(String message);
    public DAOException(String message, Throwable cause);
}
```

All methods that interact with the database throw this exception type to provide meaningful error messages.

## Usage Examples

### Creating an Entity-Specific DAO

```java
public class UserDAO extends GenericDAO<User, Long> {
    public UserDAO(SessionFactory sessionFactory) {
        super(User.class, sessionFactory);
    }
    
    // Add User-specific methods here
}
```

### Basic CRUD Operations

```java
// Create a new user
User newUser = new User("john.doe@example.com", "John Doe");
try {
    userDAO.create(newUser);
} catch (DAOException e) {
    logger.error("Failed to create user", e);
}

// Find a user by ID
try {
    Optional<User> userOpt = userDAO.findById(123L);
    userOpt.ifPresent(user -> System.out.println("Found user: " + user.getName()));
} catch (DAOException e) {
    logger.error("Failed to find user", e);
}

// Update a user
try {
    User user = userDAO.findById(123L).orElseThrow();
    user.setName("John Smith");
    userDAO.update(user);
} catch (DAOException e) {
    logger.error("Failed to update user", e);
}

// Delete a user
try {
    boolean deleted = userDAO.deleteById(123L);
    System.out.println("User deleted: " + deleted);
} catch (DAOException e) {
    logger.error("Failed to delete user", e);
}
```

### Advanced Search Example

```java
// Search for active users created in the last month, sorted by name
Map<String, Object> criteria = new HashMap<>();
criteria.put("status", "ACTIVE");

try {
    List<User> recentUsers = userDAO.search(criteria, "name", true, 0, 20);
    System.out.println("Found " + recentUsers.size() + " active users");
} catch (DAOException e) {
    logger.error("Failed to search users", e);
}
```

## Best Practices

1. **Transaction Management**: Each CRUD operation manages its own transaction, but for operations that require multiple database interactions, consider implementing service-level transaction management.

2. **Entity Design**: Ensure your entity classes have proper equals() and hashCode() implementations for reliable session caching.

3. **Exception Handling**: Always catch and handle DAOException in calling code to prevent transaction leaks.

4. **Session Management**: The DAO automatically manages Hibernate Sessions using try-with-resources blocks. No manual session closing is necessary.

5. **Logging**: Enable Hibernate SQL logging during development to help debug queries.

## Dependencies

- Hibernate ORM (5.x or higher)
- Google Gson (for JSON serialization)
- Java Persistence API (JPA)

## License

This code is available under the MIT License.

## Contributing

Feel free to submit issues or pull requests to improve this generic DAO implementation.
