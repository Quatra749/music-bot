package org.music.tg.bot.database.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

// many to many join table
@DatabaseTable(tableName = "composition_to_playlist")
public class CompositionToPlaylist {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false)
    private Long chatId;

    @DatabaseField(foreign = true, columnName = "composition_id", foreignAutoRefresh = true)
    private Composition composition;

    @DatabaseField(foreign = true, columnName = "playlist_id", foreignAutoRefresh = true)
    private Playlist playlist;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Composition getComposition() {
        return composition;
    }

    public void setComposition(Composition composition) {
        this.composition = composition;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompositionToPlaylist that = (CompositionToPlaylist) o;
        return id == that.id && Objects.equals(chatId, that.chatId) && Objects.equals(composition, that.composition) && Objects.equals(playlist, that.playlist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, composition, playlist);
    }
}
