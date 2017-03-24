package codecs;

import static codecs.Codecs.DefaultCodecs.*;
import static java.nio.ByteBuffer.*;
import static java.nio.ByteOrder.*;
import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import codecs.Codecs.ByteToMessageDecoder;
import codecs.Codecs.DefaultCodecs;
import codecs.Codecs.Pair;

public class CodecsTest {
    @Test
    public void testFixedLengthFrame() {
        final ByteToMessageDecoder<ByteBuffer> codec = fixedLengthFrame(2);
        final ByteBuffer buffer = allocate(10)
            .put((byte)0x01);

        /* simulo una escritura incompleta */
        buffer.flip();  // dispongo el buffer para lectura
        assertFalse(codec.decode(buffer, null));
        buffer.compact();
        
        /* simulo una lectura donde hay más bytes de los que se
         * necesitan
         */
        buffer.put((byte)0x02)
              .put((byte)0x03)
              .flip();  // dispongo el buffer para lectura

        final List<ByteBuffer> ret = new ArrayList<>(1);
        assertTrue(codec.decode(buffer, ret::add));
        assertEquals(1, ret.size());
        
        final ByteBuffer tmp = ret.get(0);
        assertEquals(2,  tmp.remaining());
        assertEquals((byte)0x01, tmp.get());
        assertEquals((byte)0x02, tmp.get());

        // verifico que en el buffer queden bytes que no pertencian
        // al frame.
        assertEquals(1, buffer.remaining());
        assertEquals((byte)0x03, buffer.get());
    }

    @Test
    public void nul_terminated_fill_buffer() {
        final ByteToMessageDecoder<String> codec = nulTerminatedFrame(5)
                    .transform(DefaultCodecs::toASCIIString);
        
        final ByteBuffer buffer = allocate(10)
                .put((byte)'h')
                .put((byte)'o')
                .put((byte)'l')
                .put((byte)'a');
        buffer.flip();
        assertFalse(codec.decode(buffer, null));
        buffer.compact();
        
        buffer.put((byte)0x00)
              .put((byte)'X');
        buffer.flip();
        
        final List<String> ret = new ArrayList<>(1);
        assertTrue(codec.decode(buffer, ret::add));
        assertEquals("hola", ret.get(0));
        
        // verifico que en el buffer queden bytes que no pertencian
        // al frame.
        assertEquals(1, buffer.remaining());
        assertEquals((byte)'X', buffer.get());
    }

    @Test
    public void nul_terminated_exceed_buffer() {
        final ByteToMessageDecoder<String> codec = nulTerminatedFrame(2)
                .transform(DefaultCodecs::toASCIIString);
        
        final ByteBuffer buffer = allocate(10)
                .put((byte)'h')
                .put((byte)'o')
                .put((byte)'l')
                .put((byte)'a');
        buffer.flip();
        
        final List<String> ret = new ArrayList<>(1);
        assertTrue(codec.decode(buffer, ret::add));
        assertEquals("ho", ret.get(0));
        
        // verifico que en el buffer queden bytes que no pertencian
        // al frame.
        assertEquals(2, buffer.remaining());
        assertEquals((byte)'l', buffer.get());
        assertEquals((byte)'a', buffer.get());
    }

    
    @Test
    public void chunked() {
        final ByteToMessageDecoder<String> codec = chunkedFrameDecoder(2)
                .transform(DefaultCodecs::toASCIIString);
        
        final ByteBuffer buffer = allocate(10)
                .order(BIG_ENDIAN)
                .putShort((short)4);
        buffer.flip();
        
        assertFalse(codec.decode(buffer, null));
        buffer.compact();
        
        buffer
                .put((byte)'h')
                .put((byte)'o')
                .put((byte)'l')
                .put((byte)'a')
                .put((byte)' ')
                .flip()
                ;
        
        final List<String> ret = new ArrayList<>(1);
        assertTrue(codec.decode(buffer, ret::add));
        assertEquals("hola", ret.get(0));
        
        // verifico que en el buffer queden bytes que no pertencian
        // al frame.
        assertEquals(1, buffer.remaining());
        assertEquals((byte)' ', buffer.get());
    }
    
    @Test
    public void chained() {
    	StringBuilder strBuilder = new StringBuilder();
        final ByteToMessageDecoder<StringBuilder> codec = 
        		new Codecs.ChainedByteToMessageDecoder<StringBuilder>(() -> strBuilder, 
        				new Pair<String>(chunkedFrameDecoder(1).transform(DefaultCodecs::toASCIIString), strBuilder::append),
        				new Pair<String>(nulTerminatedFrame(10).transform(DefaultCodecs::toASCIIString), strBuilder::append));
        
        ByteBuffer buffer = allocate(20);
        buffer
        	.put((byte) 0x04)
        	.put((byte) 'h')
        	.put((byte) 'o')
        	.put((byte) 'l')
        	.put((byte) 'a')
        	.put((byte) 'c')
        	.put((byte) 'h')
        	.put((byte) 'a')
        	.put((byte) 'u')
        	.put((byte) 0x00)
        	.put((byte) 'f')
        	.flip();
        
        ArrayList<StringBuilder> list = new ArrayList<>();
        
        assertTrue(codec.decode(buffer, list::add));
        assertEquals(list.size(), 1);
        assertEquals(list.get(0).length(), 8);
        assertEquals(list.get(0).toString(), "holachau");
        assertEquals(1, buffer.remaining());
        assertEquals('f', buffer.get());
    }
}