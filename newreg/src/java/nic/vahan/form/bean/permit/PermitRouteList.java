package nic.vahan.form.bean.permit;

import java.io.Serializable;

public class PermitRouteList implements Serializable {

    private String Key;
    private String value;

    public PermitRouteList(String key, String value) {
        this.Key = key;
        this.value = value;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String Key) {
        this.Key = Key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
