package cn.felix.apicommon.common;

/**
 * 返回工具类
 */
public class ResultUtils {
    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @return
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse(errorCode.getCode(), null, message);
    }

    public static <T> BaseResponse<T> dataOk(T data) {
        return new BaseResponse<T>(0 , data,"success");
    }


    public static <T> BaseResponse<T> fail() {
        return new BaseResponse<T>(50001, "system error");
    }

    public static <T> BaseResponse<T> fail(ErrorCode errorCode) {
        return new BaseResponse<T>(errorCode.getCode(), errorCode.getMessage());
    }

    public static <T> BaseResponse<T> fail(T data, String message) {
        return new BaseResponse<T>(50001, data, message);
    }

    public static <T> BaseResponse<T> fail(int code, String message) {
        return new BaseResponse<T>(code, null, message);
    }

    public static <T> BaseResponse<T> fail(String message) {
        return new BaseResponse<T>(50000, null, message);
    }

}
