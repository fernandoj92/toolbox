package mt.ferjorosa.core.learning.structural.variables;

import eu.amidst.core.datastream.Attribute;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import mt.ferjorosa.core.util.pair.SymmetricPair;

import java.util.List;

/**
 * This interface defines a measure for Feature Subset selection (i.e Mutual Information). It consists of several methods
 * that allow us to choose the best pair of attributes by maximizing a score. It is focused on bivariate scores.
 *
 * - The methods that start with "get" require first to compute ALL the pairScores of the attributes that are going to be used.
 * - Before computing it is necessary to set the data that is going to be used.
 */
public interface FSSMeasure {
    /**
     * Sets the data instances that are going to be used to calculate the bivariate scores.
     * @param data the data instances.
     */
    void setData(DataOnMemory<DataInstance> data);

    /**
     * Computes the bivariate score between two attributes presented in the data without storing it.
     * @param attributeX first attributes.
     * @param attributeY second attribute.
     * @return the computed score.
     */
    double computeBivariateScore(Attribute attributeX, Attribute attributeY);

    /**
     * Computes each bivariate score of all the attributes in the list.
     * @param attributes the list of attributes.
     */
    void computeAllPairScores(List<Attribute> attributes);

    /**
     * Returns the pair of attributes with the highest bivariate score.
     * @param attributes the considered attributes.
     * @return the pair of attributes with the highest bivariate score.
     */
    SymmetricPair<Attribute, Attribute> getBestPair(List<Attribute> attributes);

    /**
     * Returns the attribute of the "outSet" that has the highest bivariate score with any of the attributes of the
     * "activeSet".
     * @param activeSet the first list of attributes.
     * @param outSet the second list of attributes, from which the an attribute is going to be chosen.
     * @return the chosen attribute, which has the highest bivariate score with any of the attributes of the "activeSet".
     */
    Attribute getClosestAttributeToSet(List<Attribute> activeSet, List<Attribute> outSet);

    /**
     * Returns the highest bivariate score between all the pair of attributes of the first and second set. The pair
     * combinations are done with one attribute of the first list and another one of the second list.
     * @param firstSet the first list of attributes.
     * @param secondSet the second list of attributes.
     * @return the previously computed score.
     */
    double getMaxBivariateScore(List<Attribute> firstSet, List<Attribute> secondSet);
}
