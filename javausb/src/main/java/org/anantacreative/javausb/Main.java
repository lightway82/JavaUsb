package org.anantacreative.javausb;

import javax.usb.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anama on 20.10.16.
 */
public class Main {

    public static void main(String[] args) {
        try {
           // allDevices(getRootUSBHub());

            allUsbHub(getRootUSBHub()).forEach(usbHub -> {
                UsbDeviceDescriptor desc = usbHub.getUsbDeviceDescriptor();
                System.out.println(desc.idVendor());

            });
        } catch (UsbException e) {
            e.printStackTrace();
        }
    }




    public static UsbHub getRootUSBHub() throws UsbException {
       return  UsbHostManager.getUsbServices().getRootUsbHub();
    }

    public static  void allDevices(UsbHub hub) throws UsbException {

        List<UsbDevice> attachedUsbDevices = hub.getAttachedUsbDevices();

        for (UsbDevice usbDevice : attachedUsbDevices) {

            if (usbDevice.isUsbHub())
            {
                allDevices((UsbHub) usbDevice);
            }

            UsbDeviceDescriptor desc = usbDevice.getUsbDeviceDescriptor();
            System.out.println(desc.idVendor());
            System.out.println(desc.iManufacturer());
            System.out.println(desc.idProduct());

        }
    }



    public static  List<UsbHub> allUsbHub(UsbHub rootHub) throws UsbException {

        List<UsbHub> res=new ArrayList<>();

        List<UsbDevice> attachedUsbDevices = rootHub.getAttachedUsbDevices();

        for (UsbDevice usbDevice : attachedUsbDevices) {

            if (usbDevice.isUsbHub())
            {
                res.add((UsbHub)usbDevice);
                allUsbHub((UsbHub) usbDevice);
            }

        }


return res;



    }

    public static UsbDevice findDevice(UsbHub hub, short vendorId, short productId)
    {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices())
        {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            if (desc.idVendor() == vendorId && desc.idProduct() == productId) return device;
            if (device.isUsbHub())
            {
                device = findDevice((UsbHub) device, vendorId, productId);
                if (device != null) return device;
            }
        }
        return null;
    }
}



