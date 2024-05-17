package ru.sir.ymodem;

import android.util.Log;

import com.rockchip.gpadc.demo.utils.SerialPortUtil;
import com.van.uart.LastError;
import com.van.uart.UartManager;

import java.io.DataInputStream;
import java.io.IOException;


/**
 * This is core Modem class supporting XModem (and some extensions XModem-1K, XModem-CRC), and YModem.<br/>
 * YModem support is limited (currently block 0 is ignored).<br/>
 * <br/>
 * Created by Anton Sirotinkin (aesirot@mail.ru), Moscow 2014 <br/>
 * I hope you will find this program useful.<br/>
 * You are free to use/modify the code for any purpose, but please leave a reference to me.<br/>
 */
class Modem {
    private UartManager mUartManager;
    /* Protocol characters used */
    protected static final byte SOH = 0x01; /* Start Of Header */
    protected static final byte STX = 0x02; /* Start Of Text (used like SOH but means 1024 block size) */
    protected static final byte EOT = 0x04; /* End Of Transmission */
    protected static final byte ACK = 0x06; /* ACKnowlege */
    protected static final byte NAK = 0x15; /* Negative AcKnowlege */
    protected static final byte CAN = 0x18; /* CANcel character */

    protected static final byte CPMEOF = 0x1A;
    protected static final byte ST_C = 'C';

    protected static final int MAXERRORS = 10;

    protected static final int BLOCK_TIMEOUT = 1000;
    protected static final int REQUEST_TIMEOUT = 3000;
    protected static final int WAIT_FOR_RECEIVER_TIMEOUT = 60_000;
    protected static final int SEND_BLOCK_TIMEOUT = 10_000;

    protected Modem(UartManager manager) {
        mUartManager = manager;
    }
    protected Modem() {
    }


    /**
     * Wait for receiver request for transmission
     *
     * @param timer
     * @return TRUE if receiver requested CRC-16 checksum, FALSE if 8bit checksum
     * @throws IOException
     */
    protected boolean waitReceiverRequest(Timer timer, SerialPortUtil serialPortUtil) throws IOException {
        Log.e("固件升级---","准备正式发数据了");
        int character;
        while (true) {
            try {
                character = readByte(timer,serialPortUtil);
                if (character == NAK)
                    return false;
                if (character == ST_C) {
                    return true;
                }
            } catch (TimeoutException e) {
                Log.e("固件升级---","超时");
                throw new IOException("固件升级---超时");
            }
        }
    }

    protected void sendDataBlocks(DataInputStream dataStream, int blockNumber, CRC crc, byte[] block,SerialPortUtil serialPortUtil) throws IOException {
        Log.e("固件升级---","正式开始传数据");
        int dataLength;
        int count=0;
        while ((dataLength = dataStream.read(block)) != -1) {
            Log.e("固件升级---","写了："+dataLength+"次数"+count);
            count++;
            sendBlock(blockNumber++, block, dataLength, crc,serialPortUtil,false);
            block = new byte[1024];
        }
        Log.e("固件升级---","数据传输完成");
    }

    protected void sendEOT(SerialPortUtil serialPortUtil) throws IOException {
        int errorCount = 0;
        Timer timer = new Timer(BLOCK_TIMEOUT);
        int character;
        while (errorCount < 10) {
            byte[] end={EOT};
            byte[] bytes = byteMerger("+HMB:".getBytes(), end);
            serialPortUtil.sendDate(bytes);
            serialPortUtil.sendDate("\r\n".getBytes());
            try {
                character = readByte(timer.start(),serialPortUtil);
                if (character == ACK) {
//                    PostEventBus.post("pro_" + (Constants.sCurrentPro++));
                    return;
                } else if (character == CAN) {
                    Log.e("固件升级---","传播中断");
                    throw new IOException("固件升级---传播中断");
                }
            } catch (TimeoutException ignored) {
            }
            errorCount++;
        }
    }

    protected void sendBlock(int blockNumber, byte[] block, int dataLength, CRC crc, SerialPortUtil serialPortUtil,boolean isEnd) throws IOException {
        int errorCount;
        int character;
        Timer timer = new Timer(SEND_BLOCK_TIMEOUT);

        if (dataLength < block.length) {
            block[dataLength] = CPMEOF;
        }
        errorCount = 0;

        while (errorCount < MAXERRORS) {
            timer.start();

            if (block.length == 1024){
                byte[] bytes = byteMerger("+HMB:".getBytes(), new byte[]{STX});
                serialPortUtil.sendDate(bytes);

            } else{ //128
                if(isEnd){
                    byte[] bytes = byteMerger("+HMB:".getBytes(), new byte[]{SOH,00, (byte) ~blockNumber});
                    serialPortUtil.sendDate(bytes);
                }else{
                    byte[] bytes = byteMerger("+HMB:".getBytes(), new byte[]{SOH});
                    serialPortUtil.sendDate(bytes);
                }
            }
            if(!isEnd){
                serialPortUtil.sendDate(new byte[]{(byte) blockNumber});
                serialPortUtil.sendDate(new byte[]{((byte) ~blockNumber)});
            }
            serialPortUtil.sendDate(block);
            writeCRC(block, crc,serialPortUtil);
            while (true) {
                try {
                    character = readByte(timer,serialPortUtil);
                    if (character == ACK) {
//                        PostEventBus.post("pro_" + (Constants.sCurrentPro++));
                        return;
                    } else if (character == NAK) {
                        errorCount++;
                        break;
                    } else if (character == CAN) {
                        Log.e("固件升级---","传播中断");
                        throw new IOException("固件升级---传播中断");
                    }
                } catch (TimeoutException e) {
                    errorCount++;
                    break;
                }
            }

        }
        Log.e("固件升级","报错次数太多不传了");
        throw new IOException("报错次数太多不传了");
    }

    private void writeCRC(byte[] block, CRC crc,SerialPortUtil serialPortUtil) throws IOException {
        byte[] crcBytes = new byte[crc.getCRCLength()];
        long crcValue = crc.calcCRC(block);
        for (int i = 0; i < crc.getCRCLength(); i++) {
            crcBytes[crc.getCRCLength() - i - 1] = (byte) ((crcValue >> (8 * i)) & 0xFF);
        }
        serialPortUtil.sendDate(crcBytes);
        serialPortUtil.sendDate("\r\n".getBytes());
    }


    protected void sendByte(byte b) throws IOException {
        write(new byte[]{b});
    }

    private void shortSleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            try {
                interruptTransmission();
            } catch (IOException ignore) {
            }
            throw new RuntimeException("Transmission was interrupted", e);
        }
    }

    /**
     * send CAN to interrupt seance
     *
     * @throws IOException
     */
    protected void interruptTransmission() throws IOException {
        sendByte(CAN);
        sendByte(CAN);
    }


    private char readByte(Timer timer, SerialPortUtil serialPortUtil) throws IOException, TimeoutException {
        while (true) {
            char[] buf = new char[1];
            try {
                int read = serialPortUtil.inputStream.read(buf, 0, 1);
                Log.e("固件升级读取---",read+"");
//                int read = mUartManager.read(buf, 1, 100, 20);
                if (read > 0) {
                    return buf[0];
                }
            } catch (Exception lastError) {
                Log.e("固件升级报错--", lastError.toString());
                lastError.printStackTrace();
            }
            if (timer.isExpired()) {
                throw new TimeoutException();
            }
            shortSleep();
        }
    }

    public void write(byte[] b) {
        try {
            mUartManager.write(b, b.length);
        } catch (LastError lastError) {
            lastError.printStackTrace();
        }
    }

    public  byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
}

