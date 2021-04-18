package io.renren.modules.xlgl.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tomchen
 * @date 2020-04-15
 */
@Data
public class TxlInfoEntity implements Serializable {

    public final static String TYPE_SB = "sb";
    public final static String TYPE_XL = "xl";

    //sb/xl
    private String type = "";

    private String xlbh = "";

    private String sbbh = "";

    private String sbid = "";

    private String sldk = "";

    private String xldk = "";

    private Map<String, Object> sbMap = new HashMap();



}
