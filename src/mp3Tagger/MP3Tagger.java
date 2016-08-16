package mp3Tagger;


import java.io.File;
import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import com.mpatric.mp3agic.*;

public class MP3Tagger {

	public static void main(String[] args) throws UnsupportedTagException, InvalidDataException, IOException, NotSupportedException {

		System.out.println("Enter directory (open folder in explorer, right click folder name in nav bar, hit 'copy address'): ");
		Scanner sc = new Scanner(System.in);
		String address = sc.nextLine();

		address = address.replace("\\", "\\\\");


		ArrayList<File> images = new ArrayList<File>();
		File folder = new File(address);
		File[] listOfFiles = folder.listFiles();

		for(File f:listOfFiles){
			if(f.toString().endsWith("jpg") || f.toString().endsWith("png") || f.toString().endsWith("jpeg")    ){
				images.add(f);
			}
		}

		for(File f:listOfFiles){
			if(f.toString().endsWith(".mp3")){
				System.out.println("sc");
				Mp3File s = new Mp3File(f.toString());
				String[] tag = s.getFilename().split(" - ");

				String temp = tag[0];
				int index = temp.lastIndexOf("\\");
				temp = temp.substring(index+1);
				tag[0]=temp;

				String artist = tag[0];
				String album = tag[1];
				String song = tag[2];
				
				//getting image
				for(File i: images){
					String imgName = i.toString();
					int ext = imgName.indexOf(".");
					imgName = imgName.substring(address.length()-4, ext); //remove file extension
					System.out.println(imgName);
					if(imgName.equals(album)){
						RandomAccessFile file = new RandomAccessFile(i, "r");
						byte[] bytes = new byte[(int) file.length()];
						file.read(bytes);
						file.close();
						s.getId3v2Tag().setAlbumImage(bytes, "image/jpeg");
						break;
					}
				}

				song = song.substring(0, song.length()-4); 
				s.getId3v2Tag().setArtist(artist);
				s.getId3v2Tag().setAlbum(album);
				s.getId3v2Tag().setTitle(song);
				s.save(s.getFilename()+" ");
				System.out.println("Saved "+ f.toString());
				continue;
			}
			
			
		}
		sc.close();
	}
	
	

}