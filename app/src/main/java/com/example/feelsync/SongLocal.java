package com.example.feelsync;

public class SongLocal {
    private final long id;
    private final String title;
    private final String artist;
    private final String path;
    private final long duration;
    private final String album;
    private final long albumId;

    public SongLocal(long id, String title, String artist, String path, long duration, String album, long albumId) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.duration = duration;
        this.album = album;
        this.albumId = albumId;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getPath() {
        return path;
    }

    public long getDuration() {
        return duration;
    }

    public String getAlbum() {
        return album;
    }

    public long getAlbumId() {
        return albumId;
    }
}