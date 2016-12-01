package org.anantacreative.javausb.Biofon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ananta on 30.11.2016.
 */
public class BiofonBinaryFile {

    private final List<BiofonComplex> complexesList=new ArrayList<>();
    private static int MAX_FILE_BYTES=6912;


    public BiofonBinaryFile() {
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

            ///как определить сколько комплексов записано???

        } catch (BiofonComplex.ComplexParseException e) {
           throw new FileParseException(e);
        }

    }

    public List<BiofonComplex> getComplexesList() {
        return Collections.unmodifiableList(complexesList);
    }

    public void addComplex(BiofonComplex complex){
        complexesList.add(complex);
    }

    public  byte [] getData() throws MaxBytesBoundException {
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

        complexesList.forEach(c->res.addAll(c.toByteList()));

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
}
