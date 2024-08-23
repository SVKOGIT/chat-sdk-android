package sdk.chat.core.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.internal.SqlUtils;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MESSAGE".
*/
public class MessageDao extends AbstractDao<Message, Long> {

    public static final String TABLENAME = "MESSAGE";

    /**
     * Properties of entity Message.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property EntityID = new Property(1, String.class, "entityID", false, "ENTITY_ID");
        public final static Property Date = new Property(2, java.util.Date.class, "date", false, "DATE");
        public final static Property Type = new Property(3, Integer.class, "type", false, "TYPE");
        public final static Property Status = new Property(4, Integer.class, "status", false, "STATUS");
        public final static Property SenderId = new Property(5, Long.class, "senderId", false, "SENDER_ID");
        public final static Property ThreadId = new Property(6, Long.class, "threadId", false, "THREAD_ID");
        public final static Property NextMessageId = new Property(7, Long.class, "nextMessageId", false, "NEXT_MESSAGE_ID");
        public final static Property PreviousMessageId = new Property(8, Long.class, "previousMessageId", false, "PREVIOUS_MESSAGE_ID");
        public final static Property EncryptedText = new Property(9, String.class, "encryptedText", false, "ENCRYPTED_TEXT");
    }

    private DaoSession daoSession;

    private Query<Message> thread_MessagesQuery;

    public MessageDao(DaoConfig config) {
        super(config);
    }
    
    public MessageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MESSAGE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"ENTITY_ID\" TEXT UNIQUE ," + // 1: entityID
                "\"DATE\" INTEGER," + // 2: date
                "\"TYPE\" INTEGER," + // 3: type
                "\"STATUS\" INTEGER," + // 4: status
                "\"SENDER_ID\" INTEGER," + // 5: senderId
                "\"THREAD_ID\" INTEGER," + // 6: threadId
                "\"NEXT_MESSAGE_ID\" INTEGER," + // 7: nextMessageId
                "\"PREVIOUS_MESSAGE_ID\" INTEGER," + // 8: previousMessageId
                "\"ENCRYPTED_TEXT\" TEXT);"); // 9: encryptedText
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MESSAGE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Message entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String entityID = entity.getEntityID();
        if (entityID != null) {
            stmt.bindString(2, entityID);
        }
 
        java.util.Date date = entity.getDate();
        if (date != null) {
            stmt.bindLong(3, date.getTime());
        }
 
        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(4, type);
        }
 
        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(5, status);
        }
 
        Long senderId = entity.getSenderId();
        if (senderId != null) {
            stmt.bindLong(6, senderId);
        }
 
        Long threadId = entity.getThreadId();
        if (threadId != null) {
            stmt.bindLong(7, threadId);
        }
 
        Long nextMessageId = entity.getNextMessageId();
        if (nextMessageId != null) {
            stmt.bindLong(8, nextMessageId);
        }
 
        Long previousMessageId = entity.getPreviousMessageId();
        if (previousMessageId != null) {
            stmt.bindLong(9, previousMessageId);
        }
 
        String encryptedText = entity.getEncryptedText();
        if (encryptedText != null) {
            stmt.bindString(10, encryptedText);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Message entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String entityID = entity.getEntityID();
        if (entityID != null) {
            stmt.bindString(2, entityID);
        }
 
        java.util.Date date = entity.getDate();
        if (date != null) {
            stmt.bindLong(3, date.getTime());
        }
 
        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(4, type);
        }
 
        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(5, status);
        }
 
        Long senderId = entity.getSenderId();
        if (senderId != null) {
            stmt.bindLong(6, senderId);
        }
 
        Long threadId = entity.getThreadId();
        if (threadId != null) {
            stmt.bindLong(7, threadId);
        }
 
        Long nextMessageId = entity.getNextMessageId();
        if (nextMessageId != null) {
            stmt.bindLong(8, nextMessageId);
        }
 
        Long previousMessageId = entity.getPreviousMessageId();
        if (previousMessageId != null) {
            stmt.bindLong(9, previousMessageId);
        }
 
        String encryptedText = entity.getEncryptedText();
        if (encryptedText != null) {
            stmt.bindString(10, encryptedText);
        }
    }

    @Override
    protected final void attachEntity(Message entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset) ? null : cursor.getLong(offset);
    }    

    @Override
    public Message readEntity(Cursor cursor, int offset) {
        Message entity = new Message( //
            cursor.isNull(offset) ? null : cursor.getLong(offset), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // entityID
            cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)), // date
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // type
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // status
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // senderId
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // threadId
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // nextMessageId
            cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8), // previousMessageId
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // encryptedText
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Message entity, int offset) {
        entity.setId(cursor.isNull(offset) ? null : cursor.getLong(offset));
        entity.setEntityID(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDate(cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)));
        entity.setType(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setStatus(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setSenderId(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setThreadId(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setNextMessageId(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setPreviousMessageId(cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8));
        entity.setEncryptedText(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Message entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Message entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Message entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "messages" to-many relationship of Thread. */
    public List<Message> _queryThread_Messages(Long threadId) {
        synchronized (this) {
            if (thread_MessagesQuery == null) {
                QueryBuilder<Message> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.ThreadId.eq(null));
                queryBuilder.orderRaw("T.'DATE' ASC");
                thread_MessagesQuery = queryBuilder.build();
            }
        }
        Query<Message> query = thread_MessagesQuery.forCurrentThread();
        query.setParameter(0, threadId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getUserDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getThreadDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T2", daoSession.getMessageDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T3", daoSession.getMessageDao().getAllColumns());
            builder.append(" FROM MESSAGE T");
            builder.append(" LEFT JOIN USER T0 ON T.\"SENDER_ID\"=T0.\"_id\"");
            builder.append(" LEFT JOIN THREAD T1 ON T.\"THREAD_ID\"=T1.\"id\"");
            builder.append(" LEFT JOIN MESSAGE T2 ON T.\"NEXT_MESSAGE_ID\"=T2.\"_id\"");
            builder.append(" LEFT JOIN MESSAGE T3 ON T.\"PREVIOUS_MESSAGE_ID\"=T3.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Message loadCurrentDeep(Cursor cursor, boolean lock) {
        Message entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        User sender = loadCurrentOther(daoSession.getUserDao(), cursor, offset);
        entity.setSender(sender);
        offset += daoSession.getUserDao().getAllColumns().length;

        Thread thread = loadCurrentOther(daoSession.getThreadDao(), cursor, offset);
        entity.setThread(thread);
        offset += daoSession.getThreadDao().getAllColumns().length;

        Message nextMessage = loadCurrentOther(daoSession.getMessageDao(), cursor, offset);
        entity.setNextMessage(nextMessage);
        offset += daoSession.getMessageDao().getAllColumns().length;

        Message previousMessage = loadCurrentOther(daoSession.getMessageDao(), cursor, offset);
        entity.setPreviousMessage(previousMessage);

        return entity;    
    }

    public Message loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<Message> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Message> list = new ArrayList<Message>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<Message> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Message> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
