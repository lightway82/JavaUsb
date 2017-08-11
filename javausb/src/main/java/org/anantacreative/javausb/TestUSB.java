package org.anantacreative.javausb;


import org.anantacreative.javausb.USB.USBHelper;
import org.hid4java.*;
import org.hid4java.event.HidServicesEvent;

import java.io.IOException;

/**
 * <p>Demonstrate the USB HID interface using a production Bitcoin Trezor</p>
 *
 * @since 0.0.1
 *
 */
public class TestUSB implements HidServicesListener {


    private static final short VENDOR_ID = (short)0xFC82;
    private static final short PRODUCT_ID = (short)0x0001;
    private static final int PACKET_LENGTH = 64;
    public static final String SERIAL_NUMBER = null;

    public static void main(String[] args) throws HidException {

        TestUSB example = new TestUSB();
        example.executeExample();


    }

    public void executeExample() throws HidException {

        // Configure to use custom specification
        HidServicesSpecification hidServicesSpecification = new HidServicesSpecification();
        hidServicesSpecification.setAutoShutdown(false);
        hidServicesSpecification.setScanInterval(500);
        hidServicesSpecification.setPauseInterval(5000);
        hidServicesSpecification.setScanMode(ScanMode.SCAN_AT_FIXED_INTERVAL_WITH_PAUSE_AFTER_WRITE);

        // Get HID services using custom specification
        HidServices hidServices = HidManager.getHidServices(hidServicesSpecification);
        hidServices.addHidServicesListener(this);

        // Start the services
        System.out.println("Starting HID services.");
        hidServices.start();

        System.out.println("Enumerating attached devices...");

        // Provide a list of attached devices
        for (HidDevice hidDevice : hidServices.getAttachedHidDevices()) {
            System.out.println(hidDevice);
        }

        try {
            System.in.read();
            System.out.println("Выкл");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            hidServices.shutdown();
        }

    }

    @Override
    public void hidDeviceAttached(HidServicesEvent event) {

        System.out.println("Device attached: " + event);

        // Add serial number when more than one device with the same
        // vendor ID and product ID will be present at the same time
        if (event.getHidDevice().isVidPidSerial(VENDOR_ID, PRODUCT_ID, null)) {
            sendMessage(event.getHidDevice());
        }

    }

    @Override
    public void hidDeviceDetached(HidServicesEvent event) {

        System.err.println("Device detached: " + event);

    }

    @Override
    public void hidFailure(HidServicesEvent event) {

        System.err.println("HID failure: " + event);

    }

    private void sendMessage(HidDevice hidDevice) {
        // Ensure device is open after an attach/detach event
        if (!hidDevice.isOpen()) {
            if(!hidDevice.open()) throw new RuntimeException();

             byte[] commandWrite = new byte[64];
             commandWrite[0]=50;
            int val = hidDevice.write(commandWrite, commandWrite.length, (byte) 0x00);
             System.out.println("Num  bytes: " + val);
            if (val < 0)  throw new RuntimeException();
            byte[] bytes = new byte[65];
            int read = hidDevice.read(bytes, 10000);
                System.out.println("Num  bytes: " + read);
            if (val < 0)  throw new RuntimeException();



        }

    }


}
