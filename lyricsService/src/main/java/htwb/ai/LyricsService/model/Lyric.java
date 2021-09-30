package htwb.ai.LyricsService.model;

import java.io.Serializable;

public class Lyric implements Serializable{
	
	private String artist;
	
    private String title;

    private String text;

    public Lyric()
    {
    }

    public Lyric(String artist, String songTitle, String text)
    {
    	 this.artist = artist;
         this.title = songTitle;
         this.text = text;
    }
    

    public String getTitle()
    {
         return title;
    }

    public void setTitle(String title)
    {
         this.title = title;
    }

    public String getText()
    {
         return text;
    }

    public void setText(String text)
    {
         this.text = text;
    }
    
    public String getArtist()
    {
         return artist;
    }

    public void setArtist(String artist)
    {
         this.artist = artist;
    }
    
    public String toJSONString() {
        return "{" + System.lineSeparator() + 
        		"    " + "\"artist\": " + "\"" + artist + "\"" + "," + System.lineSeparator() + 
        		"    " + "\"title\": " + "\"" + title + "\"" + "," + System.lineSeparator() + 
        		"    " + "\"text\": " + "\"" + text + "\"" + System.lineSeparator() + 
        		"}";
    }
    
    public String toXMLString() {
    	return "<lyric>" + System.lineSeparator() + 
				"	<artist>" + artist + "</artist>" + System.lineSeparator() + 
				"	<title>" + title + "</title>" + System.lineSeparator() + 
				"	<text>" + text + "</released>" + System.lineSeparator() + 
				"</lyric>";
    }

}
