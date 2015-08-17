# Scope

This toolbox offers a collection of scalable and parallel algorithms for inference and learning of hybrid Bayesian networks from streaming data. For example, AMIDST provides parallel multi-core implementations for Bayesian parameter learning, using streaming variational Bayes and variational message passing. Additionally, AMIDST efficiently leverages existing functionalities and algorithms by interfacing to existing software tools such as [R](https://www.r-project.org/), [Hugin](http://www.hugin.com) and [MOA](http://moa.cms.waikato.ac.nz). AMIDST is an open source toolbox written in Java and is available under the Apache Software License 2.0.

In the next figure we show a taxonomy of relevant data mining tools dealing with PGMs and data streams. To the best of our knowledge, there is no other software for mining data streams based on PGMs, most of the existing softwares based on PGMs are only focused on mining stationary data sets. Hence, the main goal of AMIDST is to fill this gap and produce a significant contribution within the areas of PGMs and mining streaming data.

<p align="center">
<img title="Taxonomy" src="https://github.com/amidst/toolbox/blob/master/doc/Taxonomy.png?raw=true" width="400">
</p>


# Scalability

Scalability is a main concern for the AMIDST toolbox. As mentioned before, we exploit Java 8 functional programming style to provide parallel implementations of most of our algorithms. If more computation capacity is needed to process data streams, AMIDST users can also use more CPU cores. As an example, the following figure shows how the data processing capacity of our toolbox increases with the number of cores when learning a hybrid BN model with latent variables using the AMIDST's learning engine. More precisely we learn a PGM model with multinomial (blue nodes) and Gaussian (green nodes) variables, some of them are latent, non observable, variables (dashed nodes). As can be seen, using our variational learning engine AMIDST toolbox is able to process data in the order of gigabytes per hour depending on the number of available cores with large and complex PGMs with latent variables.

<p align="center">
<img src="https://github.com/amidst/toolbox/blob/master/doc/Scalability.png?raw=true" width="800">
</p>


# Documentation<a name="documentation"></a>

Click in some of following links for further information:

* [Toolbox Functionalities](http://amidst.github.io/toolbox/ToolboxFunctionalities.html) describes which are the main functionalities (i.e. pgms, learning and inference algorithms, etc) included in the current version of the toolbox.

* [Getting Started](http://amidst.github.io/toolbox/GettingStarted.html) describes how to install the toolbox as well as its module's based architecture.

* [Contributing to AMIDST](http://amidst.github.io/toolbox/ContributingToAMIDST.html) describes the steps need to contribute to this toolbox.

* [Code Examples](http://amidst.github.io/toolbox/CodeExamples.html) provides a long list of code examples covering most the of the functionalities of the toolbox.

* [API Java Doc](http://amidst.github.io/toolbox/javadoc/index.html) of the toolbox. 


## Citing AMIDST Toolbox

Include a reference to the following paper:

> AMIDST: Analysis of MassIve Data STreams using Probabilistic Graphical Models. JMLR 2015.


# AMIDST Toolbox
====

Analysis of data streams using expressive and flexible Bayesian networks

This is the repository for the open source code in the AMIDST project.

Install Java 8 and IntelliJ:

http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

http://www.jetbrains.com/idea/download/

In IntelliJ open the maven project by pointint at the pom file. Also you need to point to the Java 8 installation and you are ready to rock.

========================
Compiling & Running from the command line
========================

1- Install Maven: http://maven.apache.org/download.cgi
(Download binaries and copy to Applications folder)

2- Modify the file maven_startup.sh (which you can find in the root project folder) and fix the path of your maven (Line 5) and java installation (Line 9).

3- Create (or modify if already exists) a file ".profile" or ".bash_profile" in you home directory and add the following line,
which points to file "maven_startup.sh"

        source <project-folder>/maven_startup.sh

 Now after restarting the terminal, mvn should work.


4- The script "compile.sh" (which you can find in the root project folder) just compiles the whole project.


5- The script "run.sh" (which you can find in the root project folder) should be used to run some class. For example,

        ./run.sh eu.amidst.core.examples.learning.ParallelMaximumLikelihoodExample
