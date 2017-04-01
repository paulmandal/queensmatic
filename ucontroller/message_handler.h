/**
 * Message Handler Header
 */
#ifndef Message_Handler_h
#define Message_Handler_h

#include "Arduino.h"
#include "led_controller.h"
#include "hardware_controller.h"

// Message buffer size
extern const int MSG_BUFFER_SIZE;

class MessageHandler
{
  public:
    MessageHandler();
    void begin(LedController ledController, HardwareController hardwareController);
    void checkMessages();
  private:
    // Message buffer
    char *_msgBuf;
    // Last read byte from serial
    char _readByte;
    int _bufferPos;
    LedController *_ledController;
    HardwareController *_hardwareController;
    void _reallocateMemory();
    boolean _processCommand(char *command, int commandLength);
    void _sendStatusUpdate();
    void _outputLedState(int count);
};

#endif

