package io.renren.modules.mrtb.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MrtbMsgForm implements Serializable {

    private int mrtbId;

    private String msg;
}
