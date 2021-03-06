package org.apache.fulcrum.security.impl.db;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.sql.Connection;

import org.apache.fulcrum.security.entity.SecurityEntity;

import org.apache.fulcrum.security.util.TurbineSecurityException;

import org.apache.torque.om.Persistent;
import org.apache.torque.om.ObjectKey;


/**
 * All the DB Security objects (User, Group, Role, Permission) are
 * derived from this class which contains the base compare and management
 * methods for all security objects.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public abstract class DBObject
    implements SecurityEntity,
               Comparable,
               Persistent
{
    /** The underlying database Object which is proxied */
    protected Persistent obj = null;

    /**
     * Constructs a new DBObject
     *
     */
    public DBObject()
    {
    }

    /**
     * Constructs a new Object with the specified name.
     *
     * @param name The name of the new object.
     */
    public DBObject(String name)
    {
        this.setName(name);
    }

    /**
     * This Constructor is used when a Manager 
     * has retrieved a list of Database Objects from the peer and
     * must 'wrap' them into DBObjects.
     *
     * @param obj An Object from the peer
     */
   public  DBObject(Persistent obj)
    {
        this.obj = obj;
    }

    /**
     * Returns the underlying Object for the Peer
     *
     * @return The underlying persistent object
     *
     */
    public abstract Persistent getPersistentObj();

    /**
     * Returns the name of this object
     *
     * @return The name of the object
     */
    public abstract String getName();

    /**
     * Sets the name of this object
     *
     * @param name The name of the object
     */
    public abstract void setName(String name);

    /**
     * getter for the object primaryKey.
     *
     * @return the object primaryKey as an Object
     */
    public ObjectKey getPrimaryKey()
    {
        Persistent p = getPersistentObj();
        if(p != null)
        {
            return p.getPrimaryKey();
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the PrimaryKey for the object.
     *
     * @param primaryKey The new PrimaryKey for the object.
     *
     * @exception Exception This method might throw an exceptions
     */
    public void setPrimaryKey(ObjectKey primaryKey) 
        throws Exception
    {
        getPersistentObj().setPrimaryKey(primaryKey);
    }

    /**
     * Sets the PrimaryKey for the object.
     *
     * @param primaryKey the String should be of the form produced by
     *        ObjectKey.toString().
     *
     * @exception Exception This method might throw an exceptions
     */
    public void setPrimaryKey(String primaryKey)
        throws Exception
    {
        getPersistentObj().setPrimaryKey(primaryKey);
    }

    /**
     * Returns whether the object has been modified, since it was
     * last retrieved from storage.
     *
     * @return True if the object has been modified.
     */
    public boolean isModified()
    {
        return getPersistentObj().isModified();
    }

    /**
     * Returns whether the object has ever been saved.  This will
     * be false, if the object was retrieved from storage or was created
     * and then saved.
     *
     * @return true, if the object has never been persisted.
     */
    public boolean isNew()
    {
        return getPersistentObj().isNew();
    }

    /**
     * Setter for the isNew attribute.  This method will be called
     * by Torque-generated children and Peers.
     *
     * @param b the state of the object.
     */
    public void setNew(boolean b)
    {
        getPersistentObj().setNew(b);
    }

    /**
     * Sets the modified state for the object.
     *
     * @param m The new modified state for the object.
     */
    public void setModified(boolean m)
    {
        getPersistentObj().setModified(m);
    }

    /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.
     *
     * @param dbName The name under which the object should be stored.
     *
     * @exception Exception This method might throw an exceptions
     */
    public void save(String dbName) 
        throws Exception
    {
        getPersistentObj().save(dbName);
    }

    /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.  This method
     * is meant to be used as part of a transaction, otherwise use
     * the save() method and the connection details will be handled
     * internally
     *
     * @param con A Connection object to save the object
     *
     * @exception Exception This method might throw an exceptions
     */
    public void save(Connection con) 
        throws Exception
    {
        getPersistentObj().save(con);
    }

    /**
     * Makes changes made to the DBObject permanent.
     *
     * @throws TurbineSecurityException if there is a problem while
     *  saving data.
     */
    public abstract void save()
        throws TurbineSecurityException;

    /**
     * Used for ordering DBObjects.
     *
     * @param obj The Object to compare to.
     * @return -1 if the name of the other object is lexically greater than this
     *         group, 1 if it is lexically lesser, 0 if they are equal.
     */
    public int compareTo(Object obj)
    {
        if (this.getClass() != obj.getClass())
        {
            throw new ClassCastException();
        }
        String name1 = ((SecurityEntity) obj).getName();
        String name2 = this.getName();

        return name2.compareTo(name1);
    }

    /**
     * Compares this with another <code>BaseObject</code> instance.  If
     * <code>obj</code> is an instance of <code>BaseObject</code>, delegates to
     * <code>equals(BaseObject)</code>.  Otherwise, returns <code>false</code>.
     *
     * @param obj The object to compare to.
     * @return    Whether equal to the object specified.
     */
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof DBObject)
        {
            return equals((DBObject) obj);
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Compares the primary key of this instance with the key of another.
     *
     * @param dbObject The DBObject to compare to.
     * @return   Whether the primary keys are equal.
     */
    public boolean equals(DBObject dbObject)
    {
        if (dbObject == null)
        {
            return false;
        }
        if (this == dbObject)
        {
            return true;
        }
        else if (getPrimaryKey() == null || dbObject.getPrimaryKey() == null)
        {
            return false;
        }
        else
        {
            return getPrimaryKey().equals(dbObject.getPrimaryKey());
        }
    }

    /**
     * If the primary key is not <code>null</code>, return the hashcode of the
     * primary key.  Otherwise calls <code>Object.hashCode()</code>.
     *
     * @return an <code>int</code> value
     */
    public int hashCode()
    {
        ObjectKey ok = getPrimaryKey();
        if (ok == null)
        {
            return super.hashCode();
        }

        return ok.hashCode();
    }
}
