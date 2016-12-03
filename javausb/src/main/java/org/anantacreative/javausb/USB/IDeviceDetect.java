package org.anantacreative.javausb.USB;


public interface IDeviceDetect {
    /**
     * Запускает поток детектирования устройст
     */
     void startDeviceDetecting();

    /**
     * Останавливает поток детектирования устройств
     */
    void stopDeviceDetecting();
}
