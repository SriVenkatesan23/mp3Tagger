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

		//replace '\' with '\\' so that the directory works
		address = address.replace("\\", "\\\\");
		int nm = 0;
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
				Mp3File s = new Mp3File(f.toString());
				String[] tag = s.getFilename().split(" - ");
				String temp = tag[0];
				int index = temp.lastIndexOf("\\");
				temp = temp.substring(index+1);
				tag[0]=temp;
				String artist = tag[0];
				String album = tag[1];
				String song = tag[2];

				song = song.substring(0, song.length()-4); 
				s.getId3v2Tag().setArtist(artist);
				s.getId3v2Tag().setAlbum(album);
				s.getId3v2Tag().setTitle(song);

				//getting image
				/*
				 * cover art should have same name as track's album
				 */
				for(File i: images){
					String imgName = i.toString();
					int ext = imgName.indexOf("."); //index of extension
					imgName = imgName.substring(address.length()-4, ext); //remove file extension and directory
					System.out.println(imgName);
					if (!s.hasId3v2Tag()) {// if s doesn't have tag, give it one
						s.setId3v2Tag(new ID3v24Tag());
						System.out.println("set tag");
					}
					if(imgName.equals(album)){
						RandomAccessFile file = new RandomAccessFile(i, "r");
						byte[] bytes = new byte[(int) file.length()];
						file.read(bytes);
						file.close();
						s.getId3v2Tag().setAlbumImage(bytes, "image/jpeg");
						break;
					}
				}
				
				s.save(s.getFilename()+"(1).mp3"); //overwrite mp3
				System.out.println("Saved "+ f.toString()); 
			}


		}
		sc.close();
	}



}