package com.dominiccobo.fyp.stackexchange;

import com.dominiccobo.fyp.context.api.queries.AssociatedDocumentationQuery;
import com.dominiccobo.fyp.context.models.Documentation;
import com.dominiccobo.fyp.context.models.DocumentationTopic;
import com.dominiccobo.fyp.context.models.DocumentationType;
import com.dominiccobo.fyp.stackexchange.provider.Question;
import com.dominiccobo.fyp.stackexchange.provider.StackExchangeAPI;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;

public class AssociatedDocsQueryHandlerTest {

    public static final String TITLE_1 = "title";
    public static final String ANSWER_1 = "the answer";
    public static final String QUESTION_LINK_1 = "https://example.com/myquestionId";
    public static final String ACTIVITY_1 = "today";
    public static final List<String> TAGS_1 = Arrays.asList(new String[]{"Sometag"});
    private AssociatedDocsQueryHandler fixture;
    @Mock
    private StackExchangeAPI stackExchangeAPI;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        fixture = new AssociatedDocsQueryHandler(stackExchangeAPI);
    }

    @Test
    public void testConversionOfQuestionToDocumentationResultModel() {
        arrangeMockedAPISearchResponse();

        AssociatedDocumentationQuery query = new AssociatedDocumentationQuery.Builder(null, null)
                .forSearchTerm("Search Term")
                .forDocumentationType(DocumentationType.QA)
                .build();

        Documentation expectedContent = new Documentation.Builder().setType(DocumentationType.QA)
                .setTitle(TITLE_1)
                .setLink(QUESTION_LINK_1)
                .setContent(ANSWER_1)
                .withTopic(new DocumentationTopic(TAGS_1.get(0)))
                .setLastActivity(ACTIVITY_1).build();

        List<Documentation> result = fixture.on(query);
        // FIXME: this is a little nasty and needs the API updating to provide a real equals
        Documentation firstResult = result.get(0);
        assertThat(firstResult.getTitle()).matches(expectedContent.getTitle());
        assertThat(firstResult.getLastActivity()).matches(expectedContent.getLastActivity());
        assertThat(firstResult.getLink()).matches(expectedContent.getLink());
        assertThat(firstResult.getType()).isEqualTo(expectedContent.getType());
        assertThat(firstResult.getContent()).matches(expectedContent.getContent());
        assertThat(firstResult.getTopic()).containsExactlyElementsIn(expectedContent.getTopic());
    }

    private void arrangeMockedAPISearchResponse() {
        Question mockQuestion = new MockQuestion(TITLE_1, ANSWER_1, QUESTION_LINK_1, ACTIVITY_1, TAGS_1);
        Stream<Question> stream = Stream.of(mockQuestion);
        Mockito.when(stackExchangeAPI.search(Mockito.any()))
                .thenReturn(stream);
    }

    @Test
    public void testResponseToRequestForNonHandeableDocumentationType() {
        AssociatedDocumentationQuery query = new AssociatedDocumentationQuery.Builder(null, null)
                .forSearchTerm("Search Term")
                .forDocumentationType(DocumentationType.WIKI)
                .build();

        List<Documentation> results = fixture.on(query);
        assertThat(results).isEmpty();
    }

    static class MockQuestion implements Question {

        final String title;
        final String acceptedAnswer;
        final String questionLink;
        final String lastActivity;
        final List<String> tags;

        MockQuestion(String title, String acceptedAnswer, String questionLink, String lastActivity, List<String> tags) {
            this.title = title;
            this.acceptedAnswer = acceptedAnswer;
            this.questionLink = questionLink;
            this.lastActivity = lastActivity;
            this.tags = tags;
        }

        @Override
        public String getQuestionTitle() {
            return this.title;
        }

        @Override
        public String getAcceptedAnswer() {
            return this.acceptedAnswer;
        }

        @Override
        public String getQuestionLink() {
            return this.questionLink;
        }

        @Override
        public String getLastActivity() {
            return this.lastActivity;
        }

        @Override
        public List<String> getTags() {
            return this.tags;
        }
    }
}
