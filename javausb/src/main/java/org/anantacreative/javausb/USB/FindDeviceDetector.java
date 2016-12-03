package org.anantacreative.javausb.USB;

import org.usb4java.Device;

/**
 * Created by Ananta on 02.12.2016.
 */
public class FindDeviceDetector implements IDeviceDetect {

    private int periodDetection;
    private EventHandlingThread thread;
    /**
     * Период между детектированием секунд.
     * @param periodDetection
     */
    public FindDeviceDetector(int periodDetection) {
        this.periodDetection=periodDetection;
    }

    @Override
    public void startDeviceDetecting() {
        if(thread!=null) return;

        thread = new EventHandlingThread(periodDetection);
        thread.start();
    }

    @Override
    public void stopDeviceDetecting() {

        thread.abort();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            thread=null;

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
            Device device=null;
            while (!this.abort)
            {

                for (USBHelper.PlugListenerContainer al : USBHelper.getPlugDeviceListenerList()) {

                    try {
                        device = USBHelper.findDevice((short) al.getVid(), (short) al.getPid());
                    } catch (USBHelper.USBException e) {
                        System.out.println("Ошибка поиска устройства с vid="+(al.getVid()+" pid="+ (short) al.getPid()));
                        e.printStackTrace();
                        abort();

                    }

                    if(device!=null){

                           if(!al.isConnected()){

                               al.getPlugDeviceListener().onAttachDevice();
                               al.setConnected(true);
                           }


                       }
                        else {

                           if(al.isConnected()) {
                               al.getPlugDeviceListener().onDetachDevice();
                               al.setConnected(false);
                           }


                       }

                }

                try {
                    Thread.sleep(periodDetection*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    abort=true;
                }

            }
        }
    }



}
