package music;

import java.io.File;

public class MusicFileInformation {

    private File musicFile;
    private String newMusicFilePath;

    public File getMusicFile() {
        return musicFile;
    }

    public void setMusicFile(File musicFile) {
        this.musicFile = musicFile;
    }

    public String getNewMusicFilePath() {
        return newMusicFilePath;
    }

    public void setNewMusicFilePath(String newMusicFilePath) {
        this.newMusicFilePath = newMusicFilePath;
    }

    public MusicFileInformation(File musicFile, String newMusicFilePath) {
        setMusicFile(musicFile);
        setNewMusicFilePath(newMusicFilePath);
    }

}
