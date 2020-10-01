/*
 Rithvik Aleshetty
 Harsh Patel
 */
package app;

public class Song  {
	
	//the details which shall be shown on the interface
	private String name;
	private String artist;
	private String album;
	private String year;
	
	//self explanatory setter and getter methods for access/editing of the above details
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setArtist(String artist) {
		this.artist = artist; 
	}
	
	public void setAlbum(String album) {
		this.album = album;
	}
	
	public void setYear(String year) {
		this.year = year;
	}
	
	public Song(String name, String artist, String album, String year)
	{
		this.name = name;
		this.artist = artist;
		this.album = album;
		this.year = year;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getArtist() {
		return this.artist;
	}
	
	public String getAlbum() {
		return this.album;
	}
	
	public String getYear() {
		return this.year;
	}

}
