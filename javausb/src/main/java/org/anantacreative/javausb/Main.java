package org.anantacreative.javausb;

import org.anantacreative.javausb.Biofon.Biofon;
import org.anantacreative.javausb.Biofon.BiofonBinaryFile;
import org.anantacreative.javausb.USB.USBHelper;


/**
 * Created by anama on 20.10.16.
 */
public class Main {

    /**
     * Main method.
     *
     * @param args
     *            Command-line arguments (Ignored)
     */
    public static void main(final String[] args)
    {
        USBHelper.initContext();

        try {
            BiofonBinaryFile biofonBinaryFile = Biofon.readFromDevice(true);
            System.out.println(biofonBinaryFile);
        } catch (Biofon.ReadFromDeviceException e) {
            e.printStackTrace();
        }

        USBHelper.closeContext();

    }

}

