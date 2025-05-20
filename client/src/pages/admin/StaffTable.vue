<template>
  <DivCustom label="Danh sách nhân viên" customClasses="mt-5">
    <template #icon>
      <IdcardOutlined />
    </template>
    <template #extra>
      <div class="flex">
        <a-tooltip title="Import nhân viên" class="ml-3">
          <a-button
            type="primary"
            @click="emit('openModalImportStaff')"
            class="flex items-center justify-center px-4"
          >
            Import Excel
          </a-button>
        </a-tooltip>
        <a-tooltip title="Download template" class="ml-3">
          <a-button
            type="primary"
            @click="emit('downloadTemplateImport')"
            class="flex items-center justify-center px-4"
          >
            Download Excel
          </a-button>
        </a-tooltip>

        <a-tooltip title="Thêm mới nhân viên" class="ml-3">
          <a-button
            type="primary"
            @click="handleAddClick"
            class="flex items-center justify-center px-4"
          >
            Thêm nhân viên
          </a-button>
        </a-tooltip>

        <a-tooltip title="Lịch sử import lỗi" class="ml-3">
          <a-button
            type="primary"
            @click="emit('history')"
            class="flex items-center justify-center px-4"
          >
            Lịch sử
          </a-button>
        </a-tooltip>

        <a-tooltip title="Download template" class="ml-3">
          <a-button
            type="primary"
            @click="emit('download')"
            class="flex items-center justify-center px-4"
          >
            Download File Import Lỗi
          </a-button>
        </a-tooltip>
      </div>
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
        :scroll="{ x: 1300, y: 1000 }"
        @change="handlePageChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'nameRoles'">
            <span v-if="record.nameRoles && record.nameRoles.trim() !== ''">
              <template v-for="(role, index) in record.nameRoles.split(',')" :key="index">
                <a-tag color="success" class="mr-1">
                  {{ role.trim() }}
                </a-tag>
                <!-- <span v-if="index !== record.nameRoles.split(',').length - 1">,</span> -->
              </template>
            </span>
            <span v-else>
              <a-tag color="error">Chưa có role</a-tag>
            </span>
          </template>

          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 'ACTIVE' ? 'success' : 'error'">
              {{ record.status === 'ACTIVE' ? 'Hoạt động' : 'Đã nghỉ' }}
            </a-tag>
          </template>

          <!-- :color="record.status === 'ACTIVE' ? 'success' : 'error'" -->
          <template v-if="column.key === 'operation'">
            <div class="flex items-center gap-1 justify-center">
              <a-tooltip title="Chỉnh sửa nhân viên">
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
              <a-tooltip title="Xóa nhân viên">
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
import { defineProps, defineEmits, h, onMounted } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
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
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { TableColumnsType } from 'ant-design-vue'
import { staffResponse } from '@/services/api/admin/staff.api'
import { router } from '@/routes/router'
import { ROUTES_CONSTANTS } from '@/constants/path'
import { DeleteOutlined, EyeOutlined, FormOutlined, IdcardOutlined } from '@ant-design/icons-vue'

const props = defineProps<{
  data: staffResponse[]
  page: number
  size: number
  totalItems: number
}>()

const getTagColor = (role) => {
  switch (role) {
    case 'ADMIN':
      return 'red'
    case 'Quản Lý':
      return 'blue'
    case 'Thành Viên':
      return 'green'
    default:
      return 'gray'
  }
}

const emit = defineEmits([
  'page-change',
  'add',
  'view',
  'delete',
  'openModalImportStaff',
  'downloadTemplateImport',
  'history',
  'download'
])

const columns: TableColumnsType = [
  {
    title: 'STT',
    key: 'orderNumber',
    dataIndex: 'orderNumber',
    width: 60,
    align: 'center',
    fixed: 'left',
    customRender: ({ index }) => index + 1
  },
  {
    title: 'Mã nhân viên',
    key: 'codeStaff',
    dataIndex: 'codeStaff',
    width: 100,
    align: 'left'
  },
  { title: 'Tên nhân viên', key: 'nameStaff', dataIndex: 'nameStaff', width: 100, align: 'left' },
  { title: 'Email Fpt', key: 'emailFpt', dataIndex: 'emailFpt', width: 150, align: 'left' },
  { title: 'Email Fe', key: 'emailFe', dataIndex: 'emailFe', width: 150, align: 'left' },
  { title: 'Cơ sở', key: 'nameFacility', dataIndex: 'nameFacility', width: 100, align: 'center' },
  { title: 'Role', key: 'nameRoles', dataIndex: 'nameRoles', width: '18%', align: 'left' },
  { title: 'Trạng thái', key: 'status', dataIndex: 'status', width: 100, align: 'center' },
  {
    title: 'Hành động',
    key: 'operation',
    width: 120,
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

const handleViewClick = (id: string) => {
  emit('view', id)
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
