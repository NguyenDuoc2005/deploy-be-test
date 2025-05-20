<template>
  <BreadcrumbDefault label="Quản lý chức vụ">
    <RoleFilter
        :q="data.searchQuery"
        @update:q="dataSearch"
        :department="data.branchSearch"
        @update:department="dataSearchBranch"
    />

    <RoleTable
        :data="data?.roleData"
        :size="data.size"
        :total-items="data.totalItems"
        :page="data.page"
        @page-change="handlePageChange"
    />
  </BreadcrumbDefault>
</template>

<script setup lang="ts">
import { onMounted, reactive, watch } from 'vue'
import RoleTable from './RoleTable.vue'
import RoleFilter from './RoleFilter.vue'
import { getAllRoles, ParamsGetRole, RoleResponse } from '@/services/api/admin/role.api'
import { debounce } from 'lodash'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'

const data = reactive({
  searchQuery: '',
  branchSearch: '',
  roleData: [] as RoleResponse[],
  page: 1,
  size: 10,
  totalItems: 0
})

// Nhận dữ liệu từ RoleFilter
const dataSearch = (search: string) => {
  data.searchQuery = search
}

const dataSearchBranch = (search: string) => {
  data.branchSearch = search
}

const getAllDataRoles = async () => {
  try {
    const params: ParamsGetRole = {
      page: data.page,
      size: data.size,
      q: data.searchQuery, // Đổi từ "search" → "q"
      department: data.branchSearch // Đổi từ "branch" → "department"
    }
    const response = await getAllRoles(params)
    data.roleData = response.data.content || []
    data.totalItems = response.data.totalElements
  } catch (error) {
    console.error('Failed to fetch roles:', error)
  }
}

onMounted(() => {
  getAllDataRoles()
})

// Phân trang
const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  data.page = page
  if (pageSize) {
    data.size = pageSize
  }
  getAllDataRoles()
}

// Gọi API khi thay đổi bộ lọc (debounced)
const debouncedFetchRoles = debounce(getAllDataRoles, 300)

watch(
    () => [data.searchQuery, data.branchSearch],
    () => {
      data.page = 1
      debouncedFetchRoles()
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
