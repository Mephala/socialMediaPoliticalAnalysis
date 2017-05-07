package com.gokhanozg.dao;

import com.gokhanozg.HibernateUtil;
import com.gokhanozg.Politician;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by gokhanozg on 5/7/17.
 */
public class PoliticanDao {

    public List<Politician> getAllPoliticians() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Criteria criteria = session.createCriteria(Politician.class);
        List<Politician> politicians = criteria.list();
        session.getTransaction().commit();
        session.close();
        return politicians;
    }

    public void savePolitician(Politician politician) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(politician);
        session.getTransaction().commit();
        session.close();
    }
}
