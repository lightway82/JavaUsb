package org.anantacreative.javausb.Biofon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Представляет комплекс в биофоне
 * Created by Ananta on 30.11.2016.
 */
public class BiofonComplex
{
   private List<BiofonProgram> programs=new ArrayList<>();
   private short pauseBetweenPrograms;
   private short timeByFrequency;
   private static int MAX_PROGRAM_COUNT_IN_COMPLEX = (int)Math.pow(2,Byte.SIZE)-1;
   private static int MAX_PAUSE = (int)Math.pow(2,Byte.SIZE)-1;
   private static int MAX_TIME_BY_FREQ = (int)Math.pow(2,Byte.SIZE)-1;

    /**
     *
     * @param pauseBetweenPrograms время паузы между программами в минутах
     * @param timeByFrequency время на частоту в минутах
     * @throws MaxPauseBoundException
     * @throws MaxTimeByFreqBoundException
     */
    public BiofonComplex(byte pauseBetweenPrograms, byte timeByFrequency) throws MaxPauseBoundException, MaxTimeByFreqBoundException {
        this.pauseBetweenPrograms = pauseBetweenPrograms;
        this.timeByFrequency = timeByFrequency;
        if(this.pauseBetweenPrograms>=MAX_PAUSE)  throw new MaxPauseBoundException();
        if(this.timeByFrequency>=MAX_TIME_BY_FREQ) throw new MaxTimeByFreqBoundException();

    }

    /**
     * Переводит байтовые данные в структуру данных
     * @param complexData
     */
    public BiofonComplex(byte[] complexData) {
    }

    public List<BiofonProgram> getPrograms() {
        return Collections.unmodifiableList(programs);
    }

    /**
     * Добавляет програму в комплекс. Может выбросить исключение при привышение лимита в 255 программ.
     * @param p
     * @throws MaxCountProgramBoundException
     */
    public void addProgram(BiofonProgram p) throws MaxCountProgramBoundException {
        if(programs.size() >= MAX_PROGRAM_COUNT_IN_COMPLEX )throw new MaxCountProgramBoundException();
    }

    public short getPauseBetweenPrograms() {
        return pauseBetweenPrograms;
    }

    public short getTimeByFrequency() {
        return timeByFrequency;
    }

    public int getCountPrograms(){return programs.size();}

    protected List<Byte> toByteList(){
        //количество программ в комплексе 1 байт
        // пауза между программами 1 байт
        // время на частоту 1 байт
        //программы


        //индексы программ по 3 байта, в порядке их записи.
        List<Byte> res=new ArrayList<>();
        res.add((byte)getCountPrograms());
        res.add((byte)pauseBetweenPrograms);
        res.add((byte)timeByFrequency);
        for (BiofonProgram program : programs)   res.addAll(program.toByteList());

        return res;
    }

    /**
     * Если пауза больше возможной(255)
     */
    public static class MaxPauseBoundException extends Exception{
        protected MaxPauseBoundException() {
            super();
        }
    }
    /**
     * Если время на частоту больше возможной(255)
     */
    public static class MaxTimeByFreqBoundException extends Exception{
        protected MaxTimeByFreqBoundException() {
            super();
        }
    }
    /**
     * Указывает на то что превышено, количество программ в комплексе
     */
    public static class MaxCountProgramBoundException extends Exception{
        protected MaxCountProgramBoundException() {
            super();
        }
    }
}
