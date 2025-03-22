package dao;

import java.io.Serializable;
import java.util.Date;

/**
 * A class representing a condition for database queries.
 * Used to build dynamic queries with various comparison operators.
 */
public class QueryCondition implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Enumeration of supported operators for conditions
     */
    public enum Operator {
        EQUALS("="),
        NOT_EQUALS("!="),
        GREATER_THAN(">"),
        LESS_THAN("<"),
        GREATER_THAN_OR_EQUAL(">="),
        LESS_THAN_OR_EQUAL("<="),
        LIKE("LIKE"),
        NOT_LIKE("NOT LIKE"),
        IN("IN"),
        NOT_IN("NOT IN"),
        IS_NULL("IS NULL"),
        IS_NOT_NULL("IS NOT NULL"),
        BETWEEN("BETWEEN");
        
        private final String symbol;
        
        Operator(String symbol) {
            this.symbol = symbol;
        }
        
        public String getSymbol() {
            return symbol;
        }
    }
    
    private final String fieldName;
    private final Operator operator;
    private final Object value;
    private Object secondValue; // Used for BETWEEN operator
    
    /**
     * Creates a new query condition.
     * 
     * @param fieldName The field name to apply the condition to
     * @param operator The operator to use
     * @param value The value to compare against
     */
    public QueryCondition(String fieldName, Operator operator, Object value) {
        this.fieldName = fieldName;
        this.operator = operator;
        this.value = value;
    }
    
    /**
     * Creates a new BETWEEN query condition.
     * 
     * @param fieldName The field name to apply the condition to
     * @param value The lower bound value
     * @param secondValue The upper bound value
     */
    public QueryCondition(String fieldName, Object value, Object secondValue) {
        this.fieldName = fieldName;
        this.operator = Operator.BETWEEN;
        this.value = value;
        this.secondValue = secondValue;
    }
    
    /**
     * Creates a null check condition.
     * 
     * @param fieldName The field name to check
     * @param isNull Whether to check if the field is null or not null
     */
    public QueryCondition(String fieldName, boolean isNull) {
        this.fieldName = fieldName;
        this.operator = isNull ? Operator.IS_NULL : Operator.IS_NOT_NULL;
        this.value = null;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Operator getOperator() {
        return operator;
    }
    
    public Object getValue() {
        return value;
    }
    
    public Object getSecondValue() {
        return secondValue;
    }
    
    public void setSecondValue(Object secondValue) {
        this.secondValue = secondValue;
    }
    
    /**
     * Convenience factory method for creating an equals condition.
     */
    public static QueryCondition eq(String fieldName, Object value) {
        return new QueryCondition(fieldName, Operator.EQUALS, value);
    }
    
    /**
     * Convenience factory method for creating a not equals condition.
     */
    public static QueryCondition ne(String fieldName, Object value) {
        return new QueryCondition(fieldName, Operator.NOT_EQUALS, value);
    }
    
    /**
     * Convenience factory method for creating a greater than condition.
     */
    public static QueryCondition gt(String fieldName, Object value) {
        return new QueryCondition(fieldName, Operator.GREATER_THAN, value);
    }
    
    /**
     * Convenience factory method for creating a less than condition.
     */
    public static QueryCondition lt(String fieldName, Object value) {
        return new QueryCondition(fieldName, Operator.LESS_THAN, value);
    }
    
    /**
     * Convenience factory method for creating a greater than or equal condition.
     */
    public static QueryCondition ge(String fieldName, Object value) {
        return new QueryCondition(fieldName, Operator.GREATER_THAN_OR_EQUAL, value);
    }
    
    /**
     * Convenience factory method for creating a less than or equal condition.
     */
    public static QueryCondition le(String fieldName, Object value) {
        return new QueryCondition(fieldName, Operator.LESS_THAN_OR_EQUAL, value);
    }
    
    /**
     * Convenience factory method for creating a LIKE condition.
     */
    public static QueryCondition like(String fieldName, String value) {
        return new QueryCondition(fieldName, Operator.LIKE, value);
    }
    
    /**
     * Convenience factory method for creating a NOT LIKE condition.
     */
    public static QueryCondition notLike(String fieldName, String value) {
        return new QueryCondition(fieldName, Operator.NOT_LIKE, value);
    }
    
    /**
     * Convenience factory method for creating an IN condition.
     */
    public static QueryCondition in(String fieldName, Object... values) {
        return new QueryCondition(fieldName, Operator.IN, values);
    }
    
    /**
     * Convenience factory method for creating a NOT IN condition.
     */
    public static QueryCondition notIn(String fieldName, Object... values) {
        return new QueryCondition(fieldName, Operator.NOT_IN, values);
    }
    
    /**
     * Convenience factory method for creating an IS NULL condition.
     */
    public static QueryCondition isNull(String fieldName) {
        return new QueryCondition(fieldName, true);
    }
    
    /**
     * Convenience factory method for creating an IS NOT NULL condition.
     */
    public static QueryCondition isNotNull(String fieldName) {
        return new QueryCondition(fieldName, false);
    }
    
    /**
     * Convenience factory method for creating a BETWEEN condition.
     */
    public static QueryCondition between(String fieldName, Object value1, Object value2) {
        return new QueryCondition(fieldName, value1, value2);
    }
    
    /**
     * Convenience factory method for creating a date equals condition.
     */
    public static QueryCondition dateEq(String fieldName, Date date) {
        return new QueryCondition(fieldName, Operator.EQUALS, date);
    }
    
    /**
     * Convenience factory method for creating a date greater than condition.
     */
    public static QueryCondition dateGt(String fieldName, Date date) {
        return new QueryCondition(fieldName, Operator.GREATER_THAN, date);
    }
    
    /**
     * Convenience factory method for creating a date less than condition.
     */
    public static QueryCondition dateLt(String fieldName, Date date) {
        return new QueryCondition(fieldName, Operator.LESS_THAN, date);
    }
    
    /**
     * Convenience factory method for creating a date between condition.
     */
    public static QueryCondition dateBetween(String fieldName, Date startDate, Date endDate) {
        return new QueryCondition(fieldName, startDate, endDate);
    }
}
