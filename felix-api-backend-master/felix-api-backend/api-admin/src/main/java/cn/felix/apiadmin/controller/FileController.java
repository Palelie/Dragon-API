package cn.felix.apiadmin.controller;

import cn.felix.apiadmin.utils.AvatarUtils;
import cn.felix.apicommon.common.BaseResponse;
import cn.felix.apicommon.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件接口
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    /**
     * 头像上传请求
     */
    @PostMapping("/upload")
    public BaseResponse<Map<String, Object>> uploadFile(@RequestBody MultipartFile file, HttpServletRequest request) throws IOException {
        Map<String, Object> result = new HashMap<>(2);
        // 上传并返回新文件url
        String imageUrl = AvatarUtils.uploadAvatar(file);
        result.put("url", imageUrl);
        return ResultUtils.success(result);
    }
}
