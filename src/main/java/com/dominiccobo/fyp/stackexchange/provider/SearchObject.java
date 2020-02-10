package com.dominiccobo.fyp.stackexchange.provider;

import com.dominiccobo.fyp.context.models.Pagination;

/**
 * Convenience object just in case we decide to change / complicate the search approach later on.
 * Saves us from having to change the StackExchangeAPI interface.
 */
public class SearchObject {

    final String searchTerm;
    final Pagination pagination;

    public SearchObject(String searchTerm, Pagination pagination) {
        this.searchTerm = searchTerm;
        this.pagination = pagination;
    }

    public boolean isValidPagedQuerySize(StackExchangeConfiguration config) {
        if(pagination.page > config.getMaxPagesPerQuery()) {
            return false;
        }
        if(pagination.itemsPerPage > config.getMaxItemsPerQueryPage()) {
            return false;
        }
        return true;
    }
}
