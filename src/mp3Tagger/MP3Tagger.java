package mp3Tagger;

import java.io.IOException;

import com.mpatric.mp3agic.*;

public class MP3Tagger {

	public static void main(String[] args) throws UnsupportedTagException, InvalidDataException, IOException, NotSupportedException {
		
		
		
		Mp3File s = new Mp3File("Young Boy - The Mixtape - Lil Bois Won't win.mp3");
		String[] tag = s.getFilename().split(" - ");
		String artist = tag[0];
		String album = tag[1];
		String song = tag[2];
		song = song.substring(0, song.length()-4); 
		s.getId3v2Tag().setArtist(artist);
		s.getId3v2Tag().setAlbum(album);
		s.getId3v2Tag().setTitle(song);
		s.save(s.getFilename()+" ");
		
		
		

	}

}
