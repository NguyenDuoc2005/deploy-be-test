<template>
<BreadcrumbDefault label="Quản lý bộ môn theo cơ sở">
    <DepartmentFacilityFilter
    :searchQuery="state.searchQuery"
    @update:searchQuery="updateSearchQuery"
/>

<DepartMentFacilityTable
    :searchQuery="state.searchQuery"
    :departmentFacility="state.departmentfacility"
    :paginationParams="state.paginationParams" 
    :totalItems="state.totalItems"
    @add="openAddModal"
    @view="openViewModal"
    @page-change="handlePageChange" 
    @major-facility="openMajorFacilityModal"
/>
</BreadcrumbDefault>

<DepartmentFacilityModal
    :open="state.isModalOpen"
    :departmentFacilityId="state.departmentFacilityId"
    :title="state.departmentFacilityId ? 'Cập nhật bộ môn theo cơ sở' : 'Thêm mới bộ môn theo cơ sở'"
    @close="closeModal"
    @success="fetchDepartmentFacility"
/>

<a-modal 
        v-model:visible="state.isMajorFacilityModalOpen" 
        :width="1300"
        :footer="null"
        :z-index="9999"
        :wrap-style="{ display: 'flex', alignItems: 'center', justifyContent: 'center' }"
        :style="{top: '10px' }"
        @cancel="state.isMajorFacilityModalOpen = false"
    >
        <MajorFacility
            :departmentFacilityId="state.selectedDepartmentFacilityId" 
            :isMajorFacilityModalOpen="state.isMajorFacilityModalOpen"
            @update:isMajorFacilityModalOpen="updateMajorFacilityModalOpen"
        />
    </a-modal>

</template>

<script setup lang="ts">
import { DepartmentFacilityResponse, getDepartmentFacilitys, ParamsGetDepartmentFacility } from '@/services/api/departmentfacility.api';
import DepartMentFacilityTable from './DepartMentFacilityTable.vue';
import DepartmentFacilityFilter from './DepartmentFacilityFilter.vue';
import DepartmentFacilityModal from './DepartmentFacilityModal.vue';
import MajorFacility from '../majorFacility/MajorFacility.vue';

import { nextTick, onMounted, reactive, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { debounce } from 'lodash';
import DivCustom from '@/components/custom/Div/DivCustom.vue';
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue';

    const route = useRoute()
    const departmentId = ref<string | null>(route.query.departmentId as string || null)

    const state = reactive({
    searchQuery: '',
    facilityName: null as string | null,
    isModalOpen: false,
    departmentFacilityId: null as string | null,
    departmentfacility: [] as DepartmentFacilityResponse[],
    paginationParams: { page: 1, size: 10 },
    totalItems: 0,
    isMajorFacilityModalOpen: false,
    selectedDepartmentFacilityId: null as string | null, 
});

const fetchDepartmentFacility = async () => {
    try {
        if (!departmentId.value) return;

        const params: ParamsGetDepartmentFacility = {
            page: state.paginationParams.page,
            size: state.paginationParams.size,
            facilityName: state.searchQuery || '',
        };

        const response = await getDepartmentFacilitys(params, departmentId.value);
        state.departmentfacility = response?.data?.data || [];
        state.totalItems = response?.data?.totalElements || 0;

        // console.log('Fetched data:', state.departmentfacility)
    } catch (error) {
        console.error('Lỗi khi tải danh sách bộ môn theo cơ sở:', error);
    }
};

onMounted(fetchDepartmentFacility);

const openAddModal = () => {
    state.departmentFacilityId = null;
    state.isModalOpen = true;
};

const openViewModal = (id: string) => {
    console.log("🔍 Mở modal cập nhật với ID:", id);
    state.departmentFacilityId = id;
    state.isModalOpen = true;
};

const closeModal = () => {
    state.isModalOpen = false;
};

const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
    state.paginationParams.page = page;
    if (pageSize) {
        state.paginationParams.size = pageSize;
    }
    fetchDepartmentFacility();
};

const updateSearchQuery = (newQuery: string) => {
    state.searchQuery = newQuery.trim();
};

const debouncedFetchDepartmentFacility = debounce(fetchDepartmentFacility, 1000);

watch(() => state.searchQuery, () => {
    state.paginationParams.page = 1;
    debouncedFetchDepartmentFacility();
});

const reset = () => {
    state.selectedDepartmentFacilityId = ''; 
};

const openMajorFacilityModal = async (departmentFacilityId: string) => {
    state.selectedDepartmentFacilityId = departmentFacilityId; 
    state.isMajorFacilityModalOpen = true;
};

const updateMajorFacilityModalOpen = (value: boolean) => {
    state.isMajorFacilityModalOpen = value;
};

</script>
<style scoped>
:deep(.ant-btn) {
  background-color: var(--selected-header) !important;
  color: #ffffff !important;
  border-color: var(--selected-header) !important;
}

:deep(.ant-btn:hover),
:deep(.ant-btn:focus) {
  background-color: var(--selected-header-hover) !important;
  border-color: var(--selected-header-hover) !important;
}
</style>
