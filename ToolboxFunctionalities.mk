# Toolbox Functionalities<a name="functionalities"></a>

The AMIDST is an open source Java 8 toolbox that makes use of functional programming style to provide parallel processing on mutli-core CPUs \citep{CIM2015}. AMIDST provides a collection of functionalities and algorithms for learning hybrid Bayesian networks from streaming data. In what follows, we describe the main functionalities that AMIDST toolbox supplies.



###Data Streams<a name="datastreams"></a> 
AMIDST provides parallel processing built-in functionalities for dealing with streaming data \citep{CIM2015}. It is possible to make several passes over the data samples if the stream can be stored on disk, otherwise the samples are discarded after being processed. The data format supported by AMIDST is Weka's ARFF (Attribute-Relation File Format) \citep{Hall2009}.



###Probabilistic Graphical Models<a name="pgms"></a>
AMIDST currently includes efficient implementations for representing Bayesian networks. AMIDST supports both discrete and continuous variables, and besides Multionomial, Gaussian and conditional linear Gaussian distributions, it also supports other distributions such as Gamma, Poission, Dirichlet, etc. as far as the final BN can be represented as a \textit{conjugate-exponential family model} \citep{WinnBishop2005}.  Other kind of probabilistic graphical models, such as dynamic BNs, are expected to be included in this toolbox.



###Inference Engine<a name="inference"></a>
AMIDST includes the implementation of the \textit{variational message passing} \citep{WinnBishop2005} algorithm, and the parallel implementation of the \textit{importance sampling} \citep{hammersley1964monte,CAEPIA2015} algorithm. It also supports exact inference by interfacing with [Hugin](http://www.hugin.com)'s junction tree inference algorithm \citep{Madsen2005Hugin}. 



###Learning Engine<a name="learning"></a>  
In AMIDST, a fully Bayesian approach is pursued, which means that the parameter learning reduces to the task of inference. AMIDST provides a multi-core parallel implementation of the \textit{streaming variational Bayes} algorithm \citep{broderick2013streaming}, using \textit{variational message passing} as underlying inference engine, which can deal with large models with latent variables. When the model does not contain latent variables, an efficient parallel implementation of \textit{maximum likelihood estimation} \citep{mlestimation} can be also used by exploiting an efficient vector-based representation of BNs as \textit{exponential family models} \citep{WinnBishop2005}. For structural learning, AMIDST currently supports standard PC and parallel TAN algorithms by interfacing with [Hugin](http://www.hugin.com) \citep{Madsen2005Hugin,Madsen2014}.



###Concept drift<a name="conceptdrift"></a> 
AMIDST also offers some support for dealing with concept drift while learning BNs from data streams. Firstly, we provide an extension of the \textit{streaming variational Bayes} algorithm \citep{broderick2013streaming} which exponentially down-weights the influence of \textit{old} data samples with the use of a fading factor (TODO). So, models learnt with this approach will be \textit{focused} in most recent data. In addition, AMIDST provides a probabilistic concept drift detector based on the use of latent variables \citep{IDA2015}.

###Links to MOA, Hugin and R<a name="librarylinks"></a> 
AMIDST leverages existing functionalities and algorithms by interfacing to existing software tools such as [R](https://www.r-project.org/), [Hugin](http://www.hugin.com) and [MOA](http://moa.cms.waikato.ac.nz) (Massive Online Analysis) \citep{BifetHolmesKirkbyPfahringer10}. This allows to efficiently well established systems and also broaden the AMIDST user-base. 

* **HuginLink** consists of a set of functionalities implemented to link the AMIDST toolbox with [Hugin](http://www.hugin.com) commercial software \citep{Madsen2005Hugin}. This connection extends AMIDST by providing some of the main functionalities of [Hugin](http://www.hugin.com), such as exact inference algorithms and scalable structural learning algorithms \citep{Madsen2014}. [Hugin](http://www.hugin.com) is a third-party commercial software and to access to these functionalities it is needed a license of the software and to follow some specific installation steps (further information is given [here](http://amidst.github.io/toolbox/#installhugin)).

* **MoaLink** ensures an easy use of AMDIST functionalities within [MOA](http://moa.cms.waikato.ac.nz) \citep{BifetHolmesKirkbyPfahringer10}.  The main idea is that any model deployed in AMIDST can be integrated and evaluated using MOA's graphical user interface. As a proof of concept, \textit{MoaLink} already provides a classification, a regression and a clustering method based on BN models with latent variables. These models are learnt in a streaming fashion using AMIDST learning engine. 

* **RLink** ....