package org.anantacreative.javausb.USB;

import org.usb4java.*;

/**
 * Created by Ananta on 02.12.2016.
 */
public class HotPlugDeviceDetect implements IDeviceDetect {

    private  EventHandlingThread thread;
    private  HotplugCallbackHandle callbackHandle;
    private int periodDetection;

    /**
     * Период между детектированием секунд.
     * @param periodDetection
     */
    public HotPlugDeviceDetect(int periodDetection) {
        this.periodDetection = periodDetection;
    }

    @Override
    public  void startDeviceDetecting(){
        if(thread!=null) return;
        if(callbackHandle!=null)return;
        thread = new EventHandlingThread(periodDetection);
        thread.start();


            // Register the hotplug callback
            callbackHandle = new HotplugCallbackHandle();
            int result = LibUsb.hotplugRegisterCallback(USBHelper.getContext(),
                    LibUsb.HOTPLUG_EVENT_DEVICE_ARRIVED
                            | LibUsb.HOTPLUG_EVENT_DEVICE_LEFT,
                    LibUsb.HOTPLUG_ENUMERATE,
                    LibUsb.HOTPLUG_MATCH_ANY,
                    LibUsb.HOTPLUG_MATCH_ANY,
                    LibUsb.HOTPLUG_MATCH_ANY,
                    new Callback(), null, callbackHandle);
            if (result != LibUsb.SUCCESS)
            {
                throw new LibUsbException("Unable to register hotplug callback",
                        result);
            }

    }


    @Override
 public  void stopDeviceDetecting(){
     // Unregister the hotplug callback and stop the event handling thread
     thread.abort();
     LibUsb.hotplugDeregisterCallback(USBHelper.getContext(), callbackHandle);
     try {
         thread.join();
     } catch (InterruptedException e) {
         e.printStackTrace();
     }finally {
         thread=null;
         callbackHandle=null;
     }
 }

    private static class EventHandlingThread extends Thread{
        /** If thread should abort. */
        private volatile boolean abort;

        private int periodDetection;



        public EventHandlingThread(int periodDetection) {
            super();
            this.periodDetection = periodDetection;
        }

        /**
         * Aborts the event handling thread.
         */
        public void abort()
        {
            this.abort = true;
        }

        @Override
        public void run()
        {
            while (!this.abort)
            {
                // Let libusb handle pending events. This blocks until events
                // have been handled, a hotplug callback has been deregistered
                // or the specified time of 1 second (Specified in
                // Microseconds) has passed.
                int result = LibUsb.handleEventsTimeout(USBHelper.getContext(), periodDetection *1000000);
                if (result != LibUsb.SUCCESS)
                    throw new LibUsbException("Unable to handle events", result);
            }
        }
    }


    private static class Callback implements HotplugCallback
    {
        @Override
        public int processEvent(Context context, Device device, int event,Object userData) {

            //пройдемся по списку обработчиков, вызовем если PID и VID совпали
            DeviceDescriptor descriptor = new DeviceDescriptor();

            int result = LibUsb.getDeviceDescriptor(device, descriptor);
            if (result != LibUsb.SUCCESS)  throw new LibUsbException("Unable to read device descriptor",result);

            for (USBHelper.PlugListenerContainer al : USBHelper.getPlugDeviceListenerList()) {
                if(al.checkID(descriptor.idProduct(),descriptor.idVendor())){

                    if(event == LibUsb.HOTPLUG_EVENT_DEVICE_ARRIVED){
                       al.setConnected(true);
                        al.getPlugDeviceListener().onAttachDevice();

                    }
                    else {
                        al.setConnected(false);
                        al.getPlugDeviceListener().onDetachDevice();
                    }
                }
            }

            return 0;
        }
    }
}
