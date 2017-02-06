package org.anantacreative.javausb.m2;


public class M2
{
    public static final  short vendorId=(short)0xfc58;
    public static final  short productId=(short)0x0001;
    private static final  byte IN_END_POINT=(byte)0x81;
    private static final  byte OUT_END_POINT=(byte)0x01;

    private static final  byte READ_COMMAND = 0x34;
    private static final  byte WRITE_COMMAND = 0x33;
    private static final  int REQUEST_TIMEOUT_MS = 10000;
    private static final  int DATA_PACKET_SIZE=64;




    /**
     * Чтение комплексов с прибора
     * @return
     */
    public static M2BinaryFile readFromDevice(final boolean debug) throws ReadFromDeviceException {


        return null;
    }

    /**
     * Запись комплексов
     * @param data
     */
    public static void writeToDevice(M2BinaryFile data) throws M2BinaryFile.MaxBytesBoundException, M2Complex.ZeroCountProgramBoundException, WriteToDeviceException, LanguageDevice.NoLangDeviceSupported {

        byte[] dataToWrite = data.getData();
        writeToDevice(dataToWrite);


    }


    private static void writeToDevice(byte[] dataToWrite) throws WriteToDeviceException {

    }

    /**
     * Очистка устройства
     */
    public static void clearDevice() throws WriteToDeviceException {
        writeToDevice(new byte[M2BinaryFile.MAX_FILE_BYTES]);
    }



    public static class ReadFromDeviceException extends Exception{
        public ReadFromDeviceException(Throwable cause) {
            super(cause);
        }
    }
    public static class WriteToDeviceException extends Exception{
        public WriteToDeviceException(Throwable cause) {
            super(cause);
        }
    }
}
