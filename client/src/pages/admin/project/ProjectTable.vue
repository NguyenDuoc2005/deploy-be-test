<template>
  <DivCustom label="Danh sách dự án" customClasses="mt-5">
    <template #icon>
      <ProjectOutlined />
    </template>
    <template #extra>
      <a-tooltip title="Thêm mới dự án">
        <a-button
          type="primary"
          @click="handleAddClick"
          class="flex items-center justify-center px-4"
          >Thêm dự án
        </a-button>
      </a-tooltip>
    </template>
    <div class="min-h-[360px]">
      <a-table
        :columns="columns"
        :data-source="data"
        :pagination="{
          current: page,
          pageSize: size,
          total: totalItems,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '30', '40', '50']
        }"
        :scroll="{ x: 1300, y: 300 }"
        @change="handlePageChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getTextStatus(record.status) }}
            </a-tag>
          </template>

          <template v-if="column.key === 'operation'">
            <div class="flex items-center gap-1 justify-center">
              <a-tooltip title="Chỉnh sửa dự án">
                <a-button
                  @click="handleViewClick(record.id, record.idFacility)"
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
              <a-tooltip title="Đổi trang thái">
                <a-button
                  @click="handleDeleteClick(record.id)"
                  class="flex items-center justify-center w-8 h-8"
                  style="
                    background-color: #fee2e2 !important;
                    color: #d81a6c !important;
                    border: none !important;
                  "
                >
                  <DeleteOutlined />
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
import { TableColumnsType, Tag } from 'ant-design-vue'
import { router } from '@/routes/router'
import { ROUTES_CONSTANTS } from '@/constants/path'
import { projectResponse } from '@/services/api/admin/project/project.api'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
library.add(
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight,
  faTrash,
  faTrashAlt,
  faEye
)
import {
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight,
  faTrash,
  faTrashAlt,
  faEye
} from '@fortawesome/free-solid-svg-icons'
import {
  DeleteOutlined,
  FormOutlined,
  ProjectOutlined,
  ScheduleOutlined
} from '@ant-design/icons-vue'
const props = defineProps<{
  data: projectResponse[]
  page: number
  size: number
  totalItems: number
}>()

const emit = defineEmits(['page-change', 'add', 'view', 'delete', 'idFacility'])

const getStatusColor = (status) => {
  switch (status) {
    case 'CHUA_DIEN_RA':
      return 'red'
    case 'DANG_DIEN_RA':
      return 'success'
    case 'DA_DIEN_RA':
      return 'blue'
  }
}

const getTextStatus = (status) => {
  switch (status) {
    case 'CHUA_DIEN_RA':
      return 'Chưa diễn ra'
    case 'DANG_DIEN_RA':
      return 'Đang diễn ra'
    case 'DA_DIEN_RA':
      return 'Đã diễn ra'
  }
}

const columns: TableColumnsType = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: 40,
    align: 'left',
    fixed: 'left'
  },
  {
    title: 'Mã dự án',
    key: 'codeProject',
    dataIndex: 'codeProject',
    width: 100,
    align: 'left',
    fixed: 'left'
  },
  { title: 'Tên dự án', key: 'nameProject', dataIndex: 'nameProject', width: 100, align: 'left' },
  {
    title: 'Bộ môn',
    key: 'nameDepartment',
    dataIndex: 'nameDepartment',
    width: 100,
    align: 'left'
  },
  {
    title: 'Bắt đầu',
    key: 'startTime',
    dataIndex: 'startTime',
    width: 100,
    align: 'center'
  },
  {
    title: 'Kết thúc',
    key: 'endTime',
    dataIndex: 'endTime',
    width: 100,
    align: 'center'
  },
  {
    title: 'Trạng thái',
    key: 'status',
    dataIndex: 'status',
    width: 100,
    align: 'center',
    customRender: ({ text }) => {
      return h(
        Tag,
        { color: text === 0 ? 'red' : text === 2 ? 'blue' : 'yellow', class: 'ml-3.5' },
        {
          default: () => (text === 0 ? 'Chưa hoạt động' : text === 1 ? 'Hoạt động' : 'Đã kết thúc')
        }
      )
    }
  },
  {
    title: 'Hành động',
    key: 'operation',
    width: 60,
    align: 'center',
    fixed: 'right'
  }
]

const handlePageChange = (pagination: any) => {
  emit('page-change', { page: pagination.current, pageSize: pagination.pageSize })
}

const handleAddClick = () => {
  emit('add')
}

const handleViewClick = (id: string, idFacility: string) => {
  emit('view', id, idFacility)
}

const handleDeleteClick = (id: string) => {
  emit('delete', id)
}

const handleDetailClick = (id: string) => {
  router.push({
    name: `${ROUTES_CONSTANTS.ADMIN.children.STAFF_DETAIL.name}`,
    params: { id }
  })
}
</script>
