package com.dominiccobo.fyp.stackexchange.provider;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "stackexchange")
public class StackExchangeConfiguration {

    /**
     * The number of pages allowed to be retrieved from stack exchange's API.
     *
     * Unfortunately, StackExchange's API is very limited in terms of requests that an authenticated
     * and unauthenticated user can make. Use sparingly, as each request generally has a page size of
     * 30 items.
     */
    private int maxPagesPerQuery = 2;
    private int maxItemsPerQueryPage = 30;
    private String apiBaseUrl = "https://api.stackexchange.com/2.2";

    public final int getMaxPagesPerQuery() {
        return maxPagesPerQuery;
    }

    public final String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setMaxPagesPerQuery(int maxPagesPerQuery) {
        this.maxPagesPerQuery = maxPagesPerQuery;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public final int getMaxItemsPerQueryPage() {
        return maxItemsPerQueryPage;
    }

    public void setMaxItemsPerQueryPage(int maxItemsPerQueryPage) {
        this.maxItemsPerQueryPage = maxItemsPerQueryPage;
    }
}
