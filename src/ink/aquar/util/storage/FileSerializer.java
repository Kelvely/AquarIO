package ink.aquar.util.storage;

import ink.aquar.util.io.Serializer;

import java.io.File;
import java.nio.ByteBuffer;

public class FileSerializer implements Serializer<File> {

    public FileSerializer(File file) {
        // TODO
    }

    @Override
    public File deserialize(ByteBuffer raw) {
        return null; // TODO
    }

    @Override
    public ByteBuffer serialize(File data) {
        return null; // TODO
    }
}
