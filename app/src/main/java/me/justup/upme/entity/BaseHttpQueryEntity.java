package me.justup.upme.entity;

import java.io.Serializable;

public abstract class BaseHttpQueryEntity implements Serializable {
    private static final long serialVersionUID = 0L;

    public String jsonrpc = "2.0";

}
