## Dynamic Bayesian networks: code Examples<a name="documentation"></a>

   * [Dynamic Data Streams](#dynamicdatastreamsexample)
   * [Dynamic Random Variables](#dynamicvariablesexample)
   * [Dynamic Bayesian Networks](#dynamicbnexample)
       * [Creating Bayesian Networks](#dynamicbnnohiddenexample)
       * [Creating Bayesian Networks with latent variables](#dynamicbnhiddenexample)
       * [Modifying Bayesian Networks](#dynamicbnmodifyexample)
   * [Dynamic I/O Functionality](#dynamicioexample)
       * [I/O of Data Streams](#dynamiciodatastreamsexample)
       * [I/O of Bayesian Networks](#dynamiciobnsexample)
   * [Dynamic Inference Algorithms](#dynamicinferenceexample)
       * [The Inference Engine](#dynamicinferenceengingeexample)
       * [Variational Message Passing](#dynamicvmpexample)
       * [Importance Sampling](#dynamicisexample)
   * [Dynamic Learning Algorithms](#dynamiclearningexample)
       * [Maximum Likelihood](#dynamicmlexample)
       * [Parallel Maximum Likelihood](#dynamicpmlexample)
       * [Streaming Variational Bayes](#dynamicsvbexample)
       * [Parallel Streaming Variational Bayes](#dynamicpsvbexample)


## Dynamic Data Streams<a name="dynamicdatastreamsexample"></a>
  
In this example we show how to use the main features of a *DataStream* object. More precisely,  we show  how to load a dynamic data stream and how to iterate over the *DynamicDataInstance* objects.

```java
//Open the data stream using the class DynamicDataStreamLoader
DataStream<DynamicDataInstance> data = DynamicDataStreamLoader.loadFromFile("datasets/dynamicNB-samples.arff");

//Access the attributes defining the data stream
System.out.println("Attributes defining the data set");
for (Attribute attribute : data.getAttributes()) {
    System.out.println(attribute.getName());
}
Attribute classVar = data.getAttributes().getAttributeByName("ClassVar");

//Iterate over dynamic data instances
System.out.println("1. Iterating over samples using a for loop");
for (DynamicDataInstance dataInstance : data) {
    System.out.println("SequenceID = "+dataInstance.getSequenceID()+", TimeID = "+dataInstance.getTimeID());
    System.out.println("The value of attribute A for the current data instance is: " +
    	dataInstance.getValue(classVar));
}
```

[[Back to Top]](#documentation)

## Dynamic Random Variables<a name="dynamicvariablesexample"></a>

This example show the basic functionalities related to dynamic variables.

```java
//Create an empty DynamicVariables object
DynamicVariables variables = new DynamicVariables();

//Invoke the "new" methods of the object DynamicVariables to create new dynamic variables.

//Create a Gaussian dynamic variables
Variable gaussianVar = variables.newGaussianDynamicVariable("GaussianVar");

//Create a Multinomial dynamic variable with two states
Variable multinomialVar = variables.newMultinomialDynamicVariable("MultinomialVar", 2);

//Create a Multinomial dynamic variable with two states: TRUE and FALSE
Variable multinomialVar2 = variables.newMultinomialDynamicVariable("MultinomialVar2", Arrays.asList("TRUE, FALSE"));

//All dynamic Variables have an interface variable
Variable gaussianVarInt = gaussianVar.getInterfaceVariable();
Variable multinomialVarInt = multinomialVar.getInterfaceVariable();

//Get the "main" Variable associated with each interface variable through the DynamicVariable object
Variable mainMultinomialVar = variables.getVariableFromInterface(multinomialVarInt);

//Check whether a variable is an interface variable
System.out.println("Is Variable "+gaussianVar.getName()+" an interface variable? "
                +gaussianVar.isInterfaceVariable());
System.out.println("Is Variable "+gaussianVarInt.getName()+" an interface variable? "
                +gaussianVarInt.isInterfaceVariable());

//Check whether a variable is a dynamic variable
System.out.println("Is Variable "+multinomialVar.getName()+" a dynamic variable? "
                +gaussianVar.isDynamicVariable());
```


[[Back to Top]](#documentation)


## Dynamic Bayesian Networks<a name="dynamicbnexample"></a>

### Creating dynamic Bayesian networks<a name="bnnohiddenexample"></a>

This example creates a dynamic BN, from a dynamic data stream, with randomly generated probability distributions, then saves it to a file.

```java
//Open the data stream using the static class DynamicDataStreamLoader
DataStream<DynamicDataInstance> data = DynamicDataStreamLoader.loadFromFile(
                "datasets/syntheticDataDiscrete.arff");

/**
* 1. Once the data is loaded, we create a random variable for each of the attributes (i.e. data columns)
* in our data.
*
* 2. {@link DynamicVariables} is the class for doing that. It takes a list of Attributes and internally creates
* all the variables. We create the variables using DynamicVariables class to guarantee that each variable
* has a different ID number and make it transparent for the user. Each random variable has an associated
* interface variable.
*
* 3. We can extract the Variable objects by using the method getVariableByName();
*/
DynamicVariables dynamicVariables = new DynamicVariables(data.getAttributes());
DynamicDAG dynamicDAG = new DynamicDAG(dynamicVariables);

Variable A = dynamicVariables.getVariableByName("A");
Variable B = dynamicVariables.getVariableByName("B");
Variable C = dynamicVariables.getVariableByName("C");
Variable D = dynamicVariables.getVariableByName("D");
Variable E = dynamicVariables.getVariableByName("E");
Variable G = dynamicVariables.getVariableByName("G");

Variable A_Interface = dynamicVariables.getInterfaceVariable(A);
Variable B_Interface = dynamicVariables.getInterfaceVariable(B);

//Note that C_Interface and D_Interface are also created although they will not be used
//(we will not add temporal dependencies)

Variable E_Interface = dynamicVariables.getInterfaceVariable(E);
Variable G_Interface = dynamicVariables.getInterfaceVariable(G);

// Example of the dynamic DAG structure
// Time 0: Parents at time 0 are automatically created when adding parents at time T
dynamicDAG.getParentSetTimeT(B).addParent(A);
dynamicDAG.getParentSetTimeT(C).addParent(A);
dynamicDAG.getParentSetTimeT(D).addParent(A);
dynamicDAG.getParentSetTimeT(E).addParent(A);
dynamicDAG.getParentSetTimeT(G).addParent(A);
dynamicDAG.getParentSetTimeT(A).addParent(A_Interface);
dynamicDAG.getParentSetTimeT(B).addParent(B_Interface);
dynamicDAG.getParentSetTimeT(E).addParent(E_Interface);
dynamicDAG.getParentSetTimeT(G).addParent(G_Interface);

System.out.println(dynamicDAG.toString());

/**
* 1. We now create the Dynamic Bayesian network from the previous Dynamic DAG.
*
* 2. The DBN object is created from the DynamicDAG. It automatically looks at the distribution type
* of each variable and their parents to initialize the Distributions objects that are stored
* inside (i.e. Multinomial, Normal, CLG, etc). The parameters defining these distributions are
* properly initialized.
*
* 3. The network is printed and we can have a look at the kind of distributions stored in the DBN object.
*/
DynamicBayesianNetwork dbn = new DynamicBayesianNetwork(dynamicDAG);
System.out.printf(dbn.toString());

/**
* Finally teh Bayesian network is saved to a file.
*/
DynamicBayesianNetworkWriter.saveToFile(dbn, "networks/DBNExample.bn");
```

[[Back to Top]](#documentation)


### Creating Dynamic Bayesian Networks with Latent Variables <a name="dynamicbnhiddenexample"></a>

In this example, we simply show how to create a BN model with hidden variables. We simply create a BN for clustering, i.e.,  a naive-Bayes like structure with a single common hidden variable acting as parant of all the observable variables.
 
```java
//We can open the data stream using the static class DataStreamLoader
DataStream<DataInstance> data = DataStreamLoader.openFromFile("datasets/syntheticData.arff");

/**
 * 1. Once the data is loaded, we create a random variable for each of the attributes (i.e. data columns)
 * in our data.
 *
 * 2. {@link Variables} is the class for doing that. It takes a list of Attributes and internally creates
 * all the variables. We create the variables using Variables class to guarantee that each variable
 * has a different ID number and make it transparent for the user.
 *
 * 3. We can extract the Variable objects by using the method getVariableByName();
 */
Variables variables = new Variables(data.getAttributes());

Variable a = variables.getVariableByName("A");
Variable b = variables.getVariableByName("B");
Variable c = variables.getVariableByName("C");
Variable d = variables.getVariableByName("D");
Variable e = variables.getVariableByName("E");
Variable g = variables.getVariableByName("G");
Variable h = variables.getVariableByName("H");
Variable i = variables.getVariableByName("I");

/**
 * 1. We create the hidden variable. For doing that we make use of the method "newMultionomialVariable". When
 * a variable is created from an Attribute object, it contains all the information we need (e.g.
 * the name, the type, etc). But hidden variables does not have an associated attribute
 * and, for this reason, we use now this to provide this information.
 *
 * 2. Using the "newMultionomialVariable" method, we define a variable called HiddenVar, which is
 * not associated to any attribute and, then, it is a latent variable, its state space is a finite set with two elements, and its
 * distribution type is multinomial.
 *
 * 3. We finally create the hidden variable using the method "newVariable".
 */

Variable hidden = variables.newMultionomialVariable("HiddenVar", Arrays.asList("TRUE", "FALSE"));

/**
 * 1. Once we have defined your {@link Variables} object, including the latent variable,
 * the next step is to create a DAG structure over this set of variables.
 *
 * 2. To add parents to each variable, we first recover the ParentSet object by the method
 * getParentSet(Variable var) and then call the method addParent(Variable var).
 *
 * 3. We just put the hidden variable as parent of all the other variables. Following a naive-Bayes
 * like structure.
 */
DAG dag = new DAG(variables);

dag.getParentSet(a).addParent(hidden);
dag.getParentSet(b).addParent(hidden);
dag.getParentSet(c).addParent(hidden);
dag.getParentSet(d).addParent(hidden);
dag.getParentSet(e).addParent(hidden);
dag.getParentSet(g).addParent(hidden);
dag.getParentSet(h).addParent(hidden);
dag.getParentSet(i).addParent(hidden);

/**
 * We print the graph to see if is properly created.
 */
System.out.println(dag.toString());

/**
 * 1. We now create the Bayesian network from the previous DAG.
 *
 * 2. The BN object is created from the DAG. It automatically looks at the distribution type
 * of each variable and their parents to initialize the Distributions objects that are stored
 * inside (i.e. Multinomial, Normal, CLG, etc). The parameters defining these distributions are
 * properly initialized.
 *
 * 3. The network is printed and we can have look at the kind of distributions stored in the BN object.
 */
BayesianNetwork bn = new BayesianNetwork(dag);
System.out.println(bn.toString());

/**
 * Finally teh Bayesian network is saved to a file.
 */
BayesianNetworkWriter.saveToFile(bn, "networks/BNHiddenExample.bn");
```

[[Back to Top]](#documentation)


### Modifying Dynamic Bayesian Networks <a name="dynamicbnmodifyexample"></a>

In this example we show how to access and modify the conditional probabilities of a Bayesian network model.

```java
//We first generate a Bayesian network with one multinomial, one Gaussian variable and one link.
BayesianNetworkGenerator.setNumberOfGaussianVars(1);
BayesianNetworkGenerator.setNumberOfMultinomialVars(1,2);
BayesianNetworkGenerator.setNumberOfLinks(1);

BayesianNetwork bn = BayesianNetworkGenerator.generateBayesianNetwork();

//We print the randomly generated Bayesian networks
System.out.println(bn.toString());

//We first access the variable we are interested in
Variable multiVar = bn.getStaticVariables().getVariableByName("DiscreteVar0");

//Using the above variable we can get the associated distribution and modify it
Multinomial multinomial = bn.getConditionalDistribution(multiVar);
multinomial.setProbabilities(new double[]{0.2, 0.8});

//Same than before but accessing the another variable
Variable normalVar = bn.getStaticVariables().getVariableByName("GaussianVar0");

//In this case, the conditional distribtuion is of the type "Normal given Multinomial Parents"
Normal_MultinomialParents normalMultiDist = bn.getConditionalDistribution(normalVar);
normalMultiDist.getNormal(0).setMean(1.0);
normalMultiDist.getNormal(0).setVariance(1.0);

normalMultiDist.getNormal(1).setMean(0.0);
normalMultiDist.getNormal(1).setVariance(1.0);

//We print modified Bayesian network
System.out.println(bn.toString());
```

[[Back to Top]](#documentation)

## I/O Functionality <a name="dynamicioexample"></a>

### I/O of Data Streams <a name="dynamiciodatastreamsexample"></a>

In this example we show how to load and save data sets from [.arff](http://www.cs.waikato.ac.nz/ml/weka/arff.html) files. 

```java
//We can open the data stream using the static class DataStreamLoader
DataStream<DataInstance> data = DataStreamLoader.openFromFile("datasets/syntheticData.arff");

//We can save this data set to a new file using the static class DataStreamWriter
DataStreamWriter.writeDataToFile(data, "datasets/tmp.arff");
```

[[Back to Top]](#documentation)

### Dynamic I/O of Bayesian Networks <a name="dynamiciobnsexample"></a>


In this example we show how to load and save Bayesian networks models for a binary file with ".bn" extension. In this toolbox Bayesian networks models are saved as serialized objects.

```java
//We can load a Bayesian network using the static class BayesianNetworkLoader
BayesianNetwork bn = BayesianNetworkLoader.loadFromFile("./networks/WasteIncinerator.bn");

//Now we print the loaded model
System.out.println(bn.toString());

//Now we change the parameters of the model
bn.randomInitialization(new Random(0));

//We can save this Bayesian network to using the static class BayesianNetworkWriter
BayesianNetworkWriter.saveToFile(bn, "networks/tmp.bn");
```

[[Back to Top]](#documentation)

## Inference Algorithms <a name="dynamicinferenceexample"></a>

### The Inference Engine <a name="dynamicinferenceengingeexample"></a>

This example show how to perform inference in a Bayesian network model using the InferenceEngine static class. This class aims to be a straigthfoward way to perform queries over a Bayesian network model. By the default the \textit{VMP} inference method is invoked.

```java
//We first load the WasteIncinerator bayesian network which has multinomial 
//and Gaussian variables.
BayesianNetwork bn = BayesianNetworkLoader.loadFromFile("./networks/WasteIncinerator.bn");

//We recover the relevant variables for this example: Mout which is normally 
//distributed, and W which is multinomial.
Variable varMout = bn.getStaticVariables().getVariableByName("Mout");
Variable varW = bn.getStaticVariables().getVariableByName("W");

//Set the evidence.
Assignment assignment = new HashMapAssignment(1);
assignment.setValue(varW,0);

//Then we query the posterior of
System.out.println("P(Mout|W=0) = " + InferenceEngine.getPosterior(varMout, bn, assignment));

//Or some more refined queries
System.out.println("P(0.7<Mout<6.59 | W=0) = " + 
 InferenceEngine.getExpectedValue(varMout, bn, v -> (0.7 < v && v < 6.59) ? 1.0 : 0.0 ));
```

[[Back to Top]](#documentation)

### Variational Message Passing <a name="dynamicvmpexample"></a>

This example we show how to perform inference on a general Bayesian network using the Variational Message Passing (VMP)
algorithm detailed in

> Winn, J. M., Bishop, C. M. (2005). Variational message passing. In Journal of Machine Learning Research (pp. 661-694).



```java
//We first load the WasteIncinerator bayesian network which has multinomial 
//and Gaussian variables.
BayesianNetwork bn = BayesianNetworkLoader.loadFromFile("./networks/WasteIncinerator.bn");

//We recover the relevant variables for this example: Mout which is normally 
//distributed, and W which is multinomial.
Variable varMout = bn.getStaticVariables().getVariableByName("Mout");
Variable varW = bn.getStaticVariables().getVariableByName("W");

//First we create an instance of a inference algorithm. In this case, we use 
//the VMP class.
InferenceAlgorithm inferenceAlgorithm = new VMP();

//Then, we set the BN model
inferenceAlgorithm.setModel(bn);

//If exists, we also set the evidence.
Assignment assignment = new HashMapAssignment(1);
assignment.setValue(varW,0);
inferenceAlgorithm.setEvidence(assignment);

//Then we run inference
inferenceAlgorithm.runInference();

//Then we query the posterior of
System.out.println("P(Mout|W=0) = " + inferenceAlgorithm.getPosterior(varMout));

//Or some more refined queries
System.out.println("P(0.7<Mout<6.59 | W=0) = " + 
 inferenceAlgorithm.getExpectedValue(varMout, v -> (0.7 < v && v < 6.59) ? 1.0 : 0.0 ));

//We can also compute the probability of the evidence
System.out.println("P(W=0) = " + Math.exp(inferenceAlgorithm.getLogProbabilityOfEvidence()));
```

[[Back to Top]](#documentation)

### Importance Sampling <a name="isexample"></a>

This example we show how to perform inference on a general Bayesian network using an importance sampling
algorithm detailed in

>Fung, R., Chang, K. C. (2013). Weighing and integrating evidence for stochastic simulation in Bayesian networks. arXiv preprint arXiv:1304.1504.

```java
//We first load the WasteIncinerator bayesian network which has multinomial 
//and Gaussian variables.
BayesianNetwork bn = BayesianNetworkLoader.loadFromFile("./networks/WasteIncinerator.bn");

//We recover the relevant variables for this example: Mout which is normally 
//distributed, and W which is multinomial.
Variable varMout = bn.getStaticVariables().getVariableByName("Mout");
Variable varW = bn.getStaticVariables().getVariableByName("W");

//First we create an instance of a inference algorithm. In this case, we use 
//the ImportanceSampling class.
InferenceAlgorithm inferenceAlgorithm = new ImportanceSampling();

//Then, we set the BN model
inferenceAlgorithm.setModel(bn);

//If exists, we also set the evidence.
Assignment assignment = new HashMapAssignment(1);
assignment.setValue(varW,0);
inferenceAlgorithm.setEvidence(assignment);

//We can also set to be run in parallel on multicore CPUs
inferenceAlgorithm.setParallelMode(true);

//Then we run inference
inferenceAlgorithm.runInference();

//Then we query the posterior of
System.out.println("P(Mout|W=0) = " + inferenceAlgorithm.getPosterior(varMout));

//Or some more refined queries
System.out.println("P(0.7<Mout<6.59 | W=0) = " + 
 inferenceAlgorithm.getExpectedValue(varMout, v -> (0.7 < v && v < 6.59) ? 1.0 : 0.0 ));

//We can also compute the probability of the evidence
System.out.println("P(W=0) = " + Math.exp(inferenceAlgorithm.getLogProbabilityOfEvidence()));
```

[[Back to Top]](#documentation)

## Dynamic Learning Algorithms <a name="dynamiclearningexample"></a>
### Maximum Likelihood <a name="mlexample"></a>


This other example shows how to learn incrementally the parameters of a Bayesian network using data batches,

```java
//We can open the data stream using the static class DataStreamLoader
DataStream<DataInstance> data = 
                  DataStreamLoader.openFromFile("datasets/WasteIncineratorSample.arff");

//We create a ParameterLearningAlgorithm object with the MaximumLikehood builder
ParameterLearningAlgorithm parameterLearningAlgorithm = new ParallelMaximumLikelihood();

//We fix the DAG structure
parameterLearningAlgorithm.setDAG(getNaiveBayesStructure(data,0));

//We should invoke this method before processing any data
parameterLearningAlgorithm.initLearning();


//Then we show how we can perform parameter learnig by a sequential updating of data batches.
for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(100)){
    parameterLearningAlgorithm.updateModel(batch);
}

//And we get the model
BayesianNetwork bnModel = parameterLearningAlgorithm.getLearntBayesianNetwork();

//We print the model
System.out.println(bnModel.toString());
```

[[Back to Top]](#documentation)

### Parallel Maximum Likelihood <a name="dynamicpmlexample"></a>

This example shows how to learn in parallel the parameters of a Bayesian network from a stream of data using maximum likelihood.

```java
//We can open the data stream using the static class DataStreamLoader
DataStream<DataInstance> data = 
           DataStreamLoader.openFromFile("datasets/syntheticData.arff");

//We create a MaximumLikelihood object with the MaximumLikehood builder
MaximumLikelihood parameterLearningAlgorithm = new MaximumLikelihood();

//We activate the parallel mode.
parameterLearningAlgorithm.setParallelMode(true);

//We fix the DAG structure
parameterLearningAlgorithm.setDAG(getNaiveBayesStructure(data,0));

//We set the batch size which will be employed to learn the model in parallel
parameterLearningAlgorithm.setBatchSize(100);

//We set the data which is going to be used for leaning the parameters
parameterLearningAlgorithm.setDataStream(data);

//We perform the learning
parameterLearningAlgorithm.runLearning();

//And we get the model
BayesianNetwork bnModel = parameterLearningAlgorithm.getLearntBayesianNetwork();

//We print the model
System.out.println(bnModel.toString());
```


[[Back to Top]](#documentation)

### Streaming Variational Bayes <a name="dynamicsvbexample"></a>

This example shows how to learn incrementally the parameters of a Bayesian network from a stream of data with a Bayesian approach using the following algorithm,

>Broderick, T., Boyd, N., Wibisono, A., Wilson, A. C., \& Jordan, M. I. (2013). Streaming variational Bayes. 
In Advances in Neural Information Processing Systems (pp. 1727-1735).

In this second example we show a alternative implementation which explicitly updates the model by batches by using the class *SVB*.


```java
//We can open the data stream using the static class DataStreamLoader
DataStream<DataInstance> data = 
                      DataStreamLoader.openFromFile("datasets/WasteIncineratorSample.arff");

//We create a StreamingVariationalBayesVMP object
StreamingVariationalBayesVMP parameterLearningAlgorithm = new StreamingVariationalBayesVMP();

//We fix the DAG structure, which is a Naive Bayes with a 
//global latent binary variable
parameterLearningAlgorithm.setDAG(getHiddenNaiveBayesStructure(data));

//We fix the size of the window, which must be equal to the size of the data batches 
//we use for learning
parameterLearningAlgorithm.setWindowsSize(5);

//We can activate the output
parameterLearningAlgorithm.setOutput(true);

//We should invoke this method before processing any data
parameterLearningAlgorithm.initLearning();

//Then we show how we can perform parameter learnig by a sequential updating of 
//data batches.
for (DataOnMemory<DataInstance> batch : data.iterableOverBatches(5)){
    parameterLearningAlgorithm.updateModel(batch);
}

//And we get the model
BayesianNetwork bnModel = parameterLearningAlgorithm.getLearntBayesianNetwork();

//We print the model
System.out.println(bnModel.toString());
```

[[Back to Top]](#documentation)

### Parallel Streaming Variational Bayes <a name="dynamicpsvbexample"></a>

This example shows how to learn in the parameters of a Bayesian network from a stream of data with a Bayesian
approach using the parallel version  of the SVB algorithm, 

>Broderick, T., Boyd, N., Wibisono, A., Wilson, A. C., \& Jordan, M. I. (2013). Streaming variational Bayes. 
In Advances in Neural Information Processing Systems (pp. 1727-1735).

```java
//We can open the data stream using the static class DataStreamLoader
DataStream<DataInstance> data = 
                   DataStreamLoader.openFromFile("datasets/WasteIncineratorSample.arff");

//We create a ParallelSVB object
ParallelSVB parameterLearningAlgorithm = new ParallelSVB();

//We fix the number of cores we want to exploit
parameterLearningAlgorithm.setNCores(4);

//We fix the DAG structure, which is a Naive Bayes with a 
//global latent binary variable
parameterLearningAlgorithm.setDAG(StreamingVMPExample.getHiddenNaiveBayesStructure(data));


//We fix the size of the window
parameterLearningAlgorithm.getSVBEngine().setWindowsSize(100);

//We can activate the output
parameterLearningAlgorithm.setOutput(true);

//We set the data which is going to be used for leaning the parameters
parameterLearningAlgorithm.setDataStream(data);

//We perform the learning
parameterLearningAlgorithm.runLearning();

//And we get the model
BayesianNetwork bnModel = parameterLearningAlgorithm.getLearntBayesianNetwork();

//We print the model
System.out.println(bnModel.toString());
```

[[Back to Top]](#documentation)