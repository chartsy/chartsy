package org.chartsy.main.utils;

import java.util.Comparator;
import org.chartsy.main.dataset.DataItem;

/**
 *
 * @author viorel.gheba
 */
public class DateCompare implements Comparator<DataItem> {

    public int compare(DataItem o1, DataItem o2) {
        return o1.getDate().compareTo(o2.getDate());
    }

}
