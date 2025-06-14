<template>
  <BreadcrumbDefault label="Quản lí nhân viên">
    <StaffFilter
      :searchQuery="data.searchQuery"
      :status="data.searchStatus"
      :search-facility="data.searchFacility"
      @update:searchQuery="dataSearch"
      @update:search-facility="dataSearchFacility"
      @update:status="dataSearchStatus"
    >
    </StaffFilter>
    <StaffTable
      :page="data?.page"
      :size="data?.size"
      :data="data?.dataStaff"
      :totalItems="data?.totalItems"
      @page-change="handlePageChange"
      @add="openAddModal"
      @view="openViewModal"
      @delete="deleteStaffById"
      @open-modal-import-staff="handleOpenModalImport"
      @download-template-import="dowloadStaffExcel"
      @history="handleOpenModalImportHistory"
      @download="downloadCsv"
    >
    </StaffTable>

    <StaffModal
      :open="data.isModalOpen"
      :staffId="data.selectedStaffId"
      :title="modalTitle"
      @close="closeModal"
      @success="getAllDataStaff"
    >
    </StaffModal>

    <ImportStaffModal
      :open="data.isOpenModalImport"
      :title="'Import Nhân Viên'"
      @close="handleCloseModalImport"
      @fetch-all="getAllDataStaff()"
    />

    <a-modal
      v-model:visible="data.isOpenModalImportHistory"
      :width="1300"
      :footer="null"
      :z-index="9999"
      :wrap-style="{ display: 'flex', alignItems: 'center', justifyContent: 'center' }"
      :style="{ top: '10px' }"
      @cancel="data.isOpenModalImportHistory = false"
    >
      <HistoryImportModalTable
        :history="data.history"
        :paginationParams="{ page: data.paginationParamsHistory.page, size: data.paginationParamsHistory.size }"
        :totalItems="data.totalItemsHistory"
        @page-change="handlePageChangelog"
        @close="handleCloseModalImportHistory"
      />
    </a-modal>
  </BreadcrumbDefault>
</template>

<script setup lang="ts">
import {
  deleteStaff,
  getAllStaff,
  paramGetStaff,
  staffResponse,
  dowloadExcel,
  getAllHistory,
  historyImprort,
  downloadCSV
} from '@/services/api/admin/staff.api'
import StaffTable from './StaffTable.vue'
import { computed, onMounted, reactive, watch } from 'vue'
import { debounce } from 'lodash'
import StaffFilter from './StaffFilter.vue'
import StaffModal from './StaffModal.vue'
import HistoryImportModalTable from './HistoryImportModalTable.vue'
import { toast } from 'vue3-toastify'
import { Modal } from 'ant-design-vue'

import ImportStaffModal from './ImportStaffModal.vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'

const data = reactive({
  searchQuery: '',
  isModalOpen: false,
  isOpenModalImport: false,
  isOpenModalImportHistory: false,
  selectedStaffId: null as string | null,
  dataStaff: [] as staffResponse[],
  history: [] as historyImprort[],
  paginationParamsHistory: { page: 1, size: 10 },  
  totalItemsHistory: 0,
  page: 1,
  size: 10,
  totalItems: 0,
  idFacility: '',
  status: '',
  searchFacility: '',
  searchStatus: ''
})

const modalTitle = computed(() => {
  return data.selectedStaffId ? 'Cập nhật nhân viên' : 'Thêm nhân viên'
})

const dataSearchFacility = (search: string) => {
  data.searchFacility = search
}

const dataSearchStatus = (search: string) => {
  data.searchStatus = search
}

const dataSearch = (search: string) => {
  data.searchQuery = search
}

const openAddModal = () => {
  data.selectedStaffId = null
  data.isModalOpen = true
}

const openViewModal = (id: string) => {
  data.selectedStaffId = id
  data.isModalOpen = true
}

const deleteStaffById = (id) => {
  let res
  Modal.confirm({
    title: 'Xác nhận xóa nhân viên',
    content: 'Bạn có chắc chắn muốn xóa nhân viên này không?',
    okText: 'Xóa',
    cancelText: 'Hủy',
    okType: 'danger',
    onOk: async () => {
      try {
        res = await deleteStaff(id)
        toast.success(res.message)
        getAllDataStaff() // Tải lại danh sách nhân viên
      } catch (error) {
        toast.error(res.message)
      }
    }
  })
}

const closeModal = () => {
  data.isModalOpen = false
}

const getAllDataStaff = async () => {
  try {
    const params: paramGetStaff = {
      page: data.page,
      size: data.size,
      search: data.searchQuery,
      searchStatus: data.searchStatus,
      searchFacility: data.searchFacility
    }
    const response = await getAllStaff(params)

    data.dataStaff = response.data.data || []
    data.totalItems = response.data.totalElements
  } catch (error) {
    console.error('Failed to fetch staffs:', error)
  }
}

const fetchHistory = async () => {
  try {
    const response = await getAllHistory()
    console.log('Response data:', response)

    if (response?.data?.data) {
      data.history = response.data.data // Đảm bảo gán đúng mảng dữ liệu
      data.totalItemsHistory = response.data.totalElements
    } else {
      data.history = []
      data.totalItems = 0
    }
  } catch (error) {
    console.error('Lỗi khi tải lịch sử import:', error)
  }
}

const dowloadStaffExcel = async () => {
  try {
    const response = await dowloadExcel()
    const blob = new Blob([response], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = URL.createObjectURL(blob)

    const link = document.createElement('a')
    link.href = url
    link.download = 'template_import_nhan_vien.xlsx'
    link.click()

    URL.revokeObjectURL(url)
    toast.success('Dowload thành công')
  } catch (error) {
    console.error('Failed to download template:', error)
    toast.error('Dowload thất bại')
  }
}

const downloadCsv = async () => {
  try {
    const response = await downloadCSV()

    const blob = new Blob([response], { type: 'text/csv' })
    const url = URL.createObjectURL(blob)

    const link = document.createElement('a')
    link.href = url
    link.download = 'staff_data.csv'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    URL.revokeObjectURL(url)
    toast.success('Dowload thành công')
  } catch (error) {
    console.error('Failed to download CSV:', error)
    toast.error('Dowload thất bại')
  }
}

const handleOpenModalImport = () => {
  data.isOpenModalImport = true
}

const handleCloseModalImport = () => {
  data.isOpenModalImport = false
}

const handleOpenModalImportHistory = () => {
  data.isOpenModalImportHistory = true
}

const handleCloseModalImportHistory = () => {
  data.isOpenModalImportHistory = false
}

const handlePageChangelog = (pagination: any) => {
  data.paginationParamsHistory.page = pagination.page
  data.paginationParamsHistory.size = pagination.pageSize
  fetchHistory()
}


const debouncedFetchStaff = debounce(getAllDataStaff, 300)

onMounted(() => {
  getAllDataStaff()
  fetchHistory()
})

watch(
  () => [data.searchQuery, data.searchStatus, data.searchFacility],
  () => {
    data.page = 1
    console.log(data.searchFacility + 'NNNN')
    console.log(data.searchQuery)
    console.log(data.searchStatus)

    debouncedFetchStaff()
  }
)

const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  data.page = page
  if (pageSize) {
    data.size = pageSize
  }
  getAllDataStaff()
}
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
