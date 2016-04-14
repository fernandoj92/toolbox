package mt.ferjorosa.models.ltvariables;

import eu.amidst.core.variables.Variable;
import eu.amidst.core.variables.Variables;
import mt.ferjorosa.core.models.ltvariables.LatentVariable;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Fer on 13/04/2016.
 */
public class LatentVariableTest {

    @Test
    public void testConstructor(){

        Variables variables = new Variables();
        try{
            Variable gaussian = variables.newGaussianVariable("test");
            LatentVariable latentVariable = new LatentVariable(gaussian, 0);
            Assert.fail("Should throw an IllegalArgumentException, this variable isn't multinomial");
        }catch (IllegalArgumentException e) {
            org.junit.Assert.assertTrue(true);
        } catch (Exception e){
            org.junit.Assert.assertTrue(false);
        }
    }
}
