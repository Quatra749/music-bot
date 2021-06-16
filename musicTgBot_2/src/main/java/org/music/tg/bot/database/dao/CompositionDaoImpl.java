package org.music.tg.bot.database.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import org.music.tg.bot.database.entity.Composition;

import java.sql.SQLException;
import java.util.List;

public class CompositionDaoImpl extends BaseDaoImpl<Composition, Long>
        implements CompositionDao {
    public CompositionDaoImpl(JdbcPooledConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Composition.class);
    }

    @Override
    public List<Composition> findByName(Long chatId, String name) throws SQLException {
        return super.queryBuilder().where().eq("name", name).and().eq("chatId", chatId).query();
    }

    @Override
    public List<Composition> findByPerformer(Long chatId, Long performerId) throws SQLException {
        return super.queryBuilder().where().eq("performer_id", performerId).and().eq("chatId", chatId).query();
    }

    @Override
    public List<Composition> findByGenre(Long chatId, Long genreId) throws SQLException {
        return super.queryBuilder().where().eq("genre_id", genreId).and().eq("chatId", chatId).query();
    }
}
