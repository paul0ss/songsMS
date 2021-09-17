package htwb.ai.AuthenticationService.dao;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;

import htwb.ai.AuthenticationService.model.User;

public class DBUserDAO implements IUserDAO {

    private EntityManagerFactory emf;

    private String persistenceUnit;
    
    public DBUserDAO() {
    	
    }
    
    public void setPersistenceUnit(String pUnit) {
        System.out.println("I'm instanciated: " + pUnit);
        this.persistenceUnit = pUnit;
        emf = Persistence.createEntityManagerFactory(persistenceUnit);
    }
    
    @Override
	public boolean checkUserId(String userId) {
		EntityManager em = null;
		boolean exists = true; //WARNING!!!
		try {
			em = emf.createEntityManager();
			Query q = em.createQuery("SELECT u FROM User u WHERE u.userId = ?1"); //JPQL
			q.setParameter(1, userId);
			Object result = q.getSingleResult();
		}catch(NoResultException ne) {
			exists = false;
		}catch(NonUniqueResultException nure) {
			exists = false;
		}finally {
            if (em != null) {
                em.close();
            }
        }
		return exists;
	}
    
    public User getUserByUserId(String userId) {
    	EntityManager em = null;
    	if(checkUserId(userId)) {
        try {
            em = emf.createEntityManager(); 
            Query q = em.createQuery("SELECT u FROM User u WHERE u.userId = ?1");
            q.setParameter(1, userId);
            User songRequested = (User) q.getSingleResult();
            return songRequested;
        } finally {
            if (em != null) {
                em.close();
            }
        }}else {
        	return null;
        }
    }
}
