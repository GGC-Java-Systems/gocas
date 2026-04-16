package org.rmj.gocas.pojo;

import org.json.simple.JSONObject;

public interface JEntity {
    public boolean setData(JSONObject foData);
    public JSONObject toJSON();
    public String toJSONString();
    public String getMessage();
}
