package htwb.ai.SongsSonglistsService.dao;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;


import htwb.ai.SongsSonglistsService.model.*;

public class DBSonglistDAO implements ISonglistDAO{
	
    private EntityManagerFactory emf;

    private String persistenceUnit;
    
    public DBSonglistDAO() {
    	
    }
    
    public void setPersistenceUnit(String pUnit) {
        System.out.println("I'm instanciated: " + pUnit);
        this.persistenceUnit = pUnit;
        emf = Persistence.createEntityManagerFactory(persistenceUnit);
    }
    
	private boolean checkId(int id) {
		EntityManager em = null;
		boolean exists = true;
		try {
			em = emf.createEntityManager();
			Query q = em.createQuery("SELECT s FROM Songlist s WHERE s.id = ?1"); //JPQL
			q.setParameter(1, id);
			Object result = q.getSingleResult();
		}catch(NoResultException ne) {
			exists = false;
		}catch(NonUniqueResultException nure) {
			exists = false;
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
            if (em != null) {
                em.close();
            }
        }
		return exists;
	}
    
	@Override
	public Songlist getSonglistById(String listId) {
		System.out.println("-----------------------------------");
		System.out.println("I am in getSonglistById() method!!!");
    	EntityManager em = null;
    	Integer id = null;
    	try {
    	id = Integer.parseInt(listId);
    	}catch(NumberFormatException ex) {
    		return null;
    	}
    	if(checkId(id)) {
    		System.out.println("songlist with " + id + "exists");
        try {
            em = emf.createEntityManager(); 
            Query q = em.createQuery("SELECT s FROM Songlist s WHERE s.id = ?1");
            q.setParameter(1, id);
            Songlist songlistRequested = (Songlist) q.getSingleResult();
            System.out.println("This is my list "+songlistRequested.getSongList().toString());
            return songlistRequested;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        }else {
        	return null;
        }
	}
	
	@Override
	public Set<Songlist> getSonglistsById(String userId){
		EntityManager em = null;
		System.out.println("I am in getSonglistsById()");
        try {
            em = emf.createEntityManager(); 
            User u = em.find(User.class, userId);
            Set<Songlist> songListss = u.getSonglists();
            System.out.println(songListss.toString());
            return songListss;
        } finally {
            if (em != null) {
                em.close();
            }
        }
	}
	
	@Override
	public Set<Songlist> getPublicSonglistsById(String userId){
		EntityManager em = null;
		System.out.println("I am in getSonglistsById()");
        try {
            em = emf.createEntityManager(); 
            User u = em.find(User.class, userId);
            Set<Songlist> songListss = new HashSet();;
            for(Songlist list : u.getSonglists()) {
            	if(!list.isPrivate()) {
            		songListss.add(list);
            	}
            }
            System.out.println(songListss.toString());
            return songListss;
        } finally {
            if (em != null) {
                em.close();
            }
        }
	}
	
	@Override
	public boolean checkUserExistence(String userId) {
		EntityManager em = null;
		try {
            em = emf.createEntityManager(); 
            User u = em.find(User.class, userId);
            if(u == null) {
            	return false;
            }else {
            	return true;
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
	}
	
	@Override
	public boolean deleteSonglist(int songlistId) {
		System.out.println("-----------------------------------");
		System.out.println("I am in deleteSonglistById() method!!!");
        EntityManager em = emf.createEntityManager();
        Songlist songlist = null;
        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();
            // find Song with id
            songlist = em.find(Songlist.class, songlistId);
            if (songlist != null) {
                System.out.println("Deleting: " + songlist.getId() + " with id: " + songlist.getId() + ", named: " + songlist.getName());
                em.remove(songlist);
                transaction.commit();
                return true;
            }else {
            	return false;
            }
        } catch (Exception e) {
            System.out.println("Error removing songlist: " + e.getMessage());
            return false;
            //throw new PersistenceException("Could not remove entity: " + e.toString());
        } finally {
            if (em != null) {
                em.close();
            }
        }}

	@Override
	public Integer saveSonglist(Songlist songlist, String userId) {
		System.out.println("I am in saveSonglist()");
        EntityManager em = null;
        EntityTransaction transaction = null;
        try {
            em = emf.createEntityManager();
            User u = em.find(User.class, userId);
            songlist.setOwnerId(u);
            Set<Song> listOfSongs = songlist.getSongList();
            for(Song s : listOfSongs) {
            	Song so = em.find(Song.class, s.getId());
            	if(so == null) {
            		return -1;
            	}
            }
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(songlist);
            transaction.commit();
            return songlist.getId();
        } catch (Exception ex) {
        	ex.printStackTrace();
        	System.out.println(ex);
            if (em != null) {
                em.getTransaction().rollback();
            }
            throw new PersistenceException(ex.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
	}
	
    
    
}
