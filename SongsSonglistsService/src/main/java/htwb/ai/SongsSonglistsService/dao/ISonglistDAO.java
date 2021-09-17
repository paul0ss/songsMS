package htwb.ai.SongsSonglistsService.dao;

import java.util.List;
import java.util.Set;

import htwb.ai.SongsSonglistsService.model.Songlist;

public interface ISonglistDAO {
	
	 public Songlist getSonglistById(String id);
//	 public List<Songlist> getSonglists(String ownerId);
//	 public Integer createSonglist(Songlist songlist);
//	 public boolean deleteSonglist(String id);

	 public Set<Songlist> getSonglistsById(String userId);

	Set<Songlist> getPublicSonglistsById(String userId);

	boolean checkUserExistence(String userId);

	boolean deleteSonglist(int songlistId);

	public Integer saveSonglist(Songlist songlist, String userId);

}
