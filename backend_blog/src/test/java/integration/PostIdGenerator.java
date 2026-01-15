package integration;

import java.util.stream.Stream;

public interface PostIdGenerator {
    static Stream<Long> testExistingPostIds() {
        return Stream.iterate(1L, n -> n + 1L).limit(10);
    }

    static Stream<Long> testNotExistingPostIds() {
        return Stream.iterate(100L, n -> n + 1L).limit(10);
    }
}
