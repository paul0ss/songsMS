package htwb.ai.LyricsService.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import htwb.ai.LyricsService.exception.NotFoundException;
import htwb.ai.LyricsService.model.Lyric;

@Repository
public interface LyricsRepository {
	
	public Lyric getLyric(String artist, String title);
	public boolean save (Lyric lyric);
	public boolean changeLyric(Lyric lyric) throws NotFoundException;
	public boolean delete(String artist, String title);
	public List<Lyric> lyricsByAuthor(String author);
}
