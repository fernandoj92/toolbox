package mt.ferjorosa.core.util.pair;

/**
 * Generic Pair implementation
 *
 * This class has been created because I have found issues with comparing using the Apache commons implementations
 * when dealing with Pairs of Attribute
 * @param <A>
 * @param <B>
 */
public class PairImplementation<A, B> {

    /** */
    private A first;

    /** */
    private B second;

    /**
     *
     * @param first
     * @param second
     */
    public PairImplementation(A first, B second) {
        super();
        this.first = first;
        this.second = second;
    }

    /**
     *
     * @return
     */
    public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;

        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    /**
     *
     * @param other
     * @return
     */
    public boolean equals(Object other) {
        if (other instanceof PairImplementation) {
            PairImplementation otherPair = (PairImplementation) other;
            return
                    ((  this.first == otherPair.first ||
                            ( this.first != null && otherPair.first != null &&
                                    this.first.equals(otherPair.first))) &&
                            (	this.second == otherPair.second ||
                                    ( this.second != null && otherPair.second != null &&
                                            this.second.equals(otherPair.second))) );
        }

        return false;
    }

    /**
     *
     * @return
     */
    public String toString()
    {
        return "(" + first + ", " + second + ")";
    }

    /**
     *
     * @return
     */
    public A getFirst() {
        return first;
    }

    /**
     *
     * @return
     */
    public B getSecond() {
        return second;
    }

}
