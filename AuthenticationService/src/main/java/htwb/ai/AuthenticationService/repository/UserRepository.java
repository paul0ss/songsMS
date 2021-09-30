package htwb.ai.AuthenticationService.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import htwb.ai.AuthenticationService.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, String>{
	
	//@Query("SELECT u FROM User u WHERE u.userId = ?1")
	//Optional<User> findByUserId(String userId);
	@Nullable
	User findByUserId(String userId);
	//boolean existsUserById(String userId);
}
