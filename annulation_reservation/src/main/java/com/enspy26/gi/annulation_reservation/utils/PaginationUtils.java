package com.enspy26.gi.annulation_reservation.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public class PaginationUtils {
    /**
     * Convertit un Slice<T> en Page<T>.
     *
     * @param slice l'objet Slice contenant les données paginées.
     * @param total le nombre total d'éléments (optionnel, peut être -1 si non
     *              connu).
     * @param <T>   le type générique pour les entités.
     * @return Page<T> contenant les données et les métadonnées paginées.
     */
    public static <T> Page<T> SliceToPage(Slice<T> slice, long total) {
        List<T> content = slice.getContent();
        Pageable pageable = slice.getPageable();

        // Si total est -1, renvoyer uniquement les données Slice sans total
        if (total < 0) {
            return new PageImpl<>(content, pageable, slice.hasNext() ? Long.MAX_VALUE : content.size());
        }

        // Sinon, créer un objet Page avec le total connu
        return new PageImpl<>(content, pageable, total);
    }

    public static <T> Page<T> ContentToPage(List<T> sliceContent, Pageable pageable, long total) {

        // Sinon, créer un objet Page avec le total connu
        return new PageImpl<>(sliceContent, pageable, total);
    }
}
