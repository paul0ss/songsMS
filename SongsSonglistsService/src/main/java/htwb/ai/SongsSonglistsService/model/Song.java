package htwb.ai.SongsSonglistsService.model;

import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name="song")
public class Song {
	
	//highest id + 1 <=> id of the next inserted song
	//private static int MAXID = 1;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id", unique=true)
    private Integer id;
	
	@Column(name="title", nullable=false)
	private String title;
	@Column(name="artist")
	private String artist;
	@Column(name="label")
	private String label;
	@Column(name="released")
	private Integer released;
	
	@ManyToMany(mappedBy = "songList")
	Set<Songlist> songlists;

	public Set<Songlist> getSonglists() {
		return songlists;
	}
	public void setSonglists(Set<Songlist> songlists) {
		this.songlists = songlists;
	}
	public Song() {
		//this.id = MAXID++;
	}
	@Column(name="id")
	public Integer getId() {
		return id;
	}
	@Column(name="title")
    public String getTitle() {
		return title;
	}
	
    public void setTitle(String title) {
        this.title = title;
    }
    @Column(name="artist")
	public String getArtist() {
		return artist;
	}
    
	public void setArtist(String artist) {
        this.artist = artist;
    }
	@Column(name="label")
	public void setLabel(String label) {
		this.label = label;
	}
	public String getLabel() {
		return label;
	}

	@Column(name="released")
	public void setReleased(Integer released) {
        this.released = released;
    }
	public Integer getReleased() {
		return released;
	}


    @Override
    public String toString() {
        return "{" + System.lineSeparator() + 
        		"\"id\": " + id + "," + System.lineSeparator() + 
        		"\"title\": " + "\"" + title + "\"" + "," + System.lineSeparator() + 
        		"\"artist\": " + "\"" + artist + "\"" + "," + System.lineSeparator() + 
        		"\"label\": " + "\"" + label + "\"" + "," + System.lineSeparator() + 
        		"\"released\": " + "\"" + released + "\"" + System.lineSeparator() + 
        		"}";
    }
	public String toStringXML() {
		return "<song>" + System.lineSeparator() + 
				"	<id>" + id + "</id>" + System.lineSeparator() + 
				"	<title>" + title + "</title>" + System.lineSeparator() + 
				"	<artist>" + artist + "</artist>" + System.lineSeparator() + 
				"	<label>" + label + "</label>" + System.lineSeparator() + 
				"	<released>" + released + "</released>" + System.lineSeparator() + 
				"</song>";
	}
}