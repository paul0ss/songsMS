package htwb.ai.SongsSonglistsService.dao;

import java.util.List;

import htwb.ai.SongsSonglistsService.model.Song;

public interface ISongDAO {

	public Integer saveSong(Song song);
	public List<Song> findAllSongs();
	public Song getSongById(String songID);
	public void deleteSong(Integer id);
	public boolean updateSong(Integer id, Song song);
}
