package cn.felix.apicommon.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MethodEnum {

    /**
     *
     */
    GET(0,"GET"),
    POST(1,"POST")
    ;
    int code;
    String desc;

    public static String getDesById(Integer code) {
        MethodEnum[] enums = values();
        for (MethodEnum aEnum : enums) {
            if (aEnum.code == code) {
                return aEnum.desc;
            }
        }
        return "";
    }

    public static MethodEnum getById(Integer code) {
        MethodEnum[] enums = values();
        for (MethodEnum aEnum : enums) {
            if (aEnum.code == code) {
                return aEnum;
            }
        }
        return null;
    }


}
