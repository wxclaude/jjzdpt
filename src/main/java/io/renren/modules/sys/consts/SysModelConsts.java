package io.renren.modules.sys.consts;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author tomchen
 * @date 2020-06-24
 */
@ConfigurationProperties(prefix = "renren.model")
@Component
@Data
public class SysModelConsts {
    private String ZHZH_ID;
    private String DLRZ_NAME;
    private String MRTZ_NAME;
    private String XSCZ_NAME;

    private String NAME1;
    private String NAME1_1;
    private String NAME1_2;
    private String NAME1_3;
    private String NAME2;
    private String NAME2_1;
    private String NAME2_2;
    private String NAME2_3;
    private String NAME3;
    private String NAME3_1;
    private String NAME3_2;

    private String FILE_PATH_MRTZ;
    private String FILE_PATH_XSCZ;
}
