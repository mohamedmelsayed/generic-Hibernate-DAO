package dao;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import callingObjects.Conditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import errors.ErrorMessages;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utilities.DateUtil;
import utilities.HibernateUtil;

/**
 *
 * @author MOH OSMAN
 * @param <T>
 */
public class DAO<T> {

//    HibernateUtil h;
    public DAO() {
//        h = new HibernateUtil();

    }

    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
     */
    public boolean insert(T entity) {
        // code to save entity details to database
        Session session = HibernateUtil.getInstance().getFactory().openSession();
        try {
            System.out.println("" + entity.toString());

            Transaction transaction = session.getTransaction();
            transaction.begin();
//            Approval app=(Approval) entity;
            session.persist(entity);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException ex) {
            ex.printStackTrace();
            System.out.println("" + ex.getMessage());
            session.close();
            return false;
        }

    }

    public boolean delete(String className, String column, String value) {
        // code to save entity details to database
        Session session = HibernateUtil.getInstance().getFactory().openSession();
        System.out.println("" + className + "\n" + column + "\n" + value);
        Transaction transaction = session.beginTransaction();
        try {
            // your code
//            String hql = "delete from Vote where uid= :uid AND pid= :pid";
            String hql = "delete " + className + " where " + column + " ='" + value + "'";
            Query query = session.createQuery(hql);
//            System.out.println(user.getUid() + " and pid: " + pid);

            System.out.println(query.executeUpdate());
            // your code end

            transaction.commit();
            session.close();
            return true;
        } catch (Throwable t) {
            transaction.rollback();
            session.close();
            return false;

        }

    }

    public boolean update(T entity) {
        Session session = HibernateUtil.getInstance().getFactory().openSession();
        // code to save entity details to database
        // code to save entity details to database
        try {

            Transaction transaction = session.getTransaction();
            transaction.begin();
            session.update(entity);
            transaction.commit();
            session.close();
            return true;
        } catch (HibernateException ex) {
            ex.printStackTrace();
            session.close();
            return false;
        }
    }

    public T getUnique(String className, String column, String value) {
        Session s = HibernateUtil.getInstance().getFactory().openSession();
        Query q = s.createQuery("from " + className + " where " + column + " ='" + value + "'");

        if (q.list().size() > 0) {
            Object o = q.list().get(0);
            s.close();
            return (T) o;
        } else {
            s.close();
            return null;
        }

    }

    public <T> List<T> get(String className, String column, String value) {
        List<T> list = new ArrayList<>();
        Session s = HibernateUtil.getInstance().getFactory().openSession();
        Query q = s.createQuery("from " + className + " where " + column + "='" + value + "'");

        if (q.list().size() > 0) {
            for (Object list1 : q.list()) {
                list.add((T) list1);
            }

            s.close();
            return list;
        } else {
            s.close();
            return list;
        }

    }

    public String getWhere(String className, List<Conditions> conditions,String orderbyColumn,String dir) throws ParseException {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd ").create();
        TypeAdapter<java.util.Date> dateTypeAdapter = gson.getAdapter(java.util.Date.class);
        TypeAdapter<java.util.Date> safeDateTypeAdapter = dateTypeAdapter.nullSafe();
        Gson gsonSafe = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(java.util.Date.class, safeDateTypeAdapter)
                .create();
        List<T> list = new ArrayList<>();
        List<String> tableNames;
        Session s = HibernateUtil.getInstance().getFactory().openSession();
        String select = "";
        if (!"".equals(className)) {
            tableNames = Arrays.asList(className.split(","));

            for (String tableName : tableNames) {
                if (tableNames.size() > 1) {
                    if ("".equals(select)) {
                        select = select + "select " + tableName.substring(0, 3) + " from " + tableName + " " + tableName.substring(0, 3) + " inner join " + tableName.substring(0, 3) + ".";
                    } else {
                        select = select + tableName + " ";
                    }
                } else {
                    select = select + "from " + tableName;
                }
            }

        }
        String condition = "";
        for (Conditions con : conditions) {
            switch (con.getOperation()) {
                case "=": {
                    if ("".equals(condition)) {

                        condition = condition + " where " + con.getWhere() + " =" + con.getValue() + " ";

                    } else {
                        condition = condition + " and " + con.getWhere() + " =" + con.getValue() + " ";
                    }
                    break;
                }
                case ">": {

                    if ("".equals(condition)) {

                        condition = condition + " where " + con.getWhere() + " > " + con.getValue() + " ";

                    } else {
                        condition = condition + " and " + con.getWhere() + " > " + con.getValue() + " ";
                    }
                    break;
                }
                case "<": {
                    if ("".equals(condition)) {

                        condition = condition + " where " + con.getWhere() + " < " + con.getValue() + " ";

                    } else {
                        condition = condition + " and " + con.getWhere() + " < " + con.getValue() + " ";
                    }
                    break;
                }
                case ">date": {

                    if ("".equals(condition)) {

                        condition = condition + " where " + con.getWhere() + " > :" + con.getWhere() + " ";

                    } else {
                        condition = condition + " and " + con.getWhere() + " > :" + con.getWhere() + "2" + " ";
                    }
                    break;
                }
                case "<date": {
                    if ("".equals(condition)) {

                        condition = condition + " where " + con.getWhere() + " < :" + con.getWhere() + " ";

                    } else {
                        condition = condition + " and " + con.getWhere() + " < :" + con.getWhere() + "2" + " ";
                    }
                    break;
                }

                case "like": {
                    if ("".equals(condition)) {

                        condition = condition + " where " + con.getWhere() + " like :" + con.getWhere() + " ";

                    } else {
                        condition = condition + " and " + con.getWhere() + " like :" + con.getWhere() + " ";
                    }
                    break;
                }
                case "in": {
                    if ("".equals(condition)) {

                        condition = condition + " where " + con.getWhere() + " in (" + con.getValue() + ") ";

                    } else {
                        condition = condition + " and " + con.getWhere() + " in (" + con.getValue() + ") ";
                    }
                    break;
                }

            }

        }
        System.out.println("ordervy ="+orderbyColumn);
        if(!"".equals(orderbyColumn)){
            condition = condition + "order by "+orderbyColumn+" "+dir;
        }
        try {

            int count = 1;
            Query q = s.createQuery(select + " " + condition);
            for (Conditions con : conditions) {
               if(con.getOperation().equals("like")){
                q.setParameter(con.getWhere(), con.getValue());
            }
                if (con.getWhere().contains("Date") && (con.getOperation().equals(">") || con.getOperation().equals("<"))) {
                    DateFormat format = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                    java.util.Date utilDate = format.parse(con.getValue());
                    System.out.println("" + con.getValue());
                    System.out.println("" + utilDate);

                    if (count == 1) {
                        q.setParameter(con.getWhere(), utilDate);
                        count++;
                        System.out.println(con.getWhere().indexOf("Date"));

                    } else {
                        q.setParameter(con.getWhere() + "2", utilDate);
                    }

                }
               

            }

            if (q.list().size() > 0) {
                for (Iterator it = q.list().iterator(); it.hasNext();) {
                    Object list1 = it.next();
                    list.add((T) list1);
                }

                s.close();
                System.out.println(gsonSafe.toJson(list));
                System.out.println("List Size  =" + list.size());
                return gsonSafe.toJson(list);
            } else {
                s.close();
                errors.ErrorMessages error = new ErrorMessages();
                error.setError_code(7);
                error.setError_description("No data Availble on table " + className);
                return gson.toJson(error);

            }

        } catch (HibernateException ex) {
            s.close();
            errors.ErrorMessages error = new ErrorMessages();
            error.setError_code(9);
            error.setError_description(ex.getMessage() + " check the condition column names");
            return gson.toJson(error);
        }

    }

    public <T> List<T> getLike(String className, String column, String value) {
        List<T> list = new ArrayList<>();
        Session s = HibernateUtil.getInstance().getFactory().openSession();
//        Query q = s.createQuery("from " + className + " where " + column + "like:" + column );

        java.util.Date todayMorning = DateUtil.setZero(java.util.Date.from(Instant.now()));
        java.util.Date todayEvening = DateUtil.addDays(todayMorning, 1);//.truncatedTo((TemporalUnit) dateFormat));
        Query q2 = s.createQuery("from " + className + " where " + column + " between :current_date and :tomorrow")
                .setParameter("current_date", todayMorning)
                .setParameter("tomorrow", todayEvening);

        if (q2.list().size() > 0) {
            for (Object list1 : q2.list()) {
                list.add((T) list1);
            }

            s.close();
            return list;
        } else {
            s.close();
            return list;
        }

    }

    public <T> List<T> getAll(String className) {
        List<T> list = new ArrayList<>();
        System.out.println("ghgfdhhdfghdfghdfghdfghdfg\n *****" + className);
        Session s = HibernateUtil.getInstance().getFactory().openSession();
        Query q = s.createQuery("from " + className);
        System.out.println("85/n" + q.list().size());
        try {
            if (q.list().size() > 0) {
                for (Object list1 : q.list()) {
                    list.add((T) list1);
                    System.out.println("" + list1.toString());
                }

                s.close();
                return list;
            } else {
                s.close();
                return null;
            }
        } catch (HibernateException ez) {
            System.out.println(ez.getMessage());
            s.close();
            return null;
        }

    }

}

