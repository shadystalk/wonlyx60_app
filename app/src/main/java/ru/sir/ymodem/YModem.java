package ru.sir.ymodem;

import android.util.Log;

import com.van.uart.UartManager;
import com.wl.wlflatproject.MUtils.Constants;
import com.wl.wlflatproject.MUtils.PostEventBus;
import com.wl.wlflatproject.MUtils.SerialPortUtil;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * YModem.<br/>
 * Block 0 contain minimal file information (only filename)<br/>
 * <p>
 * Created by Anton Sirotinkin (aesirot@mail.ru), Moscow 2014<br/>
 * I hope you will find this program useful.<br/>
 * You are free to use/modify the code for any purpose, but please leave a reference to me.<br/>
 */
public class YModem {
    private Modem modem;

    public YModem(UartManager manager) {
        this.modem = new Modem(manager);
    }
    public YModem() {
        this.modem = new Modem();
    }

    /**
     * Send a file.<br/>
     * <p>
     * This method support correct thread interruption, when thread is interrupted "cancel of transmission" will be send.
     * So you can move long transmission to other thread and interrupt it according to your algorithm.
     *
     * @param file
     * @throws IOException
     */
    public void send(File file, SerialPortUtil serialPortUtil) throws IOException {
        //open file
        try (DataInputStream dataStream = new DataInputStream(new FileInputStream(file))) {
            Timer timer = new Timer(Modem.WAIT_FOR_RECEIVER_TIMEOUT).start();
//            boolean useCRC16 = modem.waitReceiverRequest(timer,serialPortUtil);

//            if (useCRC16)
            CRC crc = new CRC16();
//            else
//                crc = new CRC8();

            //send block 0
            String fileNameString = file.getName() + (char) 0 + /*getFileSizes(file)*/ file.length() + ' ';
            byte[] fileNameBytes = Arrays.copyOf(fileNameString.getBytes(), 128);
            modem.sendBlock(0, Arrays.copyOf(fileNameBytes, 128), 128, crc,serialPortUtil,false);

            modem.waitReceiverRequest(timer,serialPortUtil);
            //send data
            byte[] block = new byte[1024];
//            block[0]=43;
//            block[1]=72;
//            block[2]=77;
//            block[3]=66;
//            block[4]=58;
            modem.sendDataBlocks(dataStream, 1, crc, block,serialPortUtil);

            modem.sendEOT(serialPortUtil);
            byte[] endBlock = new byte[128];
            modem.sendBlock(0,endBlock, 128, crc,serialPortUtil,true);
        }
    }

    /**
     * Send files in batch mode.<br/>
     * <p>
     * This method support correct thread interruption, when thread is interrupted "cancel of transmission" will be send.
     * So you can move long transmission to other thread and interrupt it according to your algorithm.
     *
     * @param files
     * @throws IOException
     */
//    public void batchSend(File... files) throws IOException {
//        for (File file : files) {
//            send(file);
//        }
//        sendBatchStop();
//    }
//
//    private void sendBatchStop() throws IOException {
//        Timer timer = new Timer(Modem.WAIT_FOR_RECEIVER_TIMEOUT).start();
//        boolean useCRC16 = modem.waitReceiverRequest(timer);
//        CRC crc;
//        if (useCRC16)
//            crc = new CRC16();
//        else
//            crc = new CRC8();
//
//        //send block 0
//        byte[] bytes = new byte[128];
//        modem.sendBlock(0, bytes, bytes.length, crc);
//    }
}
