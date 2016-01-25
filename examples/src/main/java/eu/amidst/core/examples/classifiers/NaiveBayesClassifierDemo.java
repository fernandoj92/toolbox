/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package eu.amidst.core.examples.classifiers;

import eu.amidst.core.classifiers.NaiveBayesClassifier;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataStream;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.utils.BayesianNetworkGenerator;
import eu.amidst.core.utils.BayesianNetworkSampler;

/**
 * Created by andresmasegosa on 15/01/15.
 */
public class NaiveBayesClassifierDemo {
    public static void main(String[] args) {

        BayesianNetworkGenerator.loadOptions();

        BayesianNetworkGenerator.setSeed(0);
        BayesianNetworkGenerator.setNumberOfGaussianVars(5900);
        BayesianNetworkGenerator.setNumberOfMultinomialVars(100, 10);
        BayesianNetwork bn = BayesianNetworkGenerator.generateNaiveBayes(2);
        System.out.println(bn.toString());

        int sampleSize = 10000;
        BayesianNetworkSampler sampler = new BayesianNetworkSampler(bn);
        sampler.setMARVar(bn.getVariables().getVariableById(0),0.2);
        sampler.setMARVar(bn.getVariables().getVariableById(3),0.2);
        sampler.setMARVar(bn.getVariables().getVariableById(8),0.2);
        DataStream<DataInstance> data =  sampler.sampleToDataStream(sampleSize);

        long time = System.nanoTime();
        NaiveBayesClassifier model = new NaiveBayesClassifier();
        model.setClassName(data.getAttributes().getFullListOfAttributes().get(data.getAttributes().getFullListOfAttributes().size() - 1).getName());
        model.setParallelMode(true);
        model.learn(data, 1000);
        BayesianNetwork nbClassifier = model.getBNModel();
        System.out.println(nbClassifier.toString());

        System.out.println("Time: "+(System.nanoTime()-time)/1000000000.0);




        //sampler.sampleToDataStream(sampleSize).stream().forEach(d -> System.out.println(model.predict(d)[0]));

    }
}
