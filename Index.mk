# Scope

This toolbox offers a collection of scalable and parallel algorithms for inference and learning of hybrid Bayesian 
networks (BNs) from streaming data. For example, AMIDST provides parallel multi-core implementations of Bayesian parameter 
learning, using streaming variational Bayes and variational message passing. Additionally, AMIDST efficiently leverages 
existing functionalities and algorithms by interfacing to existing software tools such as [Hugin](http://www.hugin.com) 
and [MOA](http://moa.cms.waikato.ac.nz). AMIDST is an open source Java toolbox released under the 
Apache Software License version 2.0.

The figure below shows a non-exhaustive taxonomy of relevant data mining tools dealing with probabilistic graphical models (PGMs) and data streams. To the best of our knowledge, existing software systems for PGMs only focus on mining stationary data sets, and hence, the main goal of AMIDST is to fill this gap and provide a significant contribution within the areas of PGMs and mining data streams.

<p align="center">
<img title="Taxonomy" src="https://amidst.github.io/toolbox/docs/taxonomy.png" width="500">
</p>

# Scalability

Scalability is a main concern for the AMIDST toolbox. Java 8 functional programming style is used to provide parallel implementations of the algorithms. If more computation capacity is needed to process data streams, AMIDST users can also use more CPU cores. As an example, the following figure shows how the data processing capacity of our toolbox increases given the number of CPU cores when learning an hybrid BN model (including a class variable C, two latent variables (dashed nodes), multinomial (blue nodes) and Gaussian (green nodes) observable variables) using the AMIDST's learning engine. As can be seen, using our variational learning engine, AMIDST toolbox is able to process data in the order of gigabytes (GB) per hour depending on the number of available CPU cores with large and complex PGMs with latent variables. Note that, these experiments were carried out on a Ubuntu Linux server with a x86_64 architecture and 32 cores. The size of the processed data set was measured according to the [Weka](www.cs.waikato.ac.nz/ml/weka/)'s ARFF format.

<p align="center">
<img src="https://amidst.github.io/toolbox/docs/scalability.png" width="800">
</p>

# Documentation<a name="documentation"></a>

* [Getting Started!](http://amidst.github.io/toolbox/GettingStarted.html) explains how to install the AMIDST toolbox, how this toolbox make use of Java 8 new functional style programming features, and why it is based on a module based architecture.

* [Toolbox Functionalities](http://amidst.github.io/toolbox/ToolboxFunctionalities.html) describes the main functionalities (i.e., data streams, PGMs, learning and inference engines, etc.) of the AMIDST toolbox.

* [Code Examples](http://amidst.github.io/toolbox/CodeExamples.html) includes a list of source code examples explaining how to use some functionalities of the AMIDST toolbox.

* [API JavaDoc](http://amidst.github.io/toolbox/javadoc/index.html) of the AMIDST toolbox. 

# Contributing to AMIDST

AMIDST is an open source toolbox and the end-users are encouraged to upload their contributions (which may include basic contributions, major extensions, and/or use-cases) following the indications given in this [link](http://amidst.github.io/toolbox/ContributingToAMIDST.html).

# Publications & Use-Cases

The following repository [https://github.com/amidst/toolbox-usecases](https://github.com/amidst/toolbox-usecases) contains the source code and details about the publications and use-cases using the AMIDST toolbox. 

# Upcoming Developments

The AMIDST toolbox is an expanding project and upcoming developments include for instance the implementation of [dynamic models](https://en.wikipedia.org/wiki/Dynamic_Bayesian_network) for handling data streams, the integration of the toolbox in Big Data platforms like [Spark](http://spark.apache.org) and [Flink](http://flink.apache.org) to enlarge its scalability capacities, and a new link to [R](http://static.amidst.eu/upload/dokumenter/Posters/PosterUseR.pdf) to expand the AMIDST user-base.