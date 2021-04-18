package io.renren.modules.mrtb.consts;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author tomchen
 * @date 2020-06-24
 */
@ConfigurationProperties(prefix = "renren.mrtb")
@Component
@Data
public class SysMrtbConsts {
    private String VIEWSTATE;
    private String EVENTVALIDATION;
    private String ACCOUNT;
    private String PASSWORD;
}
