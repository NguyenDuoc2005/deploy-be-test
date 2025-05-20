<template>
<BreadcrumbDefault label="Quản lý cơ sở">
    <FacilityFilter
    :searchQuery="state.searchQuery"
    :searchStatus="state.facilityStatus"
    @update:searchQuery="updateSearchQuery"
    @update:searchStatus="updateSearchStatus"
/>

    <FacilityTable
        :searchQuery="state.searchQuery"
        :facility="state.facility"
        :paginationParams="state.paginationParams" 
        :totalItems="state.totalItems"
        @add="openAddModal"
        @view="openViewModal"
        @page-change="handlePageChange" 
        @update-status="handleUpdateStatusClick"
    />
</BreadcrumbDefault>
    <FacilityModal
        :open="state.isModalOpen"
        :facilityId="state.id"
        :title="state.id ? 'Cập nhật cơ sở' : 'Thêm mới cơ sở'"
        @close="closeModal"
        @success="fetchFacility"
    />
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, watch } from 'vue'
import { debounce } from 'lodash';
import { changeStatusFacility, FacilityResponse, getAllFacility, ParamsGetFacility } from '@/services/api/facility.api';

import FacilityTable from './FacilityTable.vue';
import FacilityFilter from './FacilityFilter.vue';
import FacilityModal from './FacilityModal.vue';
import { toast } from 'vue3-toastify';
import DivCustom from '@/components/custom/Div/DivCustom.vue';
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue';

const state = reactive({
    searchQuery: '',
    facilityName: null as string | null,
    facilityStatus: null as string | null,
    isModalOpen: false,
    id: null as string | null,
    facility: [] as FacilityResponse[],
    paginationParams: { page: 1, size: 10 },
    totalItems: 0,
});

const fetchFacility = async () => {
    try {
        const params: ParamsGetFacility = {
            page: state.paginationParams.page,
            size: state.paginationParams.size,
            facilityName: state.searchQuery || '',
            facilityStatus: state.facilityStatus ?? undefined,
        };
        const response = await getAllFacility(params);
        state.facility = response?.data?.data || [];
        state.totalItems = response?.data?.totalElements || 0;
    } catch (error) {
        console.error('Lỗi khi tải danh sách cơ sở:', error);
    }
};

onMounted(fetchFacility);

const openAddModal = () => {
    state.id = null;
    state.isModalOpen = true;
};

const openViewModal = (id: string) => {
    state.id = id;
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
    fetchFacility();
};

const handleUpdateStatus = async (id: string) => {
    try {
        await changeStatusFacility(id)
        toast.success('Cập nhật trạng thái thành công!')
        fetchFacility()
    } catch (error) {
        toast.error('Cập nhật trạng thái thất bại!')
    }
}

const debouncedFetchFacility = debounce(fetchFacility, 1000);

const updateSearchQuery = (newQuery: string) => {
    state.searchQuery = newQuery.trim();
};

const updateSearchStatus = (newQuery: string) =>{
    state.facilityStatus = newQuery
}

watch(() => state.searchQuery, () => {
    state.paginationParams.page = 1;
    debouncedFetchFacility();
});

watch(() => state.facilityStatus, () => {
    state.paginationParams.page = 1;
    debouncedFetchFacility();
});

const handleUpdateStatusClick = (id: string) => {
    handleUpdateStatus(id)
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
