<template>
  <DivCustom label="Danh sách bộ môn theo cơ sở" customClasses="mt-5">
    <template #icon>
      <GlobalOutlined />
    </template>
    <template #extra>
      <a-tooltip title="Thêm mới bộ môn theo cơ sở">
        <a-button
          type="primary"
          @click="handleAddClick"
          class="flex items-center justify-center px-4"
        >
          Thêm Bộ Môn
        </a-button>
      </a-tooltip>
    </template>
    <div class="min-h-[360px]">
      <a-table
        :columns="columns"
        :data-source="departmentFacility"
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
                  @click="handleViewClick(record.departmentFacilityId)"
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

              <a-tooltip title="Chuyên ngành theo cơ sở">
                <a-button
                  @click="handleMajorFacility(record.departmentFacilityId)"
                  class="flex items-center justify-center w-8 h-8 cursor-pointer p-2"
                  style="
                    background-color: #ffc0a3 !important; /* Màu nền xanh nhạt */
                    color: #f77b00 !important;
                    border: none !important;
                  "
                >
                  <ExclamationCircleOutlined />
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
import { defineProps, defineEmits, h } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import {
  EditOutlined,
  ExclamationCircleOutlined,
  FormOutlined,
  GlobalOutlined
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
  departmentFacility: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
}>()
const emit = defineEmits(['page-change', 'add', 'view', 'major-facility'])

const columns: TableColumnsType = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: 5,
    align: 'center'
  },
  { title: 'Mã', key: 'facilityCode', dataIndex: 'facilityCode', width: 10, align: 'center' },
  { title: 'Tên', key: 'facilityName', dataIndex: 'facilityName', width: 10, align: 'center' },
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

const handleViewClick = (departmentFacilityId: string) => {
  emit('view', departmentFacilityId)
}

const handleMajorFacility = (departmentFacilityId: string) => {
  console.log('🆕 Gửi ID sang cha:', departmentFacilityId)
  emit('major-facility', departmentFacilityId)
}
</script>
