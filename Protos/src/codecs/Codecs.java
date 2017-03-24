package codecs;

import static java.nio.charset.Charset.*;
import static java.util.Arrays.*;
import static java.util.Objects.*;
import static codecs.Codecs.DefaultCodecs.*;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import codecs.Codecs.ByteToMessageDecoder;

public interface Codecs {

    public interface ByteToMessageDecoder<O> {
        /**
         * Decodifica un stream de bytes y produce mensajes: se encarga de la delimitación.
         * Si no produce ningún mensaje se entiende que se requieren
         * más bytes.
         * 
         * @return como ayuda, indica con <code>true</code> si se produjo algún mensaje
         */
        boolean decode(final ByteBuffer in, Consumer<O> consumer) ;
        
        default boolean decode(final ByteBuffer in) {
            return decode(in, x -> {}) ;
        }
        
        /** cambia el tipo de salida del decoder mediante una transformación */
        default <X> ByteToMessageDecoder<X> transform(final Function<O, X> fnc) {
            return (in, consumer) -> ByteToMessageDecoder.this.decode(in, x-> consumer.accept(fnc.apply(x)));
        }
    }

    /** fábrica de codecs */
    public final class DefaultCodecs {
        /** frame de ancho fijo: |a|b|c|d| -> |ab|cd|d…| */
        public static ByteToMessageDecoder<ByteBuffer> fixedLengthFrame(final int frameLength) {
            return new FixedLengthFrameDecoder(frameLength);
        }
        
        /** frame delimitado por \0: |a|b|c|\0|d|e| -> |abc|de…| */
        public static ByteToMessageDecoder<ByteBuffer> nulTerminatedFrame(final int maxFrameLength) {
            return new ByteTerminatedDelimiterBasedFrameDecoder(maxFrameLength, (byte) 0x00);
        }
        
        /** frame de ancho variable. Ej con lengthBytes=1: |0x04|a|b|c|d|efgh ->|abcd|efgh|… */
        public static ByteToMessageDecoder<ByteBuffer> chunkedFrameDecoder(final int lengthBytes) {
            return new LengthFieldBasedFrameDecoder(lengthBytes);
        }
        
        /** decodifica un entero de 16 bits. @see ByteBuffer.order */
        public static ByteToMessageDecoder<Short> shortDecoder() {
            return (in, consumer) -> {
                boolean ret = false;
                try {
                    consumer.accept(in.getShort());
                    ret = true;
                } catch(final BufferUnderflowException e) {
                    // ok
                }
                return ret;
            };
        }
        
        private static final Charset ASCII = forName("ASCII");
        public static String toASCIIString(final ByteBuffer buffer) {
            final byte []bytes = buffer.array();
            return new String(bytes, buffer.position(), buffer.remaining(), ASCII);        
        }

        /** utiliza un único byte del buffer */
        public static byte oneByte(final ByteBuffer buffer) {
            return buffer.get();
        }
        
        /** convierte el buffer a un byte[] */
        public static byte[] toByteArray(final ByteBuffer buffer) {
            final byte [] ret = new byte[buffer.remaining()];
            
            buffer.get(ret);
            return ret;
        }
        
        static int assertFrameLength(final int n) {
            if(n < 1) {
                throw new IllegalArgumentException("la longitud del frame debe ser mayor a cero");
            }
            return n;
        }
    }

    /** permite asociar un decodificador con una acción de decodificación */
    static class Pair<T> {
        final ByteToMessageDecoder<T> deco;
        final Consumer<T> action;
        
        public Pair(final ByteToMessageDecoder<T> deco, final Consumer<T> action) {
            this.deco = deco;
            this.action = action;
        }
        public static <T> Pair<T> pair(final ByteToMessageDecoder<T> deco, final Consumer<T> action) {
            return new Pair<>(deco, action);
        }
    }
    
    /** decodificador que permite encadenar otros decodificadores */
    public class ChainedByteToMessageDecoder<T> implements ByteToMessageDecoder<T>  {
        private final Supplier<T> factory;
        private Iterator<Pair> pipeline;
        private Pair current;

        public static <T> Pair<T> pair(final ByteToMessageDecoder<T> deco, final Consumer<T> action) {
            return new Pair<>(deco, action);
        }
        
        public ChainedByteToMessageDecoder(final Supplier<T> factory, final Pair ... pipeline) {
            this.pipeline = asList(pipeline).iterator();
            this.factory = requireNonNull(factory);
        }
        
        @Override
        public boolean decode(final ByteBuffer in, final Consumer<T> consumer) {
            if(current == null) {
                current = pipeline.next();
            }
            boolean ret = false;
            
            while(in.hasRemaining()) {
                ret = false;
                ret = current.deco.decode(in, o -> current.action.accept(o));
                if(ret) {
                    if(pipeline.hasNext()) {
                        current = pipeline.next();
                    } else {
                        consumer.accept(factory.get());
                        current = null;
                        pipeline = null;
                        return ret;
                    }
                }
            }
            
            return ret;
        }
    }
}

class FixedLengthFrameDecoder implements ByteToMessageDecoder<ByteBuffer> {
    private final ByteBuffer buff;
    public FixedLengthFrameDecoder(final int frameLength) {
        buff = ByteBuffer.allocate(assertFrameLength(frameLength));
    }

    @Override
    public boolean decode(final ByteBuffer in, final Consumer<ByteBuffer> consumer) {
        boolean ret = false;
        while(buff.hasRemaining() && in.hasRemaining()) {
            buff.put(in.get());
        }
        if(!buff.hasRemaining()) {
            consumer.accept((ByteBuffer)buff.flip());
            ret = true;
        }
        return ret;
    }
}

/** Delimita mensajes utilizando un único caracter (por ejemplo \0 o \n) */
class ByteTerminatedDelimiterBasedFrameDecoder implements ByteToMessageDecoder<ByteBuffer> {
    private final int maxFrameLength;
    private final byte delimiter;
    
    private final ByteBuffer buff;
    
    public ByteTerminatedDelimiterBasedFrameDecoder(final int maxFrameLength, final byte delimiter) {
        this.maxFrameLength = assertFrameLength(maxFrameLength);
        this.delimiter = delimiter;
        buff = ByteBuffer.allocate(maxFrameLength);
    }
    
    @Override
    public boolean decode(final ByteBuffer in, final Consumer<ByteBuffer> consumer) {
        boolean ret = false;

        while(buff.hasRemaining() && in.hasRemaining()) {
            final byte b = in.get();
            if(b == delimiter || buff.remaining() == 1) {
                if(b != delimiter) {
                    buff.put(b);
                }
                consumer.accept((ByteBuffer)buff.flip());
                ret = true;
                break;
            } else {
                buff.put(b);
            }
        }
        return ret;
    }
}

class LengthFieldBasedFrameDecoder implements ByteToMessageDecoder<ByteBuffer> {
    private final int lengthBytes;
    private ByteBuffer buff;
    
    public LengthFieldBasedFrameDecoder(final int lengthBytes) {
        this.lengthBytes = assertFrameLength(lengthBytes);
    }
    
    @Override
    public boolean decode(final ByteBuffer in, final Consumer<ByteBuffer> consumer) {
        boolean ret = false;
        if(buff == null) {
            final int n = readInteger(in);
            try {
                buff = ByteBuffer.allocate(n);
            } catch(final BufferUnderflowException e) {
                // ok
            }
        }
        if(buff != null) {
            while(buff.hasRemaining() && in.hasRemaining()) {
                buff.put(in.get());
            }
            if(!buff.hasRemaining()) {
                consumer.accept((ByteBuffer)buff.flip());
                ret = true;
            }
        }
        return ret;
    }

    private int readInteger(final ByteBuffer in) {
        int n = 0;
        switch (lengthBytes) {
            case 1:
                n = (in.get()) & 0xff;
                break;
            case 2:
                n = in.getShort();
                break;
            case 4:
                n = in.getInt();
                break;
            default:
                break;
        }
        return n;
    }
}

