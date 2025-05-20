<template>
    <a-modal :title="`Tổng kết dự án ${dataSummaryProject?.name}`" :open="props.isOpen" width="800px"
        @cancel="closeModal">
        <template #footer>
            <a-popconfirm title="Bạn có chắc chắn muốn lưu thay đổi?" @confirm="handleSubmit" ok-text="Đồng ý"
                cancel-text="Huỷ">
                <a-button type="primary">Kết thúc</a-button>
            </a-popconfirm>
            <a-button @click="closeModal">Huỷ</a-button>
        </template>
        <div class="flex  h-[300px] ">
            <div class="w-2/5 p-4">
                <div class="line">
                    <p class="title-line">Tên dự án : </p>
                    <p class="">{{ dataSummaryProject?.name }}</p>
                </div>
                <div class="line">
                    <p class="title-line">Mã dự án : </p>
                    <p class="">{{ dataSummaryProject?.code }}</p>
                </div>
                <div class="line">
                    <p class="title-line">Chuyên ngành cơ sở : </p>
                    <p class="">{{ dataSummaryProject?.nameDepartment }}</p>
                </div>
                <div class="line">
                    <p class="title-line">Trạng thái : </p>
                    <a-tag :color="`${getColorStatusProject(dataSummaryProject?.status as StatusProject)}`">{{
                        getTextStatusProject(dataSummaryProject?.status as StatusProject) }}</a-tag>
                </div>
                <div class="line">
                    <p class="title-line">Ngày bắt đầu : </p>
                    <p class="">{{ formatDate(dataSummaryProject?.startTime as number) }}</p>
                </div>
                <div class="line">
                    <p class="title-line">Ngày kết thúc : </p>
                    <p class="">{{ formatDate(dataSummaryProject?.endTime as number) }}</p>
                </div>
            </div>
            <div class="w-3/5 p-4">
                <v-chart class="chart" :option="optionCharts" autoresize />
            </div>
        </div>
    </a-modal>
</template>

<script lang="ts" setup>
import { finishEarlyProject, getDetailSummaryProject, ProjectDetailSummaryResponse } from '@/services/api/manage/project/maproject.api';
import { countTodoByTodoStatus } from '@/services/api/manage/todo/todo.api';
import { PieChart } from 'echarts/charts';
import {
    LegendComponent,
    TitleComponent,
    TooltipComponent,
} from 'echarts/components';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { computed, defineProps, ref, watch } from 'vue';
import VChart from 'vue-echarts';
import { toast } from 'vue3-toastify';

const props = defineProps<{
    isOpen: boolean,
    idProject: string,
}>();

type StatusProject = "CHUA_DIEN_RA" | "DANG_DIEN_RA" | "DA_DIEN_RA";

type StatusTodo = 'CHUA_HOAN_THANH' | 'DA_HOAN_THANH' | 'HOAN_THANH_SOM' | 'QUA_HAN'

const emits = defineEmits(['close', 'success']);

const getTextStatusProject = (status: StatusProject) => {
    switch (status) {
        case 'CHUA_DIEN_RA':
            return 'Chưa diễn ra';
        case 'DANG_DIEN_RA':
            return 'Đang diễn ra';
        case 'DA_DIEN_RA':
            return 'Đã diễn ra';
    }
}

const getColorStatusProject = (status: StatusProject) => {
    switch (status) {
        case 'CHUA_DIEN_RA':
            return 'red'
        case 'DANG_DIEN_RA':
            return 'green'
        case 'DA_DIEN_RA':
            return 'orange'
        default:
            return 'gray'
    }
}

const getTextStatusTodo = (status: StatusTodo) => {
    const translations = {
        CHUA_HOAN_THANH: "Chưa hoàn thành",
        DA_HOAN_THANH: "Đã hoàn thành",
        HOAN_THANH_SOM: "Hoàn thành sớm",
        QUA_HAN: "Quá hạn"
    };

    return translations[status] || "Không xác định";
}

const dataSummaryProject = ref<ProjectDetailSummaryResponse>();

const fetchDetailSummaryProject = async () => {
    const res = await getDetailSummaryProject(props.idProject);

    dataSummaryProject.value = res.data;
}

watch(() => props.idProject,
    (newId) => {
        fetchDetailSummaryProject();
        fetchDataCharts();
    }
)

const formatDate = (timestamp: number) => {
    if (!timestamp) return 'N/A'
    const date = new Date(timestamp)
    return date.toLocaleDateString('vi-VN')
}

// charts

use([
    CanvasRenderer,
    PieChart,
    TitleComponent,
    TooltipComponent,
    LegendComponent,
]);

const dataCharts = ref<{ value: number, name: string }[]>();

const fetchDataCharts = async () => {
    const res = await countTodoByTodoStatus(props.idProject);

    dataCharts.value = res.data.map((data) => ({ value: data.amount, name: getTextStatusTodo(data.todoStatus as StatusTodo) }))
}

watch(dataSummaryProject, (newValue) => {

})

const optionCharts = computed(() => ({
    title: {
        text: 'Tỷ lệ công việc hoàn thành',
        left: 'center',
        textStyle: {
            fontFamily: 'Arial, sans-serif',
            fontSize: 18,
        }
    },
    tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)',
    },
    legend: {
        orient: 'vertical',
        top: 40,
        right: 'top',
        data: dataCharts.value?.map((data) => data.name) || [],
    },
    series: [
        {
            name: 'Tỷ lệ công việc hoàn thành',
            type: 'pie',
            radius: '55%',
            center: ['50%', '60%'],
            data: dataCharts.value || [],
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)',
                },
            },
            top: '40',
        },
    ],
}));

const handleSubmit = async () => {
    try {
        await finishEarlyProject(props.idProject);
        toast.success('Thay đổi trạng thái thành công');
        emits('success');

        closeModal();
    } catch (error) {
        toast.error('Có lỗi xảy ra');
        console.log(error);
    }
}

const closeModal = () => {
    console.log('close modal')
    emits('close');
}

</script>

<style scoped>
.line {
    display: flex;
    align-items: center;
    font-size: 16px;
}

.line+.line {
    margin-top: 10px;
}

.line .title-line {
    font-weight: 600;
    margin-right: 5px;
}
</style>