package mt.ferjorosa.core.util.stream;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility class designed to work with Streams.
 */
public class StreamUtil {
    /**
     * Generates a stream from an iterable object.
     * @param iterable the Iterable object input.
     * @param <T> Generic type.
     * @return a stream equivalent of the iterable object.
     */
    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        iterable.iterator(),
                        Spliterator.ORDERED
                ),
                false
        );
    }
}
