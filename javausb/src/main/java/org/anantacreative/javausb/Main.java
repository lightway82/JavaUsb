package org.anantacreative.javausb;

import org.anantacreative.javausb.Biofon.Biofon;
import org.anantacreative.javausb.Biofon.BiofonBinaryFile;
import org.anantacreative.javausb.USB.PlugDeviceListener;
import org.anantacreative.javausb.USB.USBHelper;

import java.io.IOException;



public class Main {


    public static void main(final String[] args) {
        try {
            USBHelper.initContext();
        } catch (USBHelper.USBException e) {
           throw new RuntimeException(e);
        }

        USBHelper.addPlugEventHandler(Biofon.productId, Biofon.vendorId, new PlugDeviceListener() {
            @Override
            public void onAttachDevice() {
                System.out.println("Устройство присобачили");
                try {
                    BiofonBinaryFile biofonBinaryFile = Biofon.readFromDevice(true);
                    System.out.println(biofonBinaryFile);
                } catch (Biofon.ReadFromDeviceException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDetachDevice() {
                System.out.println("Устройство отсобачили");
            }
        });

        USBHelper.startHotPlugListener(2);

        try {
            System.in.read();
            System.out.println("Выкл");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            USBHelper.stopHotPlugListener();

            USBHelper.closeContext();

        }


    }

}

