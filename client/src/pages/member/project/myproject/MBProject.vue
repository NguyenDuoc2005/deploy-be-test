<template>
  <BreadcrumbDefault label="Quản lí dự án">
    <MBProjectFilter
      :searchQuery="data.searchQuery"
      @update:searchQuery="dataSearch"
      :department="data.nameDepartmentSearch"
      :status="data.status"
      @update:search-department="dataSearchDepartment"
      @update:searchStatus="dataSearchStatus"
    ></MBProjectFilter>

    <MBProjectTable
      :data="data?.dataProject"
      :size="data.size"
      :total-items="data.totalItems"
      :page="data.page"
      @page-change="handlePageChange"
      @detail=""
      @view="openViewModal"
    >
    </MBProjectTable>

  </BreadcrumbDefault>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'


import {
  getAllProject,
  projectRequest,
  projectResponse
} from '@/services/api/manage/project/maproject.api'
import { debounce } from 'lodash'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { localStorageAction } from '@/utils/storage'
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey'
import { useRoute } from 'vue-router'
import MBProjectTable from './MBProjectTable.vue'
import MBProjectFilter from './MBProjectFilter.vue'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'

const data = reactive({
  searchQuery: '',
  isModalOpen: false,
  selectedProjectId: null as string | null,
  dataProject: [] as projectResponse[],
  nameDepartmentSearch: '',
  page: 1,
  size: 10,
  status: '',
  totalItems: 0
})

const user = localStorageAction.get(USER_INFO_STORAGE_KEY)
onMounted(() => {
  console.table(user)
})

const idFacilityProject = ref()



const modalTitle = computed(() => {
  return data.selectedProjectId ? 'Cập nhật dự án' : 'Thêm dự án'
})

const dataSearch = (search: string) => {
  data.searchQuery = search
}

const dataSearchDepartment = (search: string) => {
  data.nameDepartmentSearch = search
}

const dataSearchStatus = (search: string) => {
  data.status = search
}

const openViewModal = (id: string, idFacility: string) => {
  data.selectedProjectId = id
  idFacilityProject.value = idFacility
  data.isModalOpen = true
}

onMounted(() => {
  getAllDataProject()
})
const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)
const getAllDataProject = async () => {
  try {
    const params: projectRequest = {
      page: data.page,
      size: data.size,
      search: data.searchQuery,
      status: data.status,
      idUser: userLogin.userId,
      nameDepartment: data.nameDepartmentSearch
    }
    const response = await getAllProject(params)

    data.dataProject = response.data.content || []
    data.totalItems = response.data.totalElements
  } catch (error) {
    console.error('Failed to fetch project:', error)
  }
}

const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  data.page = page
  if (pageSize) {
    data.size = pageSize
  }
  getAllDataProject()
}

const debouncedFetchProject = debounce(getAllDataProject, 300)

watch(
  () => [data.searchQuery, data.nameDepartmentSearch, data.status],

  () => {
    data.page = 1
    debouncedFetchProject()
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