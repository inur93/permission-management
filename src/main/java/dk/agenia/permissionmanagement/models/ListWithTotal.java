package dk.agenia.permissionmanagement.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created: 15-09-2018
 * author: Runi
 */
public interface ListWithTotal<T> {
    List<T> getList();
    Long getCount();
}
