package org.anantacreative.javausb.Biofon;

import org.anantacreative.javausb.USB.ByteHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Ananta on 30.11.2016.
 */
public class BiofonBinaryFile {

    private final List<BiofonComplex> complexesList=new ArrayList<>();
    private static int MAX_FILE_BYTES=6912;
    private static int PROGRAM_ID_BYTE_SIZE=3;//сколько байт занимает индекс программы в файле


    public BiofonBinaryFile(BiofonComplex complex1,BiofonComplex complex2,BiofonComplex complex3) {
        complexesList.add(complex1);
        complexesList.add(complex2);
        complexesList.add(complex3);
    }

    /**
     * Переводит байтовые данные в структуру данных
     * @param fileData
     */
    public BiofonBinaryFile(byte[] fileData) throws FileParseException {
        //всего 3 комплекса. Ограничение прибора.
        try {
            BiofonComplex biofonComplex1 = new BiofonComplex(fileData, 0);
            complexesList.add(biofonComplex1);

            BiofonComplex biofonComplex2 = new BiofonComplex(fileData, biofonComplex1.getLastComplexInArrayPosition()+1);
            complexesList.add(biofonComplex2);

            BiofonComplex biofonComplex3 = new BiofonComplex(fileData, biofonComplex2.getLastComplexInArrayPosition()+1);
            complexesList.add(biofonComplex3);

            ///как определить сколько комплексов записано. Их всегда должно быть три!!! И в каждом хотябы 1 программа

            int countAllProgram =biofonComplex1.getCountPrograms()+biofonComplex2.getCountPrograms()+biofonComplex3.getCountPrograms();
            int nextReadPosition=biofonComplex3.getLastComplexInArrayPosition()+1;

            for (BiofonComplex complex : complexesList) {

                for (BiofonProgram program : complex.getPrograms()) {
                    program.setProgramID(ByteHelper.byteArray3ToInt(fileData,nextReadPosition, ByteHelper.ByteOrder.BIG_TO_SMALL));
                    nextReadPosition+=PROGRAM_ID_BYTE_SIZE;

                }

            }


        } catch (BiofonComplex.ComplexParseException e) {
           throw new FileParseException(e);
        } catch (Exception e) {
            throw new FileParseException(e);
        }

    }

    public List<BiofonComplex> getComplexesList() {
        return Collections.unmodifiableList(complexesList);
    }



    public  byte [] getData() throws MaxBytesBoundException, BiofonComplex.ZeroCountProgramBoundException {
        //количество програм в комплексе 1 байт
        // пауза между програмами 1 байт
        // время на частоту 1 байт
        //програма1 - количество частот 1байт, по 4 байта сами частоты
        //програма2 - количество частот 1байт, по 4 байта сами частоты
        //програма3 - количество частот 1байт, по 4 байта сами частоты
        //...
        //индексы программ по 3 байта, в порядке их записи.



        final List<Byte> res=new ArrayList<>();
        final byte[] result=new byte[res.size()];


        for (BiofonComplex biofonComplex : complexesList) {
            res.addAll(biofonComplex.toByteList());
        }

        for (BiofonComplex biofonComplex : complexesList){
            for (BiofonProgram biofonProgram : biofonComplex.getPrograms()) {
                res.addAll(ByteHelper.intTo3ByteList(biofonProgram.getProgramID(), ByteHelper.ByteOrder.BIG_TO_SMALL));
            }

        }


        if(res.size()> MAX_FILE_BYTES)  throw new MaxBytesBoundException();



       for(int i=0;i<res.size();i++)result[i]=res.get(i);
       return result;
    }

    public static class MaxBytesBoundException extends Exception{
        protected MaxBytesBoundException() {
            super();
        }
    }

    public static class FileParseException extends Exception{
        public FileParseException(Throwable cause) {
            super(cause);
        }
    }



    @Override
    public String toString() {
        return "BiofonBinaryFile:\n" +
                  complexesList.stream().map(c->c.toString()).collect(Collectors.joining("\n"));
    }
}
