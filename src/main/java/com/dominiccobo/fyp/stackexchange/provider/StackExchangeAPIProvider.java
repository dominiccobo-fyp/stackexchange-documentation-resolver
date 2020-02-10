package com.dominiccobo.fyp.stackexchange.provider;

import com.dominiccobo.fyp.context.models.Pagination;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class StackExchangeAPIProvider implements StackExchangeAPI {

    private static final Logger LOG = LoggerFactory.getLogger(StackExchangeAPIProvider.class);

    public static final String DEFAULT_ORDER = "desc";
    public static final String SORT_BY_ACTIVITY = "activity";
    private final StackExchangeConfiguration config;
    private final RestTemplate restTemplate;
    @Override
    public Stream<Question> search(SearchObject search) {
        Pagination pagination = search.pagination;
        if (!search.isValidPagedQuerySize(config)) {
            LOG.warn("Query for {} exceeded maximum page criteria. {}/{} {}/{}",
                    search.searchTerm, pagination.itemsPerPage,
                    config.getMaxItemsPerQueryPage(), pagination.page, config.getMaxPagesPerQuery());
            return Stream.empty();
        }

        Optional<StackExchangeSearchResult> searchResults = searchExcerpts(search);
        if (searchResults.isPresent()) {
            StackExchangeSearchResult stackExchangeSearchResult = searchResults.get();
            LOG.info("Remaining quota is: {}/{}", stackExchangeSearchResult.getQuotaRemaining(), stackExchangeSearchResult.getQuotaMax());
            return stackExchangeSearchResult.getItems().stream().map(BoundarySafeApiObject::new);
        }

        return Stream.empty();
    }

    private static class BoundarySafeApiObject implements Question {
        private final StackExchangeResultItem apiResponse;

        private BoundarySafeApiObject(StackExchangeResultItem apiResponse) {
            this.apiResponse = apiResponse;
        }

        @Override
        public String getQuestionTitle() {
            return this.apiResponse.getTitle();
        }

        @Override
        public String getAcceptedAnswer() {
            return this.apiResponse.getBody();
        }

        @Override
        public String getQuestionLink() {
            return String.format("https://stackoverflow.com/questions/%d", this.apiResponse.getQuestionId());
        }

        @Override
        public String getLastActivity() {
            return this.apiResponse.getLastActivityDate();
        }

        @Override
        public List<String> getTags() {
            return this.apiResponse.getTags();
        }
    }



    @Autowired
    public StackExchangeAPIProvider(StackExchangeConfiguration config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }



    private Optional<StackExchangeSearchResult> searchExcerpts(SearchObject searchObject) {
        String uriTemplate = "/search/excerpts?order={order}&sort={sortBy}&q={searchQuestion}&accepted={searchOnlyAccepted}&site={stackSite}";
        HashMap<String, String> variables = new HashMap<>();
        variables.put("order", DEFAULT_ORDER);
        variables.put("sortBy", SORT_BY_ACTIVITY);
        variables.put("searchQuestion",searchObject.searchTerm);
        variables.put("searchOnlyAccepted", "true");
        variables.put("stackSite", "stackoverflow");
        ResponseEntity<StackExchangeSearchResult> result = this.restTemplate.getForEntity(uriTemplate, StackExchangeSearchResult.class, variables);
        if(result.getStatusCode().is2xxSuccessful()) {
            return Optional.ofNullable(result.getBody());
        }
        else return Optional.empty();
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    private final static class StackExchangeSearchResult {

        private StackExchangeSearchResult() { }

        private ArrayList<StackExchangeResultItem> items;
        private boolean hasMore;
        private int quotaMax;
        private int quotaRemaining;


        public final ArrayList<StackExchangeResultItem> getItems() {
            return items;
        }

        public final boolean isHasMore() {
            return hasMore;
        }

        public final int getQuotaMax() {
            return quotaMax;
        }

        public final int getQuotaRemaining() {
            return quotaRemaining;
        }
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    private final static class StackExchangeResultItem {
        private ArrayList<String> tags;
        private int questionScore;
        private int questionId;
        private boolean isAccepted;
        private boolean hasAcceptedAnswer;
        private String itemType;
        private String lastActivityDate;
        private String body;
        private String title;

        private StackExchangeResultItem() {}

        public final ArrayList<String> getTags() {
            return tags;
        }

        public final int getQuestionScore() {
            return questionScore;
        }

        public final int getQuestionId() {
            return questionId;
        }

        public final boolean isAccepted() {
            return isAccepted;
        }

        public final boolean isHasAcceptedAnswer() {
            return hasAcceptedAnswer;
        }

        public final String getItemType() {
            return itemType;
        }

        public final String getLastActivityDate() {
            return lastActivityDate;
        }

        public final String getBody() {
            return body;
        }

        public final String getTitle() {
            return title;
        }
    }
}
