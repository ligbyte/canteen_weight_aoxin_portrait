package com.stkj.aoxin.weight.weight;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import android_serialport_api.SerialPort;

public class WeightUtils {
    private static final String TAG = "WeightUtils";
    private static final int MAX_SIZE = 2048;
    private static final int UNIT_LENGTH = 26;
    private static final byte START_FLAG = '=';
    private static final byte[] DATA_POOL = new byte[MAX_SIZE];
    private static final String[] STABLE_FLAGS = {"2", "3", "6", "7"};
    private static final StringBuilder CACHE = new StringBuilder();
    private static int maxSize = 0;
    private static SerialPort serialPort;
    private static WeightCallback callback;
    private static boolean running = false;

    public static void start(String port, WeightCallback callback) {
        WeightUtils.callback = callback;
        stop();
        if (port == null || port.isEmpty()) throw new IllegalArgumentException("端口号不能为空");
        try {
            //波特率默认是9600,flags固定为0
            serialPort = new SerialPort(new File(port), 9600, 0);
            running = true;
            getWeight();
        } catch (Exception e) {
            if (callback != null) callback.callback(null, "start: " + e.getMessage());
            Log.e(TAG, "start: ", e);
        }
    }

    private static void getWeight() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!running) continue;
                    byte[] buffer = new byte[512];
                    if (serialPort == null) return;
                    int size = 0;
                    try {
                        size = serialPort.getInputStream().read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Weight weight = parseWeight(buffer, size);
                    if (callback != null)
                        callback.callback(weight, CACHE.toString());
                }
            }
        };
        new Thread(runnable).start();
    }

    private static Weight parseWeight(byte[] buffer, int size) {
        if (size <= 0) return null;
        if (maxSize + size > MAX_SIZE) maxSize = 0;
        if (CACHE.length() >= 700) CACHE.setLength(0);
        byte[] cacheByte = new byte[size];
        System.arraycopy(buffer, 0, cacheByte, 0, size);
        CACHE.append(new String(cacheByte, StandardCharsets.UTF_8));
        //复制数据到缓存池
        System.arraycopy(buffer, 0, DATA_POOL, maxSize, size);
        maxSize += size;
        //标准数据搁置最短为26位
        if (maxSize < UNIT_LENGTH) return null;
        for (int i = 0; i < maxSize; i++) {
            if (DATA_POOL[i] == START_FLAG && maxSize - i >= UNIT_LENGTH) {
                byte[] temp = new byte[UNIT_LENGTH];
                System.arraycopy(DATA_POOL, i, temp, 0, UNIT_LENGTH);
                String str = new String(temp, StandardCharsets.UTF_8);
                try {
                    double netWeight = Double.parseDouble(str.substring(17, 24).replace(" ", ""));
                    double tareWeight = Double.parseDouble(str.substring(9, 16).replace(" ", ""));
                    String flag = str.substring(25, 26);
                    boolean stabled = false;
                    for (String stableFlag : STABLE_FLAGS) {
                        if (stableFlag.equals(flag)) {
                            stabled = true;
                            break;
                        }
                    }
                    Weight weight = new Weight();
                    weight.netFlag = tareWeight > 0;
                    weight.netWeight = netWeight;
                    weight.tareWeight = tareWeight;
                    weight.stabled = stabled;
                    weight.zeroFlag = netWeight == 0;
                    maxSize = 0;
                    return weight;
                } catch (Exception e) {
                    Log.e(TAG, "parseWeight: ", e);
                    if (callback != null) callback.callback(null, "parseWeight: " + e.getMessage());
                }
            }
        }
        return null;
    }

    public static void tare() throws IOException {
        if (serialPort != null) {
            //T去皮
            serialPort.getOutputStream().write(new byte[]{0x54});
            readResponse();
        }
    }

    public static void zero() throws IOException {
        if (serialPort != null) {
            //Z置零
            serialPort.getOutputStream().write(new byte[]{0x5A});
            readResponse();
        }
    }


    private static void readResponse() {
        running = false;
        final byte[][] response = {new byte[1024]};
        if (serialPort == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean read = true;
                while (read) {
                    byte[] buffer = new byte[512];
                    int size = 0;
                    try {
                        size = serialPort.getInputStream().read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (size > 0) {
                        String success = new String(new byte[]{0x0D, 0x0A});
                    }


                    if (size > 0) {
                        byte[] data = Arrays.copyOf(buffer, size);

                        byte[] old = response[0];
                        response[0] = new byte[old.length + data.length];
                        System.arraycopy(old, 0, response[0], 0, old.length);
                        System.arraycopy(data, 0, response[0], old.length, data.length);
                        //设置成功返回
                        String success = new String(new byte[]{0x0D, 0x0A});
                        String str = new String(response[0]);
                        if (str.contains(success)) {
                            Log.e(TAG, "run: 操作成功");
                            read = false;
                            running = true;
                        }
                    }
                }
            }
        }).start();
    }

    public static void stop() {
        if (serialPort != null)
            serialPort.close();
        serialPort = null;
        Log.e(TAG, "stop: ");
    }

    //需要使用2字节表示b
    public static String numToHex16(int b) {
        return String.format("%04x", b);
    }

    /**
     * 去皮预设
     *
     * @param num
     * @throws IOException
     */
    public static void peelOffPreset(String num) throws IOException {
        int peel = 0;
        try {
            peel = Integer.parseInt(num);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //组织合并数据,数据长度7位
        //开始2位为起始位A5 A5
        int data1 = 0XA5;
        int data2 = 0XA0;
        //数据第345位去皮数据
        int data3 = (peel & 0XFF0000) >> 16;
        int data4 = (peel & 0X00FF00) >> 8;
        int data5 = peel & 0X0000FF;
        //数据第67位校验位
        String[] dataArr = new String[]{numToHex16(data1), numToHex16(data2), numToHex16(data3), numToHex16(data4), numToHex16(data5)};
        Log.e(TAG, Arrays.toString(dataArr));
        byte[] param = Crc16Util.getData(dataArr);
        Log.e(TAG, Crc16Util.byteTo16String(param));
        if (serialPort != null) {
            //去皮预设
            serialPort.getOutputStream().write(param);
            readResponse();
        }

    }
}
