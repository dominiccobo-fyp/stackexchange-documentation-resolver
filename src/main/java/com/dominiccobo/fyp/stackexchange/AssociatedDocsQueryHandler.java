package com.dominiccobo.fyp.stackexchange;

import com.dominiccobo.fyp.context.api.queries.AssociatedDocumentationQuery;
import com.dominiccobo.fyp.context.listeners.DocumentationQueryListener;
import com.dominiccobo.fyp.context.models.Documentation;
import com.dominiccobo.fyp.context.models.DocumentationTopic;
import com.dominiccobo.fyp.context.models.DocumentationType;
import com.dominiccobo.fyp.stackexchange.provider.Question;
import com.dominiccobo.fyp.stackexchange.provider.SearchObject;
import com.dominiccobo.fyp.stackexchange.provider.StackExchangeAPI;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssociatedDocsQueryHandler implements DocumentationQueryListener {

    private final StackExchangeAPI api;

    @Autowired
    public AssociatedDocsQueryHandler(StackExchangeAPI api) {
        this.api = api;
    }

    @QueryHandler
    @Override
    public List<Documentation> on(AssociatedDocumentationQuery query) {

        if(!query.getDocumentationTypes().contains(DocumentationType.QA)) {
            return new ArrayList<>();
        }

        return api.search(getSearchObjectFromQuery(query))
                .map(this::mapToWorkItem)
                .collect(Collectors.toList());
    }

    private SearchObject getSearchObjectFromQuery(AssociatedDocumentationQuery associatedDocumentationQuery) {
        return new SearchObject(associatedDocumentationQuery.getSearchTerm(), associatedDocumentationQuery.getPagination());
    }

    private Documentation mapToWorkItem(Question question) {
        Documentation.Builder builder = new Documentation.Builder()
                .setLastActivity(question.getLastActivity())
                .setTitle(question.getQuestionTitle())
                .setLink(question.getQuestionLink())
                .setType(DocumentationType.QA)
                .setContent(question.getAcceptedAnswer());

        for(String tag: question.getTags()) {
            builder.withTopic(new DocumentationTopic(tag));
        }

        return builder.build();
    }
}
