package org.anantacreative.javausb.m2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.anantacreative.javausb.USB.ByteHelper.*;


public class M2Program {
    private List<Double> frequencies = new ArrayList<>();
    private List<Integer> frequenciesInDeviceFormat;
    private static  final  double FREQ_FORMAT_COEFF = 100;
    private static final  int FREQ_NUM_BYTES = 4;
    private static final  int FREQ_PRECISE = 2;
    private static final  double MAX_FREQ_VALUE = 20000.0;
    private int lastPositionInArray;
    private int programID;
    private String name;
    private String langAbbr;
    private static final  int MAX_PROGRAM_ID_VALUE=(int)Math.pow(2,Byte.SIZE*3)-1;



    /**
     * @param frequencies
     * @param programID
     * @param name имя программы в UTF8
     * @param langAbbr аббривиатура языка на котором будет отображаться имя в приборе( из списка в LanguageDevice)
     */
    public M2Program(List<Double> frequencies, int programID, String name, String langAbbr) throws MinFrequenciesBoundException, MaxProgramIDValueBoundException, ZeroValueFreqException {
            //if(programID > MAX_PROGRAM_ID_VALUE) throw new MaxProgramIDValueBoundException();
        this.programID=programID;
        this.name = name;
        this.langAbbr = langAbbr;
        if(frequencies.size()==0) throw new MinFrequenciesBoundException();



        this.frequencies.addAll(frequencies.stream().map(f-> f.doubleValue() > MAX_FREQ_VALUE ? MAX_FREQ_VALUE:f ).collect(Collectors.toList()));
        //преобразование в формат прибора
        frequenciesInDeviceFormat = this.frequencies.stream()
                                                    .map(M2Program::freqToDeviceFormat)
                                                    .collect(Collectors.toList());

        if(frequenciesInDeviceFormat.stream().filter(f-> f < 1 ).count()>0) throw new ZeroValueFreqException();

    }

    /**
     * Создает программу из байтового представления прибора
     *
     * @param programInBytes
     */
    public M2Program(byte[] programInBytes, int startPosition) throws ProgramParseException {
        int position = startPosition;
        frequenciesInDeviceFormat = new ArrayList<>();

        try {
            int countFreq = byteArray1ToInt(programInBytes, position++);//первый байт содержит количество частот
            if (countFreq == 0) throw new ZeroFrequenciesException();

            for (int i = 0; i < countFreq; i++) {

                frequenciesInDeviceFormat.add(byteArray4ToInt(programInBytes, position, ByteOrder.BIG_TO_SMALL));
                position += FREQ_NUM_BYTES;//4 байта

            }
            //последняя позиция в массиве программы
            lastPositionInArray=position-1;
            frequenciesInDeviceFormat.forEach(f -> frequencies.add(freqFromDeviceFormat(f)));


        } catch (ZeroFrequenciesException e) {
            throw new ProgramParseException(e);
        } catch (Exception e) {
            throw new ProgramParseException(e);
        }


    }

    protected int getLastPositionInArray(){return lastPositionInArray;}

    /**
     * Преобразует частоту в формат прибора
     *
     * @param freq
     * @return
     */
    private static int freqToDeviceFormat(double freq) {
        return (int) Math.round(freq * FREQ_FORMAT_COEFF);
    }

    /**
     * Преобразует частоту из формата прибора в обычную частоту.
     *
     * @param freq
     * @return
     */
    private static double freqFromDeviceFormat(int freq) {
        double f = freq / FREQ_FORMAT_COEFF;
        return new BigDecimal(f).setScale(FREQ_PRECISE, RoundingMode.UP).doubleValue();

    }


    public List<Double> getFrequencies() {
        return Collections.unmodifiableList(this.frequencies);
    }

    public List<Integer> getFrequenciesInDeviceFormat() {
        return Collections.unmodifiableList(frequenciesInDeviceFormat);
    }

    public int getCountFrequencies() {
        return frequencies.size();
    }


    protected void setProgramID(int programID) {
        this.programID = programID;
    }

    /**
     * ID програмы в программе(из какой программы была получена в программе эта программа)
     *
     * @return
     */
    public int getProgramID() {
        return programID;
    }

    /**
     * Преобразует программу в байтовое представление для прибора
     *
     * @return
     */
    protected List<Byte> toByteList() throws  LanguageDevice.NoLangDeviceSupported {

        /*
        програма1:
                1 байт  - колличество символов названия программы ( желательно ограничить до 20 символов )
                n  байт - название программы ( желательно ограничить до 20 символов )
                1  байт  - z колличество частот в воспроизводимых за раз(сейчас всегда три)
                z раз по 4-е байта частоты
                  1  байт  - z колличество частот в воспроизводимых за раз(сейчас всегда три, последний кусок может быть меньше 3)
                z раз по 4-е байта частоты
                ...
                 if(колличество частот воспроизводимых за раз == 0) признак конца программы


         */

        List<Byte> res = new ArrayList<>();
        List<Byte> bytesName =  LanguageDevice.getBytesInDeviceLang(name,langAbbr);

        int partition1=frequencies.size()/3;
        int ostatok =frequencies.size()-partition1;

        res.add((byte)bytesName.size());//размер строки названия
        res.addAll(bytesName);//имя программы

        //частоты
        for(int i=0;i<partition1;i+=3){
            res.add((byte)3);
            res.addAll(intToByteList(frequenciesInDeviceFormat.get(i), ByteOrder.BIG_TO_SMALL));
            res.addAll(intToByteList(frequenciesInDeviceFormat.get(i+1), ByteOrder.BIG_TO_SMALL));
            res.addAll(intToByteList(frequenciesInDeviceFormat.get(i+2), ByteOrder.BIG_TO_SMALL));
        }

        if( ostatok !=0){
            res.add((byte)ostatok);
            for(int i=partition1;i<frequencies.size();i++){
                res.addAll(intToByteList(frequenciesInDeviceFormat.get(i), ByteOrder.BIG_TO_SMALL));
            }
        }

        res.add((byte)0);//признак конца программы

        return res;
    }


    /**
     * Программа содержит нулевое поле числа частот
     */
    public static class ZeroFrequenciesException extends Exception {
        public ZeroFrequenciesException() {
        }
    }

    /**
     * Ошибка парсинга байтового масиива программы
     */
    public static class ProgramParseException extends Exception {
        public ProgramParseException(Throwable cause) {
            super(cause);
        }
    }


    /**
     * Указывает на то , что в программе нет частот
     */
    public static class MinFrequenciesBoundException extends Exception {
        protected MinFrequenciesBoundException() {
            super();
        }
    }

    public static class MaxProgramIDValueBoundException extends Exception {
        protected MaxProgramIDValueBoundException() {
            super();
        }
    }

    /**
     * Одна из частот имеет значение 0, что не допустимо
     */
    public static class ZeroValueFreqException extends Exception {
        protected ZeroValueFreqException() {
            super();
        }
    }
    @Override
    public String toString() {
        return "M2Program{\n" +
                "countFrequencies = "+frequencies.size()+
                "\n frequenciesInDeviceFormat=" + frequenciesInDeviceFormat +
                "\n lastPositionInArray=" + lastPositionInArray +
                ", programID=" + programID +
                ", name=" + name +
                ", lang=" + langAbbr +
                " \nfrequencies = [ " + frequencies.stream().map(f->f.toString()).collect(Collectors.joining("; ")) +" ]\n}\n";

    }
}
