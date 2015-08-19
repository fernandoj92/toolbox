# Toolbox Functionalities<a name="functionalities"></a>

The AMIDST system is an open source Java 8 toolbox that makes use of a functional programming style to support parallel processing on mutli-core CPUs (Masegosa et al., 2015). AMIDST provides a collection of functionalities and algorithms for learning hybrid Bayesian networks from streaming data. In what follows, we describe the main functionalities that the AMIDST toolbox supplies.

###Data Streams<a name="datastreams"></a> 
AMIDST supports parallel processing by using built-in functionalities for dealing with streaming data. It is possible to make several passes over the data samples if the stream can be stored on disk, otherwise the samples are discarded after being processed. The data format supported by AMIDST is [Weka](www.cs.waikato.ac.nz/ml/weka/)’s attribute-relation file format (ARFF).

###Probabilistic Graphical Models<a name="pgms"></a>
AMIDST currently includes implementations for representing BNs in both standard and as a conjugate exponential family model (Winn and Bishop, 2005). The toolbox supports both discrete and continuous variables having multionomial, Gaussian and conditional linear Gaussian distributions. It can be easily extended to also support other distributions in the exponential family such as Gamma, Poisson, Dirichlet, etc.

###Inference Engine<a name="inference"></a>
AMIDST includes the variational message passing (Winn and Bishop, 2005) algorithm, 
and a parallel implementation of the importance sampling (Hammersley and Handscomb, 1964; Salmeron et al., 2015) 
algorithm for BNs. It also supports exact inference by interfacing with HUGIN’s junction tree inference algorithm 
(Madsen et al., 2005).

###Learning Engine<a name="learning"></a>  
In AMIDST, a fully Bayesian approach is pursued, which means that parameter learning reduces to the 
task of inference. AMIDST provides a multi-core parallel implementation of the streaming variational 
Bayes algorithm (Broderick et al., 2013), using variational message passing as the underlying inference 
engine, that deals with large latent variable models. When the model does not contain latent variables, one may alternatively use an efficient parallel implementation of maximum likelihood estimation (Scholz, 2004) that exploits a vector-based representation of BNs as exponential family models (Winn and Bishop, 2005). For structural learning, AMIDST currently supports the standard PC and parallel version of the TAN algorithm by interfacing with the HUGIN software (Madsen et al., 2014).

###Concept drift<a name="conceptdrift"></a> 
AMIDST also offers some support for dealing with concept drift while learning BNs from data streams. More precisely, the toolbox supports a novel probabilistic approach based on latent variables (Borchani et al., 2015) for detecting and adapting to concept drifts.

###Links to MOA and Hugin<a name="librarylinks"></a> 
AMIDST leverages existing functionalities and algorithms by interfacing to software tools such as 
[Hugin](http://www.hugin.com), and [MOA](http://moa.cms.waikato.ac.nz) (Massive Online Analysis). 
This allows the toolbox to efficiently exploit well-established systems and also broaden the AMIDST 
user-base. 

* **HuginLink** consists of a set of functionalities implemented to link the AMIDST toolbox with [Hugin](http://www.hugin.com) commercial software. This connection extends AMIDST by providing some of the main functionalities of [Hugin](http://www.hugin.com), such as exact inference algorithms and scalable structural learning algorithms (Madsen et al., 2014). [Hugin](http://www.hugin.com) is a third-party commercial software and to access to these functionalities it is needed a license of the software and to follow some specific installation steps (further information is given [here](http://amidst.github.io/toolbox/#installhugin)).

* **MoaLink** ensures an easy use of AMDIST functionalities within [MOA](http://moa.cms.waikato.ac.nz).  The main idea is that any model deployed in AMIDST can be integrated and evaluated using MOA's graphical user interface. As a proof of concept, \textit{MoaLink} already provides a classification, a regression and a clustering method based on BN models with latent variables. These models are learnt in a streaming fashion using AMIDST learning engine. 

###Bibliography

Hanen Borchani, Ana M. Martınez, Andres R. Masegosa, and et al. Modeling concept drift: A probabilistic graphical model based approach. In The Fourteenth International Symposium on Intelligent Data Analysis, volume in press, 2015.

Tamara Broderick, Nicholas Boyd, Andre Wibisono, Ashia C. Wilson, and Michael I. Jor- dan. Streaming variational Bayes. In Advances in Neural Information Processing Systems, pages 1727–1735, 2013.

J.M. Hammersley and D.C. Handscomb. Monte Carlo Methods. Methuen & Co, London, UK, 1964.

Anders L. Madsen, Frank Jensen, Uffe B. Kjærulff, and Michael Lang. HUGIN-the tool for Bayesian networks and influence diagrams. International Journal of Artificial Intelligence Tools, 14(3):507–543, 2005.

Anders L. Madsen, Frank Jensen, Antonio Salmeron, and et al. A new method for vertical parallelisation of TAN learning Based on balanced incomplete block designs. In The Seventh European Workshop on Probabilistic Graphical Models, pages 302–317, 2014.

Andres R. Masegosa, Ana M. Martınez, and Hanen Borchani. Probabilistic graphical models on multi-core CPUs using Java 8. IEEE Computational Intelligence Magazine, Special Issue on Computational Intelligence Software, Under review, 2015.

Antonio Salmeron, Darıo Ramos-Lopez, Hanen Borchani, and et al. Parallel importance sampling in conditional linear Gaussian networks. In Conferencia de la Asociaci ́on Espan ̃ola para la Inteligencia Artificial, volume in press, 2015.

F. W. Scholz. Maximum Likelihood Estimation. John Wiley & Sons, Inc., 2004.

John M. Winn and Christopher M. Bishop. Variational message passing. Journal of Machine Learning Research, 6:661–694, 2005.