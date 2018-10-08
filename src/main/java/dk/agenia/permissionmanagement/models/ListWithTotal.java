package dk.agenia.permissionmanagement.models;

import java.util.List;

/**
 * Created: 15-09-2018
 * author: Runi
 */
public interface ListWithTotal<T> {
    List<T> getList();
    Long getCount();
}
