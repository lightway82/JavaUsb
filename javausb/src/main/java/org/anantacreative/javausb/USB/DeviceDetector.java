package org.anantacreative.javausb.USB;

import org.hid4java.*;
import org.hid4java.event.HidServicesEvent;

public class DeviceDetector implements IDeviceDetect {

    private HidServicesListener listener = new HIDListener();
    HidServicesSpecification hidServicesSpecification;
    private HidServices hidServices;

    public DeviceDetector() {
        HidServicesSpecification hidServicesSpecification;
        hidServicesSpecification = new HidServicesSpecification();
        hidServicesSpecification.setAutoShutdown(true);
        hidServicesSpecification.setScanInterval(500);
        hidServicesSpecification.setPauseInterval(5000);
        hidServicesSpecification.setScanMode(ScanMode.SCAN_AT_FIXED_INTERVAL_WITH_PAUSE_AFTER_WRITE);


    }

    @Override
    public synchronized void startDeviceDetecting() {

        hidServices.start();
    }

    @Override
    public synchronized void stopDeviceDetecting() {
        hidServices.removeUsbServicesListener(listener);
        hidServices.shutdown();
        hidServices =null;
    }


    private static class HIDListener implements HidServicesListener{
        @Override
        public void hidDeviceAttached(HidServicesEvent hidServicesEvent) {

        }

        @Override
        public void hidDeviceDetached(HidServicesEvent hidServicesEvent) {

        }

        @Override
        public void hidFailure(HidServicesEvent hidServicesEvent) {

        }
    }





}
