package ink.aquar.util.encoding;

import java.nio.ByteBuffer;

public interface Encoder {

    public String encode(ByteBuffer bytes);

    public ByteBuffer decode(String code);

}
