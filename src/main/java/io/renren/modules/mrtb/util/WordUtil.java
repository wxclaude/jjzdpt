package io.renren.modules.mrtb.util;


import cn.hutool.core.util.StrUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.Text;

import org.apache.commons.codec.binary.Base64;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @Descript TODO (利用freemark生成word及zip)
 * @author yeting
 * @date 2019年3月19日
 *
 */
public class WordUtil {
    public static Logger logger = LoggerFactory.getLogger(WordUtil.class);

    /**
     * 生成word文件(全局可用)
     * @param dataMap word中需要展示的动态数据，用map集合来保存
     * @param templateName word模板名称，例如：test.ftl
     * @param fileFullPath 要生成的文件全路径
     */
    @SuppressWarnings("unchecked")
    public static void createWord(Map dataMap, String templateName, String fileName,HttpServletResponse response) {
        logger.info("【createWord】：==>方法进入");
        //logger.info("【fileFullPath】：==>" + fileFullPath);
        logger.info("【templateName】：==>" + templateName);

        try {
            // 创建配置实例
            Configuration configuration = new Configuration();
            logger.info("【创建配置实例】：==>");

            // 设置编码
            configuration.setDefaultEncoding("UTF-8");
            logger.info("【设置编码】：==>");

            // 设置处理空值
            configuration.setClassicCompatible(true);

            // 设置错误控制器
//            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

//            String pathName = Text.class.getResource("/template").getFile();
//            File templateFile = new File(pathName);
//            logger.info("【pathName】：==>" + pathName);
//            logger.info("【templateFile】：==>" + templateFile.getName());
//            configuration.setDirectoryForTemplateLoading(templateFile);

            // 设置ftl模板文件加载方式
            configuration.setClassForTemplateLoading(WordUtil.class,"/template");

            //创建文件
            //File file = new File(fileFullPath);
            //// 如果输出目标文件夹不存在，则创建
            //if (!file.getParentFile().exists()) {
            //    file.getParentFile().mkdirs();
            //}

            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            // 将模板和数据模型合并生成文件
            Writer out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
            // 获取模板
            Template template = configuration.getTemplate(templateName);
            // 生成文件
            template.process(dataMap, out);

            // 清空缓存
            out.flush();
            // 关闭流
            out.close();

        } catch (Exception e) {
            logger.info("【生成word文件出错】：==>" + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 下载生成的文件(全局可用)
     * @param fullPath 全路径
     * @param response
     */
    public static void downLoadFile(String fullPath, HttpServletResponse response) {
        logger.info("【downLoadFile:fullPath】：==>" + fullPath);

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            //创建文件
            File file = new File(fullPath);
            String fileName = file.getName();

            //读文件流
            inputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);

            //清空响应
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream; charset=utf-8");
            // response.setContentType("application/msword");
            response.setHeader("Content-Disposition","attachment; filename=" + new String(fileName.getBytes(), "ISO8859-1"));
            response.setHeader("Content-Length", "" + file.length());

            //写文件流
            outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(buffer);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 下载网络文件到本地(主要用于下载简历附件)
     * @param urlAddress 网络url地址,为空时直接返回
     * @param fileFullPath 文件全路径
     */
    public static void createFromUrl(String urlAddress,String fileFullPath) {
        logger.info("【service:开始下载网络文件】:==> 网上文件地址：" + urlAddress + "文件保存路径:" + fileFullPath);

        if(StrUtil.isEmpty(urlAddress)) {
            return ;
        }

        DataInputStream dataInputStream = null;
        FileOutputStream fileOutputStream =null;
        try {

            URL url = new URL(urlAddress);

            dataInputStream = new DataInputStream(url.openStream());//打开网络输入流

            //创建文件
            File file = new File(fileFullPath);
            // 如果输出目标文件夹不存在，则创建
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            fileOutputStream = new FileOutputStream(file);//建立一个新的文件

            byte[] buffer = new byte[1024];
            int length;

            while((length = dataInputStream.read(buffer))>0){//开始填充数据
                fileOutputStream.write(buffer,0,length);
            }

            fileOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(dataInputStream!=null) {
                    dataInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if(fileOutputStream!=null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从网上或本地获得图片的base64码(主要用于插入生成word中的图片)
     * @param urlAddress 网络路径,二选一,目前有问题
     * @param pathAddress 本地路径，二选一
     * @return 返回base64码或null
     */
    public static String getImageBase(String urlAddress,String pathAddress) {
        byte[] buffer = null;
        InputStream inputStream = null;
        String imageCodeBase64 = null;

        try {
            if(!StrUtil.isEmpty(urlAddress)){
                URL url = new URL(urlAddress);
                inputStream = new DataInputStream(url.openStream());//打开网络输入流
                buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
            }else if(!StrUtil.isEmpty(pathAddress)){
                inputStream = new BufferedInputStream(new FileInputStream(new File(pathAddress)));//读文件流
                buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
            }else {
                return null;
            }

            imageCodeBase64 = Base64.encodeBase64String(buffer);
//            System.out.println(imageCodeBase64);
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(inputStream!=null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageCodeBase64;
    }




}