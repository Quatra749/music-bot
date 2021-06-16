package org.music.tg.bot.database.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.music.tg.bot.database.dao.CompositionDaoImpl;

import java.util.Objects;

@DatabaseTable(tableName = "compositions", daoClass = CompositionDaoImpl.class)
public class Composition {
    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(canBeNull = false)
    private Long chatId;
    @DatabaseField(canBeNull = false)
    private String fileId;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Performer performer;
    @DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true)
    private Genre genre;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Performer getPerformer() {
        return performer;
    }

    public void setPerformer(Performer performer) {
        this.performer = performer;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Composition that = (Composition) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(chatId, that.chatId) && Objects.equals(fileId, that.fileId) && Objects.equals(performer, that.performer) && Objects.equals(genre, that.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, chatId, fileId, performer, genre);
    }
}
