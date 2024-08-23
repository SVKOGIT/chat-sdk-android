/*
 * Created by Itzik Braun on 12/3/2015.
 * Copyright (c) 2015 deluge. All rights reserved.
 *
 * Last Modification at: 3/12/15 4:27 PM
 */

package sdk.chat.core.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.QueryBuilder;
import org.pmw.tinylog.Logger;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import sdk.chat.core.interfaces.CoreEntity;
import sdk.chat.core.session.ChatSDK;

/**
 * Manage all creation, deletion and updating Entities.
 */
public class DaoCore {

    private static final String DB_NAME = "andorid-chatsdk-database";
    private static String dbName;

    public static final int ORDER_ASC = 0;
    public static final int ORDER_DESC = 1;

    private static Context context;

    private static DaoMaster.OpenHelper helper;

    @SuppressWarnings("all")
    private static SQLiteDatabase db;
    public static DaoMaster daoMaster;
    public static DaoSession daoSession;
    public static AsyncSession asyncSession;

    /**
     * The property of the "EntityID" of the saved object. This entity comes from the server, For example Firebase server save Entities id's with an Char and Integers sequence.
     * The link between Entities in the databse structure is based on a long id generated by the database automatically.
     * To obtain an CoreEntity using his server id we have to user this property.
     * Each CoreEntity generates its own EntityID property so its very important to save the property id as the first property right after the long id property.
     * A workaround this is available by Checking for certain classes and use a different property for this class.
     */
    public final static Property EntityID = new Property(1, String.class, "entityID", false, "ENTITY_ID");

    public static void init(Context ctx) {
        dbName = DB_NAME;
        context = ctx;

        if (helper == null) {
            openDB();
        }
    }

    public static void init(Context ctx, String databaseName) {
        context = ctx;
        dbName = databaseName;

        if (helper == null)
            openDB();
    }

    private static void openDB() {
        if (context == null)
            throw new NullPointerException("Context is null, Did you initialized DaoCore?");

        if (ChatSDK.config().debug) {
            helper = new DaoMaster.DevOpenHelper(context, dbName, null);
        } else {
            helper = new DatabaseUpgradeHelper(context, dbName);
        }

        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        asyncSession = daoSession.startAsyncSession();
    }

    public static String generateRandomName() {
        return new BigInteger(130, new Random()).toString(32);
    }

    /**
     * Fetch entity for given entity ID, If more then one found the first will be returned.
     */
    public static <T extends CoreEntity> T fetchEntityWithEntityID(Class<T> c, Object entityID) {
        Property[] properties = daoSession.getDao(c).getProperties();

        if (!properties[1].columnName.equals(EntityID.columnName))
            return null; // EntityId is missing from dao table, must always be first property after id

        return fetchEntityWithProperty(c, properties[1], entityID);
    }

    /**
     * Fetch an entity for given property and value. If more then one found the first will be returned.
     */
    public static <T extends CoreEntity> T fetchEntityWithProperty(Class<T> c, Property property, Object value) {
        QueryBuilder<T> qb = daoSession.queryBuilder(c);
        qb.where(property.eq(value));

        List<T> list = qb.list();
        if (list != null && list.size() > 0)
            return list.get(0);
        else return null;
    }

    public static <T> T fetchEntityWithProperties(Class<T> c, Property[] properties, Object... values) {
        List<T> list = fetchEntitiesWithPropertiesAndOrder(c, null, -1, properties, values);

        if (list == null || list.size() == 0)
            return null;

        return list.get(0);
    }

    /**
     * Fetch a list of entities for a given property and value.
     */
    public static <T> List<T> fetchEntitiesOfClass(Class<T> c) {
        QueryBuilder<T> qb = daoSession.queryBuilder(c);
        return qb.list();
    }

    /**
     * Fetch a list of entities for a given property and value.
     */
    public static <T> List<T> fetchEntitiesWithProperty(Class<T> c, Property property, Object value) {
        QueryBuilder<T> qb = daoSession.queryBuilder(c);
        qb.where(property.eq(value));
        return qb.list();
    }

    /**
     * Fetch a list of entities for a given properties and values.
     */
    public static <T> List<T> fetchEntitiesWithProperties(Class<T> c, Property[] properties, Object... values) {
        return fetchEntitiesWithPropertiesAndOrder(c, null, -1, properties, values);
    }

    /**
     * Fetch a list of entities for a given property and value. Entities are arrange by given order.
     */
    public static <T> List<T> fetchEntitiesWithPropertyAndOrder(Class<T> c, Property whereOrder, int order, Property property, Object value) {
        return fetchEntitiesWithPropertiesAndOrder(c, whereOrder, order, new Property[]{property}, value);
    }

    public static <T> List<T> fetchEntitiesWithPropertiesAndOrder(Class<T> c, Property whereOrder, int order, Property[] properties, Object... values) {

        if (values == null || properties == null)
            throw new NullPointerException("You must have at least one value and one property");

        if (values.length != properties.length)
            throw new IllegalArgumentException("Values size should match properties size");

        QueryBuilder<T> qb = daoSession.queryBuilder(c);
        qb.where(properties[0].eq(values[0]));

        for (int i = 0; i < values.length; i++)
            qb.where(properties[i].eq(values[i]));

        if (whereOrder != null && order != -1)
            switch (order) {
                case ORDER_ASC:
                    qb.orderAsc(whereOrder);
                    break;

                case ORDER_DESC:
                    qb.orderDesc(whereOrder);
                    break;
            }

        return qb.list();
    }

    /* Update, Create and Delete*/
    public static <T> T createEntity(T entity) {
        if (entity == null) {
            return null;
        }

        daoSession.insertOrReplace(entity);

        return entity;
    }

    public static <T> T deleteEntity(T entity) {
        if (entity == null) {
            return null;
        }

        daoSession.delete(entity);
        daoSession.clear();

        Logger.debug("Update Entity: " + entity);
        return entity;
    }

    public static <T extends CoreEntity> T updateEntity(T entity) {
        if (entity == null) {
            return null;
        }
        asyncSession.update(entity);
        Logger.debug("Update Entity: " + entity);
        return entity;
    }

    public static boolean connectUserAndThread(User user, Thread thread) {
        Logger.debug("connectUserAndThread, CoreUser ID: %s, Name: %s, ThreadID: %s", +user.getId(), user.getName(), thread.getIdentifier());
        if (!thread.hasUser(user)) {
            UserThreadLink linkData = new UserThreadLink();
            linkData.setThreadId(thread.getIdentifier());
            linkData.setThread(thread);
            linkData.setUserId(user.getId());
            linkData.setUser(user);
            createEntity(linkData);
            return true;
        }
        return false;
    }

    public static boolean breakUserAndThread(User user, Thread thread) {
        Logger.debug("breakUserAndThread, CoreUser ID: %s, Name: %s, ThreadID: %s", +user.getId(), user.getName(), thread.getIdentifier());
        UserThreadLink linkData = fetchEntityWithProperties(UserThreadLink.class, new Property[]{UserThreadLinkDao.Properties.ThreadId, UserThreadLinkDao.Properties.UserId}, thread.getIdentifier(), user.getId());
        if (linkData != null) {
            deleteEntity(linkData);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getEntityForClass(Class<T> c) {
        // Create the new entity.
        Class<T> clazz;
        T o = null;
        try {
            clazz = (Class<T>) Class.forName(c.getName());
            Constructor<T> ctor = clazz.getConstructor();
            o = ctor.newInstance();
        } catch (Exception e) {
            Logger.error(e);
        }

        return o;
    }

    public static void drop() {
        for (AbstractDao<?, ?> dao : daoSession.getAllDaos()) {
            if (!(dao instanceof UserDao)) {
                dao.deleteAll();
            }
        }
    }
}
