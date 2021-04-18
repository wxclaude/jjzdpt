package io.renren.modules.dm.form;

import io.renren.modules.dm.entity.HydmEntity;
import io.renren.modules.dm.entity.TyjddEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class YjddForm implements Serializable {

    private Date dmTime;

    private List<TyjddEntity> data;

}
