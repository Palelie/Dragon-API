import {
    getUserVOByIdUsingGET,
    updateSecretKeyUsingPOST,
    updateUserUsingPOST,
    userLoginUsingPOST,
} from '@/services/felix-api-backend/userController';
import {useModel} from '@@/exports';
import {
    CommentOutlined,
    FieldTimeOutlined,
    LoadingOutlined,
    LockOutlined,
    PlusOutlined,
    UnlockOutlined,
    UserOutlined,
    VerifiedOutlined,
} from '@ant-design/icons';
import {PageContainer, ProForm, ProFormInstance, ProFormText} from '@ant-design/pro-components';
import {Button, Card, Col, Divider, message, Modal, Row, Typography, Upload, UploadFile, UploadProps,} from 'antd';
import {RcFile, UploadChangeParam} from 'antd/es/upload';
import React, {useEffect, useRef, useState} from 'react';
import {uploadFileUsingPOST} from "@/services/felix-api-backend/fileController";

const { Paragraph } = Typography;

const avatarStyle: React.CSSProperties = {
    width: '100%',
    textAlign: 'center',
};
const buttonStyle: React.CSSProperties = {
    marginLeft: '30px',
};

/**
 * 上传前校验
 * @param file 文件
 */
const beforeUpload = (file: RcFile) => {
    const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
    if (!isJpgOrPng) {
        message.error('仅允许上传 JPG/PNG 格式的文件!');
    }
    const isLt2M = file.size / 1024 / 1024 < 5;
    if (!isLt2M) {
        message.error('文件最大上传大小为 5MB!');
    }
    return isJpgOrPng && isLt2M;
};

const Profile: React.FC = () => {
    const [data, setData] = useState<API.UserVO>({});
    const [visible, setVisible] = useState<boolean>(false);
    const [flag, setFlag] = useState<boolean>(false);
    const [open, setOpen] = useState<boolean>(false);
    const [loading, setLoading] = useState(false);
    const [imageUrl, setImageUrl] = useState<string>();
    const { initialState, setInitialState } = useModel('@@initialState');
    const formRef = useRef<
        ProFormInstance<{
            userPassword: string;
        }>
    >();

    useEffect(() => {
        try {
            getUserInfo(initialState?.loginUser?.id);
        } catch (e: any) {
            console.log(e);
        }
    }, []);

    // 获取用户信息
    const getUserInfo = async (id: any) => {
        return getUserVOByIdUsingGET({ id }).then((res) => {
            if (res.data) {
                setInitialState((s: any) => ({ ...s, loginUser: res.data }));
                setData(res.data);
                setImageUrl(res.data.userAvatar);
            }
        });
    };

    // 显示秘钥
    const showSecretKey = async () => {
        let userPassword = formRef?.current?.getFieldValue('userPassword');

        // 登录
        const res = await userLoginUsingPOST({
            userAccount: data?.userAccount,
            userPassword: userPassword,
        });
        if (res.code === 0) {
            setOpen(false);
            setVisible(true);
            formRef?.current?.resetFields();
        }
    };

    // 更新用户头像
    const updateUserAvatar = async (id: number, userAvatar: string) => {
        // 更新用户头像
        const res = await updateUserUsingPOST({
            id,
            userAvatar,
        });
        if (res.code !== 0) {
            message.success(`更新用户头像失败`);
        } else {
            getUserInfo(id);
        }
    };

    /**
     * 上传图片
     * @param info
     */
    const handleChange: UploadProps['onChange'] =(info: UploadChangeParam<UploadFile>) => {
        if (info.file.status === 'uploading') {
            setLoading(true);
            return;
        }
        if (info.file.status === 'done') {
            // const res = await uploadFileUsingPOST({
            //     file: info.file.originFileObj as any
            // })
            if (info.file.response.code === 0) {
                message.success(`上传成功`);
                const id = initialState?.loginUser?.id as number;
                const userAvatar = info.file.response.data.url;
                setLoading(false);
                setImageUrl(userAvatar);
                updateUserAvatar(id, userAvatar);
            }else {
                message.error(info.file.response.message);
            }
        }
    };

    const uploadButton = (
        <div>
            {loading ? <LoadingOutlined /> : <PlusOutlined />}
            <div style={{ marginTop: 8 }}>Upload</div>
        </div>
    );

    // 重置秘钥
    const resetSecretKey = async () => {
        try {
            let userPassword = formRef?.current?.getFieldValue('userPassword');
            // 登录
            const res = await userLoginUsingPOST({
                userAccount: data?.userAccount,
                userPassword: userPassword,
            });
            if (res.code === 0) {
                const res = await updateSecretKeyUsingPOST({
                    id: data?.id,
                });
                if (res.data) {
                    getUserInfo(data?.id);
                    message.success('重置成功！');
                    setOpen(false);
                }
            }
        } catch (e: any) {
            console.log(e);
        }
    };
    return (
        <PageContainer>
            <Row gutter={24}>
                <Col span={8}>
                    <Card title="个人信息" bordered={false}>
                        <Row>
                            <Col style={avatarStyle}>
                                <Upload
                                    name="file"
                                    listType="picture-circle"
                                    showUploadList={false}
                                    //action="http://124.70.63.241:8101/api/file/upload"
                                    action="http://localhost:8101/api/file/upload"
                                    beforeUpload={beforeUpload}
                                    onChange={handleChange}
                                >
                                    {imageUrl ? (
                                        <img
                                            src={data?.userAvatar}
                                            alt="avatar"
                                            style={{ width: '100%', borderRadius: '50%' }}
                                        />
                                    ) : (
                                        uploadButton
                                    )}
                                </Upload>
                            </Col>
                        </Row>
                        <Divider />
                        <Row>
                            <Col>
                                <UserOutlined /> 用户名称：{data?.userName}
                            </Col>
                        </Row>
                        <Divider />
                        <Row>
                            <Col>
                                <CommentOutlined /> 用户账号：{data?.userAccount}
                            </Col>
                        </Row>
                        <Divider />
                        <Row>
                            <Col>
                                <VerifiedOutlined /> 用户角色：{data?.userRole}
                            </Col>
                        </Row>
                        <Divider />
                        <Row>
                            <Col>
                                <FieldTimeOutlined /> 注册时间：{data?.createTime}
                            </Col>
                        </Row>
                    </Card>
                </Col>
                <Col span={16}>
                    <Card title="秘钥操作" bordered={false}>
                        <Row>
                            <Col>
                                {visible ? (
                                    <Paragraph
                                        copyable={{
                                            text: data?.accessKey,
                                        }}
                                    >
                                        <LockOutlined /> accessKey：{data?.accessKey}
                                    </Paragraph>
                                ) : (
                                    <Paragraph>
                                        <UnlockOutlined /> secretKey：*********
                                    </Paragraph>
                                )}
                            </Col>
                        </Row>
                        <Divider />
                        <Row>
                            <Col>
                                {visible ? (
                                    <Paragraph
                                        copyable={{
                                            text: data?.secretKey,
                                        }}
                                    >
                                        <UnlockOutlined /> secretKey：{data?.secretKey}
                                    </Paragraph>
                                ) : (
                                    <Paragraph>
                                        <UnlockOutlined /> secretKey：*********
                                    </Paragraph>
                                )}
                            </Col>
                        </Row>
                        <Divider />
                        <Row>
                            <Col>
                                {!visible ? (
                                    <Button
                                        type="primary"
                                        onClick={() => {
                                            setOpen(true);
                                            setFlag(true);
                                        }}
                                    >
                                        查看秘钥
                                    </Button>
                                ) : (
                                    <Button type="primary" onClick={() => setVisible(false)}>
                                        隐藏秘钥
                                    </Button>
                                )}
                                <Button
                                    style={buttonStyle}
                                    onClick={() => {
                                        setOpen(true);
                                        setFlag(false);
                                    }}
                                    type="primary"
                                    danger
                                >
                                    重置秘钥
                                </Button>
                            </Col>
                        </Row>
                    </Card>
                </Col>
            </Row>
            <Modal
                title="查看秘钥"
                open={open}
                onOk={flag ? showSecretKey : resetSecretKey}
                onCancel={() => setOpen(false)}
            >
                <ProForm<{
                    userPassword: string;
                }>
                    formRef={formRef}
                    formKey="check-user-password-form"
                    autoFocusFirstInput
                    submitter={{
                        resetButtonProps: {
                            style: {
                                display: 'none',
                            },
                        },
                        submitButtonProps: {
                            style: {
                                display: 'none',
                            },
                        },
                    }}
                >
                    <ProFormText.Password name="userPassword" placeholder="请输入用户密码" />
                </ProForm>
            </Modal>
        </PageContainer>
    );
};

export default Profile;
