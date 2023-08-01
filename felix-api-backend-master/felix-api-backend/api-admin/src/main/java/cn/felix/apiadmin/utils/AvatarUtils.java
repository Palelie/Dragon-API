package cn.felix.apiadmin.utils;

import cn.felix.apiadmin.exception.BusinessException;
import cn.felix.apicommon.common.ErrorCode;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;

import static cn.felix.apiadmin.constant.secretConstant.*;

/**
 * 头像上传工具
 */
public class AvatarUtils {

    /**
     * 获取图床网站token
     */
    private static String getToken(){
        HashMap<String, String> map = new HashMap<>();
        map.put("phoneNum",PHONE_NUM);
        map.put("pwd",PWD);
        String body = JSONUtil.toJsonStr(map);
        String res = HttpRequest.post(GET_TOKEN_URL)
                .form("phoneNum",PHONE_NUM)
                .form("pwd",PWD)
                .execute()
                .body();
        HashMap resMap = JSONUtil.toBean(res, HashMap.class);
        String token = (String) resMap.get("token");
        if (StrUtil.isBlank(token)){
            throw new BusinessException(ErrorCode.AVATAR_TOKEN_ERROR);
        }
        return token;
    }

    /**
     * 上传头像
     * @return 头像url地址
     */
    public static String uploadAvatar(MultipartFile multipartFile){
        // String token = getToken();

        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 1024 * 1024L;
        if (fileSize > ONE_M) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
        }
        if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
        }

        //将MultipartFile转换为File
        //文件上传前的名称
        String fileName = multipartFile.getOriginalFilename();
        File file = new File(fileName);
        OutputStream out = null;

        try{
            //获取文件流，以文件流的方式输出到新文件
//    InputStream in = multipartFile.getInputStream();
            out = new FileOutputStream(file);
            byte[] ss = multipartFile.getBytes();
            for(int i = 0; i < ss.length; i++){
                out.write(ss[i]);
            }
        }catch(IOException e){
            throw new BusinessException(ErrorCode.AVATR_FORMAT_ERROR);
        }finally {
            if (out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
            }
        }

        String resJson = HttpRequest.post(UPLOAD_AVATAR_URL)
                .header("token",TOKEN)
                .form("file", file)
                .execute()
                .body();

        HashMap resMap = JSONUtil.toBean(resJson, HashMap.class);

        JSONArray array = JSONUtil.parseArray(resMap.get("rows"));
        HashMap map = JSONUtil.toBean(array.get(0).toString(), HashMap.class);
        String url = (String) map.get("url");

        if (StrUtil.isBlank(url)){
            throw new BusinessException(ErrorCode.AVATAR_UPLOAD_ERROR);
        }

        // 操作完上的文件 需要删除在根目录下生成的文件
        File f = new File(file.toURI());
        f.delete();

        return url;
    }
}
