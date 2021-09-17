package htwb.ai.AuthenticationService.model;

import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name="user")
public class User {
	
	@Id
	@Column(name="userId")
    private String userId;
	@Column(name="password")
    private String password;
	@Column(name="firstName")
    private String firstName;
	@Column(name="lastName")
    private String lastName;
	
	@OneToMany(mappedBy="ownerId", cascade=CascadeType.ALL, orphanRemoval=true)
	Set<Songlist> songlists;
    
    public Set<Songlist> getSonglists() {
		return songlists;
	}

	public void setSonglists(Set<Songlist> songlists) {
		this.songlists = songlists;
	}

	@Override
    public String toString() {
        return "User [userId=" + userId + ", firstname=" + firstName + ", lastname=" + lastName + "]";
    }
    
    public User() {}
    
    public User(String userId) {
        this.userId = userId;
    }
    @Column(name="userId")
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userid) {
        this.userId = userid;
    }
    
    @Column(name="password")
    public String getPassword() {
    	return password;
    }
    
    public void setPassword(String password) {
    	this.password = password;
    }
    
    @Column(name="firstName")
    public String getFirstname() {
        return firstName;
    }
    
    public void setFirstname(String firstname) {
        this.firstName = firstname;
    }
    
    @Column(name="lastName")
    public String getLastname() {
        return lastName;
    }
    
    public void setLastname(String lastname) {
        this.lastName = lastname;
    }
}
