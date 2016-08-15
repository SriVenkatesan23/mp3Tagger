package mp3Tagger;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import com.mpatric.mp3agic.*;

public class MP3Tagger {

	public static void main(String[] args) throws UnsupportedTagException, InvalidDataException, IOException, NotSupportedException {
		
		System.out.println("Enter directory (open folder in explorer, right click folder name in nav bar, hit 'copy address'): ");
		Scanner sc = new Scanner(System.in);
		String address = sc.nextLine();
		
		address = address.replace("\\", "\\\\");
		
		
		File folder = new File(address);
		File[] listOfFiles = folder.listFiles();
		
		
		for(File f:listOfFiles){
			Mp3File s = new Mp3File(f.toString());
			String[] tag = s.getFilename().split(" - ");
			
			String temp = tag[0];
			int index = temp.lastIndexOf("\\");
			temp = temp.substring(index+1);
			tag[0]=temp;
			
			for(int i=0;i<3;i++){
				System.out.println(tag[i]);
			}
			String artist = tag[0];
			String album = tag[1];
			String song = tag[2];
			song = song.substring(0, song.length()-4); 
			s.getId3v2Tag().setArtist(artist);
			s.getId3v2Tag().setAlbum(album);
			s.getId3v2Tag().setTitle(song);
			s.save(s.getFilename()+" ");
			System.out.println("Saved "+ f.toString());
		}
		
		sc.close();
		

	}

}