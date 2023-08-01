import { listInterfaceInfoVOByPageUsingPOST } from '@/services/felix-api-backend/interfaceInfoController';
import { addUserInterfaceInfoUsingPOST } from '@/services/felix-api-backend/userInterfaceInfoController';
import { ActionType, PageContainer, ProColumns, ProTable } from '@ant-design/pro-components';
import { Button, Layout, message } from 'antd';
import Search from 'antd/es/input/Search';
import { Content, Header } from 'antd/es/layout/layout';
import React, { useEffect, useRef, useState } from 'react';
import { history } from 'umi';

const headerStyle: React.CSSProperties = {
    textAlign: 'center',
    height: '64px',
    paddingInline: '30%',
    lineHeight: '64px',
    color: '#fff',
    background: '#fcfcfc',
};

const contentStyle: React.CSSProperties = {
    minHeight: 120,
    lineHeight: '120px',
};


const Index: React.FC = () => {
    const [loading, setLoading] = useState(false);
    const [list, setList] = useState<API.InterfaceInfoVO[]>([]);
    const [total, setTotal] = useState<number>(0);
    const ref = useRef<ActionType>();

    const loadData = async (searchText = '', current = 1, pageSize = 10) => {
        setLoading(true);
        try {
            await listInterfaceInfoVOByPageUsingPOST({
                searchText,
                current,
                pageSize,
            }).then((res) => {
                setList(res?.data?.records ?? []);
                setTotal(res?.data?.total ?? 0);
            });
        } catch (error: any) {
            message.error('请求失败，' + error.message);
        }
        setLoading(false);
    };


    /**
     * table 展示的列
     * */
    const columns: ProColumns<API.InterfaceInfoVO>[] = [
        {
            title: 'id',
            dataIndex: 'id',
            valueType: 'index',
            align: 'center',
        },
        {
            title: '接口名称',
            dataIndex: 'name',
            valueType: 'text',
            align: 'center',
        },
        {
            title: '描述',
            dataIndex: 'description',
            valueType: 'textarea',
            align: 'center',
        },
        {
            title: '请求方法',
            dataIndex: 'method',
            valueType: 'text',
            align: 'center',
        },
        {
            title: '状态',
            dataIndex: 'status',
            hideInForm: true,
            valueEnum: {
                0: {
                    text: '关闭',
                    status: 'Default',
                },
                1: {
                    text: '开启',
                    status: 'Processing',
                },
            },
            align: 'center',
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            valueType: 'dateTime',
            align: 'center',
        },
        {
            title: '操作',
            dataIndex: 'option',
            valueType: 'option',
            render: (_, record) => {
                return record.isOwnerByCurrentUser ? (
                    <Button
                        type="primary"
                        key="onlineUse"
                        onClick={() => {
                            history.push(`/interface_info/${record.id}`);
                        }}
                    >
                        在线调用
                    </Button>
                ) : (
                    <Button
                        key="applyInterface"
                        onClick={async () => {
                            const res = await addUserInterfaceInfoUsingPOST({
                                interfaceInfoId: Number(record.id),
                            });
                            if (res.code === 0) {
                                message.success('申请成功');
                                // 刷新表格
                                await loadData();
                            }
                        }}
                    >
                        开通接口
                    </Button>
                );
            },
        },
    ];

    useEffect(() => {
        loadData();
    }, []);

    const onSearch = (value: string) => {
        loadData(value);
    };

    return (
        <PageContainer>
            <Layout>
                <Header style={headerStyle}>
                    <Search
                        size={'large'}
                        placeholder="请输入接口名称或描述"
                        onSearch={onSearch}
                        enterButton
                    />
                </Header>
                <Content style={contentStyle}>
                    <ProTable<API.RequestParamsRemarkVO>
                        rowKey="id"
                        toolBarRender={false}
                        columns={columns}
                        dataSource={list}
                        loading={loading}
                        actionRef={ref}
                        pagination={{
                            showTotal: (total) => {
                                return '总数：' + total;
                            },
                            total,
                            pageSize: 10,
                            onChange: (page, pageSize) => {
                                loadData('', page, pageSize);
                            },
                        }}
                        search={false}
                    />
                </Content>
            </Layout>
        </PageContainer>
    );
};

export default Index;
