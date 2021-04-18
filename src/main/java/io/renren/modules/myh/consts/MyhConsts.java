package io.renren.modules.myh.consts;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author tomchen
 * @date 2020-06-24
 */
@ConfigurationProperties(prefix = "renren.myh")
@Component
@Data
public class MyhConsts {

    private String UPLOAD_DIR;
    private String FILE_PREFIX;

    private Map<String,String> dwNumMap;
//
//    {
//        dwNumMap.put("hs", "27");
//        dwNumMap.put("sjy", "11");
//        dwNumMap.put("xs", "10");
//        dwNumMap.put("nsq", "10");
//        dwNumMap.put("ch", "8");
//        dwNumMap.put("xh", "7");
//        dwNumMap.put("jw", "6");
//        dwNumMap.put("jy", "10");
//        dwNumMap.put("zx", "11");
//        dwNumMap.put("hss", "6");
//        dwNumMap.put("fx", "5");
//        dwNumMap.put("ym", "8");
//        dwNumMap.put("gq", "4");
//        dwNumMap.put("gd", "4");
//        dwNumMap.put("gz", "4");
//        dwNumMap.put("ys", "4");
//        dwNumMap.put("xtj", "10");
//
//    }

}
