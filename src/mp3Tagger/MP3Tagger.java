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
	private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir")); //allows user to choose files

	public static void main(String[] args)  throws UnsupportedTagException, InvalidDataException, IOException, NotSupportedException {
		MP3Tagger m = new MP3Tagger();
		m.dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		m.dialog.setAcceptAllFileFilterUsed(false);
		m.setVisible(true);
		m.setFocusable(true);
		m.setSize(700,200);
	}

	public MP3Tagger(){

		setTitle("Automatic MP3 Tagger");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		createMenu();
		//Input text area
		textbox.setBackground(Color.darkGray);
		textbox.setText(" Use 'File' tab to select directory, or replace this text with directory path and hit button below. \r\n (To get directory path, right click the folder name in explorer nav bar, and hit 'Copy Address')");
		textbox.setForeground(Color.lightGray);
		textbox.setFont(new Font("Trebuchet MS", 12, 12));
		//Set content pane
		Container c = getContentPane();
		//Add all components to content pane      
		c.add(textbox, BorderLayout.CENTER);
		c.add(tagButton, BorderLayout.SOUTH);
		//once user enters directory into the text field, they can press the button to tag all mp3's
		MP3Tagger.tagButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dir = textbox.getText();
				textbox.setText("");    
				try {
					tag();
				} catch (UnsupportedTagException | InvalidDataException | NotSupportedException | IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	/**
	 *Create menu from which user can select directories 
	 */
	
	private JMenuBar createMenu(){
		JMenuBar JMB = new JMenuBar();
		setJMenuBar(JMB);
		JMenu file = new JMenu("File"); //file dropdown menu
		JMB.add(file);
		file.add(Open);
		file.addSeparator();
		file.getItem(0).setIcon(null);
		return JMB;
	}
	
	/**
	 * User selects folder and the tag method is automatically called 
	 */
	Action Open = new AbstractAction("Select Directory", new ImageIcon("open.gif")) {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			if(dialog.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				//dialog.getSelectedFile returns file with name given by dialog
				File directory = dialog.getSelectedFile(); 
				dir  = directory.toString();
				textbox.setText("Directory: "+dir); //print selected directory to the text field
				try { 
					tag();
				} catch (UnsupportedTagException | InvalidDataException | NotSupportedException | IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	};

	/**
	 * Loops through each file in the directory and uses the filename and local image files
	 * to tag the mp3 with artist, album, song title, and album cover
	 * 
	 * @throws UnsupportedTagException
	 * @throws InvalidDataException
	 * @throws IOException
	 * @throws NotSupportedException
	 */
	
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
		/*
		 * loop through all mp3 files and tag them
		 */
		for(File f:listOfFiles){
			if(f.toString().endsWith(".mp3")){
				Mp3File s = new Mp3File(f.toString());

				String[] tag = s.getFilename().split(" - ");
				String temp = tag[0];
				int index = temp.lastIndexOf("\\"); //get rid of directory in string
				temp = temp.substring(index+1);
				tag[0]=temp;
				String artist = tag[0];
				String album = tag[1];
				String song = tag[2];
				/*Example:
				* File name: Led Zeppelin - Houses Of The Holy - D'yer Mak'er
				* index 0 of array holds "Led Zeppelin"
				* index 1 of array holds "Houses Of The Holy"
				* index 2 of array holds "D'yer Mak'er"
				*/
				
				
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
		textbox.setText(textbox.getText()+"\r\n All files tagged!");
	}


}
