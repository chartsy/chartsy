package org.chartsy.main.data;

import java.util.Comparator;

/**
 *
 * @author viorel.gheba
 */
public class DateCompare implements Comparator<DataItem> {

    public int compare(DataItem o1, DataItem o2) {
        return o1.getDate().compareTo(o2.getDate());
    }

}
