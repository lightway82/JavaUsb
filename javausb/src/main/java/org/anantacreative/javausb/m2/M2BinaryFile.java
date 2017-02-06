package org.anantacreative.javausb.m2;


import org.anantacreative.javausb.USB.ByteHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class M2BinaryFile {

    private final List<M2Complex> complexesList=new ArrayList<>();
    public static final int MAX_FILE_BYTES=100000;
    private static final int ALIGN_FILE_BYTE_SIZE=64;//длина файла должна быть кратна ALIGN_FILE_BYTE_SIZE


public M2BinaryFile() {
    }

    public void addComplex(M2Complex complex){complexesList.add(complex);}

    /**
     * Переводит байтовые данные в структуру данных
     * @param fileData
     */
    public M2BinaryFile(byte[] fileData) throws FileParseException {
        //всего 3 комплекса. Ограничение прибора.
       /*
        try {
            M2Complex m2Complex1 = new M2Complex(fileData, 0);
            complexesList.add(m2Complex1);

            M2Complex m2Complex2 = new M2Complex(fileData, m2Complex1.getLastComplexInArrayPosition()+1);
            complexesList.add(m2Complex2);

            M2Complex m2Complex3 = new M2Complex(fileData, m2Complex2.getLastComplexInArrayPosition()+1);
            complexesList.add(m2Complex3);

            ///как определить сколько комплексов записано. Их всегда должно быть три!!! И в каждом хотябы 1 программа

            int countAllProgram =m2Complex1.getCountPrograms()+m2Complex2.getCountPrograms()+m2Complex3.getCountPrograms();
            int nextReadPosition=m2Complex3.getLastComplexInArrayPosition()+1;

            for (M2Complex complex : complexesList) {

                for (M2Program program : complex.getPrograms()) {
                    program.setProgramID(ByteHelper.byteArray3ToInt(fileData,nextReadPosition, ByteHelper.ByteOrder.BIG_TO_SMALL));
                    nextReadPosition+=PROGRAM_ID_BYTE_SIZE;

                }

            }


        } catch (M2Complex.ComplexParseException e) {
           throw new FileParseException(e);
        } catch (Exception e) {
            throw new FileParseException(e);
        }
*/
    }

    public List<M2Complex> getComplexesList() {
        return Collections.unmodifiableList(complexesList);
    }



    public  byte [] getData() throws MaxBytesBoundException, M2Complex.ZeroCountProgramBoundException, LanguageDevice.NoLangDeviceSupported {


        final List<Byte> res=new ArrayList<>();
        final List<List<Byte>> complexesData=new ArrayList<>();



        for (M2Complex m2Complex : complexesList) {
            complexesData.add(m2Complex.toByteList());
        }



        int currentAdr=4*complexesList.size();

        for (int i=0; i<complexesData.size();i++) {


            res.addAll(ByteHelper.intToByteList(currentAdr, ByteHelper.ByteOrder.BIG_TO_SMALL));
            currentAdr += complexesData.get(i).size();
        }

        for (List<Byte> cData : complexesData) {
            res.addAll(cData);
        }


        if(res.size()> MAX_FILE_BYTES)  throw new MaxBytesBoundException();

        int additionalBytes = ALIGN_FILE_BYTE_SIZE -res.size() % ALIGN_FILE_BYTE_SIZE;

        final byte[] result=new byte[res.size()+additionalBytes];
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
        return "M2BinaryFile:\n" +
                  complexesList.stream().map(c->c.toString()).collect(Collectors.joining("\n"));
    }
}
