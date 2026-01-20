package integration;

import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public interface TagNamesGenerator {
    static Stream<Arguments> existingTagNames() {
        return Stream.of(
                Arguments.of(List.of("Java", "Hibernate", "Backend")),
                Arguments.of(List.of("Spring")),
                Arguments.of(List.of("Database", "SQL", "Hibernate", "Backend"))
        );
    }
    static Stream<Arguments> notExistingTagNames() {
        return Stream.of(
                Arguments.of(List.of("Data", "QL")),
                Arguments.of(List.of("va", "nate")),
                Arguments.of(List.of("Sng", "Band"))
        );
    }
    static Stream<Arguments> tagNamesMix() throws Exception {
        return Stream.of(
                Arguments.of(List.of("Data","Hibernate", "Spring", "QL")),
                Arguments.of(List.of("va","Database", "Spring","SQL", "nate")),
                Arguments.of(List.of("Sng","Spring", "Band"))
        );
    }
}
