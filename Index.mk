# Scope

This toolbox offers a collection of scalable and parallel algorithms for inference and learning of hybrid Bayesian 
networks from streaming data. For example, AMIDST provides parallel multi-core implementations of Bayesian parameter 
learning, using streaming variational Bayes and variational message passing. Additionally, AMIDST efficiently leverages 
existing functionalities and algorithms by interfacing to existing software tools such as [Hugin](http://www.hugin.com) 
and [MOA](http://moa.cms.waikato.ac.nz). AMIDST is an open source toolbox written in Java and is available under the 
Apache Software License 2.0.

In the next figure we show a taxonomy of relevant data mining tools dealing with PGMs and data streams. To the best of our knowledge, there is no other software for mining data streams based on PGMs, most of the existing softwares based on PGMs are only focused on mining stationary data sets. Hence, the main goal of AMIDST is to fill this gap and produce a significant contribution within the areas of PGMs and mining streaming data.

<p align="center">
<img title="Taxonomy" src="https://amidst.github.io/toolbox/docs/taxonomy.pdf" width="500">
</p>

# Scalability

Scalability is a main concern for the AMIDST toolbox. We exploit Java 8 functional 
programming style to provide parallel implementations of most of our algorithms. If more computation capacity 
is needed to process data streams, AMIDST users can also use more CPU cores. As an example, the following 
figure shows how the data processing capacity of our toolbox increases with the number of cores when learning 
a hybrid BN model with latent variables using the AMIDST's learning engine. More precisely, we learn a PGM model 
with multinomial (blue nodes) and Gaussian (green nodes) variables, some of them are latent, non observable, 
variables (dashed nodes). As can be seen, using our variational learning engine AMIDST toolbox is able to 
process data in the order of gigabytes per hour depending on the number of available cores with large and 
complex PGMs with latent variables(*).


<p align="center">
<img src="https://amidst.github.io/toolbox/docs/scalability.pdf" width="800">
</p>


# Documentation<a name="documentation"></a>

Click in some of following links for further information:

* [Getting Started](http://amidst.github.io/toolbox/GettingStarted.html) describes how to install the toolbox, how this toolbox integrates with Java 8 new functional-style programming features, and why it is based on a module based architecture.

* [Toolbox Functionalities](http://amidst.github.io/toolbox/ToolboxFunctionalities.html) describes which are the main functionalities (i.e. pgms, learning and inference algorithms, etc) included in the current version of the toolbox.

* [Contributing to AMIDST](http://amidst.github.io/toolbox/ContributingToAMIDST.html) describes the steps needed to contribute to this toolbox. This toolbox is intended to be a highly collaborative project.

* [Code Examples](http://amidst.github.io/toolbox/CodeExamples.html) provides a long list of code examples covering most the of the functionalities of the toolbox.

* [API Java Doc](http://amidst.github.io/toolbox/javadoc/index.html) of the toolbox. 


## Citing AMIDST Toolbox

Include a reference to the following paper:

> AMIDST: Analysis of MassIve Data STreams using Probabilistic Graphical Models. Submitted to JMLR. 2015. 

## AMIDST's Papers & Use-Cases

In the following repository it is hosted the software code of some use-cases and scientific papers using AMIDST toolbox. AMIDST users are encouraged to upload their contributions to this repository following the indications given in this [link](http://amidst.github.io/toolbox/ContributingToAMIDST.html).

[https://github.com/amidst/toolbox-usecases](https://github.com/amidst/toolbox-usecases)


## Upcoming Developments

AMIDST Toolbox is an expanding project and upcoming developments include the implementation of [dynamic models](https://en.wikipedia.org/wiki/Dynamic_Bayesian_network) for handling streaming data, the integration of the toolbox in Big Data platforms like [Spark](http://spark.apache.org) and [Flink](http://flink.apache.org) to enlarge its scalability capacities, and a new link to [R](http://static.amidst.eu/upload/dokumenter/Posters/PosterUseR.pdf) to expand the users base.

(*)These experiments were carried out on a Ubuntu Linux server with a x86_64 architecture and 32 cores. The size of the processed data set was measured according to the [Weka](www.cs.waikato.ac.nz/ml/weka/)'s ARFF format.