import {PageContainer} from '@ant-design/pro-components';
import React, {useEffect, useState} from 'react';
import ReactECharts from 'echarts-for-react';
import {listTopInvokeInterfaceInfoUsingGET} from "@/services/felix-api-backend/analysisController";

const InterfaceAnalysis: React.FC = () => {
    const [data, setData] = useState<API.InterfaceInfoVO[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        try {
            listTopInvokeInterfaceInfoUsingGET().then((res) => {
                if (res.data) {
                    setData(res.data);
                    setLoading(false);
                }
            })
        } catch (e: any) {
            console.log(e)
        }
    }, [])

    const chartData = data.map((item) => {
        return {
            name: item.name,
            value: item.totalNum
        }
    });

    const option = {
        title: {
            text: '调用次数统计',
            subtext: 'TOP3',
            left: 'center'
        },
        tooltip: {
            trigger: 'item'
        },
        legend: {
            orient: 'vertical',
            left: 'left'
        },
        series: [
            {
                name: '调用次数',
                type: 'pie',
                radius: '50%',
                data: chartData,
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };

    return (
        <PageContainer>
            <ReactECharts showLoading={loading} option={option} />
        </PageContainer>
    );
};

export default InterfaceAnalysis;
