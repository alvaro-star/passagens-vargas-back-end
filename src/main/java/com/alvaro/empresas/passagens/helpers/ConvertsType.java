package com.alvaro.empresas.passagens.helpers;

import java.nio.ByteBuffer;
import java.util.UUID;

public class ConvertsType {
    public static UUID convertBytesToUUIDHelper(byte[] bytes) {
        if (bytes.length < 16) {
            throw new IllegalArgumentException("A array de bytes deve ter pelo menos 16 bytes.");
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long mostSignificantBits = byteBuffer.getLong();
        long leastSignificantBits = byteBuffer.getLong();
        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
