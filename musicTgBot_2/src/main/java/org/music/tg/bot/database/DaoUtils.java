package org.music.tg.bot.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import org.music.tg.bot.database.dao.CompositionDao;
import org.music.tg.bot.database.dao.PerformerDao;
import org.music.tg.bot.database.entity.*;

import java.sql.SQLException;

public final class DaoUtils {
    private DaoUtils(){}

    public static CompositionDao getCompositionDao(ConnectionSource connectionSource) throws SQLException {
        return DaoManager.createDao(connectionSource, Composition.class);
    }

    public static PerformerDao getPerformerDao(ConnectionSource connectionSource) throws SQLException {
        return DaoManager.createDao(connectionSource, Performer.class);
    }

    public static Dao<Genre, ?> getGenreDao(ConnectionSource connectionSource) throws SQLException {
        return DaoManager.createDao(connectionSource, Genre.class);
    }

    public static Dao<Playlist, ?> getPlaylistDao(ConnectionSource connectionSource) throws SQLException {
        return DaoManager.createDao(connectionSource, Playlist.class);
    }

    public static Dao<CompositionToPlaylist, ?> getCompositionToPlaylistDao(ConnectionSource connectionSource) throws SQLException {
        return DaoManager.createDao(connectionSource, CompositionToPlaylist.class);
    }

}
