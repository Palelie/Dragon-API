package cn.felix.apicommon.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 测试调用请求类
 *
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 请求参数
     */
    private String requestParams;
    /**
     * 主机号
     */
    private String host;
}
