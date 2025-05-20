<template>
  <BreadcrumbDefault label="Quản lí sinh viên">
    <StudentFilter
      :searchQuery="state.searchQuery"
      @update:searchQuery="updateSearchQuery"
      @update:searchDF="updateSearchQueryDF"
    />

    <StudentTable
      :searchQuery="state.searchQuery"
      :student="state.student"
      :paginationParams="state.paginationParams"
      :totalItems="state.totalItems"
      @add="openAddModal"
      @view="openViewModal"
      @delete="deleteStudentById"
      @page-change="handlePageChange"
      @download-template-import="downloadTemplateExcel"
      @open-modal-import-student="handleOpenModalImport"
      @import-log="handleOpenModalImportHistory"
      @download-import-log="downloadImportLogExcel"
    />
  </BreadcrumbDefault>

  <StudentModal
    :open="state.isModalOpen"
    :id="state.id"
    :title="state.id ? 'Cập nhật sinh viên' : 'Thêm mới sinh viên'"
    @close="closeModal"
    @success="fetchStudent"
  />

  <StudentModalImport
    :open="state.isOpenModalImport"
    :title="'Import Sinh Viên'"
    @close="handleCloseModalImport"
    @fetch-all="fetchStudent"
    @update-log="handleNewLog"
  />

  <!-- <StudentModalImportLog
    ref="logRef"
    :open="dataStudentLog.isModalOpenLog"
    :title="'Sinh viên import lỗi'"
    :data="dataStudentLog.studentLog"
    @close="closeModalStudentLog"
  /> -->

  <a-modal
      v-model:visible="state.isOpenModalImportHistory"
      :width="1300"
      :footer="null"
      :z-index="9999"
      :wrap-style="{ display: 'flex', alignItems: 'center', justifyContent: 'center' }"
      :style="{ top: '10px' }"
      @cancel="state.isOpenModalImportHistory = false"
    >
      <HistoryImportStudentModalTable
        :history="state.history"
        :paginationParams="{ page: state.paginationParamsHistory.page, size: state.paginationParamsHistory.size }"
        :totalItems="state.totalItemsHistory"
        @close="handleCloseModalImportHistory"
        @page-change="handlePageChangelog"
      />
    </a-modal>
</template>

<script setup lang="ts">
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import StudentTable from './StudentTable.vue'
import StudentFilter from './StudentFilter.vue'
import StudentModal from './StudentModal.vue'
import { onMounted, reactive, ref, watch } from 'vue'
import {
  deleteStudent,
  downloadImportLog,
  getAllHistoryStudent,
  getStudent,
  historyImprort,
  ParamsGetStudent,
  StudentResponse
} from '@/services/api/admin/student.api'
import { debounce } from 'lodash'
import { Modal } from 'ant-design-vue'
import { toast } from 'vue3-toastify'

import { downloadExcelStudent } from '@/services/api/admin/project/project.api'
import StudentModalImport from './StudentModalImport.vue'
import StudentModalImportLog from './StudentModalImportLog.vue'
import HistoryImportStudentModalTable from './HistoryImportStudentModalTable.vue'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'

const state = reactive({
  searchQuery: '',
  isModalOpen: false,
  id: null as string | null,
  student: [] as StudentResponse[],
  paginationParams: { page: 1, size: 10 },
  totalItemsHistory: 0,
  paginationParamsHistory: { page: 1, size: 10 },  
  totalItems: 0,
  isOpenModalImport: false,
  searchQueryDF: '',
  isOpenModalImportHistory: false,
  history: [] as historyImprort[],
  page: 1,
  size: 10,
})

const handlePageChangelog = (pagination: any) => {
  state.paginationParamsHistory.page = pagination.page
  state.paginationParamsHistory.size = pagination.pageSize
  fetchHistory()
}


const logRef = ref()

const handleNewLog = (newEntry) => {
  if (logRef.value) {
    logRef.value.addNewLogEntry(newEntry) // Thêm log mới vào bảng Import Log
  }
}

const dataStudentLog = reactive({
  isModalOpenLog: false,
  studentLog: [] as StudentResponse[]
})

const closeModalStudentLog = () => {
  dataStudentLog.isModalOpenLog = false
}

const handleOpenModalImportHistory = () => {
  state.isOpenModalImportHistory = true
}

const handleCloseModalImportHistory = () => {
  state.isOpenModalImportHistory = false
}

const openModalStudentLog = () => {
  dataStudentLog.isModalOpenLog = true
}

const downloadImportLogExcel = async () => {
  try {
    const blob = await downloadImportLog()

    const url = window.URL.createObjectURL(new Blob([blob]))

    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'import-log.csv')
    document.body.appendChild(link)
    link.click()

    // Dọn dẹp URL sau khi tải xong
    window.URL.revokeObjectURL(url)
    link.remove()
  } catch (error) {
    console.error('Lỗi khi tải file:', error)
  }
}

const downloadTemplateExcel = async () => {
  try {
    const response = await downloadExcelStudent()
    const blob = new Blob([response], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = URL.createObjectURL(blob)

    const link = document.createElement('a')
    link.href = url
    link.download = 'template_import_sinh_vien.xlsx'
    link.click()

    URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Failed to download template:', error)
  }
}

const handleOpenModalImport = () => {
  state.isOpenModalImport = true
}

const openAddModal = () => {
  state.id = null
  state.isModalOpen = true
}

const openViewModal = (id: string) => {
  state.id = id
  state.isModalOpen = true
}

const deleteStudentById = async (id: string) => {
  let res
  Modal.confirm({
    title: 'Xác nhận xóa nhân viên',
    content: 'Bạn có chắc chắn muốn xóa nhân viên này không?',
    okText: 'Xóa',
    cancelText: 'Hủy',
    okType: 'danger',
    onOk: async () => {
      try {
        res = await deleteStudent(id)
        toast.success(res.message)
        fetchStudent()
      } catch (error) {
        toast.error(res.message)
      }
    }
  })
}

const closeModal = () => {
  state.isModalOpen = false
}

const handleCloseModalImport = () => {
  state.isOpenModalImport = false
}

const fetchStudent = async () => {
  try {
    const params: ParamsGetStudent = {
      page: state.paginationParams.page,
      size: state.paginationParams.size,
      search: state.searchQuery || null,
      searchDF: state.searchQueryDF || null
    }
    const response = await getStudent(params)
    state.student = response?.data?.data || []
    state.totalItems = response?.data?.totalElements || 0

  } catch (error) {
    console.error('Lỗi khi tải danh sách sinh viên:', error)
  }
}

onMounted(fetchStudent)

const fetchHistory = async () => {
  try {
    const response = await getAllHistoryStudent()
    console.log('Response data:', response)

    if (response?.data?.data) {
      state.history = response.data.data // Đảm bảo gán đúng mảng dữ liệu
      state.totalItemsHistory = response.data.totalElements
    } else {
      state.history = []
      state.totalItems = 0
    }
  } catch (error) {
    console.error('Lỗi khi tải lịch sử import:', error)
  }
}

onMounted(fetchHistory)

const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  state.paginationParams.page = page
  if (pageSize) {
    state.paginationParams.size = pageSize
  }
  fetchStudent()
}

watch(
  () => state.searchQuery,
  () => {
    state.paginationParams.page = 1
    debouncedFetchStudent()
  }
)

const updateSearchQueryDF = (newQuery: string) => {
  state.searchQueryDF = newQuery
}

watch(
  () => state.searchQueryDF,
  () => {
    state.paginationParams.page = 1
    debouncedFetchStudent()
  }
)

const debouncedFetchStudent = debounce(fetchStudent, 1000)

const updateSearchQuery = (newQuery: string) => {
  state.searchQuery = newQuery.trim()
}

watch(
  () => state.searchQuery,
  () => {
    state.paginationParams.page = 1
    debouncedFetchStudent()
  }
)
</script>
<style scoped>
:deep(.ant-btn) {
  background-color: var(--selected-header) !important;
  color: var(--selected-text) !important;
  border-color: var(--selected-header) !important;
}

:deep(.ant-btn:hover),
:deep(.ant-btn:focus) {
  background-color: var(--selected-header-hover) !important;
  border-color: var(--selected-header-hover) !important;
}
</style>
