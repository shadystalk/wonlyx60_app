package com.wl.wlflatproject.MService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.wl.wlflatproject.IdInterface;

public class IdService extends Service {
    private IdInterface.Stub binder=new IdInterface.Stub() {
        private String id="00:00:00:00:00:00:00:00:00:00";
        @Override
        public String getMachineId() throws RemoteException {
            return id;
        }

        @Override
        public void setId(String id) throws RemoteException {
            this.id=id;
        }


    };
    public IdService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}