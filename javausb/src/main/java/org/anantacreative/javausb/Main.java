package org.anantacreative.javausb;

import org.usb4java.*;

import java.io.IOException;
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
        byte[] bytes = new byte[64];
        bytes[0]=0x0;
        bytes[0]=0x34;

        USBHelper.write(usbDeviceHandle,bytes,OUT_END_POINT,10000);
        ByteBuffer data = USBHelper.read(usbDeviceHandle, 64, IN_END_POINT, 10000);
        data.position(0);
        byte[] bytes2 = new byte[64];

        data.get(bytes2);
        System.out.println(bytesToHex(bytes2));
        data.position(1);
        System.out.println("Size = "+(int)data.getChar());

        USBHelper.closeDevice(usbDeviceHandle,0);


        USBHelper.closeContext();

    }
   /* fenfz, при порядке от старшего к младшему преобразуется так:

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

ENCODING="UTF-8" - для Android или же можно просто вызывать new String(buf) - тогда будет применяться кодировка по умолчанию.

Update

Если требуется записать байты строкой, то поможет такой методочек:

public static String bytesToHex(byte[] array)
{
    char[] val = new char[2*array.length];
    String hex = "0123456789ABCDEF";
    for (int i = 0; i < array.length; i++)
    {
        int b = array[i] & 0xff;
        val[2*i] = hex.charAt(b >>> 4);
        val[2*i + 1] = hex.charAt(b & 15);
    }
    return String.valueOf(val);
}

*/
   public static String bytesToHex(byte[] array)
   {
       char[] val = new char[2*array.length];
       String hex = "0123456789ABCDEF";
       for (int i = 0; i < array.length; i++)
       {
           int b = array[i] & 0xff;
           val[2*i] = hex.charAt(b >>> 4);
           val[2*i + 1] = hex.charAt(b & 15);
       }
       return String.valueOf(val);
   }
}



