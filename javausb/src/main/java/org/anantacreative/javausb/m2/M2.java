package org.anantacreative.javausb.m2;


import org.anantacreative.javausb.USB.ByteHelper;
import org.anantacreative.javausb.USB.USBHelper;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class M2
{
    public static final  short vendorId=(short)0xfc58;
    public static final  short productId=(short)0x0002;
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
    public static void writeToDevice(M2BinaryFile data,int langID) throws M2BinaryFile.MaxBytesBoundException, M2Complex.ZeroCountProgramBoundException, LanguageDevice.NoLangDeviceSupported, WriteToDeviceException {

        byte[] dataToWrite = data.getData();
        writeToDevice(dataToWrite,langID,data.getComplexesList().size());


    }


    private static void writeToDevice(byte[] dataToWrite, int langID,int countComplexes) throws WriteToDeviceException {
        USBHelper.USBDeviceHandle usbDeviceHandle=null;
        try{


            usbDeviceHandle = USBHelper.openDevice(productId, vendorId, 0);
            byte[] commandWrite = new byte[DATA_PACKET_SIZE];
            commandWrite[0]=WRITE_COMMAND;

            byte[] lenBytes = ByteHelper.intTo2ByteArray(dataToWrite.length, ByteHelper.ByteOrder.BIG_TO_SMALL);
            commandWrite[1]=lenBytes[0];
            commandWrite[2]=lenBytes[1];

            commandWrite[3]=(byte)langID;
            commandWrite[4]=(byte)countComplexes;

            //команда на запись
            USBHelper.write(usbDeviceHandle,commandWrite,OUT_END_POINT,REQUEST_TIMEOUT_MS);

            ByteBuffer writeResponse = USBHelper.read(usbDeviceHandle, DATA_PACKET_SIZE, IN_END_POINT, REQUEST_TIMEOUT_MS);

            //запись всего пакета в прибор по 64 байта. Нужно не забыть проверять ответ и статус записи, чтобы отловить ошибки
            for(int i=0;i < dataToWrite.length/DATA_PACKET_SIZE;i++){

                USBHelper.write(usbDeviceHandle, Arrays.copyOfRange(dataToWrite,DATA_PACKET_SIZE*i,DATA_PACKET_SIZE*i+DATA_PACKET_SIZE),OUT_END_POINT,REQUEST_TIMEOUT_MS);            //читаем
                writeResponse = USBHelper.read(usbDeviceHandle, DATA_PACKET_SIZE, IN_END_POINT, REQUEST_TIMEOUT_MS);
            }

        } catch (USBHelper.USBException e) {
            throw new WriteToDeviceException(e);
        }finally {
            try {
                USBHelper.closeDevice(usbDeviceHandle,0);
            } catch (USBHelper.USBException e) {

            }
        }
    }

    /**
     * Очистка устройства
     */
    public static void clearDevice() throws WriteToDeviceException {
        writeToDevice(new byte[M2BinaryFile.MAX_FILE_BYTES],1,0);
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
