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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
					System.out.println("error :(");
				}
				
			}
		
			
		String txtFile = "songs.txt";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		try {
			br = new BufferedReader(new FileReader(txtFile));
			while ((line = br.readLine()) != null) {	
				String[] songDetails = new String[4];
				String[] temp = line.split(cvsSplitBy);	

				for(int i = 0; i < temp.length; i++) {
					songDetails[i] = temp[i];
				}

				Song song = new Song(songDetails[0], songDetails[1], songDetails[2], songDetails[3]);
    			songList.add(song);	
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
		
		SongName.setText(songS.getName());
		ArtistName.setText(songS.getArtist());
		AlbumName.setText(songS.getAlbum());
		YearEdit.setText(songS.getYear());
		if(songS == null) {
			
			screener.setText("Nothing Selected");
		}
		else {
			textSetter(songS);
			
			System.out.println(songS.getName());
		}
	
	}
		
	//fields song and artist are the minimum info needed to be filled in 
	private boolean badInput() {
		
		if(SongName.getText().isEmpty() || ArtistName.getText().isEmpty()) {
			
			System.out.println("Bad Input: try again human");
			//give an alert to the user as well?
			Alert dupAlert = new Alert(Alert.AlertType.INFORMATION);
			dupAlert.setTitle("Warning");
			dupAlert.setHeaderText("Input");
			dupAlert.setContentText("Song and Artist are required Fields");
			dupAlert.showAndWait();
			
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
			Alert dupAlert = new Alert(Alert.AlertType.INFORMATION);
			dupAlert.setTitle("Warning");
			dupAlert.setHeaderText("Input");
			dupAlert.setContentText("Duplicate Song");
			dupAlert.showAndWait();
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
	textSetter(newSong);	
		save();
		System.out.println("test");
		
		//clear the text fields
		cancel();
	}
	
	@FXML
	private void cancel() {
		SongName.clear();
		YearEdit.clear();
		ArtistName.clear();
		AlbumName.clear();
	}
	
	@FXML
	private void edit(){
		if(badInput()) return;
		
		Song currentSelection = listView.getSelectionModel().getSelectedItem();
		//SongName.setText(currentSelection.getName());
		Song newSong = new Song(SongName.getText(), ArtistName.getText(), AlbumName.getText(), YearEdit.getText());
		
		//check the case in which songName or artistName changes
		if(!SongName.getText().equalsIgnoreCase(currentSelection.getName()) 
		|| !ArtistName.getText().equalsIgnoreCase(currentSelection.getArtist())) {
			//in this case, we have to check for duplicates 

			if(sortOnInsertion(newSong) == 0) {
				//duplicates exist
				Alert dupAlert = new Alert(Alert.AlertType.INFORMATION);
				dupAlert.setTitle("Warning");
				dupAlert.setHeaderText("Input");
				dupAlert.setContentText("Duplicate Song");
				dupAlert.showAndWait();
				
			}
			else {
				//song was inserted
				delete();
				listView.getSelectionModel().select(newSong);
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
			
		}
		else {
			//case in which the songName and artistName do not change
			//in this case we do not have to check for duplicates
			delete(); addS();
			listView.getSelectionModel().select(newSong);
		}
		cancel();
		save();
	}
	
	//use this method instead of just the sort method of FX collections because we use album title to sort in case of ties
	//use lexicographic ordering
	@FXML
	private int sortOnInsertion(Song x) {
		if (songList.size() == 0) {
			songList.add(x);
			return 1;
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
		
		//select next song in list if there is one
		if(indexDeleted != songList.size()-1 && songList.size()!=1) {
			listView.getSelectionModel().selectNext();
			Song newSelection = listView.getSelectionModel().getSelectedItem();
			textSetter(newSelection);	
				songList.remove(currentSelection);
				save();
				System.out.println("testing");
			}
		
		//if there is no next song, select the previous song
		else if (indexDeleted == songList.size()-1 && songList.size()!= 1) {
			listView.getSelectionModel().selectPrevious();
			
			Song newSelection = listView.getSelectionModel().getSelectedItem();
			textSetter(newSelection);
				
				songList.remove(currentSelection);
				save();
		}
		//if list is empty, then empty the details shown
		else if(songList.size() == 1) {
			songList.remove(currentSelection);
			save();
			String outPut = "No Songs";
			screener.setText(outPut);

			
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

	//Show the stuff on the interface
	public void textSetter(Song y) {
		String name="", artist="", album= "", year = "";
		if (y.getName()!=null) {
			name = y.getName();
		}
		if(y.getArtist()!=null) {
			artist = y.getArtist();
		}
		if(y.getAlbum()!=null) {
			album = y.getAlbum();
		}
		if(y.getYear()!=null) {
			year = y.getYear();
		}
		
		String output = "Song:     " + name + "          " + "Artist:     " +  artist + "\n          " +
				"Album:     " + album + "          " +"Year:     " + year;
		
			screener.setText(output);;
		
	}
}
