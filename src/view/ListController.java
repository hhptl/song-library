/*
 Rithvik Aleshetty
 Harsh Patel
 */

package view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import app.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;



public class ListController {

	//the ListView on the left side of the interface
	@FXML ListView<Song> listView;
	
	//the selection TextField on the right side
	@FXML TextField screener;
	
	//TextFields named after the items from the fxml
	//show on bottom of the screen of the interfaceGUI
	@FXML TextField SongName, ArtistName, AlbumName, YearEdit;
	
	@FXML Button deleteBtn, editBtn;
	
	//list of the song objects using javaFX collections API
	@FXML private ObservableList<Song> songList = FXCollections.observableArrayList();              

	public void start(Stage mainStage) {    
		//handle the file stuff 
		File f = new File("songs.txt");
		
			if(!f.exists()) {
				try {
					f.createNewFile();
				}
				
				catch(IOException e){
					e.printStackTrace();
				}
				
			}
		
			
		String txtFile = "songs.txt";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		try {
			br = new BufferedReader(new FileReader(txtFile));
			while ((line = br.readLine()) != null) {	//for each song in the song list txt,
				String[] songDetails = new String[4];
				String[] temp = line.split(cvsSplitBy);	//create array of song details, separated by ","

				for(int i = 0; i < temp.length; i++) {
					songDetails[i] = temp[i];
				}

				Song song = new Song(songDetails[0], songDetails[1], songDetails[2], songDetails[3]);
    			songList.add(song);	//add song array entry 0 (song title) to songs list
    		}
			
			//source: https://stackoverflow.com/questions/50296723/setcellfactory-override-updateitem-and-wrap-text
			listView.setItems(songList);
			listView.setCellFactory((list) -> {
				return new ListCell<Song>() {
					protected void updateItem(Song item, boolean empty) {
						super.updateItem(item, empty);

						if (item == null || empty) {
							setText(null);
						} else {
							setText(item.getName() + ", by " + item.getArtist());
						}
					}
				};
			});
			
			
		
		}
		
		catch(IOException e) {
			System.out.println("Error with file");
			
		}
		
	}
	
	
	//display the details on the right side of the screen
	@FXML
	private void displaySelection(MouseEvent event) {
		
		Song songS= listView.getSelectionModel().getSelectedItem();
		
		
		if(songS == null) {
			
			screener.setText("Nothing Selected");
		}
		else {
			String outPut = "Song:     " + songS.getName() + "\n          " + "Artist:     " +  songS.getArtist() + "\n          " +
					"Album:     " + songS.getAlbum() +"\n          " +"Year:     " + songS.getYear();
					
			screener.setText(outPut);
			
			System.out.println(songS.getName());
		}
	
	}
	
	//fields song and artist are the minimum info needed to be filled in 
	private boolean badInput() {
		
		String name = SongName.getText();
		
		if(SongName.getText().isEmpty() || ArtistName.getText().isEmpty()) {
			
			System.out.println("Bad Input: try again human");
			//give an alert to the user as well?
			
			return true;
		}
		
		return false;
	}
	
	@FXML
	private void addS() {
		
		if(badInput()) {
			return; 
		}
		
		Song newSong = new Song(SongName.getText(), ArtistName.getText(), AlbumName.getText(), YearEdit.getText());
		if (sortOnInsertion(newSong) == 0) {
			//there was a duplicate
		}
		
		//source: https://stackoverflow.com/questions/50296723/setcellfactory-override-updateitem-and-wrap-text
				listView.setItems(songList);
				listView.setCellFactory((list) -> {
					return new ListCell<Song>() {
						protected void updateItem(Song item, boolean empty) {
							super.updateItem(item, empty);

							if (item == null || empty) {
								setText(null);
							} else {
								setText(item.getName() + ", by " + item.getArtist());
							}
						}
					};
				});
		
		
				
	listView.getSelectionModel().select(newSong);
	String outPut = "Song:     " + newSong.getName() + "\n          " + "Artist:     " +  newSong.getArtist() + "\n          " +
			"Album:     " + newSong.getAlbum() +"\n          " +"Year:     " + newSong.getYear();
			
		screener.setText(outPut);	
		save();
		System.out.println("test");
		
		//clear the text fields
		
		SongName.clear();
		YearEdit.clear();
		ArtistName.clear();
		AlbumName.clear();
		
	}
	
	
	@FXML
	private void edit(){
		
	}
	
	//use this method instead of just the sort method of FX collections because we use album title to sort in case of ties
	//use lexicographic ordering
	@FXML
	private int sortOnInsertion(Song x) {
		if (songList.size() == 0) {
			songList.add(x);
			return 0;
		}
		
		for(int i = 0; i<songList.size(); i++) {
			if(x.getName().compareToIgnoreCase(songList.get(i).getName()) < 0) {
				songList.add(i,x);
				return 1;
			}
			
			else if (x.getName().compareToIgnoreCase(songList.get(i).getName()) == 0) {
				//same songName
				if(x.getArtist().compareToIgnoreCase(songList.get(i).getArtist()) == 0) {
				//same album and same song DUPLICATE ALERT
				System.out.println("same song same artist");
				return 0;
				}
				
				if(x.getArtist().compareToIgnoreCase(songList.get(i).getArtist()) < 0) {
					//same songName but different artist
					System.out.println("same song diff artist");
					songList.add(i, x);
					return 1;
				}
			}
		}
		songList.add(songList.size(),x);
		
		return 1;
	}
	
	@FXML
	private void delete() {
		Song currentSelection = listView.getSelectionModel().getSelectedItem();
		int indexDeleted = listView.getSelectionModel().getSelectedIndex();
		
		if(currentSelection == null) {
			return;
		}
		
		//songList.remove(currentSelection);
	//	save();
		
		//select next song in list if there is one
		if(indexDeleted != songList.size()-1 && songList.size()!=1) {
			listView.getSelectionModel().selectNext();
			Song newSelection = listView.getSelectionModel().getSelectedItem();
			String outPut = "Song:     " + newSelection.getName() + "\n          " + "Artist:     " +  newSelection.getArtist() + "\n          " +
					"Album:     " + newSelection.getAlbum() +"\n          " +"Year:     " + newSelection.getYear();
					
				screener.setText(outPut);	
				songList.remove(currentSelection);
				save();
				System.out.println("we testing");
			}
		
		//if there is no next song, select the previous song
		else if (indexDeleted == songList.size()-1 && songList.size()!= 1) {
			listView.getSelectionModel().selectPrevious();
			
			Song newSelection = listView.getSelectionModel().getSelectedItem();
			String outPut = "Song:     " + newSelection.getName() + "\n          " + "Artist:     " +  newSelection.getArtist() + "\n          " +
					"Album:     " + newSelection.getAlbum() +"\n          " +"Year:     " + newSelection.getYear();
					
				screener.setText(outPut);
				
				songList.remove(currentSelection);
				save();
		}
		//if list is empty, then empty the details shown
		else if(songList.size() == 1) {
			songList.remove(currentSelection);
			save();
			String outPut = "No Songs";
			screener.setText(outPut);
			//deleteBtn.setDisable(true);
			//editBtn.setDisable(true);
			
		}

	}
	
	public void save() {
		String txtFile = "songs.txt";
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter(txtFile));


			for(int i = 0; i < songList.size(); i++) {
				Song song = songList.get(i);
				String album = song.getAlbum();
				String year = song.getYear();

				if(album == null) {
					album = "";
				}

				if(year == null) {
					year = "";
				}

				writer.write(song.getName() + "," + song.getArtist() + "," + album + "," + year + "\n" );
			}
		}
		catch ( IOException e)
		{
		}
			try
			{
				if ( writer != null)
					writer.close( );
			}
			catch ( IOException e)
			{
			}
	
	}


}
