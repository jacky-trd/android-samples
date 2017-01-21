package com.jikexueyuan.taxibookingserver;

import com.jikexueyuan.minaconnection.MyCoderFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public static void main(String args[]) {
        NioSocketAcceptor acceptor = new NioSocketAcceptor();
        //添加自定义的filter
        acceptor.getFilterChain().addLast("MyHandler", new ProtocolCodecFilter(new MyCoderFactory()));
        //设置Handler
        acceptor.setHandler(new MyHandler());
        try {
            //启动服务，监听8000端口
            acceptor.bind(new InetSocketAddress(8000));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
