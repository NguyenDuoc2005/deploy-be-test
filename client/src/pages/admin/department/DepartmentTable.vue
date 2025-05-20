<template>
  <DivCustom label="Danh sách bộ môn" customClasses="mt-5">
    <template #icon>
      <ReadOutlined />
    </template>
    <template #extra>
      <a-tooltip title="Thêm mới bộ môn">
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
        :data-source="department"
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
              <a-tooltip title="Quản lí chuyên ngành">
                <a-button
                  @click="handleManageMajor(record.departmentId)"
                  class="flex items-center justify-center w-8 h-8"
                  style="
                    background-color: #b9f7dc !important; /* Màu nền xanh nhạt */
                    color: #798e00 !important;
                    border: none !important;
                  "
                >
                  <ReadOutlined />
                </a-button>
              </a-tooltip>

              <a-tooltip title="Cập nhật">
                <a-button
                  @click="handleViewClick(record.departmentId)"
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

              <a-tooltip title="Bộ môn theo cơ sở">
                <a-button
                  @click="handleNavigateToDepartmentFacility(record.departmentId)"
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
import { TableColumnsType, Tag } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { ROUTES_CONSTANTS } from '@/constants/path'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faReceipt, faPenToSquare, faCircleInfo, faFilter } from '@fortawesome/free-solid-svg-icons'
import {
  ExclamationCircleOutlined,
  ExclamationOutlined,
  FormOutlined,
  MoreOutlined,
  ReadOutlined
} from '@ant-design/icons-vue'

library.add(faReceipt, faPenToSquare, faCircleInfo, faFilter)

defineProps<{
  searchQuery: string
  department: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
}>()

const emit = defineEmits(['page-change', 'add', 'view', 'manage-major'])

const columns: TableColumnsType = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: 5,
    align: 'center'
  },
  { title: 'Mã', key: 'departmentCode', dataIndex: 'departmentCode', width: 10, align: 'center' },
  { title: 'Tên', key: 'departmentName', dataIndex: 'departmentName', width: 10, align: 'center' },
  {
    title: 'Trạng thái',
    key: 'departmentStatus',
    dataIndex: 'departmentStatus',
    width: 10,
    align: 'center',

    customRender: ({ text }) => {
      return h(
        Tag,
        {
          color: text === 'ACTIVE' ? 'green' : 'red'
        },
        () => (text === 'ACTIVE' ? 'Hoạt động' : 'Ngừng hoạt động')
      )
    }
  },
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

const handleViewClick = (departmentId: string) => {
  emit('view', departmentId)
}

const handleManageMajor = (departmentId: string) => {
  emit('manage-major', departmentId)
}

const router = useRouter()
const handleNavigateToDepartmentFacility = (departmentId: string) => {
  router.push({
    path: ROUTES_CONSTANTS.ADMIN.children.DEPARTMENT_FACILITY.path,
    query: { departmentId }
  })
}
</script>
