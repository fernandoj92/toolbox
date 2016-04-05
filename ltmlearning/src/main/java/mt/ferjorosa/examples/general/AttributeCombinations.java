package mt.ferjorosa.examples.general;

import java.util.ArrayList;

/**
 * Created by Fer on 17/03/2016.
 */

// Probado con ejemplos 4,5,6,14,16 y 30
public class AttributeCombinations {

    public static void main(String[] args) throws Exception {
        int contarCombinacion = 0;
        int numberOfAttributes = 4;
        int[] attributeIndexes ={3,4,5,9}; // arraySize == numberOfAttributes

        for(int i=2;i<=numberOfAttributes/2;i++) {
            int left = i;
            int right = numberOfAttributes - i;

            // initial array of attribute indexes [0,1,2,...,n-1]
            int[] leftIndexes = new int[left]; // length = left
            for (int alpha = 0; alpha < leftIndexes.length; alpha++) {
                leftIndexes[alpha] = alpha;
            }

            int pos = leftIndexes.length - 1; // leftIndexes.final
            int resetPos = leftIndexes.length - 1;

            while (leftIndexes[0] < (numberOfAttributes - (leftIndexes.length - 1))){
                // Generar combinacion
                // Siempre iteramos en la última posicion, lo demas son resets
                while(leftIndexes[pos] < numberOfAttributes){
                    mostrarCombinacion(leftIndexes);
                    contarCombinacion++;
                    leftIndexes[pos]++;
                }
                // Reset
                if(resetPos >= 0){

                    if(leftIndexes[resetPos] >= numberOfAttributes){
                        resetPos = resetPos - 1;
                    }

                    int valor = leftIndexes[resetPos];
                    for(int j=resetPos;j < leftIndexes.length;j++){
                        valor = valor + 1;
                        leftIndexes[j] = valor;
                    }
                }
            }
        }

        System.out.println("N Combinaciones: "+contarCombinacion);

    }

    private static void mostrarCombinacion(int[] array){
        String combinacion = "";
        for(int index = 0; index < array.length; index++)
            combinacion += array[index] + " ";
        System.out.println(combinacion);
    }

}
