package mt.ferjorosa.core.util.pair;

/**
 * Generic Pair implementation
 *
 * This class has been created because I have found issues with the equals() method implemented in the Apache commons
 * library when dealing with Pairs of {@link eu.amidst.core.datastream.Attribute}.
 *
 * @param <A> the type of the first element.
 * @param <B> the type of the second element.
 */
public class SymmetricPair<A, B> {

    /** First element of the pair. */
    private A first;

    /** Second element of the pair. */
    private B second;

    /**
     * Creates a new instance
     * @param first
     * @param second
     */
    public SymmetricPair(A first, B second) {
        super();
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the hashCode of this Pair of objects.
     * @return the hashCode of this Pair of objects.
     */
    public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;

        return (hashFirst + hashSecond) * 7;
    }

    /**
     * Tests whether two symmetric Pairs are equal or not.
     * Two symmetric pairs are equal if the elements pf the pair in the same position have the same value
     * or if the diagonal values are the same.
     *
     * @param other a SymmetricPair object to be compared with this one.
     * @return true if the two SymmetricPairs are equals, false otherwise.
     */
    public boolean equals(Object other) {

        if(other == null)
            return false;

        if (other instanceof SymmetricPair) {
            SymmetricPair otherPair = (SymmetricPair) other;

            if((this.first == otherPair.first ||  this.first.equals(otherPair.first)) &&
                    ((this.second == otherPair.second ||  this.second.equals(otherPair.second))))
                return true;

            if((this.first == otherPair.second ||  this.first.equals(otherPair.second)) &&
                    ((this.second == otherPair.first ||  this.second.equals(otherPair.first))))
                return true;
        }

        return false;
    }

    /**
     * Returns a string equivalent of the Pair of objects.
     * @return a string equivalent of the Pair of objects.
     */
    public String toString()
    {
        return "(" + first + ", " + second + ")";
    }

    /**
     * Returns the first element of the pair.
     * @return the first element of the pair.
     */
    public A getFirst() {
        return first;
    }

    /**
     * Returns the second element of the pair.
     * @return the second element of the pair.
     */
    public B getSecond() {
        return second;
    }

}
