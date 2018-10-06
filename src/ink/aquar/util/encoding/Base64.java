package ink.aquar.util.encoding;

import java.nio.ByteBuffer;

public class Base64 implements Encoder {
    @Override
    public String encode(ByteBuffer bytes) {
        return null; // TODO
    }

    @Override
    public ByteBuffer decode(String code) {
        return null; // TODO
    }

    public static class Profile {
        public char character62 = '+';
        public char character63 = '/';
        public char padding = '=';
        public boolean doPadding = true;
    }

}
