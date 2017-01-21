// IAppServiceRemoteBinder.aidl
package com.jikexueyuan.communicatewithinsideservice;

// Declare any non-default types here with import statements
import com.jikexueyuan.communicatewithinsideservice.TimerServiceCallback;

interface IAppServiceRemoteBinder {
    void registCallback(TimerServiceCallback callback);
    void unregistCallback(TimerServiceCallback callback);
}
