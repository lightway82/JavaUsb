package org.anantacreative.javausb.USB;


/**
 * Интерфейс для реализации слушателей конкретных устройств
 */
public interface PlugDeviceListener{

    void onAttachDevice();
    void onDetachDevice();
}
