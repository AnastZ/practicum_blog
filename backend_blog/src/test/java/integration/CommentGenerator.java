package integration;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public interface CommentGenerator {
    static Stream<Arguments> existingPostAndComments() {
        return Stream.of(
                Arguments.of(1L, 2L),
                Arguments.of(2L, 3L),
                Arguments.of(3L, 1L)
        );
    }
    static Stream<Arguments> notCorrespondingPostAndComment() {
        return Stream.of(
                Arguments.of(1L, 3L),
                Arguments.of(2L, 1L),
                Arguments.of(3L, 2L)
        );
    }
}
