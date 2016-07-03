package kr.pe.kwonnam.hibernate4memcached.spymemcached;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import de.javakaffee.kryoserializers.KryoReflectionFactorySupport;
import kr.pe.kwonnam.hibernate4memcached.util.IntToBytesUtils;
import kr.pe.kwonnam.hibernate4memcached.util.Lz4CompressUtils;
import kr.pe.kwonnam.hibernate4memcached.util.OverridableReadOnlyProperties;
import net.spy.memcached.CachedData;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Default transcoder for {@link SpyMemcachedAdapter}.
 *
 * This transcoder uses Kryo Serializer and compress data with Lz4 when data size is greater than compression
 * threashold.
 *
 * @author KwonNam Son (kwon37xi@gmail.com)
 */
public class KryoTranscoder implements InitializableTranscoder<Object> {
    private Logger log = LoggerFactory.getLogger(KryoTranscoder.class);

    public static final String COMPRESSION_THREASHOLD_PROPERTY_KEY = SpyMemcachedAdapter.PROPERTY_KEY_PREFIX + ".kryotranscoder.compression.threashold.bytes";

    public static final int BASE_FLAG = 0;

    public static final int COMPRESS_FLAG = 4; // 0b0100

    public static final int DEFAULT_BUFFER_SIZE = 1024 * 20; // 20kb

    public static final int DECOMPRESSED_SIZE_STORE_BYTES_LENGTH = 4;

    private OverridableReadOnlyProperties properties;

    private int bufferSize = DEFAULT_BUFFER_SIZE;

    private int compressionThreasholdBytes;

    @Override
    public void init(OverridableReadOnlyProperties properties) {
        this.properties = properties;

        String compressionThreasholdBytesProperty = properties.getRequiredProperty(COMPRESSION_THREASHOLD_PROPERTY_KEY);
        compressionThreasholdBytes = Integer.parseInt(compressionThreasholdBytesProperty);
    }

    @Override
    public boolean asyncDecode(CachedData d) {
        return false;
    }

    /**
     * Override this method to change serialization rule.
     *
     * @return Kryo serializer instance.
     */
    protected Kryo createKryo() {
        Kryo kryo = new KryoReflectionFactorySupport();
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
        return kryo;
    }

    @Override
    public CachedData encode(Object object) {
        int flags = BASE_FLAG;

        byte[] encodedBytes = kryoEncode(object);

        boolean compressionRequired = encodedBytes.length > compressionThreasholdBytes;

        if (compressionRequired) {
            int beforeSize = encodedBytes.length;
            encodedBytes = compress(encodedBytes);

            log.debug("kryotranscoder compress required : {}, original {} bytes -> compressed {} bytes", compressionRequired, beforeSize, encodedBytes.length);
            flags = flags | COMPRESS_FLAG;
        }
        return new CachedData(flags, encodedBytes, getMaxSize());
    }

    private byte[] kryoEncode(Object o) {
        Kryo kryo = createKryo();

        Output output = new Output(bufferSize, getMaxSize());
        kryo.writeClassAndObject(output, o);
        return output.toBytes();
    }

    byte[] compress(byte[] encodedBytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(encodedBytes.length);
        try {
            baos.write(IntToBytesUtils.intToBytes(encodedBytes.length));

            byte[] compressedBytes = Lz4CompressUtils.compress(encodedBytes);
            baos.write(compressedBytes);
        } catch (IOException e) {
            throw new IllegalStateException("Failed do compress kryo serialized data.", e);
        }
        return baos.toByteArray();
    }

    @Override
    public Object decode(CachedData data) {
        int flags = data.getFlags();

        byte[] decodedBytes = data.getData();

        boolean compressed = (flags & COMPRESS_FLAG) > 0;

        if (compressed) {
            decodedBytes = decompress(decodedBytes);
        }

        Kryo kryo = createKryo();
        return kryo.readClassAndObject(new Input(decodedBytes));
    }

    private byte[] decompress(byte[] decodedBytes) {
        int decompressedSize = IntToBytesUtils.bytesToInt(ArrayUtils.subarray(decodedBytes, 0, DECOMPRESSED_SIZE_STORE_BYTES_LENGTH));

        return Lz4CompressUtils.decompressFast(decodedBytes, DECOMPRESSED_SIZE_STORE_BYTES_LENGTH, decompressedSize);
    }

    @Override
    public int getMaxSize() {
        return CachedData.MAX_SIZE;
    }
}