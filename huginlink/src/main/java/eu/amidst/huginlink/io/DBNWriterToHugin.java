package eu.amidst.huginlink.io;

import COM.hugin.HAPI.ExceptionHugin;
import eu.amidst.dynamic.models.DynamicBayesianNetwork;

/**
 * This class is a writer of dynamic Bayesian networks in AMIDST format to Hugin files.
 */
public class DBNWriterToHugin {

    /**
     * Creates a Hugin Bayesian network file from a AMIDST Dynamic Bayesian network model.
     * In order to simplify, we assume that the Hugin DBN model name is the file name without extension.
     * @param amidstDBN the <code>DynamicBayesianNetwork</code> in AMIDST format.
     * @param file the name of the Hugin file in which the model is saved.
     * @throws ExceptionHugin
     */
    public static void saveToHuginFile(DynamicBayesianNetwork amidstDBN, String file) throws ExceptionHugin {

        BNWriterToHugin.saveToHuginFile(amidstDBN.toBayesianNetworkTimeT(),file);

        /*
        Class huginDBN = DBNConverterToHugin.convertToHugin(amidstDBN);

        //The network name must be the same as the file name (without .dbn)
        String[] parts = file.split("/");
        String fileName = parts[parts.length-1];
        String networkName = fileName.substring(0,fileName.length()-5);

        System.out.println(networkName);
        huginDBN.setName(networkName);

        huginDBN.saveAsNet(file);
        */

    }
}
