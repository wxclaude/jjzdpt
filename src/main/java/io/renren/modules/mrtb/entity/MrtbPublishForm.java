package io.renren.modules.mrtb.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MrtbPublishForm implements Serializable {

    private int mrtbId;

    private String publishContent;
}
