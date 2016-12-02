package org.anantacreative.javausb;

import org.anantacreative.javausb.Biofon.BiofonBinaryFile;
import org.anantacreative.javausb.Biofon.BiofonComplex;
import org.anantacreative.javausb.USB.ByteHelper;
import org.anantacreative.javausb.USB.USBHelper;

import java.nio.ByteBuffer;



/**
 * Created by anama on 20.10.16.
 */
public class Main {

    private static short vendorId=(short)0xfc58;
    private static short productId=(short)0x0001;
    private static byte IN_END_POINT=(byte)0x81;
    private static byte OUT_END_POINT=(byte)0x01;






    /**
     * Main method.
     *
     * @param args
     *            Command-line arguments (Ignored)
     */
    public static void main(final String[] args)
    {
        USBHelper.initContext();
/*
        Device device = USBHelper.findDevice(vendorId, productId);
        if(device==null) {
            System.out.println("Устройство не обнаружено");
            System.exit(0);
        }
        USBHelper.dumpDevice(device);


        USBHelper.addPlugEventHandler(productId, vendorId, new USBHelper.PlugDeviceListener() {
            @Override
            public void onAttachDevice(Device device) {
               System.out.println("Устройство присобачили");
            }

            @Override
            public void onDetachDevice(Device device) {
                System.out.println("Устройство отсобачили");
            }
        });

        USBHelper.startHotPlugListener();

        try {
            System.in.read();
            System.out.println("Выкл");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            USBHelper.stopHotPlugListener();
            USBHelper.closeContext();
        }
 */
        USBHelper.USBDeviceHandle usbDeviceHandle = USBHelper.openDevice(productId, vendorId, 0);
        byte[] commandRead = new byte[64];
        commandRead[0]=0x34;
        USBHelper.write(usbDeviceHandle,commandRead,OUT_END_POINT,10000);

        //читаем
        ByteBuffer data = USBHelper.read(usbDeviceHandle, 64, IN_END_POINT, 10000);
        data.position(0);
        byte[] bytes2 = new byte[64];

        data.get(bytes2);
        System.out.println(ByteHelper.bytesToHex(bytes2,64,' '));
        data.position(1);
        int size = data.getChar();
        System.out.println("Size = "+ size);
        data.position(1);
      //  int  dlina = data.get(1) * 256 + data.get(2);
       // System.out.println("Size2 = "+dlina);


        int packets = (int)Math.ceil(size / 64);
        System.out.println("packets = "+packets);
        System.out.println("___________________");

        byte[] deviceData = new byte[64*packets];
        for(int i=0;i<packets;i++){

            USBHelper.write(usbDeviceHandle,commandRead,OUT_END_POINT,10000);

            //читаем
            data = USBHelper.read(usbDeviceHandle, 64, IN_END_POINT, 10000);
            data.position(0);

            data.get(deviceData,i*64,64);

        }
        System.out.println(ByteHelper.bytesToHex(deviceData,16,' '));
        System.out.print("Parse data...");
        try {
            BiofonBinaryFile biofonBinaryFile = new BiofonBinaryFile(deviceData);
            System.out.println("OK");
            //System.out.println(biofonBinaryFile.toString());

            System.out.println(ByteHelper.bytesToHex(biofonBinaryFile.getData(),16,' '));
            System.out.println(biofonBinaryFile.toString());

        } catch (BiofonBinaryFile.FileParseException e) {
            System.out.println("FAIL");
            e.printStackTrace();
        } catch (BiofonBinaryFile.MaxBytesBoundException e) {
            System.out.println("FAIL");
            System.out.println("Слишком большой файл");
            e.printStackTrace();
        } catch (BiofonComplex.ZeroCountProgramBoundException e) {
            System.out.println("FAIL");
            System.out.println("В каких-то комплексах нет программ");
            e.printStackTrace();
        }


        USBHelper.closeDevice(usbDeviceHandle,0);
        USBHelper.closeContext();

    }





   public static void parseDeviceData(byte[] data,int byteSize){


   }
}

 /* При порядке от старшего к младшему преобразуется так:

    int i = ((byte_array[0] & 0xFF) << 24) + ((byte_array[1] & 0xFF) << 16) + ((byte_array[2] & 0xFF) << 8) + (byte_array[3] & 0xFF);



    При порядке от младшего к старшему индексы массива меняются в обратном направлении - от 3 к 0.


    public static String byteArrayToString(byte[] buf)
{
    try
    {
        return new String(buf, ENCODING);
    }
    catch (UnsupportedEncodingException ex)
    {
        return null;
    }
}
*/

