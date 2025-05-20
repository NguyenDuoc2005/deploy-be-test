<template>
  <DivCustom label="Danh sách chuyên ngành theo cơ sở" customClasses="mt-5">
    <template #icon>
      <ClusterOutlined />
    </template>
    <template #extra>
      <a-tooltip title="Thêm mới chuyên ngành theo cơ sở">
        <a-button
          type="primary"
          @click="handleAddClick"
          class="flex items-center justify-center px-4"
        >
          Thêm chuyên ngành theo cơ sở
        </a-button>
      </a-tooltip>
    </template>
    <div class="min-h-[360px]">
      <a-table
        :columns="columns"
        :data-source="majorFacility"
        :pagination="{
          current: paginationParams.page,
          pageSize: paginationParams.size,
          total: totalItems,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '30', '40', '50']
        }"
        :scroll="{ y: 300 }"
        @change="handlePageChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'operation'">
            <div class="flex flex gap-1 justify-center">
              <a-tooltip title="Cập nhật">
                <a-button
                  @click="handleViewClick(record.majorFacilityId)"
                  class="flex items-center justify-center w-8 h-8"
                  style="
                    background-color: #fbe0fc !important; /* Màu nền xanh nhạt */
                    color: #bf63ed !important;
                    border: none !important;
                  "
                >
                  <FormOutlined />
                </a-button>
              </a-tooltip>
            </div>
          </template>
        </template>
      </a-table>
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import {
  AppstoreOutlined,
  ClusterOutlined,
  EditOutlined,
  FormOutlined
} from '@ant-design/icons-vue'
import { TableColumnsType } from 'ant-design-vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import {
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight
} from '@fortawesome/free-solid-svg-icons'

library.add(faReceipt, faPenToSquare, faCircleInfo, faFilter, faRotateRight)

defineProps<{
  searchQuery: string
  majorFacility: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
  departmentFacilityId: string
}>()

const emit = defineEmits(['page-change', 'add', 'view'])

const columns: TableColumnsType = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: 5,
    align: 'center'
  },
  { title: 'Mã', key: 'majorCode', dataIndex: 'majorCode', width: 10, align: 'center' },
  { title: 'Tên', key: 'majorName', dataIndex: 'majorName', width: 10, align: 'center' },

  {
    title: 'Hành động',
    key: 'operation',
    width: 10,
    align: 'center',
    fixed: 'right'
  }
]

const handlePageChange = (Pagination: any) => {
  emit('page-change', { page: Pagination.current, pageSize: Pagination.pageSize })
}

const handleAddClick = () => {
  emit('add')
}

const handleViewClick = (majorFacilityId: string) => {
  emit('view', majorFacilityId)
}
</script>
