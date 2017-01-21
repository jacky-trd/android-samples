package com.jikexueyuan.taxibookingserver;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyHandler extends IoHandlerAdapter {

    Map<IoSession, String> userSession = new HashMap<>();
    Map<IoSession, String> driverSession = new HashMap<>();
    Map<String, Map<String, Object>> orderMap = new HashMap<>();
    Map<String, Map<String, Object>> driverMap = new HashMap<>();
    private OrderDatabase odb;

    public MyHandler() {
        super();
        //创建数据库表
        odb = new OrderDatabase();
        odb.createDatabase("orderrecords");
    }

    public MyHandler(OrderDatabase odb) {
        super();
        this.odb = odb;
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        System.out.println("Session " + session.getRemoteAddress() + "connected");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        if (driverSession.containsKey(session)) {
            //删除对应司机信息
            driverMap.remove(driverSession.get(session));
            driverSession.remove(session);
            sendDriverMap();
        } else {
            //删除对应乘客订单信息
            orderMap.remove(userSession.get(session));
            userSession.remove(session);
            sendOderMap();
        }
        System.out.println("Session " + session.getRemoteAddress() + "disconnected");
    }

    //通过接收到的不同信息，进行不同的处理
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);

        String data = message.toString();
        System.out.println("接收到内容："+data);
        JSONObject root = new JSONObject(data);

        switch (root.getString("type")) {
            //客户端发送了订单
            case "user":
                JSONObject order = root.getJSONObject("order");
                Map<String, Object> userorder = new HashMap<>();
                userorder.put("latitude", order.getDouble("latitude"));
                userorder.put("longitude", order.getDouble("longitude"));
                userorder.put("origin", order.getString("origin"));
                userorder.put("destination", order.getString("destination"));
                userorder.put("phonenumber", order.getString("phonenumber"));
                userorder.put("username", order.getString("username"));
                orderMap.put(order.getString("username"),userorder);
                userSession.put(session, order.getString("username"));

                sendOderMap();
                sendDriverMap();

                break;
            //司机端上线操作
            case "driver":
                JSONObject driverInfo = root.getJSONObject("info");
                Map<String, Object> driver = new HashMap<>();
                driver.put("latitude", driverInfo.getDouble("latitude"));
                driver.put("longitude", driverInfo.getDouble("longitude"));
                driver.put("username", driverInfo.getString("username"));
                driver.put("phonenumber", driverInfo.getString("phonenumber"));
                driverMap.put(driverInfo.getString("username") ,driver);
                driverSession.put(session, driverInfo.getString("username"));

                sendDriverMap();
                sendOderMap();
                break;
            //司机端接受了订单
            case "getorder":
                JSONObject driverOrder = root.getJSONObject("driverorder");
                String username = driverOrder.getString("username");
                String drivername = driverOrder.getString("drivername");
                Map<String, Object> user = orderMap.get(username);
                if (user != null) {
                    sendOrderToDriver(session, "true", username);
                    IoSession usersession = (IoSession) getMapKey(userSession, username);
                    sendOrderToUser(usersession, drivername);
                    driverMap.remove(drivername);
                    orderMap.remove(username);
                    System.out.println(">>>>>>>>>>>>"+orderMapToJson());
                } else {
                    sendOrderToDriver(session, "false", username);
                }
                sendDriverMap();
                sendOderMap();
                odb.insert("orderrecords", new String[]{"driver", "user", "state"}, new String[]{driverOrder.getString("drivername"), driverOrder.getString("username"), "processing"});
                break;
            //订单完成
            case "finish":
                JSONObject finish = new JSONObject();
                finish.put("tag", "finish");
                IoSession uSession = (IoSession) getMapKey(userSession, root.getString("username"));
                uSession.write(finish.toString()+"\n");
                System.out.println("发送信息"+finish.toString()+" to "+uSession.getRemoteAddress());
                odb.update("orderrecords", "state", "finish", "driver", driverSession.get(session));
                break;
            default:
                break;
        }

    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
    }


    /**
     * 通过Map的value获取key
     * @param map Map实例
     * @param value value值
     * @return
     */
    public Object getMapKey(Map map, Object value){
        Iterator<Object> it = map.keySet().iterator();
        while(it.hasNext()){
            Object key = it.next();
            if (map.get(key).equals(value)) {
                return key;
            }
        }
        return null;
    }

    /**
     * 发送订单结果给司机
     * @param session 司机端的session
     * @param result 订单请求的结果
     * @param username 订单用户的名称
     */
    private void sendOrderToDriver(IoSession session, String result, String username) {
        JSONObject root = new JSONObject();
        try {
            root.put("tag", "order");
            root.put("result", result);
            root.put("username", orderMap.get(username).get("username").toString());
            root.put("userphone", orderMap.get(username).get("phonenumber").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        session.write(root.toString()+"\n");
        System.out.println("信息:"+root.toString()+"发送给了"+session.getRemoteAddress());
    }

    /**
     * 发送订单结果给乘客
     * @param session 乘客端的session
     * @param drivername 接单司机的名称
     */
    private void sendOrderToUser(IoSession session, String drivername) {
        JSONObject root =  new JSONObject();
        try {
            root.put("tag", "order");
            root.put("drivername", drivername);
            root.put("phonenumber", driverMap.get(drivername).get("phonenumber"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        session.write(root.toString()+"\n");
        System.out.println("信息:"+root.toString()+"发送给了"+session.getRemoteAddress());
    }

    /**
     * 发送订单列表给司机
     */
    private void sendOderMap(){
        for (IoSession x:driverSession.keySet()) {
            x.write(orderMapToJson()+"\n");
            System.out.println("信息:"+ orderMapToJson()+"发送给了"+x.getRemoteAddress());
        }
    }

    /**
     * 发送司机列表给乘客
     */
    private void sendDriverMap(){
        for (IoSession x:userSession.keySet()){
            x.write(driverMapToJson()+"\n");
            System.out.println("信息:"+ driverMapToJson()+"发送给了"+x.getRemoteAddress());
        }

    }

    /**
     * 将订单列表转换为Json格式
     * @return 返回Json字符串
     */
    private String orderMapToJson(){
        JSONObject root = new JSONObject();
        JSONArray list = new JSONArray();
        Iterator<Map<String, Object>> it = orderMap.values().iterator();

        try {
            while (it.hasNext()) {
                JSONObject item = new JSONObject();
                Map<String, Object> order = it.next();
                item.put("username", order.get("username"));
                item.put("phonenumber", order.get("phonenumber"));
                item.put("latitude", order.get("latitude"));
                item.put("longitude", order.get("longitude"));
                item.put("origin", order.get("origin"));
                item.put("destination", order.get("destination"));
                list.put(item);
            }
            root.put("list", list);
            root.put("tag", "orderlist");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root.toString();
    }

    /**
     * 将司机列表转换为Json格式
     * @return 返回Json字符串
     */
    private String driverMapToJson(){
        JSONObject root = new JSONObject();
        JSONArray list = new JSONArray();

        try {
            Iterator<Map<String, Object>> it = driverMap.values().iterator();
            while (it.hasNext()) {
                JSONObject item = new JSONObject();
                Map<String, Object> driver = it.next();
                item.put("latitude", driver.get("latitude"));
                item.put("longitude", driver.get("longitude"));
                list.put(item);
            }
            root.put("list", list);
            root.put("tag", "driverlist");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root.toString();
    }
}
