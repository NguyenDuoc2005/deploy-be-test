<template>
<BreadcrumbDefault label="Quản lý bộ môn">    
    <DepartmentFilter 
        :searchQuery="state.searchQuery"
        @update:searchQuery="updateSearchQuery"
        :searchStatus="state.departmentStatus"
        @update:searchStatus="updateSearchStatus"
    />

    <DepartmentTable 
        :searchQuery="state.searchQuery"
        :department="state.department"
        :paginationParams="state.paginationParams" 
        :totalItems="state.totalItems"
        @add="openAddModal"
        @view="openViewModal"
        @page-change="handlePageChange" 
        @manage-major="openManageMajorModal"
    />
    </BreadcrumbDefault>
    <DepartmentModal
        :open="state.isModalOpen"
        :departmentId="state.departmentId"
        :title="state.departmentId ? 'Cập nhật bộ môn' : 'Thêm mới bộ môn'"
        @close="closeModal"
        @success="fetchDepartment"
    />

    <a-modal 
        v-model:visible="state.isManageMajorModalOpen" 
        :width="1300"
        :footer="null"
        :z-index="9999"
        :wrap-style="{ display: 'flex', alignItems: 'center', justifyContent: 'center' }"
        :style="{top: '10px', width: '60vw' }"
        @cancel="state.isManageMajorModalOpen = false"
    >
        <Major 
            :departmentId="state.selectedDepartmentId" 
            :isManageMajorModalOpen="state.isManageMajorModalOpen"
            @update:isManageMajorModalOpen="updateIsManageMajorModalOpen"
        />
    </a-modal>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, watch } from 'vue'
import DepartmentTable from './DepartmentTable.vue';
import DepartmentModal from './DepartmentModal.vue';
import DepartmentFilter from './DepartmentFilter.vue';
import Major from '../major/Major.vue';
import { debounce } from 'lodash';
import { DepartmentResponse, getDepartments, ParamsGetDepartment } from '@/services/api/department.api';
import { MajorResponse } from '@/services/api/major.api';
import DivCustom from '@/components/custom/Div/DivCustom.vue';
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue';

const state = reactive({
    searchQuery: '',
    isModalOpen: false,
    departmentId: null as string | null,
    department: [] as DepartmentResponse[],
    departmentStatus: null as string | null,
    major: [] as MajorResponse[],
    paginationParams: { page: 1, size: 10 },
    totalItems: 0,
    isManageMajorModalOpen: false,  
    selectedDepartmentId: null as string | null, 
});

const fetchDepartment = async () => {
    try {
        const params: ParamsGetDepartment = {
            page: state.paginationParams.page,
            size: state.paginationParams.size,
            departmentSearch: state.searchQuery || null,
            departmentStatus: state.departmentStatus ?? undefined,

        };
        const response = await getDepartments(params);
        state.department = response?.data?.data || [];
        state.totalItems = response?.data?.totalElements || 0;
    } catch (error) {
        console.error('Lỗi khi tải danh sách bộ môn:', error);
    }
};

const debouncedFetchDepartment = debounce(fetchDepartment, 1000);

const updateSearchQuery = (newQuery: string) => {
    state.searchQuery = newQuery.trim();
};

// Theo dõi sự thay đổi của bộ lọc để gọi API
watch(() => state.searchQuery, () => {
    state.paginationParams.page = 1;
    debouncedFetchDepartment();
});

watch(() => state.departmentStatus, () => {
    state.paginationParams.page = 1;
    debouncedFetchDepartment();
});

onMounted(fetchDepartment);

const openAddModal = () => {
    state.departmentId = null;
    state.isModalOpen = true;
};

const openViewModal = (id: string) => {
    state.departmentId = id;
    state.isModalOpen = true;
};

const openManageMajorModal = (departmentId: string) => {
    state.selectedDepartmentId = departmentId;
    state.isManageMajorModalOpen = true;
};

const closeModal = () => {
    state.isModalOpen = false;
};

const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
    state.paginationParams.page = page;
    if (pageSize) {
        state.paginationParams.size = pageSize;
    }
    fetchDepartment();
};

const updateIsManageMajorModalOpen = (value: boolean) => {
    state.isManageMajorModalOpen = value;
};

const updateSearchStatus = (newStatus: string | null) => {
    state.departmentStatus = newStatus;
    state.paginationParams.page = 1;
    debouncedFetchDepartment(); // Gọi API sau khi cập nhật trạng thái
};

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
