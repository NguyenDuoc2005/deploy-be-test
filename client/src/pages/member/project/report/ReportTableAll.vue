<template>
    <div class="min-h-[360px] bg-white rounded-xl p-4 shadow-md">
      <a-table 
        :columns="columns" 
        :data-source="report" 
        class="custom-table"
        :pagination="{
          current: paginationParams.page,
          pageSize: paginationParams.size,
          total: totalItems,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '30', '40', '50']
        }" 
        :scroll="{ x: 1000 }" 
        @change="handlePageChange"
        bordered>
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'statusReport'">
            <a-tag :color="getStatusColor(record.statusReport)" class="">
              {{ convertStatus(record.statusReport) }}
            </a-tag>
          </template>
          <template v-if="column.key === 'operation'">
            <div class="flex gap-2 justify-center">
              <a-button type="primary" ghost @click="emit('view', record)">Xem</a-button>
            </div>
          </template>
        </template>
      </a-table>
    </div>
</template>

<script setup lang="ts">
import DivCustom from '@/components/custom/Div/DivCustom.vue';
import { TableColumnsType } from 'ant-design-vue';
import { getDateFormat } from '@/utils/commom.helper';
import { h } from 'vue';

const props = defineProps<{ searchQuery: string; report: any[]; paginationParams: { page: number; size: number }; totalItems: number }>();
const emit = defineEmits(['page-change', 'view']);

const columns: TableColumnsType = [
  { title: 'STT', key: 'orderNumber', dataIndex: 'orderNumber', width: 10, align: 'center', customRender: ({ index }) => index + 1 },
  

  {
    title: 'Báo cáo',
    key: 'reportDetails',
    dataIndex: 'reportDetails',
    width: 200,
    align: 'center',
    customRender: ({ record }) => {

      return h('div', { style: 'text-align: left;' }, [
        h('p', { style: 'margin: 0;' }, [
          h('span', { style: 'font-weight: bold;' }, 'Công việc hôm nay: '),
          h('span', {}, record.wordDoneTodayReport || 'Không có')
        ]),
        h('p', { style: 'margin: 0;' }, [
          h('span', { style: 'font-weight: bold;' }, 'Khó khăn gặp phải: '),
          h('span', {}, record.obstaclesReport || 'Không có')
        ]),
        h('p', { style: 'margin: 0;' }, [
          h('span', { style: 'font-weight: bold;' }, 'Kế hoạch ngày mai: '),
          h('span', {}, record.wordPlanTomorrowReport || 'Không có')
        ]),
      ]);

    }
  },


  { title: 'Thời gian', key: 'reportTime', dataIndex: ['reportTime'], width: 80, align: 'center', customRender: ({ record }) => getDateFormat(record.reportTime) },
  { title: 'Trạng thái', key: 'statusReport', dataIndex: 'statusReport', width: 80, align: 'center' }
];

const convertStatus = (status: string) => {
  switch (status) {
    case 'CHUA_BAO_CAO': return 'Chưa báo cáo';
    case 'DA_BAO_CAO': return 'Đã báo cáo';
    case 'BAO_CAO_MUON': return 'Báo cáo muộn';
    default: return 'Không xác định';
  }
};

const getStatusColor = (status: string) => {
  switch (status) {
    case 'CHUA_BAO_CAO': return 'red';
    case 'DA_BAO_CAO': return 'green';
    case 'BAO_CAO_MUON': return 'orange';
    default: return 'gray';
  }
};

const handlePageChange = (pagination: any) => {
  emit('page-change', { page: pagination.current, pageSize: pagination.pageSize });
};
</script>

<style scoped>
/* shared-table.css */
.custom-table :deep(.ant-table-thead > tr > th) {
  color: #060606;
  font-weight: bold;
  text-align: center;
}

.custom-table :deep(.ant-table-tbody > tr:hover) {
  background: #f0faff;
  transition: 0.3s;
}

/* Đặt chiều cao tối đa & hỗ trợ cuộn dọc */
.table-wrapper .custom-table {
  max-height: 300px; /* 👈 Đảm bảo bảng không chiếm quá nhiều không gian */
  overflow-y: auto;
}

</style>
