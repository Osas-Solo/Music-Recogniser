package music;

public class Music {

    private String artist;
    private String title;
    private String album;
    private String genre;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Music(String artist, String title, String album, String genre) {
        setArtist(artist);
        setTitle(title);
        setAlbum(album);
        setGenre(genre);
    }  //  end of constructor

}   //  end of class
