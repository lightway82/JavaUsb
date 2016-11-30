package org.anantacreative.javausb;

import org.usb4java.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Обязательна инициализация initContext()
 * Если был вызван closeContext(), то требуется переинициализация
 *
 * Created by anama on 21.11.16.
 */
public class USBHelper{

    private static  Context context=null;
    private static List<PlugListenerContainer> plugDeviceListenerList=new ArrayList<>();

   public static void initContext()
   {
       if(context!=null) return;
        // Create the libusb context
         context = new Context();

        // Initialize the libusb context
        int result = LibUsb.init(context);
        if (result < 0)
        {
            throw new LibUsbException("Unable to initialize libusb", result);
        }
    }

  public void addDevice(int pid,int vid){

  }


    public static Device findDevice(short vendorId, short productId)
    {
        // Read the USB device list
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(null, list);
        if (result < 0) throw new LibUsbException("Unable to get device list", result);

        try
        {
            // Iterate over all devices and scan for the right one
            for (Device device: list)
            {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to read device descriptor", result);
                if (descriptor.idVendor() == vendorId && descriptor.idProduct() == productId) return device;
            }
        }
        finally
        {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        // Device not found
        return null;
    }

    /**
     * Dumps the specified device to stdout.
     *
     * @param device
     *            The device to dump.
     */
    public static void dumpDevice(final Device device)
    {
        // Dump device address and bus number
        final int address = LibUsb.getDeviceAddress(device);
        final int busNumber = LibUsb.getBusNumber(device);
        System.out.println(String
                .format("Device %03d/%03d", busNumber, address));

        // Dump port number if available
        final int portNumber = LibUsb.getPortNumber(device);
        if (portNumber != 0)
            System.out.println("Connected to port: " + portNumber);

        // Dump parent device if available
        final Device parent = LibUsb.getParent(device);
        if (parent != null)
        {
            final int parentAddress = LibUsb.getDeviceAddress(parent);
            final int parentBusNumber = LibUsb.getBusNumber(parent);
            System.out.println(String.format("Parent: %03d/%03d",
                    parentBusNumber, parentAddress));
        }

        // Dump the device speed
        System.out.println("Speed: "
                + DescriptorUtils.getSpeedName(LibUsb.getDeviceSpeed(device)));

        // Read the device descriptor
        final DeviceDescriptor descriptor = new DeviceDescriptor();
        int result = LibUsb.getDeviceDescriptor(device, descriptor);
        if (result < 0)
        {
            throw new LibUsbException("Unable to read device descriptor",
                    result);
        }

        // Try to open the device. This may fail because user has no
        // permission to communicate with the device. This is not
        // important for the dumps, we are just not able to resolve string
        // descriptor numbers to strings in the descriptor dumps.
        DeviceHandle handle = new DeviceHandle();
        result = LibUsb.open(device, handle);
        if (result < 0)
        {
            System.out.println(String.format("Unable to open device: %s. "
                            + "Continuing without device handle.",
                    LibUsb.strError(result)));
            handle = null;
        }

        // Dump the device descriptor
        System.out.print(descriptor.dump(handle));

        // Dump all configuration descriptors
        dumpConfigurationDescriptors(device, descriptor.bNumConfigurations());

        // Close the device if it was opened
        if (handle != null)
        {
            LibUsb.close(handle);
        }
    }

    /**
     * Dumps all configuration descriptors of the specified device. Because
     * libusb descriptors are connected to each other (Configuration descriptor
     * references interface descriptors which reference endpoint descriptors)
     * dumping a configuration descriptor also dumps all interface and endpoint
     * descriptors in this configuration.
     *
     * @param device
     *            The USB device.
     * @param numConfigurations
     *            The number of configurations to dump (Read from the device
     *            descriptor)
     */
    private static void dumpConfigurationDescriptors(final Device device,
                                                    final int numConfigurations)
    {
        for (byte i = 0; i < numConfigurations; i += 1)
        {
            final ConfigDescriptor descriptor = new ConfigDescriptor();
            final int result = LibUsb.getConfigDescriptor(device, i, descriptor);
            if (result < 0)
            {
                throw new LibUsbException("Unable to read config descriptor",
                        result);
            }
            try
            {
                System.out.println(descriptor.dump().replaceAll("(?m)^",
                        "  "));
            }
            finally
            {
                // Ensure that the config descriptor is freed
                LibUsb.freeConfigDescriptor(descriptor);
            }
        }
    }


    /**
     * Добавит слушатель подключений, отключений устройств
     * @param el
     */
    public static void addPlugEventHandler(int pid, int vid, PlugDeviceListener el){

        plugDeviceListenerList.add(new PlugListenerContainer(pid,vid,el));
    }

    /**
     * Удалит слушатель подключений и отключений устройств
     * @param el
     */
    public static void removePlugEventHandler(PlugDeviceListener el){

        Iterator<PlugListenerContainer> iterator = plugDeviceListenerList.iterator();
        PlugListenerContainer next=null;
        while (iterator.hasNext()){
            next=iterator.next();
            if(next.getPlugDeviceListener()==el) {
                next.clear();
                iterator.remove();
            }
        }


    }
    private static EventHandlingThread thread;
    private static HotplugCallbackHandle callbackHandle;

    public synchronized static void startHotPlugListener(){

        if(thread!=null) return;
        if(callbackHandle!=null)return;
        thread = new EventHandlingThread();
        thread.start();

        // Register the hotplug callback
         callbackHandle = new HotplugCallbackHandle();
        int result = LibUsb.hotplugRegisterCallback(null,
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

    /**
     * Остановка слушателей событий устройств
     */
    public static synchronized void stopHotPlugListener()
    {

        // Unregister the hotplug callback and stop the event handling thread
        thread.abort();
        LibUsb.hotplugDeregisterCallback(null, callbackHandle);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            thread=null;
            callbackHandle=null;
        }


    }


static class USBDeviceHandle{
    private DeviceHandle handle;
    private boolean needDetach;

    public USBDeviceHandle(DeviceHandle handle, boolean needDetach) {
        this.handle = handle;
        this.needDetach = needDetach;
    }

    public DeviceHandle getHandle() {
        return handle;
    }

    public boolean isNeedDetach() {
        return needDetach;
    }
}



    /**
     * Открывает устройство для чтения и записи.
     * После завершения работы необходимо вызвать closeDevice(USBDeviceHandle handle, int interfaceNum)
     * @param pid
     * @param vid
     * @param interfaceNum номер интерфейса
     * @return
     */
    public static USBDeviceHandle openDevice(int pid, int vid, int interfaceNum){
        DeviceHandle handle = LibUsb.openDeviceWithVidPid(context, (short)vid,
                (short)pid);

        if (handle == null)
        {
            System.err.println("Test device not found.");
            System.exit(1);
        }

        boolean detach = LibUsb.hasCapability(LibUsb.CAP_SUPPORTS_DETACH_KERNEL_DRIVER)
                && LibUsb.kernelDriverActive(handle, interfaceNum)==1;

// Detach the kernel driver
        if (LibUsb.kernelDriverActive(handle, interfaceNum)==1)
        {
            int result = LibUsb.detachKernelDriver(handle,  interfaceNum);
            if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to detach kernel driver", result);
        }


        // Claim the ADB interface
        int result = LibUsb.claimInterface(handle, interfaceNum);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to claim interface", result);
        }
        USBDeviceHandle usbDeviceHandle=new USBDeviceHandle(handle,detach);

        return usbDeviceHandle;
    }

    /**
     * Закрывает устройство. Освобождает ресурсы
     * @param handle  USBDeviceHandle полученый при открытии устройства
     * @param interfaceNum  номер интерфейса, который открывался
     */
    public static void closeDevice(USBDeviceHandle handle, int interfaceNum){
        // Release the ADB interface
        int result = LibUsb.releaseInterface(handle.getHandle(), interfaceNum);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to release interface", result);
        }


        // Close the device
        LibUsb.close(handle.getHandle());

        if (handle.isNeedDetach())
        {
             result = LibUsb.attachKernelDriver(handle.getHandle(),  interfaceNum);
            if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to re-attach kernel driver", result);
        }
    }

    /**
     *
     * @param handle USBDeviceHandle получаемый при открытии устройства
     * @param data байтовый массив
     * @param outEndPoint адрес конечной точки
     * @param timeout
     */
    public static void write(USBDeviceHandle handle, byte[] data, byte outEndPoint, long timeout)
    {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(data.length);
        buffer.put(data);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(handle.getHandle(), outEndPoint, buffer, transferred, timeout);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to send data", result);
        }
        //System.out.println(transferred.get() + " bytes sent to device");
    }

    /**
     *
     * @param handle
     * @param size
     * @param inEndPoint
     * @param timeout
     * @return
     */
    public static ByteBuffer read(USBDeviceHandle handle, int size, byte inEndPoint, long timeout)
    {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(size);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(handle.getHandle(), inEndPoint, buffer,
                transferred, timeout);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to read data", result);
        }
        //System.out.println(transferred.get() + " bytes read from device");
        return buffer;
    }



    /**
     * Контейнер слушателей событий устройств
     */
    private static class PlugListenerContainer{
        private int pid;
        private int vid;
        private PlugDeviceListener pdl;

        public PlugListenerContainer(int pid, int vid, PlugDeviceListener pdl) {
            this.pid = pid;
            this.vid = vid;
            this.pdl = pdl;
        }

        public int getPid() {
            return pid;
        }

        public int getVid() {
            return vid;
        }


        public PlugDeviceListener getPlugDeviceListener() {
            return pdl;
        }

        /**
         * Очистка ресурсов контейнера
         */
        public void clear(){this.pdl=null;}

        /**
         * Проверка на соответствие контейнера VID и PID
         * @param pid
         * @param vid
         * @return
         */
        public boolean checkID(int pid, int vid){
            if (pid != this.pid) return false;
            return vid == this.vid;
        }



        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PlugListenerContainer that = (PlugListenerContainer) o;

            if (pid != that.pid) return false;
            return vid == that.vid;

        }

        @Override
        public int hashCode() {
            int result = pid;
            result = 31 * result + vid;
            return result;
        }
    }












    /**
     * Явное закрытие контекста. Деинициализация работы с USB, освобождение ресурсов.
     * После чего класс становиться не работоспособным
     */
   public static void closeContext(){

       LibUsb.exit(context);
       context=null;
   }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        LibUsb.exit(context);
        context=null;

    }

/*****************************   Классы для HotPlug обработчика  **********************************************/

    public interface PlugDeviceListener{

     void onAttachDevice(Device device);
     void onDetachDevice(Device device);
    }



     private static class EventHandlingThread extends Thread{
        /** If thread should abort. */
        private volatile boolean abort;

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
                int result = LibUsb.handleEventsTimeout(null, 1000000);
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

            for (PlugListenerContainer al : plugDeviceListenerList) {
                if(al.checkID(descriptor.idProduct(),descriptor.idVendor())){

                    if(event == LibUsb.HOTPLUG_EVENT_DEVICE_ARRIVED)al.getPlugDeviceListener().onAttachDevice(device);
                    else al.getPlugDeviceListener().onDetachDevice(device);
                }
            }




            return 0;
        }
    }
}
