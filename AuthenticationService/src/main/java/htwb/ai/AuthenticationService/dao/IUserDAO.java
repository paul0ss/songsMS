package htwb.ai.AuthenticationService.dao;

import org.springframework.stereotype.Component;

import htwb.ai.AuthenticationService.model.User;

public interface IUserDAO {

    public User getUserByUserId (String userId);

	boolean checkUserId(String userId);
}
