package htwb.ai.SongsSonglistsService.dao;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.RollbackException;

import htwb.ai.SongsSonglistsService.model.Song;


public class DBSongDAO implements ISongDAO{
	
    private EntityManagerFactory emf;

    private String persistenceUnit;
    
    public DBSongDAO() {
    	
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
			Query q = em.createQuery("SELECT s FROM Song s WHERE s.id = ?1"); //JPQL
			q.setParameter(1, id);
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

	@Override
	public Integer saveSong(Song song) {
		System.out.println("I am in saveSong()");
        EntityManager em = null;
        EntityTransaction transaction = null;
        try {
            em = emf.createEntityManager();
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(song);
            transaction.commit();
            return song.getId();
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

	@Override
	public List<Song> findAllSongs() {
        EntityManager em = null;
        try {
            em = emf.createEntityManager(); 
            Query q = em.createQuery("SELECT u FROM Song u"); //JPQL
            List<Song> songList = q.getResultList();
            return songList;
        } finally {
            if (em != null) {
                em.close();
            }
        }
	}

	@Override
	public Song getSongById(String songID) {
		System.out.println("-----------------------------------");
		System.out.println("I am in getSongById() method!!!");
    	EntityManager em = null;
    	Integer id = null;
    	try {
    	id = Integer.parseInt(songID);
    	}catch(NumberFormatException ex) {
    		return null;
    	}
    	if(checkId(id)) {
        try {
            em = emf.createEntityManager(); 
            Query q = em.createQuery("SELECT s FROM Song s WHERE s.id = ?1");
            q.setParameter(1, id);
            Song songRequested = (Song) q.getSingleResult();
            return songRequested;
        } finally {
            if (em != null) {
                em.close();
            }
        }}else {
        	return null;
        }
	}

	@Override
	public void deleteSong(Integer id) {
        EntityManager em = emf.createEntityManager();
        Song song = null;
        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();
            // find Song with id
            song = em.find(Song.class, id);
            if (song != null) {
                System.out.println("Deleting: " + song.getId() + " with titel: " + song.getTitle());
                em.remove(song);
                transaction.commit();
            }
        } catch (Exception e) {
            System.out.println("Error removing song: " + e.getMessage());
            throw new PersistenceException("Could not remove entity: " + e.toString());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public boolean updateSong(Integer id, Song song){
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = null;
        boolean success = true;
        if(checkId(id)){
            try{
                em = emf.createEntityManager();
                transaction = em.getTransaction();
                transaction.begin();
                Query q = em.createQuery("UPDATE Song s SET s.title = ?2, s.artist = ?3, s.label = ?4, s.released = ?5 WHERE s.id = ?1");
                q.setParameter(1, id);
                q.setParameter(2, song.getTitle());
                q.setParameter(3, song.getArtist());
                q.setParameter(5, song.getReleased());
                q.setParameter(4, song.getLabel());
                q.executeUpdate();
                transaction.commit();
            }catch(Exception ex){
                success = false;
            }finally {
                if (em != null) {
                    em.close();
                }
            }
        }else{
            success = false;
        }
        return success;
    }
	}

