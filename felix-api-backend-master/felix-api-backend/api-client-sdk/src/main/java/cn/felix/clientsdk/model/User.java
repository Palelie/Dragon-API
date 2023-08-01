package cn.felix.clientsdk.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class User {
    /**
     * 用户名
     */
    private String username;

    /**
     * 主机号
     */
    private String host;

}
