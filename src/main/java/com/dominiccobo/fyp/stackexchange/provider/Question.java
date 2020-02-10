package com.dominiccobo.fyp.stackexchange.provider;

import com.dominiccobo.fyp.context.models.DocumentationType;

import java.util.List;

public interface Question {

    String getQuestionTitle();

    String getAcceptedAnswer();

    String getQuestionLink();

    default DocumentationType getQuestionType() {
        return DocumentationType.QA;
    }

    String getLastActivity();

    List<String> getTags();
}
