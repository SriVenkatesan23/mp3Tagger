package mp3Tagger;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import com.mpatric.mp3agic.*;

public class MP3Tagger extends JFrame {

	private static final long serialVersionUID = 1L;
	public static JTextArea textbox = new JTextArea(10, 50);
	public static JButton tagButton = new JButton("Tag all files in this directory");
	public static String dir;

	public MP3Tagger(){

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		//INPUT TEXT AREA
		textbox.setBackground(Color.darkGray);
		textbox.setText("Replace this text with directory name (open folder in explorer, right click folder name in nav bar, hit 'copy dir'):  ");
		textbox.setForeground(Color.lightGray);
		textbox.setFont(new Font("Trebuchet MS", 12, 12));

		//SET CONTENT PANE
		Container c = getContentPane();
		//ADD COMPONENTS TO CONTENT PANE        
		c.add(textbox, BorderLayout.CENTER);
		c.add(tagButton, BorderLayout.SOUTH);
		MP3Tagger.tagButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dir = textbox.getText();
				textbox.setText("");    
				try {
					tag();
				} catch (UnsupportedTagException | InvalidDataException | NotSupportedException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	public static void main(String[] args)  throws UnsupportedTagException, InvalidDataException, IOException, NotSupportedException {
		MP3Tagger m = new MP3Tagger();
		m.setVisible(true);
		m.setFocusable(true);
		m.setSize(700,200);
	}

	public void tag() throws UnsupportedTagException, InvalidDataException, IOException, NotSupportedException{
	
		dir = dir.replace("\\", "\\\\");
		ArrayList<File> images = new ArrayList<File>();
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();

		/**
		 * add all image files to separate List
		 */
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
				int index = temp.lastIndexOf("\\"); //get rid of directory dir in string
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
					imgName = imgName.substring(dir.length()-4, ext); //remove file extension and directory

					if (!s.hasId3v2Tag()) {// if s doesn't have tag, give it one
						s.setId3v2Tag(new ID3v24Tag());

					}
					if(imgName.equals(album)){ //album art found
						RandomAccessFile file = new RandomAccessFile(i, "r");
						byte[] bytes = new byte[(int) file.length()];
						file.read(bytes);
						file.close();
						s.getId3v2Tag().setAlbumImage(bytes, "image/jpeg");
						break;
					}
				}

				s.save(s.getFilename()+".mp3"); //overwrite mp3
				f.delete();

			}


		}
		textbox.setText("All files tagged!");
	}


}