package htwb.ai.SongsSonglistsService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import htwb.ai.SongsSonglistsService.model.Songlist;
import htwb.ai.SongsSonglistsService.model.User;

@Repository
public interface SonglistRepository extends CrudRepository<Songlist, Integer>{
	
	/**
	 * CrudRepository Methoden:
	 * Songlist save(Songlist entity);
	 * Optional<Songlist> findById(Integer primaryKey);
	 * Iterable<Songlist> findAll();
	 * long count();
	 * void delete(Songlist entity);
	 * boolean existsById(Integer primaryKey); 
	 * @return 
	 */
	
	@Query("SELECT s FROM Songlist s WHERE s.ownerId = :ownerId")
	List<Songlist> findAllByOwnerId(@Param(value = "ownerId")String ownerId);
	
	@Query("SELECT s FROM Songlist s WHERE s.ownerId = :ownerId AND s.isPrivate = 0")
	List<Songlist> findAllPublicByOwnerId(@Param(value = "ownerId")String ownerId);
	
//	@Modifying(clearAutomatically = true)
//	@Transactional
//	@Query("UPDATE Songlist s SET s.title = :title, s.artist = :artist, s.label = :label, s.released = :released WHERE s.id = :id")
//	int updateSong(@Param(value = "id") Integer id, @Param(value = "title")String title, @Param(value = "artist")String artist, @Param(value = "label")String lable, @Param(value = "released")Integer released);
}
