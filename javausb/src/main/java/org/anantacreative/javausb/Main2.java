package org.anantacreative.javausb;

import org.anantacreative.javausb.USB.PlugDeviceListener;
import org.anantacreative.javausb.USB.USBHelper;
import org.anantacreative.javausb.m2.*;
import org.hid4java.HidDevice;

import java.io.IOException;

public class Main2 {


    public static void main(final String[] args) {
       // Test test=new Test();
        //test.test();


        try {
            USBHelper.initContext();
        } catch (USBHelper.USBException e) {
            throw new RuntimeException(e);
        }

        USBHelper.addPlugEventHandler(M2.productId, M2.vendorId, new PlugDeviceListener() {
            @Override
            public void onAttachDevice(HidDevice device) {
                System.out.println("Device connected");
/*
                Test test=new Test();
                try {
                    M2.clearDevice(true);
                    //M2.writeToDevice(test.testData(),1,true) ;
                    System.out.println("DDDDD");
                } catch (M2.WriteToDeviceException e) {
                    e.printStackTrace();
                }
                */
                    try {
                    USBHelper.dumpDevice(device);
                    System.out.println("Device read name: " + M2.readDeviceName(true));
                } catch (USBHelper.USBException e) {
                    e.printStackTrace();
                } catch (M2.WriteToDeviceException e) {
                    e.printStackTrace();
                }
/*
                try {


                    System.out.println("Device read name: " + M2.readDeviceName(true));

                    System.out.println("Device read data: ");
                     M2BinaryFile biofonBinaryFile = M2.readFromDevice(true);
                     biofonBinaryFile = M2.readFromDevice(true);
                     biofonBinaryFile = M2.readFromDevice(true);
                    if (biofonBinaryFile.getRawReadedData() != null) System.out.println(biofonBinaryFile);

                    System.out.println("");


                } catch (M2.WriteToDeviceException e) {
                    e.printStackTrace();
                } catch (M2.ReadFromDeviceException e) {
                    e.printStackTrace();
                }catch (Exception e){e.printStackTrace();}



                try {
        for(int i=0;i<100; i++){
            System.out.println(i);
            HidDevice device = USBHelper.findDevice(M2.vendorId, M2.productId);
            if(device !=null) {
                USBHelper.openDevice(device);
                device.close();
            }else throw new RuntimeException();
        }
                } catch (USBHelper.USBException e) {
                    e.printStackTrace();
                }


                try {
                    HidDevice device = USBHelper.findDevice(M2.vendorId, M2.productId);

                    if(device ==null) System.out.println("1");
                    else {
                        USBHelper.openDevice(device);
                        device.close();
                    }
                    device = USBHelper.findDevice(M2.vendorId, M2.productId);
                    if(device ==null) System.out.println("2");
                    else {
                        USBHelper.openDevice(device);
                        device.close();
                    }
                    device = USBHelper.findDevice(M2.vendorId, M2.productId);
                    if(device ==null) System.out.println("3");
                    else {
                        USBHelper.openDevice(device);
                        device.close();
                    }
                } catch (USBHelper.USBException e) {
                    e.printStackTrace();
                }


                try {
                    USBHelper.dumpDevice(USBHelper.findDevice(M2.vendorId, M2.productId));
                } catch (USBHelper.USBException e) {
                    e.printStackTrace();
                }
                */
            }

            @Override
            public void onDetachDevice(HidDevice device) {
                System.out.println("Device disconnected");
            }

            @Override
            public void onFailure(USBHelper.USBException e) {
               e.printStackTrace();
            }
        });

        try {
            USBHelper.startHotPlugListener();
        } catch (USBHelper.USBException e) {
          throw new RuntimeException(e);
        }

        try {
            System.in.read();
            System.out.println("Выкл");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                USBHelper.stopHotPlugListener();
            } catch (USBHelper.USBException e) {
                throw new RuntimeException(e);
            }finally {
                USBHelper.closeContext();
            }
        }

    }

}

