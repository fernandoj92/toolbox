# Refactorizacion

Crear una clase llamada LTMLearner o similar que seria estatica y que se encargaria de aprender LTMs, cntedndria por el momento metodos del algoritmo BI, asi como la funcionalidad de KnownStructureLTM.

El metodo learn2dimensionalLTM se puede ampliar a X dimensiones facilmente, aunque para el BI solo necesitamos 2

Como hago el chow liu entre LVs sino utilizo el EM, ya que no completo los datos... Puedo utilizar la multivariate MI o la bivariate MI entre las variables observadas y ver cual es la mas cercana que no pertenezca al LCM suyoy entonces uno los padres

TODO: Hacer un Hill climbing para escoger cual es la mejor direccion en el MWST (la mejor raiz)
TODO: En vez de aumentar la cardinalidad de las islas, mirar a aumentar la cardinalidad de las LV dentro del flat-LTM, puede que de mejores resultados.
TODO: Hacer un Hill climbing para escoger relaciones entre las variables observadas.
TODO: Hacer una clase de configuracion para el Approximate BI alg

IMPORTANT: Parallel SVB returns different ELBO results and theresfore when used in the ABI alg, it returns different structures.

--------------------- 07-08-2016 --------------------

TODO: Crear una clase con 1 método estático: BNtoJson(BayesianNetwork)
