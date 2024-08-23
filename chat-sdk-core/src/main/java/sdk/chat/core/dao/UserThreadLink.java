package sdk.chat.core.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;

import sdk.chat.core.session.ChatSDK;
import sdk.chat.core.types.Affiliation;

@Entity
public class UserThreadLink {

    @Id
    private Long id;
    private Long userId;
    private Long threadId;

    @ToMany(referencedJoinProperty = "userThreadLinkId")
    private List<UserThreadLinkMetaValue> metaValues;

    @ToOne(joinProperty = "userId")
    private User user;
    @ToOne(joinProperty = "threadId")
    private Thread thread;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1486061129)
    private transient UserThreadLinkDao myDao;

    @Generated(hash = 1628597386)
    public UserThreadLink(Long id, Long userId, Long threadId) {
        this.id = id;
        this.userId = userId;
        this.threadId = threadId;
    }

    @Generated(hash = 1452575878)
    public UserThreadLink() {
    }
    @Generated(hash = 251390918)
    private transient Long user__resolvedKey;
    @Generated(hash = 1974258785)
    private transient Long thread__resolvedKey;
    
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUserId() {
        return this.userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getThreadId() {
        return this.threadId;
    }
    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 859885876)
    public User getUser() {
        Long __key = this.userId;
        if (user__resolvedKey == null || !user__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User userNew = targetDao.load(__key);
            synchronized (this) {
                user = userNew;
                user__resolvedKey = __key;
            }
        }
        return user;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1065606912)
    public void setUser(User user) {
        synchronized (this) {
            this.user = user;
            userId = user == null ? null : user.getId();
            user__resolvedKey = userId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1483947909)
    public Thread getThread() {
        Long __key = this.threadId;
        if (thread__resolvedKey == null || !thread__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ThreadDao targetDao = daoSession.getThreadDao();
            Thread threadNew = targetDao.load(__key);
            synchronized (this) {
                thread = threadNew;
                thread__resolvedKey = __key;
            }
        }
        return thread;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 372886343)
    public void setThread(Thread thread) {
        synchronized (this) {
            this.thread = thread;
            threadId = thread == null ? null : thread.getIdentifier();
            thread__resolvedKey = threadId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2059316886)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserThreadLinkDao() : null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 532971758)
    public List<UserThreadLinkMetaValue> getMetaValues() {
        if (metaValues == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserThreadLinkMetaValueDao targetDao = daoSession.getUserThreadLinkMetaValueDao();
            List<UserThreadLinkMetaValue> metaValuesNew = targetDao
                    ._queryUserThreadLink_MetaValues(id);
            synchronized (this) {
                if (metaValues == null) {
                    metaValues = metaValuesNew;
                }
            }
        }
        return metaValues;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 365870950)
    public synchronized void resetMetaValues() {
        metaValues = null;
    }

    @Keep
    public UserThreadLinkMetaValue metaValueForKey (String key) {
        return MetaValueHelper.metaValueForKey(key, getMetaValues());
    }

    @Keep
    public boolean setMetaValue (String key, String value) {
        UserThreadLinkMetaValue metaValue = metaValueForKey(key);

        if (metaValue == null || metaValue.getValue() == null || !metaValue.getValue().equals(value)) {
            if (metaValue == null) {
                metaValue = ChatSDK.db().createEntity(UserThreadLinkMetaValue.class);
                metaValue.setUserThreadLinkId(this.getId());
                getMetaValues().add(metaValue);
            }

            metaValue.setValue(value);
            metaValue.setKey(key);
            metaValue.update();
            update();

            return true;
        }
        return false;
    }

    @Keep
    public String getAffiliation() {
        return valueForKey(Keys.Affiliation);
    }

    @Keep
    public boolean setAffiliation(String affiliation) {
        return setMetaValue(Keys.Affiliation, affiliation);
    }

    @Keep
    public String getRole() {
        return valueForKey(Keys.Role);
    }

    @Keep
    public boolean setRole(String role) {
        return setMetaValue(Keys.Role, role);
    }

    @Keep
    public boolean hasLeft() {
        return Boolean.parseBoolean(valueForKey(Keys.HasLeft));
    }

    @Keep
    public boolean setHasLeft(boolean hasLeft) {
        return setMetaValue(Keys.HasLeft, String.valueOf(hasLeft));
    }

    @Keep
    public boolean isActive() {
        return Boolean.parseBoolean(valueForKey(Keys.Active));
    }

    @Keep
    public boolean setIsActive(boolean active) {
        return setMetaValue(Keys.Active, String.valueOf(active));
    }

    @Keep
    public boolean isBanned() {
        boolean banned = Boolean.parseBoolean(valueForKey(Keys.Banned));
        if (banned) {
            return true;
        }
        String affiliation = getAffiliation();
        if (affiliation != null) {
            return affiliation.equals(Affiliation.outcast.name());
        }
        return false;
    }

    @Keep
    public boolean setIsBanned(boolean banned) {
        return setMetaValue(Keys.Banned, String.valueOf(banned));
    }

    public String valueForKey(String key) {
        UserThreadLinkMetaValue value = metaValueForKey(key);
        if (value != null) {
            return value.getValue();
        }
        return null;
    }

    public void cascadeDelete() {
        for (UserThreadLinkMetaValue value : getMetaValues()) {
            value.delete();
        }
        delete();
    }
}
