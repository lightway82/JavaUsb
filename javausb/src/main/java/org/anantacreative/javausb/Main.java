package org.anantacreative.javausb;

import org.anantacreative.javausb.Biofon.Biofon;
import org.anantacreative.javausb.USB.PlugDeviceListener;
import org.anantacreative.javausb.USB.USBHelper;
import org.hid4java.HidDevice;

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
/*

                try {

                    Biofon.writeToDevice(Test.testData(),true);
                    Thread.sleep(1000);

                        BiofonBinaryFile biofonBinaryFile = Biofon.readFromDevice(true);
                        System.out.println(biofonBinaryFile);

                } catch (BiofonBinaryFile.MaxBytesBoundException e) {
                    e.printStackTrace();
                } catch (BiofonComplex.ZeroCountProgramBoundException e) {
                    e.printStackTrace();
                } catch (Biofon.WriteToDeviceException e) {
                    e.printStackTrace();
                } catch (BiofonComplex.MaxTimeByFreqBoundException e) {
                    e.printStackTrace();
                } catch (BiofonComplex.MaxPauseBoundException e) {
                    e.printStackTrace();
                } catch (BiofonProgram.MaxProgramIDValueBoundException e) {
                    e.printStackTrace();
                } catch (BiofonProgram.MaxFrequenciesBoundException e) {
                    e.printStackTrace();
                } catch (BiofonProgram.MinFrequenciesBoundException e) {
                    e.printStackTrace();
                } catch (BiofonComplex.MaxCountProgramBoundException e) {
                    e.printStackTrace();
                } catch (Biofon.ReadFromDeviceException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                */
                try {
                    for(int i=0;i<100; i++){
                        Thread.sleep(1000);
                        System.out.println(i);
                        HidDevice device = USBHelper.findDevice(Biofon.vendorId, Biofon.productId);
                        if(device !=null) {
                            USBHelper.openDevice(device);
                            device.close();
                        }else throw new RuntimeException();
                    }
                } catch (USBHelper.USBException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDetachDevice() {
                System.out.println("Устройство отсобачили");
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

