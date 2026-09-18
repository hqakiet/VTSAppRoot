package com.vts.vtsapproot.API.Response;

import java.io.Serializable;
import java.util.Objects;

public class ResModDetail_RemoteDB implements Serializable {
    private int RemoteDB;
    private String RemoteDBDatabaseName;
    private String RemoteDBDescription;

    public int getRemoteDB() {
        return RemoteDB;
    }

    public String getRemoteDBDatabaseName() {
        return RemoteDBDatabaseName;
    }

    public String getRemoteDBDescription() {
        return RemoteDBDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResModDetail_RemoteDB item = (ResModDetail_RemoteDB) o;
        return Objects.equals(RemoteDB, item.RemoteDB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(RemoteDB);
    }
}