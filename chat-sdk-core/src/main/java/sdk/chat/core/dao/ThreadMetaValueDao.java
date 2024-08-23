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
 * DAO for table "THREAD_META_VALUE".
*/
public class ThreadMetaValueDao extends AbstractDao<ThreadMetaValue, Long> {

    public static final String TABLENAME = "THREAD_META_VALUE";

    /**
     * Properties of entity ThreadMetaValue.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Key = new Property(1, String.class, "key", false, "KEY");
        public final static Property StringValue = new Property(2, String.class, "stringValue", false, "STRING_VALUE");
        public final static Property BooleanValue = new Property(3, Boolean.class, "booleanValue", false, "BOOLEAN_VALUE");
        public final static Property IntegerValue = new Property(4, Integer.class, "integerValue", false, "INTEGER_VALUE");
        public final static Property LongValue = new Property(5, Long.class, "longValue", false, "LONG_VALUE");
        public final static Property FloatValue = new Property(6, Float.class, "floatValue", false, "FLOAT_VALUE");
        public final static Property ThreadId = new Property(7, Long.class, "threadId", false, "THREAD_ID");
    }

    private DaoSession daoSession;

    private Query<ThreadMetaValue> thread_MetaValuesQuery;

    public ThreadMetaValueDao(DaoConfig config) {
        super(config);
    }
    
    public ThreadMetaValueDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"THREAD_META_VALUE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"KEY\" TEXT," + // 1: key
                "\"STRING_VALUE\" TEXT," + // 2: stringValue
                "\"BOOLEAN_VALUE\" INTEGER," + // 3: booleanValue
                "\"INTEGER_VALUE\" INTEGER," + // 4: integerValue
                "\"LONG_VALUE\" INTEGER," + // 5: longValue
                "\"FLOAT_VALUE\" REAL," + // 6: floatValue
                "\"THREAD_ID\" INTEGER);"); // 7: threadId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"THREAD_META_VALUE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ThreadMetaValue entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String key = entity.getKey();
        if (key != null) {
            stmt.bindString(2, key);
        }
 
        String stringValue = entity.getStringValue();
        if (stringValue != null) {
            stmt.bindString(3, stringValue);
        }
 
        Boolean booleanValue = entity.getBooleanValue();
        if (booleanValue != null) {
            stmt.bindLong(4, booleanValue ? 1L: 0L);
        }
 
        Integer integerValue = entity.getIntegerValue();
        if (integerValue != null) {
            stmt.bindLong(5, integerValue);
        }
 
        Long longValue = entity.getLongValue();
        if (longValue != null) {
            stmt.bindLong(6, longValue);
        }
 
        Float floatValue = entity.getFloatValue();
        if (floatValue != null) {
            stmt.bindDouble(7, floatValue);
        }
 
        Long threadId = entity.getThreadId();
        if (threadId != null) {
            stmt.bindLong(8, threadId);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ThreadMetaValue entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String key = entity.getKey();
        if (key != null) {
            stmt.bindString(2, key);
        }
 
        String stringValue = entity.getStringValue();
        if (stringValue != null) {
            stmt.bindString(3, stringValue);
        }
 
        Boolean booleanValue = entity.getBooleanValue();
        if (booleanValue != null) {
            stmt.bindLong(4, booleanValue ? 1L: 0L);
        }
 
        Integer integerValue = entity.getIntegerValue();
        if (integerValue != null) {
            stmt.bindLong(5, integerValue);
        }
 
        Long longValue = entity.getLongValue();
        if (longValue != null) {
            stmt.bindLong(6, longValue);
        }
 
        Float floatValue = entity.getFloatValue();
        if (floatValue != null) {
            stmt.bindDouble(7, floatValue);
        }
 
        Long threadId = entity.getThreadId();
        if (threadId != null) {
            stmt.bindLong(8, threadId);
        }
    }

    @Override
    protected final void attachEntity(ThreadMetaValue entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset) ? null : cursor.getLong(offset);
    }    

    @Override
    public ThreadMetaValue readEntity(Cursor cursor, int offset) {
        ThreadMetaValue entity = new ThreadMetaValue( //
                cursor.isNull(offset) ? null : cursor.getLong(offset), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // key
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // stringValue
            cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0, // booleanValue
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // integerValue
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // longValue
            cursor.isNull(offset + 6) ? null : cursor.getFloat(offset + 6), // floatValue
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7) // threadId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ThreadMetaValue entity, int offset) {
        entity.setId(cursor.isNull(offset) ? null : cursor.getLong(offset));
        entity.setKey(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setStringValue(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setBooleanValue(cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0);
        entity.setIntegerValue(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setLongValue(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setFloatValue(cursor.isNull(offset + 6) ? null : cursor.getFloat(offset + 6));
        entity.setThreadId(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ThreadMetaValue entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ThreadMetaValue entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ThreadMetaValue entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "metaValues" to-many relationship of Thread. */
    public List<ThreadMetaValue> _queryThread_MetaValues(Long threadId) {
        synchronized (this) {
            if (thread_MetaValuesQuery == null) {
                QueryBuilder<ThreadMetaValue> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.ThreadId.eq(null));
                thread_MetaValuesQuery = queryBuilder.build();
            }
        }
        Query<ThreadMetaValue> query = thread_MetaValuesQuery.forCurrentThread();
        query.setParameter(0, threadId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getThreadDao().getAllColumns());
            builder.append(" FROM THREAD_META_VALUE T");
            builder.append(" LEFT JOIN THREAD T0 ON T.\"THREAD_ID\"=T0.\"id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected ThreadMetaValue loadCurrentDeep(Cursor cursor, boolean lock) {
        ThreadMetaValue entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Thread thread = loadCurrentOther(daoSession.getThreadDao(), cursor, offset);
        entity.setThread(thread);

        return entity;    
    }

    public ThreadMetaValue loadDeep(Long key) {
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
    public List<ThreadMetaValue> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<ThreadMetaValue> list = new ArrayList<ThreadMetaValue>(count);
        
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
    
    protected List<ThreadMetaValue> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<ThreadMetaValue> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
