package htwb.ai.LyricsService.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Repository;

import htwb.ai.LyricsService.exception.NotFoundException;
import htwb.ai.LyricsService.model.Lyric;

@Repository
public class LyricsRepositoryImpl implements LyricsRepository{
	
	private final String DIRECTORY;
	
	private static FileWriter fileWriter;
	
	public LyricsRepositoryImpl() {
		//Setting storage directory
		String projectPath = System.getProperty("user.dir");
		DIRECTORY = projectPath + File.separatorChar + "storage";
	}

	@Override
	public boolean save(Lyric lyric) {
		boolean success = false;
		String fileName = lyric.getArtist() + "-" + lyric.getTitle();
		try {
			FileOutputStream fos = new FileOutputStream(DIRECTORY + File.separatorChar + fileName);
			ObjectOutputStream objectOut = new ObjectOutputStream(fos);
			objectOut.writeObject(lyric);
			objectOut.close();
			success = true;
		} catch (FileNotFoundException e) {
			System.out.println("File with the name \"" + fileName + "\" was not found");
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return success;
	}

	@Override
	public boolean changeLyric(Lyric lyric) throws NotFoundException {
		delete(lyric.getArtist(), lyric.getTitle());
		if(save(lyric)) {
			return true;
		}else {			
			return false;
		}
	}

	@Override
	public boolean delete(String artist, String title) {
		File file = new File(DIRECTORY + File.separatorChar + artist + "-" + title);
		if(file.delete()) {
			return true;
		}else {
			return false;			
		}
	}

	@Override
	public Lyric getLyric(String artist, String title) {
		String name = artist.trim() + "-" + title.trim();
		FileInputStream fos;
		Lyric lyric = null;
		try {
			fos = new FileInputStream(DIRECTORY + File.separatorChar + name);
			ObjectInputStream objectIn = new ObjectInputStream(fos);
			lyric = (Lyric) objectIn.readObject();
		} catch (FileNotFoundException e) {
			lyric = null;
			e.printStackTrace();
		}finally {			
			return lyric;
		}
	}

	@Override
	public List<Lyric> lyricsByAuthor(String author) {
		File folder = new File(DIRECTORY);
		File[] files = folder.listFiles();
		List<Lyric> lyricsList = new ArrayList<Lyric>();
		String pattern = author + "-" + ".*";
		for(File f : files) {
			//If file is found with the pattern "*author*"
			if(Pattern.matches(pattern, f.getName())) {
				System.out.println(f.getName());
				String[] filename = f.getName().split("-");
				System.out.println("Author:" + filename[0] + ". Songname: " + filename[1]);
				Lyric lyric = this.getLyric(filename[0], filename[1]);
				lyricsList.add(lyric);
			}
		}
		return lyricsList;
	}

}
