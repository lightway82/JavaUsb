package org.anantacreative.javausb;

import org.anantacreative.javausb.USB.PlugDeviceListener;
import org.anantacreative.javausb.USB.USBHelper;
import org.anantacreative.javausb.m2.*;

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
            public void onAttachDevice() {
                System.out.println("Device connected");
                try {
                    M2BinaryFile biofonBinaryFile = M2.readFromDevice(true);
                   // System.out.println(biofonBinaryFile);
                    Test test=new Test();


                    M2.writeToDevice(test.testData(),1);

                    System.out.println("COMPLETED!");

                } catch (M2.ReadFromDeviceException e) {
                    e.printStackTrace();
                } catch (M2Program.ZeroValueFreqException e) {
                    e.printStackTrace();
                } catch (M2Complex.MaxPauseBoundException e) {
                    e.printStackTrace();
                } catch (M2BinaryFile.MaxBytesBoundException e) {
                    e.printStackTrace();
                } catch (M2Program.MaxProgramIDValueBoundException e) {
                    e.printStackTrace();
                } catch (M2Complex.MaxCountProgramBoundException e) {
                    e.printStackTrace();
                } catch (M2Program.MinFrequenciesBoundException e) {
                    e.printStackTrace();
                } catch (LanguageDevice.NoLangDeviceSupported noLangDeviceSupported) {
                    noLangDeviceSupported.printStackTrace();
                } catch (M2Complex.ZeroCountProgramBoundException e) {
                    e.printStackTrace();
                } catch (M2Complex.MaxTimeByFreqBoundException e) {
                    e.printStackTrace();
                } catch (M2.WriteToDeviceException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDetachDevice() {
                System.out.println("Device disconnected");
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

