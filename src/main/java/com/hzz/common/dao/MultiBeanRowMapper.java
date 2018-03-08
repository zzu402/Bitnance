package com.hzz.common.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by hongshuiqiao on 2017/9/15.
 */
public class MultiBeanRowMapper implements RowMapper<Map<String,AbstractModel>> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /** Whether we're defaulting primitives when mapping a null value */
    private boolean primitivesDefaultedForNullValue = false;
    /** ConversionService for binding JDBC values to bean properties */
    private ConversionService conversionService = DefaultConversionService.getSharedInstance();
    private JoinModel[] models;
    /** Map of the fields we provide mapping for */
    private Map<String,Map<String, PropertyDescriptor>> mappedFields=new LinkedHashMap<>();
    /** Set of bean properties we provide mapping for */
    private Map<String,Set<String>> mappedProperties=new LinkedHashMap<>();
    private Map<String, Class<? extends AbstractModel>> mappedClasses = new LinkedHashMap<>();

    public MultiBeanRowMapper(JoinModel[] models) {
        this.models = models;
        for (JoinModel model : models){
            String aliasName = model.getAliasName();
            Class<? extends AbstractModel> joinModel = model.getJoinModel();
            initialize(aliasName, joinModel);
        }
    }

    protected void initialize(String key, Class<? extends AbstractModel> mappedClass) {
        mappedClasses.put(key,mappedClass);

        Map<String,PropertyDescriptor> mappedFields = new HashMap<String, PropertyDescriptor>();
        Set<String> mappedProperties = new HashSet<String>();
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
        for (PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() != null) {
                mappedFields.put(lowerCaseName(pd.getName()), pd);
                String underscoredName = underscoreName(pd.getName());
                if (!lowerCaseName(pd.getName()).equals(underscoredName)) {
                    mappedFields.put(underscoredName, pd);
                }
                mappedProperties.add(pd.getName());
            }
        }
        this.mappedFields.put(key, mappedFields);
        this.mappedProperties.put(key, mappedProperties);
    }

    protected String lowerCaseName(String name) {
        return name.toLowerCase(Locale.US);
    }

    protected String underscoreName(String name) {
        if (!StringUtils.hasLength(name)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append(lowerCaseName(name.substring(0, 1)));
        for (int i = 1; i < name.length(); i++) {
            String s = name.substring(i, i + 1);
            String slc = lowerCaseName(s);
            if (!s.equals(slc)) {
                result.append("_").append(slc);
            }
            else {
                result.append(s);
            }
        }
        return result.toString();
    }

    /**
     * Set whether we're defaulting Java primitives in the case of mapping a null value
     * from corresponding database fields.
     * <p>Default is {@code false}, throwing an exception when nulls are mapped to Java primitives.
     */
    public void setPrimitivesDefaultedForNullValue(boolean primitivesDefaultedForNullValue) {
        this.primitivesDefaultedForNullValue = primitivesDefaultedForNullValue;
    }

    /**
     * Return whether we're defaulting Java primitives in the case of mapping a null value
     * from corresponding database fields.
     */
    public boolean isPrimitivesDefaultedForNullValue() {
        return this.primitivesDefaultedForNullValue;
    }

    /**
     * Set a {@link ConversionService} for binding JDBC values to bean properties,
     * or {@code null} for none.
     * <p>Default is a {@link DefaultConversionService}, as of Spring 4.3. This
     * provides support for {@code java.time} conversion and other special types.
     * @since 4.3
     * @see #initBeanWrapper(BeanWrapper)
     */
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * Return a {@link ConversionService} for binding JDBC values to bean properties,
     * or {@code null} if none.
     * @since 4.3
     */
    public ConversionService getConversionService() {
        return this.conversionService;
    }

    @Override
    public Map<String, AbstractModel> mapRow(ResultSet rs, int rowNumber) throws SQLException {
        Map<String, AbstractModel> map = new HashMap<>();
        Map<String, BeanWrapper> beanMap = new HashMap<>();
        for (JoinModel model : models){
            String aliasName = model.getAliasName();
            Class<? extends AbstractModel> joinModel = model.getJoinModel();
            AbstractModel mappedObject = BeanUtils.instantiateClass(joinModel);
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
            initBeanWrapper(bw);
            map.put(aliasName, mappedObject);
            beanMap.put(aliasName, bw);
        }

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        for (int index = 1; index <= columnCount; index++) {
            String column = JdbcUtils.lookupColumnName(rsmd, index);
            int nameIndex = column.indexOf("___");
            if(nameIndex<=0)
                continue;
            String keyName = column.substring(0,nameIndex);
            String columnName = column.substring((nameIndex+3));
            if(!mappedClasses.containsKey(keyName))
                continue;
            String field = lowerCaseName(columnName.replaceAll(" ", ""));
            PropertyDescriptor pd = this.mappedFields.get(keyName).get(field);
            if (pd != null) {
                try {
                    Object value = getColumnValue(rs, index, pd);
                    if (rowNumber == 0 && logger.isDebugEnabled()) {
                        logger.debug("Mapping column '" + column + "' to property '" + pd.getName() +
                                "' of type '" + ClassUtils.getQualifiedName(pd.getPropertyType()) + "'");
                    }
                    try {
                        beanMap.get(keyName).setPropertyValue(pd.getName(), value);
                    }
                    catch (TypeMismatchException ex) {
                        if (value == null && this.primitivesDefaultedForNullValue) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Intercepted TypeMismatchException for row " + rowNumber +
                                        " and column '" + column + "' with null value when setting property '" +
                                        pd.getName() + "' of type '" +
                                        ClassUtils.getQualifiedName(pd.getPropertyType()) +
                                        "' on object: " + map.get(keyName), ex);
                            }
                        }
                        else {
                            throw ex;
                        }
                    }
                }
                catch (NotWritablePropertyException ex) {
                    throw new DataRetrievalFailureException(
                            "Unable to map column '" + column + "' to property '" + pd.getName() + "'", ex);
                }
            }
            else {
                // No PropertyDescriptor found
                if (rowNumber == 0 && logger.isDebugEnabled()) {
                    logger.debug("No property found for column '" + column + "' mapped to field '" + field + "'");
                }
            }
        }
        return map;
    }

    /**
     * Initialize the given BeanWrapper to be used for row mapping.
     * To be called for each row.
     * <p>The default implementation applies the configured {@link ConversionService},
     * if any. Can be overridden in subclasses.
     * @param bw the BeanWrapper to initialize
     * @see #getConversionService()
     * @see BeanWrapper#setConversionService
     */
    protected void initBeanWrapper(BeanWrapper bw) {
        ConversionService cs = getConversionService();
        if (cs != null) {
            bw.setConversionService(cs);
        }
    }

    /**
     * Retrieve a JDBC object value for the specified column.
     * <p>The default implementation calls
     * {@link JdbcUtils#getResultSetValue(ResultSet, int, Class)}.
     * Subclasses may override this to check specific value types upfront,
     * or to post-process values return from {@code getResultSetValue}.
     * @param rs is the ResultSet holding the data
     * @param index is the column index
     * @param pd the bean property that each result object is expected to match
     * (or {@code null} if none specified)
     * @return the Object value
     * @throws SQLException in case of extraction failure
     * @see JdbcUtils#getResultSetValue(ResultSet, int, Class)
     */
    protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
    }


    /**
     * Static factory method to create a new {@code BeanPropertyRowMapper}
     * (with the mapped class specified only once).
     * @param mappedClass the class that each row should be mapped to
     */
    public static <T> BeanPropertyRowMapper<T> newInstance(Class<T> mappedClass) {
        return new BeanPropertyRowMapper<T>(mappedClass);
    }
}
