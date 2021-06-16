package org.music.tg.bot.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.music.tg.bot.database.dao.CompositionDao;
import org.music.tg.bot.database.dao.PerformerDao;
import org.music.tg.bot.database.entity.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import static org.music.tg.bot.database.DaoUtils.*;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class.getName());
    private final String databaseUrl;

    // table entities. if change, drop database for update
    private static final List<Class<?>> tables = List.of(
            Genre.class,
            Performer.class,
            Composition.class,
            Playlist.class,
            CompositionToPlaylist.class
    );

    // genres list. if change, drop database for update
    private static final List<String> genres = List.of(
            "Pop", "Rock", "Classic", "Techno", "Alternative"
    );

    public DatabaseManager(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    // create tables if not exists
    public void initDatabase() {
        try(var connectionSource = getConnection()) {
            for (Class<?> aClass : tables) {
                TableUtils.createTableIfNotExists(connectionSource, aClass);
            }
            initGenres(connectionSource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initGenres(ConnectionSource connectionSource) throws SQLException {
        Dao<Genre, ?> genreDao = getGenreDao(connectionSource);
        if (genreDao.countOf() == 0) {
            var genres = DatabaseManager.genres.stream().map(name -> {
                Genre genre = new Genre();
                genre.setName(name);
                return genre;
            }).toList();
            genreDao.create(genres);
        }
    }

    public ConnectionSource getConnection() throws SQLException {
        return new JdbcPooledConnectionSource(databaseUrl);
    }

    public Performer findOrCreatePerformer(ConnectionSource source, Long chatId, String name) throws SQLException {
        PerformerDao performerDao = getPerformerDao(source);
        List<Performer> performers = performerDao.findByName(chatId, name);
        // create if not exists
        if (performers.isEmpty()) {
            Performer performer = new Performer();
            performer.setName(name);
            performer.setChatId(chatId);
            performerDao.create(performer);
            return performer;
        }
        return performers.get(0);
    }

    public void setCompositionGenre(Long compositionId, Long genreId) {
        try (var connection = getConnection()) {
            CompositionDao compositionDao = getCompositionDao(connection);
            var composition = compositionDao.queryForId(compositionId);
            Genre genre = new Genre();
            genre.setId(genreId);
            composition.setGenre(genre);
            compositionDao.update(composition);
        } catch (Exception e) {
            logger.error(e, "Error set composition genre " + compositionId);
            throw new RuntimeException(e);
        }
    }

    public void addCompositionPlaylist(Long chatId, Long compositionId, Long playlistId) {
        try (var connection = getConnection()) {
            var compositionToPlaylistDao = getCompositionToPlaylistDao(connection);
            var compositionToPlaylist = new CompositionToPlaylist();
            var composition = new Composition();
            composition.setId(compositionId);
            var playlist = new Playlist();
            playlist.setId(playlistId);
            compositionToPlaylist.setComposition(composition);
            compositionToPlaylist.setPlaylist(playlist);
            compositionToPlaylist.setChatId(chatId);
            compositionToPlaylistDao.create(compositionToPlaylist);
        } catch (Exception e) {
            logger.error(e, "Error set composition genre " + compositionId);
            throw new RuntimeException(e);
        }
    }

    public void addCompositionToNewPlaylist(Long chatId, Long compositionId, String playlistName) {
        try (var connection = getConnection()) {
            var playlist = new Playlist();

            var playlistDao = getPlaylistDao(connection);
            List<Playlist> savedPlaylists = playlistDao.queryBuilder().where().eq("name", playlistName).and().eq("chatId", chatId).query();
            if (!savedPlaylists.isEmpty()) {
                playlist = savedPlaylists.get(0);
            } else {
                playlist.setChatId(chatId);
                playlist.setName(playlistName);
                playlistDao.create(playlist);
            }
            var compositionToPlaylistDao = getCompositionToPlaylistDao(connection);
            var compositionToPlaylist = new CompositionToPlaylist();
            var composition = new Composition();
            composition.setId(compositionId);
            compositionToPlaylist.setComposition(composition);
            compositionToPlaylist.setPlaylist(playlist);
            compositionToPlaylist.setChatId(chatId);
            compositionToPlaylistDao.create(compositionToPlaylist);
        } catch (Exception e) {
            logger.error(e, "Error set composition genre " + compositionId);
            throw new RuntimeException(e);
        }
    }

    public void removeCompositionFromPlaylist(Long chatId, Long compositionId, Long playlistId) {
        try (var connection = getConnection()) {
            var compositionToPlaylistDao = getCompositionToPlaylistDao(connection);
            var values = new HashMap<String, Object>();
            values.put("playlist_id", playlistId);
            values.put("composition_id", compositionId);
            values.put("chatId", chatId);
            List<CompositionToPlaylist> compositionToPlaylists = compositionToPlaylistDao.queryForFieldValues(values);
            for (CompositionToPlaylist toPlaylist : compositionToPlaylists) {
                compositionToPlaylistDao.delete(toPlaylist);
            }
        } catch (Exception e) {
            logger.error(e, "Error remove composition from playlist " + compositionId);
            throw new RuntimeException(e);
        }
    }

    public void removePlaylist(Long playlistId) {
        try (var connection = getConnection()) {
            var compositionToPlaylistDao = getCompositionToPlaylistDao(connection);
            var compositionToPlaylists = compositionToPlaylistDao.queryForEq("playlist_id", playlistId);
            for (var compositionToPlaylist : compositionToPlaylists) {
                compositionToPlaylistDao.delete(compositionToPlaylist);
            }
            Playlist playlist = new Playlist();
            playlist.setId(playlistId);
            getPlaylistDao(connection).delete(playlist);
        } catch (Exception e) {
            logger.error(e, "Error remove playlist " + playlistId);
            throw new RuntimeException(e);
        }
    }

    public void removeComposition(Long compositionId) {
        try (var connection = getConnection()) {
            var compositionToPlaylistDao = getCompositionToPlaylistDao(connection);
            var compositionToPlaylists = compositionToPlaylistDao.queryForEq("composition_id", compositionId);
            for (var compositionToPlaylist : compositionToPlaylists) {
                compositionToPlaylistDao.delete(compositionToPlaylist);
            }
            Composition composition = new Composition();
            composition.setId(compositionId);
            getCompositionDao(connection).delete(composition);
        } catch (Exception e) {
            logger.error(e, "Error remove composition " + compositionId);
            throw new RuntimeException(e);
        }
    }

    public List<Composition> listCompositions(Long chatId) {
        try (var connection = getConnection()) {
            CompositionDao compositionDao = getCompositionDao(connection);
            return compositionDao.queryForEq("chatId", chatId);
        } catch (Exception e) {
            logger.error(e, "Error listing compositions");
            throw new RuntimeException(e);
        }
    }

    public List<Performer> listPerformers(Long chatId) {
        try (var connection = getConnection()) {
            var performerDao = getPerformerDao(connection);
            return performerDao.queryForEq("chatId", chatId);
        } catch (Exception e) {
            logger.error(e, "Error listing performers");
            throw new RuntimeException(e);
        }
    }

    public List<Genre> listGenres() {
        try (var connection = getConnection()) {
            var genresDao = getGenreDao(connection);
            return genresDao.queryForAll();
        } catch (Exception e) {
            logger.error(e, "Error listing genres");
            throw new RuntimeException(e);
        }
    }

    public List<Playlist> listPlaylists(Long chatId) {
        try (var connection = getConnection()) {
            var playlistDao = getPlaylistDao(connection);
            return playlistDao.queryForEq("chatId", chatId);
        } catch (Exception e) {
            logger.error(e, "Error listing genres");
            throw new RuntimeException(e);
        }
    }

    public List<Composition> listCompositionsByPerformer(Long chatId, Long performerId) {
        try (var connection = getConnection()) {
            CompositionDao compositionDao = getCompositionDao(connection);
            return compositionDao.findByPerformer(chatId, performerId);
        } catch (Exception e) {
            logger.error(e, "Error listing compositions");
            throw new RuntimeException(e);
        }
    }

    public List<Composition> listCompositionsByPlaylist(Long chatId, Long playlistId) {
        try (var connection = getConnection()) {
            Dao<CompositionToPlaylist, ?> compositionToPlaylistDao = getCompositionToPlaylistDao(connection);
            return compositionToPlaylistDao.queryBuilder().where().eq("playlist_id", playlistId).and().eq("chatId", chatId).query()
                    .stream().map(CompositionToPlaylist::getComposition).toList();
        } catch (Exception e) {
            logger.error(e, "Error listing compositions by playlist");
            throw new RuntimeException(e);
        }
    }

    public List<Composition> listCompositionsByGenre(Long chatId, Long genreId) {
        try (var connection = getConnection()) {
            CompositionDao compositionDao = getCompositionDao(connection);
            return compositionDao.findByGenre(chatId, genreId);
        } catch (Exception e) {
            logger.error(e, "Error listing compositions");
            throw new RuntimeException(e);
        }
    }
}
