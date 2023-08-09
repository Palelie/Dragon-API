import type { RequestOptions } from '@@/plugin-request/request';
import type { RequestConfig } from '@umijs/max';
import {message} from "antd";

// 错误处理方案： 错误类型
enum ErrorShowType {
    SILENT = 0,
    WARN_MESSAGE = 1,
    ERROR_MESSAGE = 2,
    NOTIFICATION = 3,
    REDIRECT = 9,
}

// 与后端约定的响应数据格式
interface ResponseStructure {
    success: boolean;
    data: any;
    errorCode?: number;
    errorMessage?: string;
    showType?: ErrorShowType;
}

/**
 * @name 错误处理
 * pro 自带的错误处理， 可以在这里做自己的改动
 * @doc https://umijs.org/docs/max/request#配置
 */
export const requestConfig: RequestConfig = {
    //baseURL: 'http://localhost:8101',
    baseURL: 'http://123.60.212.37:8101',
    withCredentials: true,

    // 请求拦截器
    requestInterceptors: [
        (config: RequestOptions) => {
            // 拦截请求配置，进行个性化处理。
            const url = config?.url?.concat('?token = 123');
            return { ...config, url };
        },
    ],

    // 响应拦截器
    responseInterceptors: [
        (response) => {
            // 拦截响应数据，进行个性化处理
            const { data } = response as unknown as ResponseStructure;

            if (data.code !== 0) {
                message.error(data.message).then(r => {})
            }
            return response;
        },
    ],
};
