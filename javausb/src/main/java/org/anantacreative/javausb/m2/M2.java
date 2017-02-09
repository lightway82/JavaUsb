package org.anantacreative.javausb.m2;


import org.anantacreative.javausb.USB.ByteHelper;
import org.anantacreative.javausb.USB.USBHelper;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class M2
{
    public static final  short vendorId=(short)0xFC82;
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


    public static String readDeviceName() throws WriteToDeviceException {
        USBHelper.USBDeviceHandle usbDeviceHandle=null;
       String str="";
        try{


            usbDeviceHandle = USBHelper.openDevice(productId, vendorId, 0);
            byte[] commandWrite = new byte[DATA_PACKET_SIZE];
            commandWrite[0]=50;

            //команда на запись
            USBHelper.write(usbDeviceHandle,commandWrite,OUT_END_POINT,REQUEST_TIMEOUT_MS);

            ByteBuffer writeResponse = USBHelper.read(usbDeviceHandle, DATA_PACKET_SIZE, IN_END_POINT, REQUEST_TIMEOUT_MS);

            writeResponse.position(0);
            byte[] bytes2 = new byte[DATA_PACKET_SIZE];
            writeResponse.get(bytes2);
            int strSize=0;
            for(int i=0;i<bytes2.length;i++){
                if(bytes2[i]==0){
                   strSize=i;
                   break;
                }
            }

            str= ByteHelper.byteArrayToString(bytes2,0,strSize, ByteHelper.ByteOrder.BIG_TO_SMALL,"Cp1250");



        } catch (USBHelper.USBException e) {
            throw new WriteToDeviceException(e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                USBHelper.closeDevice(usbDeviceHandle,0);
            } catch (USBHelper.USBException e) {

            }
        }
        return str;
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
            System.out.print("Write command send..");

            usbDeviceHandle = USBHelper.openDevice(productId, vendorId, 0);
            byte[] commandWrite = new byte[DATA_PACKET_SIZE];
            commandWrite[0]=WRITE_COMMAND;

            byte[] lenBytes = ByteHelper.intToByteArray(dataToWrite.length, ByteHelper.ByteOrder.BIG_TO_SMALL);
            commandWrite[1]=lenBytes[0];
            commandWrite[2]=lenBytes[1];
            commandWrite[3]=lenBytes[2];
            commandWrite[4]=lenBytes[3];

            commandWrite[5]=(byte)langID;
            commandWrite[6]=(byte)countComplexes;
            System.out.print("COMMAND PACKET = ");

            for (int i=0;i<commandWrite.length;i++) {

                System.out.print((commandWrite[i]<0?commandWrite[i]+256:commandWrite[i])+", ");

            }
            System.out.println("OUT_END_POINT="+OUT_END_POINT+" IN_END_POINT="+IN_END_POINT);

            //команда на запись
            USBHelper.write(usbDeviceHandle,commandWrite,OUT_END_POINT,REQUEST_TIMEOUT_MS);
            System.out.print("READ RESPONSE...");
            ByteBuffer writeResponse = USBHelper.read(usbDeviceHandle, DATA_PACKET_SIZE, IN_END_POINT, REQUEST_TIMEOUT_MS);

            writeResponse.position(0);
            byte[] bytes2 = new byte[DATA_PACKET_SIZE];
            writeResponse.get(bytes2);
            System.out.println("Device response: "+ByteHelper.bytesToHex(bytes2,64,' '));
            System.out.println("OK");



            System.out.print("WRITE DATA...");

            //запись всего пакета в прибор по 64 байта. Нужно не забыть проверять ответ и статус записи, чтобы отловить ошибки
            for(int i=0;i < dataToWrite.length/DATA_PACKET_SIZE;i++){
                System.out.print("WRITE 64 byte...");
                USBHelper.write(usbDeviceHandle, Arrays.copyOfRange(dataToWrite,DATA_PACKET_SIZE*i,DATA_PACKET_SIZE*i+DATA_PACKET_SIZE),OUT_END_POINT,REQUEST_TIMEOUT_MS);            //читаем
                writeResponse = USBHelper.read(usbDeviceHandle, DATA_PACKET_SIZE, IN_END_POINT, REQUEST_TIMEOUT_MS);
                writeResponse.position(0);
                byte[] bytes3 = new byte[DATA_PACKET_SIZE];
                writeResponse.get(bytes3);
                System.out.println("Device response: "+ByteHelper.bytesToHex(bytes3,64,' '));
                System.out.println(i);
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
