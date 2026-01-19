package ru.yandex.practicum.repository;

import jakarta.validation.constraints.NotNull;


public class Utils {

    /**
     * Посчитать количество страниц относительно количества записей на странице. Формула с округлением вверх.
     * Если хоть один входной параметр меньше либо равен нулю, то возвращается значение 1.
     * @param countRecords количество запией всего.
     * @param pageSize количество записей на одной странице (максимальное).
     * @return количество страниц или 1.
     */
    public static long calculateCountPages(@NotNull final Long countRecords,
                                          final int pageSize){
        if(countRecords <= 0 || pageSize <= 0){
            return 1;
        }
        return (long) Math.ceil((double) countRecords / pageSize);
    }
}
