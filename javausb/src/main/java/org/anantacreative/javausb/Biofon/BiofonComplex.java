package org.anantacreative.javausb.Biofon;

import org.anantacreative.javausb.USB.ByteHelper;

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
   private int pauseBetweenPrograms;
   private int timeByFrequency;
   private static int MAX_PROGRAM_COUNT_IN_COMPLEX = (int)Math.pow(2,Byte.SIZE)-1;
   private static int MAX_PAUSE = (int)Math.pow(2,Byte.SIZE)-1;
   private static int MAX_TIME_BY_FREQ = (int)Math.pow(2,Byte.SIZE)-1;
   private int lastComplexInArrayPosition;

    /**
     *
     * @param pauseBetweenPrograms время паузы между программами в минутах
     * @param timeByFrequency время на частоту в минутах
     * @throws MaxPauseBoundException
     * @throws MaxTimeByFreqBoundException
     */
    public BiofonComplex(int pauseBetweenPrograms, int timeByFrequency) throws MaxPauseBoundException, MaxTimeByFreqBoundException {
        this.pauseBetweenPrograms = pauseBetweenPrograms;
        this.timeByFrequency = timeByFrequency;
        if(this.pauseBetweenPrograms>=MAX_PAUSE)  throw new MaxPauseBoundException();
        if(this.timeByFrequency>=MAX_TIME_BY_FREQ) throw new MaxTimeByFreqBoundException();

    }

    /**
     * Переводит байтовые данные в структуру данных
     * @param complexData
     * @param startPosition
     */
    public BiofonComplex(byte[] complexData, int startPosition) throws ComplexParseException {
        int position = startPosition;
        int countPrograms =0;
        try {
            //количество программ в комплексе 1 байт
             countPrograms= ByteHelper.byteArray1ToInt(complexData,position++);


            // пауза между программами 1 байт
            this.pauseBetweenPrograms = ByteHelper.byteArray1ToInt(complexData,position++);
            // время на частоту 1 байт
            this.timeByFrequency = ByteHelper.byteArray1ToInt(complexData,position++);
            //программы

            for(int i=0;i<countPrograms;i++){

                BiofonProgram biofonProgram  = new BiofonProgram(complexData, position );
                programs.add(biofonProgram);
                position = biofonProgram.getLastPositionInArray()+1;//укажет на след. стартовую поз. программы

            }

            lastComplexInArrayPosition=position-1;

        }catch (BiofonProgram.ProgramParseException e) {
           throw new ComplexParseException(e);
        } catch (Exception e){
            throw new ComplexParseException(e);
        }



    }

    protected int getLastComplexInArrayPosition() {
        return lastComplexInArrayPosition;
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

    public int getPauseBetweenPrograms() {
        return pauseBetweenPrograms;
    }

    public int getTimeByFrequency() {
        return timeByFrequency;
    }

    public int getCountPrograms(){return programs.size();}

    protected List<Byte> toByteList(){
        //количество программ в комплексе 1 байт
        // пауза между программами 1 байт
        // время на частоту 1 байт
        //программы

        List<Byte> res=new ArrayList<>();
        res.add((byte)getCountPrograms());
        res.add((byte)pauseBetweenPrograms);
        res.add((byte)timeByFrequency);
        for (BiofonProgram program : programs)   res.addAll(program.toByteList());

        return res;
    }



    /**
     * Ошибка парсинга байтового масиива комплекса
     */
    public static class ComplexParseException extends Exception {
        public ComplexParseException(Throwable cause) {
            super( cause);
        }
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
