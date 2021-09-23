package htwb.ai.SongsSonglistsService.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import htwb.ai.SongsSonglistsService.model.Song;

@Repository
public interface SongRepository extends CrudRepository<Song, Integer>{
	
	/**
	 * CrudRepository Methoden:
	 * Song save(Song entity);
	 * Optional<Song> findById(Integer primaryKey);
	 * Iterable<Song> findAll();
	 * long count();
	 * void delete(Song entity);
	 * boolean existsById(Integer primaryKey); 
	 * @return 
	 */
	
	public void deleteById(Integer id);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("UPDATE Song s SET s.title = :title, s.artist = :artist, s.label = :label, s.released = :released WHERE s.id = :id")
	int updateSong(@Param(value = "id") Integer id, @Param(value = "title")String title, @Param(value = "artist")String artist, @Param(value = "label")String lable, @Param(value = "released")Integer released);
}
