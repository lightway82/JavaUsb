package org.anantacreative.javausb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ananta on 30.11.2016.
 */
public class BiofonProgram
{
    private List<Double> frequencies=new ArrayList<>();
    private List<Integer> frequenciesInDeviceFormat;
    private static double FREQ_FORMAT_COEFF = 85899.34592;
    private static int MAX_FREQUENCIES_COUNT = (int)Math.pow(2,Byte.SIZE)-1;
    private int programID;

    /**
     *
     * @param frequencies
     * @param programID
     */
    public BiofonProgram(List<Double> frequencies,int programID) throws MaxFrequenciesBoundException {

        if(frequencies.size() >= MAX_FREQUENCIES_COUNT) throw new MaxFrequenciesBoundException();
        this.frequencies.addAll( frequencies );
        //преобразование в формат прибора
        frequenciesInDeviceFormat=this.frequencies.stream()
                .map(f->(int)Math.round(f*FREQ_FORMAT_COEFF))
                .collect(Collectors.toList());

    }

    /**
     * Создает программу из байтового представления прибора
     * @param programInBytes
     */
    public BiofonProgram(byte[] programInBytes){

    }



    public List<Double> getFrequencies() {
        return Collections.unmodifiableList(this.frequencies);
    }

    public List<Integer> getFrequenciesInDeviceFormat(){
        return  Collections.unmodifiableList(frequenciesInDeviceFormat);
    }

    public int getCountFrequencies(){return frequencies.size();}

    /**
     * ID програмы в программе(из какой программы была получена в программе эта программа)
     * @return
     */
    public int getProgramID() {
        return programID;
    }

    /**
     * Преобразует программу в байтовое представление для прибора
     * @return
     */
    protected List<Byte> toByteList() {

        //програма1 - количество частот 1байт, по 4 байта сами частоты
        List<Byte> res=new ArrayList<>();

        res.add((byte)frequencies.size());
        for (Integer freq : frequenciesInDeviceFormat) {
            res.addAll(ByteHelper.intToByteList(freq, ByteHelper.ByteOrder.BIG_TO_SMALL));
        }


        return res;
    }

    /**
     * Указывает на превышение количества частот програме заданого предела(255)
     */
    public static class MaxFrequenciesBoundException extends Exception {
        protected MaxFrequenciesBoundException() {
            super();
        }
    }

}
