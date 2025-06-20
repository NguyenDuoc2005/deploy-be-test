<template>
  <DivCustom label="Danh sách sinh viên" customClasses="mt-5">
    <template #icon>
      <UsergroupAddOutlined />
    </template>
    <template #extra>
      <div class="flex items-center gap-4">
        <!-- Nút Thêm sinh viên -->
        <a-tooltip title="Thêm mới sinh viên">
          <a-button
            type="primary"
            @click="handleAddClick"
            class="flex items-center justify-center px-4"
          >
            Thêm sinh viên
          </a-button>
        </a-tooltip>

        <!-- Nút Import sinh viên -->
        <a-tooltip title="Import sinh viên">
          <a-button
            type="primary"
            @click="emit('openModalImportStudent')"
            class="flex items-center justify-center px-4"
          >
            Import sinh viên
          </a-button>
        </a-tooltip>

        <!-- Nút Download template -->
        <a-tooltip title="Download template">
          <a-button
            type="primary"
            @click="emit('downloadTemplateImport')"
            class="flex items-center justify-center px-4"
          >
            Download template
          </a-button>
        </a-tooltip>
        <a-tooltip title="Import Log">
          <a-button
            type="primary"
            @click="emit('importLog')"
            class="flex items-center justify-center px-4"
          >
            Lịch sử
          </a-button>
        </a-tooltip>

        <a-tooltip title="Download file import lỗi">
          <a-button
            type="primary"
            @click="emit('downloadImportLog')"
            class="flex items-center justify-center px-4"
          >
            Download file import lỗi
          </a-button>
        </a-tooltip>
      </div>
    </template>

    <div class="min-h-[360px]">
      <a-table
        :columns="columns"
        :data-source="student"
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
          <!-- Kiểm tra nếu là cột majorFacility -->
          <template v-if="column.key === 'majorFacility'">
            <a-tag :color="record.majorFacility ? 'success' : 'error'">
              {{ record.majorFacility || 'Chưa phân' }}
            </a-tag>
          </template>

          <!-- Xử lý các hành động -->
          <template v-if="column.key === 'operation'">
            <div class="flex gap-1 items-center justify-center text-center">
              <a-tooltip title="Chỉnh sửa sinh viên">
                <a-button
                  @click="handleViewClick(record.id)"
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

              <a-tooltip title="Xóa sinh viên">
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

              <a-tooltip title="Chuyên ngành/bộ môn/role">
                <a-button
                  @click="handleDetailClick(record.id)"
                  class="flex items-center justify-center w-8 h-8"
                  style="
                    background-color: #fef9c3 !important;
                    color: #f96807 !important;
                    border: none !important;
                  "
                >
                  <EyeOutlined />
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
import { TableColumnsType } from 'ant-design-vue'
import { router } from '@/routes/router'
import { ROUTES_CONSTANTS } from '@/constants/path'
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

import { DeleteOutlined, EyeOutlined, FormOutlined, IdcardOutlined, UsergroupAddOutlined } from '@ant-design/icons-vue'

defineProps<{
  searchQuery: string
  student: any[]
  paginationParams: { page: number; size: number }
  totalItems: number
}>()

const columns: TableColumnsType = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: 5,
    align: 'center'
  },
  { title: 'Mã sinh viên', key: 'code', dataIndex: 'code', width: 15, align: 'left' },
  { title: 'Tên sinh viên', key: 'name', dataIndex: 'name', width: 15, align: 'left' },
  { title: 'Email', key: 'email', dataIndex: 'email', width: 15, align: 'left' },
  {
    title: 'Chuyên ngành cơ sở',
    key: 'majorFacility',
    dataIndex: 'majorFacility',
    width: 15,
    align: 'left'
  },
  {
    title: 'Hành động',
    key: 'operation',
    width: 15,
    align: 'center',
    fixed: 'right'
  }
]
const emit = defineEmits([
  'page-change',
  'add',
  'view',
  'downloadTemplateImport',
  'openModalImportStudent',
  'delete',
  'importLog',
  'downloadImportLog'
])

const handlePageChange = (Pagination: any) => {
  emit('page-change', { page: Pagination.current, pageSize: Pagination.pageSize })
}

const handleAddClick = () => {
  emit('add')
}

const handleViewClick = (id: string) => {
  emit('view', id)
}

const handleDeleteClick = (id: string) => {
  emit('delete', id)
}

const handleDetailClick = (id: string) => {
  router.push({
    name: `${ROUTES_CONSTANTS.ADMIN.children.STUDENT_DETAIL.name}`,
    params: { id }
  })
}
</script>
