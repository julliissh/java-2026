package service;

import entity.BaseEntity;
import java.util.Collection;

public final class EntityUtils {

    private EntityUtils() {
    }

    public static void printAll(Collection<? extends BaseEntity> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        for (BaseEntity item : items) {
            System.out.println("  " + item);
        }
    }
}
