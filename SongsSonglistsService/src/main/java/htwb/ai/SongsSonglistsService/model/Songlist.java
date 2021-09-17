package htwb.ai.SongsSonglistsService.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name="songlist")
public class Songlist {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="ownerId")
    private User ownerId;

    @Column(name="name")
    private String name;

    @Column(name="private")
    private boolean isPrivate;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "songlist_song",joinColumns = {@JoinColumn( name = "songlistId", referencedColumnName = "id")},inverseJoinColumns = {@JoinColumn( name = "songId", referencedColumnName = "id")})
    private Set<Song> songList;
    
    public void addSongList(Song song) {
    	if(this.songList == null) {
    		songList = new HashSet<>();
    	}
    	song.getSonglists().add(this);
    	songList.add(song);
    }

    public Set<Song> getSongList() {
		return songList;
	}

	public void setSongList(Set<Song> songList) {
		this.songList = songList;
	}

	public Songlist(){
        
    }

    @Column(name="id")
    public Integer getId() {
        return id;
    }
//    @Column(name="ownerId")
//    public User getOwner() {
//        return ownerId;
//    }
//    public void setOwner(User user) {
//        this.ownerId = user;
//    }
    @Column(name="name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Column(name="private")
    public void setPublic(boolean priv) {
        this.isPrivate = priv;
    }
    
    public boolean isPrivate() {
        return isPrivate;
    }

    @Override
    public String toString() {
        String songs = "";
        List<Song> list = new ArrayList<Song>();
        list.addAll(songList);
        for(int i = 0; i < list.size(); i++) {
        	Song s = list.get(i);
        	if(i != list.size() - 1) {
        		songs += s.toString() + "," + System.lineSeparator();
        	}else {
        		songs += s.toString() + System.lineSeparator();
        	}
        }

        return "{" + System.lineSeparator() + 
                "\"isPrivate\":" + isPrivate + "," + System.lineSeparator() +
                "\"name\":" + "\"" + name + "\"" + "," + System.lineSeparator() +
                "\"songList\": [" + System.lineSeparator() + 
                songs + System.lineSeparator() + 
                "]" + System.lineSeparator() +
                '}';
    }
    
    public String toStringXML() {
    	String songs = "";
    	for(Song s : songList) {
    		songs += s.toStringXML() + System.lineSeparator();
    	}
    	return "<songlist>" + System.lineSeparator() + 
    			"	<id>" + id + "</id>" + System.lineSeparator() + 
    			"	<ownerId>" + ownerId.getUserId() + "</ownerId>" + System.lineSeparator() + 
				"	<name>" + name + "</name>" + System.lineSeparator() + 
				"	<private>" + isPrivate + "</private>" + System.lineSeparator() + 
				"	<songs>" + System.lineSeparator() +
				"	" + songs + 
				"	</songs>" + System.lineSeparator() +
				"</songlist>";
    }

	public User getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(User ownerId) {
		this.ownerId = ownerId;
	}
}
