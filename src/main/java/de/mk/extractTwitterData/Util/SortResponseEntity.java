package de.mk.extractTwitterData.Util;

import de.mk.extractTwitterData.API.responseEntity;

import java.util.Comparator;

/**
 * @author Marcel
 * @project extractTwitterData
 * @email mk@mkarrasch.de
 * @createdOn 27.11.2016
 * @package de.mk.extractTwitterData.Util
 */

public class SortResponseEntity implements Comparator<responseEntity> {

    /**
     * Sort all Entities alphabetical (A->Z) by comparing to entities
     *
     * @param e1 First Entity
     * @param e2 Second Entity
     * @return Return result of sorting
     */
    @Override
    public int compare (responseEntity e1, responseEntity e2) {
        return e1.getEntity().compareTo(e2.getEntity());
    }


}
