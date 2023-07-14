package com.third.libcommon.n8;

import com.blankj.utilcode.util.StringUtils;

/**
 * CreateBy:Joker
 * CreateTime:2023/6/19 16:25
 * description：
 */
public class N8Base64 {
    public static String encode(String src) {
        if (StringUtils.isEmpty(src)) {
            return src;
        }

        byte[] n8EncodeData = N8CodecUtil.encode(src.getBytes());
        return Base64Utils.encode(n8EncodeData);
    }

    public static String decode(String src) {
        if (StringUtils.isEmpty(src)) {
            return src;
        }

        try {
            byte[] base64DecodeData = Base64Utils.decode(src);
            byte[] n8DecodeData = N8CodecUtil.decode(base64DecodeData);
            return new String(n8DecodeData);
        } catch (Exception ignore) {
        }
        return src;
    }
}
