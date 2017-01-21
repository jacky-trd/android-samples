package com.jkxy;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Socket相关处理
 */
public class SocketHandler extends IoHandlerAdapter {

    private List<IoSession> mSessionList =new ArrayList<>();

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        //添加对话链接
        mSessionList.add(session);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        //删除对话链接
        mSessionList.remove(session);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);

        //发送消息，客户端自己发送的消息不再发送
        for (IoSession temp : mSessionList) {
            if (temp.equals(session)) {
                continue;
            } else {
                temp.write(message);
            }
        }
    }
}
