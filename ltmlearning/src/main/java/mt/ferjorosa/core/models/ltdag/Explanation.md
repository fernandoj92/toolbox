# Latent Tree Models DAGs

### Why the need of a new distinction between variables and what does it mean?

Latent Tree Models are a probabilistic tree-structured graphical model where leaf nodes are observed while internal nodes can either be observed or latent. This constrains its creation and to make it more understandable I decided to explicitly distinguish between them and create my own constrained DAG.

This way it is not necessary to modify the core functionality of the toolbox to be able to handle this kind of structures. Moreover, this way it is possible to distinguish between an "observable variable" (a variable with known evidence) and an "observed variable" (a variable that is not hidden, whose data is completely observable).