package org.music.tg.bot.database.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import org.music.tg.bot.database.entity.Performer;

import java.sql.SQLException;
import java.util.List;

public class PerformerDaoImpl extends BaseDaoImpl<Performer, Long>
        implements PerformerDao {
    public PerformerDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Performer.class);
    }

    @Override
    public List<Performer> findByName(Long chatId, String name) throws SQLException {
        return super.queryBuilder().where().eq("name", name).and().eq("chatId", chatId).query();
    }
}
