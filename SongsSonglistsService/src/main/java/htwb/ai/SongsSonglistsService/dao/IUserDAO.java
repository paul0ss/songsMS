package htwb.ai.SongsSonglistsService.dao;

import htwb.ai.SongsSonglistsService.model.User;

public interface IUserDAO {

    public User getUserByUserId (String userId);

	boolean checkUserId(String userId);
}
