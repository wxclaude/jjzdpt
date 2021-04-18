package io.renren.modules.df.consts;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author tomchen
 * @date 2020-06-24
 */
@ConfigurationProperties(prefix = "renren.df")
@Component
@Data
public class DfConsts {

    private String SJFN;
    private String ZHZH;
    private String JQZLKH;
    private String QBXX;
    private String KJXX;
    private String QBXS;
    private String ZDMB;

}
