/*
 * $Id$ 
 * $Revision$ $Date$
 *
 * This file is part of ***  M y C o R e  ***
 * See http://www.mycore.de/ for details.
 *
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 2
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, in a file called gpl.txt or license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 */
package org.mycore.mir.authorization.accesskeys;

import org.hibernate.Session;
import org.mycore.backend.hibernate.MCRHIBConnection;
import org.mycore.datamodel.metadata.MCRObjectID;

/**
 * Provides methods to store, update, delete and retrieve
 * {@link MCRObject} access keys.
 * 
 * @author Ren\u00E9 Adler (eagle)
 * @since 0.3
 */
public final class MIRAccessKeyManager {

    private static final MCRHIBConnection MCRHIB_CONNECTION = MCRHIBConnection.instance();

    /**
     * Returns the {@link MIRAccessKeyPair} for given {@link MCRObjectID}.
     * 
     * @param mcrObjectId the {@link MCRObjectID}
     * @return the {@link MIRAccessKeyPair}
     */
    public static MIRAccessKeyPair getKeyPair(final MCRObjectID mcrObjectId) {
        final Session session = MCRHIB_CONNECTION.getSession();

        return (MIRAccessKeyPair) session.get(MIRAccessKeyPair.class, mcrObjectId.toString());
    }

    /**
     * Checks if an {@link MIRAccessKeyPair} exists for given {@link MCRObjectID}.
     * 
     * @param mcrObjectId the {@link MCRObjectID}.
     * @return <code>true</code> if exists or <code>false</code> if not
     */
    public static boolean existsKeyPair(final MCRObjectID mcrObjectId) {
        final Session session = MCRHIB_CONNECTION.getSession();

        final MIRAccessKeyPair accKP = getKeyPair(mcrObjectId);
        boolean exists = accKP != null;

        if (exists) {
            session.evict(accKP);
        }

        return exists;
    }

    /**
     * Persists the given {@link MIRAccessKeyPair}.
     * 
     * @param accKP the {@link MIRAccessKeyPair}
     */
    public static void createKeyPair(final MIRAccessKeyPair accKP) {
        if (existsKeyPair(accKP.getMCRObjectId()))
            throw new IllegalArgumentException("Access key pair for MCRObject " + accKP.getObjectId()
                    + " already exists");

        final Session session = MCRHIB_CONNECTION.getSession();
        session.save(accKP);
    }

    /**
     * Updates the given {@link MIRAccessKeyPair} or create a new one if not exists.
     * 
     * @param accKP the {@link MIRAccessKeyPair}
     */
    public static void updateKeyPair(final MIRAccessKeyPair accKP) {
        if (!existsKeyPair(accKP.getMCRObjectId())) {
            createKeyPair(accKP);
            return;
        }

        final Session session = MCRHIB_CONNECTION.getSession();
        session.update(accKP);
    }

    /**
     * Deletes the {@link MIRAccessKeyPair} for given {@link MCRObjectID}.
     * 
     * @param mcrObjectId the {@link MCRObjectID}
     */
    public static void deleteKeyPair(final MCRObjectID mcrObjectId) {
        if (!existsKeyPair(mcrObjectId))
            throw new IllegalArgumentException("Couldn't delete non exists key pair for MCRObject " + mcrObjectId);

        final Session session = MCRHIB_CONNECTION.getSession();
        session.delete(getKeyPair(mcrObjectId));
    }
}
