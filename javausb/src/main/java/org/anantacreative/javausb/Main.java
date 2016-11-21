package org.anantacreative.javausb;

import org.usb4java.*;

import java.io.IOException;

/**
 * Created by anama on 20.10.16.
 */
public class Main {

    private static short vendorId=(short)0xfc58;
    private static short productId=(short)0x0001;





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
        */

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



    }


}



