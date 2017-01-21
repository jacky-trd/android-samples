package com.jikexueyuan.minaconnection;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

//用于初始化自定义的filter
public class MyCoderFactory implements ProtocolCodecFactory {
    private MyEncoder myEncoder;
    private MyDecoder myDecoder;

    public MyCoderFactory(){
        myEncoder = new MyEncoder();
        myDecoder = new MyDecoder();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return myEncoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return myDecoder;
    }
}
