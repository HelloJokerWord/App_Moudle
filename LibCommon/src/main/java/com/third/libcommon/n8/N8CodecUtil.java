package com.third.libcommon.n8;


/**
 * CreateBy:Joker
 * CreateTime:2023/6/19 16:26
 * description：
 */
public class N8CodecUtil {
    public static final byte[] MASK = {(byte) 238, (byte) 185, (byte) 233, (byte) 179, (byte) 129, (byte) 142, (byte) 151, (byte) 167};


    public static byte[] encode(byte[] srcBuf) {
        if (srcBuf == null) {
            return null;
        }
        byte[] dstBuf = new byte[srcBuf.length + 2];
        byte maskS = (byte) 0;
        for (int i = 0; i < srcBuf.length; i++) {
            dstBuf[i] = ((byte) (srcBuf[i] ^ MASK[i % MASK.length]));
            maskS ^= srcBuf[i];
        }

        dstBuf[srcBuf.length] = (byte) (maskS ^ MASK[0]);
        dstBuf[srcBuf.length + 1] = ((byte) (maskS ^ MASK[1]));

        return dstBuf;
    }

    public static byte[] decode(byte[] srcBuf) {
        if (srcBuf == null || srcBuf.length < 2) {
            return null;
        }
        byte maskS = (byte) 0;
        byte[] dstBuf = new byte[srcBuf.length - 2];

        for (int i = 0; i < srcBuf.length - 2; i++) {
            dstBuf[i] = (byte) (srcBuf[i] ^ MASK[i % MASK.length]);
            maskS ^= dstBuf[i];
        }

        if (srcBuf[srcBuf.length - 2] == (byte) (maskS ^ MASK[0])
                && srcBuf[srcBuf.length - 1] == (byte) (maskS ^ MASK[1])) {
            return dstBuf;
        }
        return null;
    }

}