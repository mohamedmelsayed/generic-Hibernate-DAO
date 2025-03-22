package dao;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * Generic Data Access Object (DAO) implementation providing CRUD operations
 * for entity objects using Hibernate.
 *
 * @author Improved version
 * @param <T> The entity type this DAO manages
 * @param <ID> The type of the entity's primary key
 */
public class GenericDAO<T, ID extends Serializable> {

    private static final Logger LOGGER = Logger.getLogger(GenericDAO.class.getName());
    private final Class<T> entityClass;
    private final SessionFactory sessionFactory;
    private final Gson gson;

    /**
     * Constructs a new GenericDAO for the specified entity class.
     * 
     * @param entityClass The class of the entity this DAO manages
     * @param sessionFactory The Hibernate SessionFactory to use
     */
    public GenericDAO(Class<T> entityClass, SessionFactory sessionFactory) {
        this.entityClass = entityClass;
        this.sessionFactory = sessionFactory;
        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeNulls()
                .create();
    }

    /**
     * Opens a new Hibernate session.
     * 
     * @return A new Hibernate Session
     */
    protected Session openSession() {
        return sessionFactory.openSession();
    }

    /**
     * Persists a new entity to the database.
     * 
     * @param entity The entity to persist
     * @return The persisted entity with potentially generated ID
     * @throws DAOException If an error occurs during the operation
     */
    public T create(T entity) throws DAOException {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(entity);
                tx.commit();
                LOGGER.log(Level.INFO, "Entity created successfully: {0}", entity);
                return entity;
            } catch (HibernateException ex) {
                tx.rollback();
                LOGGER.log(Level.SEVERE, "Error creating entity", ex);
                throw new DAOException("Failed to create entity", ex);
            }
        }
    }

    /**
     * Retrieves an entity by its ID.
     * 
     * @param id The ID of the entity to retrieve
     * @return An Optional containing the entity if found, empty otherwise
     * @throws DAOException If an error occurs during the operation
     */
    public Optional<T> findById(ID id) throws DAOException {
        try (Session session = openSession()) {
            T entity = session.get(entityClass, id);
            return Optional.ofNullable(entity);
        } catch (HibernateException ex) {
            LOGGER.log(Level.SEVERE, "Error finding entity by ID: " + id, ex);
            throw new DAOException("Failed to find entity by ID: " + id, ex);
        }
    }

    /**
     * Retrieves all entities of the managed type.
     * 
     * @return A list of all entities, or an empty list if none exist
     * @throws DAOException If an error occurs during the operation
     */
    public List<T> findAll() throws DAOException {
        try (Session session = openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            cq.select(root);
            
            return session.createQuery(cq).getResultList();
        } catch (HibernateException ex) {
            LOGGER.log(Level.SEVERE, "Error finding all entities", ex);
            throw new DAOException("Failed to retrieve all entities", ex);
        }
    }

    /**
     * Updates an existing entity in the database.
     * 
     * @param entity The entity to update
     * @return The updated entity
     * @throws DAOException If an error occurs during the operation
     */
    public T update(T entity) throws DAOException {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                T mergedEntity = (T) session.merge(entity);
                tx.commit();
                LOGGER.log(Level.INFO, "Entity updated successfully: {0}", entity);
                return mergedEntity;
            } catch (HibernateException ex) {
                tx.rollback();
                LOGGER.log(Level.SEVERE, "Error updating entity", ex);
                throw new DAOException("Failed to update entity", ex);
            }
        }
    }

    /**
     * Deletes an entity from the database.
     * 
     * @param entity The entity to delete
     * @throws DAOException If an error occurs during the operation
     */
    public void delete(T entity) throws DAOException {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.remove(entity);
                tx.commit();
                LOGGER.log(Level.INFO, "Entity deleted successfully: {0}", entity);
            } catch (HibernateException ex) {
                tx.rollback();
                LOGGER.log(Level.SEVERE, "Error deleting entity", ex);
                throw new DAOException("Failed to delete entity", ex);
            }
        }
    }

    /**
     * Deletes an entity by its ID.
     * 
     * @param id The ID of the entity to delete
     * @return true if the entity was deleted, false if no entity with the given ID exists
     * @throws DAOException If an error occurs during the operation
     */
    public boolean deleteById(ID id) throws DAOException {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                T entity = session.get(entityClass, id);
                if (entity != null) {
                    session.remove(entity);
                    tx.commit();
                    LOGGER.log(Level.INFO, "Entity deleted successfully with ID: {0}", id);
                    return true;
                } else {
                    tx.rollback();
                    LOGGER.log(Level.INFO, "No entity found with ID: {0}", id);
                    return false;
                }
            } catch (HibernateException ex) {
                tx.rollback();
                LOGGER.log(Level.SEVERE, "Error deleting entity with ID: " + id, ex);
                throw new DAOException("Failed to delete entity with ID: " + id, ex);
            }
        }
    }

    /**
     * Finds entities by a specific field value.
     * 
     * @param fieldName The name of the field to match
     * @param value The value to match
     * @return A list of matching entities, or an empty list if none match
     * @throws DAOException If an error occurs during the operation
     */
    public List<T> findByField(String fieldName, Object value) throws DAOException {
        try (Session session = openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            cq.select(root).where(cb.equal(root.get(fieldName), value));
            
            return session.createQuery(cq).getResultList();
        } catch (HibernateException ex) {
            LOGGER.log(Level.SEVERE, "Error finding entities by field: " + fieldName, ex);
            throw new DAOException("Failed to find entities by field: " + fieldName, ex);
        }
    }

    /**
     * Finds a unique entity by a specific field value.
     * 
     * @param fieldName The name of the field to match
     * @param value The value to match
     * @return An Optional containing the entity if found, empty otherwise
     * @throws DAOException If an error occurs during the operation or multiple entities are found
     */
    public Optional<T> findUniqueByField(String fieldName, Object value) throws DAOException {
        List<T> results = findByField(fieldName, value);
        
        if (results.isEmpty()) {
            return Optional.empty();
        } else if (results.size() > 1) {
            throw new DAOException("Multiple entities found with " + fieldName + " = " + value);
        } else {
            return Optional.of(results.get(0));
        }
    }

    /**
     * Executes a named query with parameters.
     * 
     * @param queryName The name of the query to execute
     * @param parameters Parameters to bind to the query
     * @return A list of results, or an empty list if no results
     * @throws DAOException If an error occurs during the operation
     */
    public List<T> executeNamedQuery(String queryName, Map<String, Object> parameters) throws DAOException {
        try (Session session = openSession()) {
            Query<T> query = session.createNamedQuery(queryName, entityClass);
            
            if (parameters != null) {
                parameters.forEach(query::setParameter);
            }
            
            return query.getResultList();
        } catch (HibernateException ex) {
            LOGGER.log(Level.SEVERE, "Error executing named query: " + queryName, ex);
            throw new DAOException("Failed to execute named query: " + queryName, ex);
        }
    }

    /**
     * Searches for entities using multiple criteria with pagination.
     * 
     * @param criteria A map of field names and their values to match
     * @param sortBy The field to sort by
     * @param ascending Whether to sort in ascending order
     * @param offset The starting index of results
     * @param limit The maximum number of results to return
     * @return A list of entities matching the criteria
     * @throws DAOException If an error occurs during the operation
     */
    public List<T> search(Map<String, Object> criteria, String sortBy, boolean ascending, 
                         int offset, int limit) throws DAOException {
        try (Session session = openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            
            // Add search criteria
            if (criteria != null && !criteria.isEmpty()) {
                List<Predicate> predicates = new ArrayList<>();
                
                criteria.forEach((field, value) -> {
                    if (value instanceof String) {
                        predicates.add(cb.like(root.get(field), "%" + value + "%"));
                    } else {
                        predicates.add(cb.equal(root.get(field), value));
                    }
                });
                
                cq.where(predicates.toArray(new Predicate[0]));
            }
            
            // Add sorting
            if (sortBy != null && !sortBy.isEmpty()) {
                Order order = ascending ? cb.asc(root.get(sortBy)) : cb.desc(root.get(sortBy));
                cq.orderBy(order);
            }
            
            // Execute with pagination
            return session.createQuery(cq)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        } catch (HibernateException ex) {
            LOGGER.log(Level.SEVERE, "Error searching entities", ex);
            throw new DAOException("Failed to search entities", ex);
        }
    }

    /**
     * Counts all entities of the managed type.
     * 
     * @return The total count of entities
     * @throws DAOException If an error occurs during the operation
     */
    public long count() throws DAOException {
        try (Session session = openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> root = cq.from(entityClass);
            cq.select(cb.count(root));
            
            return session.createQuery(cq).getSingleResult();
        } catch (HibernateException ex) {
            LOGGER.log(Level.SEVERE, "Error counting entities", ex);
            throw new DAOException("Failed to count entities", ex);
        }
    }

    /**
     * Finds entities created today.
     * 
     * @param dateField The name of the date field to check
     * @return A list of entities created today
     * @throws DAOException If an error occurs during the operation
     */
    public List<T> findCreatedToday(String dateField) throws DAOException {
        try (Session session = openSession()) {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
            
            Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
            
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            
            cq.select(root).where(
                cb.between(root.get(dateField), startDate, endDate)
            );
            
            return session.createQuery(cq).getResultList();
        } catch (HibernateException ex) {
            LOGGER.log(Level.SEVERE, "Error finding entities created today", ex);
            throw new DAOException("Failed to find entities created today", ex);
        }
    }

    /**
     * Converts a date string to a Date object.
     * 
     * @param dateString The date string to convert
     * @param format The format of the date string
     * @return The converted Date object
     * @throws ParseException If the date string cannot be parsed
     */
    protected Date parseDate(String dateString, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        return dateFormat.parse(dateString);
    }

    /**
     * Converts an entity to its JSON representation.
     * 
     * @param entity The entity to convert
     * @return A JSON string representing the entity
     */
    public String toJson(T entity) {
        return gson.toJson(entity);
    }

    /**
     * Converts a list of entities to its JSON representation.
     * 
     * @param entities The list of entities to convert
     * @return A JSON string representing the list of entities
     */
    public String toJson(List<T> entities) {
        return gson.toJson(entities);
    }
    
    /**
     * Creates an error response in JSON format.
     * 
     * @param errorCode The error code
     * @param errorMessage The error message
     * @return A JSON string representing the error
     */
    protected String createErrorResponse(int errorCode, String errorMessage) {
        JsonObject error = new JsonObject();
        error.addProperty("error_code", errorCode);
        error.addProperty("error_description", errorMessage);
        return gson.toJson(error);
    }
    
    /**
     * Custom exception class for DAO operations.
     */
    public static class DAOException extends Exception {
        private static final long serialVersionUID = 1L;
        
        public DAOException(String message) {
            super(message);
        }
        
        public DAOException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
