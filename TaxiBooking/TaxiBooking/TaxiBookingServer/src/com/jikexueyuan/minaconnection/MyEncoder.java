package com.jikexueyuan.minaconnection;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;

//用于编码
public class MyEncoder implements ProtocolEncoder {
    @Override
    public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
        Charset charset = Charset.forName("utf-8");
        IoBuffer ioBuffer = IoBuffer.allocate(100).setAutoExpand(true);
        ioBuffer.putString(o.toString(), charset.newEncoder());
        ioBuffer.flip();
        protocolEncoderOutput.write(ioBuffer);
    }

    @Override
    public void dispose(IoSession ioSession) throws Exception {

    }
}
