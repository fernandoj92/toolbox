package mt.ferjorosa.models.ltvariables;

import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;
import mt.ferjorosa.core.models.ltvariables.ObservedVariable;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Fer on 13/04/2016.
 */
public class ObservedVariableTest {

    @Test
    public void testConstructor(){

        Variables variables = new Variables();
        try{
            Variable gaussian = variables.newGaussianVariable("test");
            ObservedVariable observedVariable = new ObservedVariable(gaussian);
            Assert.fail("Should throw an IllegalArgumentException, this variableisn't multinomial");
        }catch (IllegalArgumentException e) {
            org.junit.Assert.assertTrue(true);
        } catch (Exception e){
            org.junit.Assert.assertTrue(false);
        }
    }
}
