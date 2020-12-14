package gui;

import acrConfiguration.ACRConfiguration;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import music.*;
import com.acrcloud.utils.*;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.json.*;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.audio.exceptions.*;
import org.jaudiotagger.tag.*;

public class MusicRecogniserController {

    public static ArrayList<MusicFileInformation> selectMusicFiles(Stage window, TextArea filesNameDisplay) {
        ArrayList<MusicFileInformation> musicFiles = new ArrayList<>();

        FileChooser musicSelector = new FileChooser();
        musicSelector.setTitle("Select Music");

        List<File> files = musicSelector.showOpenMultipleDialog(window);

        if (files != null) {
            for (File file : files) {
                musicFiles.add(new MusicFileInformation(file, file.getPath()));
                filesNameDisplay.appendText("\"" + file.getPath() + "\"" + "\n");
            }
        }

        return musicFiles;
    }   //  end of selectMusicFiles()

    public static void renameMusicFiles(ArrayList<MusicFileInformation> musicFilesInformation, Stage window, TextArea resultDisplay) {
        DirectoryChooser directorySelector = new DirectoryChooser();
        directorySelector.setTitle("Select Directory For Recognised Music");

        File directory = directorySelector.showDialog(window);
        String newFilePath = directory.getPath();

        Map<String, Object> configuration = new HashMap<String, Object>();
        setupConfiguration(configuration);

        ACRCloudRecognizer audioRecogniser = new ACRCloudRecognizer(configuration);

        if (musicFilesInformation != null) {
            for (MusicFileInformation musicFileInfo : musicFilesInformation) {
                createRecognisedMusic(audioRecogniser, musicFileInfo, newFilePath, resultDisplay);
            }
        }

    }   //  end of renameMusicFiles()

    private static void setupConfiguration(Map<String, Object> config) {
        config.put("host", ACRConfiguration.HOST);
        config.put("access_key", ACRConfiguration.ACCESS_KEY);
        config.put("access_secret", ACRConfiguration.ACCESS_SECRET_CODE);
        config.put("rec_type", ACRCloudRecognizer.RecognizerType.AUDIO);
        config.put("debug", false);
        config.put("timeout", 10); // seconds
    }   //  end of setupConfiguration()

    private static void createRecognisedMusic(ACRCloudRecognizer audioRecogniser, MusicFileInformation musicFileInfo,
                                              String newFilePath, TextArea resultDisplay) {
        String recognisedMusicJson = audioRecogniser.recognizeByFile(musicFileInfo.getMusicFile().getPath(), 10);

        System.out.println(recognisedMusicJson);

        if (!recognisedMusicJson.contains("Http Error")) {
            JSONObject musicJsonObject = new JSONObject(recognisedMusicJson);
            JSONObject musicDetails = musicJsonObject.getJSONObject("metadata").getJSONArray("music").getJSONObject(0);

            Music music;

            String title = "";
            String artist = "";
            String album = "";
            String genre = "";

            try {
                title = musicDetails.getString("title");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                artist = musicDetails.getJSONArray("artists").getJSONObject(0).getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                album = musicDetails.getJSONObject("album").getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                genre = getGenreFromJSON(musicDetails);
            } catch (Exception e) {
                e.printStackTrace();
            }

            music = new Music(artist, title, album, genre);

            String fileExtension = ".mp3";
            if (musicFileInfo.getMusicFile().getPath().contains(".")) {
                fileExtension = musicFileInfo.getMusicFile().getPath().substring(musicFileInfo.getMusicFile().getPath().lastIndexOf("."));
            }
            String newMusicFileName = artist + " - " + title + fileExtension;
            newFilePath += "\\" + newMusicFileName;
            musicFileInfo.setNewMusicFilePath(newFilePath);

            try {
                Files.copy(musicFileInfo.getMusicFile().toPath(), new File(newFilePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (fileExtension == ".mp3") {
                setMP3MusicTag(musicFileInfo, music);
            } else {
                setOtherMusicFormatTag(musicFileInfo, music);
            }

            resultDisplay.appendText(musicFileInfo.getMusicFile().getPath() + "\n -> " + newFilePath);
            resultDisplay.appendText("\n\n");
        }   //  end of if Http Error

        else {
            showAlert("Music Recognition Error", "Please ensure you have good internet connection during the recognition process");
        }
    }   //  end of createRecognisedMusic()

    private static String getGenreFromJSON(JSONObject musicDetails) {
        String genre = "";

        JSONArray genreJSONArray = musicDetails.getJSONArray("genres");

        for (int i = 0; i < genreJSONArray.length(); i++) {
            if (!genre.isEmpty()) {
                genre += ", ";
            }

            genre += genreJSONArray.getJSONObject(i).getString("name");
        }

        return genre;
    }

    private static void setMP3MusicTag(MusicFileInformation musicFileInfo, Music music) {
        try {
            MP3File audioFile = new MP3File(musicFileInfo.getNewMusicFilePath());
            AbstractID3v2Tag audioTag = audioFile.getID3v2Tag();
            audioTag.setField(FieldKey.ARTIST, music.getArtist());
            audioTag.setField(FieldKey.TITLE, music.getTitle());
            audioTag.setField(FieldKey.ALBUM, music.getAlbum());;
            audioTag.setField(FieldKey.ALBUM_ARTIST, music.getArtist());
            audioTag.setField(FieldKey.GENRE, music.getGenre());;
            audioFile.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        } catch (CannotWriteException e) {
            e.printStackTrace();
        }
    }

    private static void setOtherMusicFormatTag(MusicFileInformation musicFileInfo, Music music) {
        try {
            AudioFile audioFile = AudioFileIO.read(musicFileInfo.getMusicFile());
            Tag audioTag = audioFile.getTagOrCreateDefault();
            audioTag.setField(FieldKey.ARTIST, music.getArtist());
            audioTag.setField(FieldKey.TITLE, music.getTitle());
            audioTag.setField(FieldKey.ALBUM, music.getAlbum());;
            audioTag.setField(FieldKey.ALBUM_ARTIST, music.getArtist());
            audioTag.setField(FieldKey.GENRE, music.getGenre());;
            audioFile.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        } catch (CannotWriteException e) {
            e.printStackTrace();
        }
    }

    public static void showAlert(String title, String contentText) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText("");
        errorAlert.setContentText(contentText);
        errorAlert.showAndWait();
    }   //  end of showAlert()

    public static void reset(ArrayList<MusicFileInformation> musicFilesInformation, TextArea filesNameDisplay, TextArea resultDisplay) {
        musicFilesInformation.clear();
        filesNameDisplay.clear();
        resultDisplay.clear();
        resultDisplay.appendText("Converted files\n\n");
    }   //  end of reset()

}   //  end of class