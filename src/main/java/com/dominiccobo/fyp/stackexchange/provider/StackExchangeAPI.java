package com.dominiccobo.fyp.stackexchange.provider;

import java.util.stream.Stream;

public interface StackExchangeAPI {
    Stream<Question> search(SearchObject search);
}
