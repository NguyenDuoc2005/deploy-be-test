<template>
    <BreadcrumbDefault label="Báo cáo hàng ngày">
        <div class="space-y-6">
            <!-- TODO Project Table -->
            <div class="bg-white shadow-md rounded-lg p-4">
                <h2 class="text-xl font-semibold mb-2">Danh sách công việc</h2>
                <TodoProjectTable
                    :todoProject="state.todoProjetc"
                    :paginationParams="state.paginationParams" 
                    :totalItems="state.totalItems"
                    @page-change="handlePageChange" 
                />
            </div>

            <!-- Report Section -->
            <div >
                <h2 class="text-xl font-semibold mb-2">Báo cáo</h2>

                <Reportfilter
                    :reportTime="stateReport.reportTime"
                    @update:reportTime="updateSearch"
                />
                <ReportTableAll
                    :search-query="stateReport.searchQuery"
                    :report="stateReport.report"
                    :paginationParams="stateReport.paginationParams" 
                    :totalItems="stateReport.totalItems"
                    @page-change="handlePageChangeReport" 
                />
                <br>
            </div>

                <ReportTable
                    @update-report-list="fetchReport"
                />
        </div>
    </BreadcrumbDefault>
</template>



<script lang="ts" setup>
import { useRoute } from 'vue-router';
import TodoProjectTable from './TodoProjectTable.vue';
import ReportTableAll from './ReportTableAll.vue';
import DivCustom from '@/components/custom/Div/DivCustom.vue';
import Reportfilter from './Reportfilter.vue';
import ReportTable from './ReportTable.vue';
import { getAllReport, getTodoByProject, ParamsGetReport, ParamsGetTodoProject, ReportResponse, TodoProjectResponse } from '@/services/api/member/report/report.api';
import { onMounted, reactive, ref, watch } from 'vue';
import { debounce } from 'lodash';
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue';

const route = useRoute()
const projectId = ref<string | null>(route.params.id as string || null)

const state = reactive({
    searchQuery: '',
    name: null as string | null,
    isModalOpen: false,
    todoProjetc: [] as TodoProjectResponse[],
    paginationParams: { page: 1, size: 10 },
    totalItems: 0,
});

const stateReport = reactive({
    searchQuery: '',
    reportTime: null as number | null,
    name: null as string | null,
    isModalOpen: false,
    report: [] as ReportResponse[],
    paginationParams: { page: 1, size: 10 },
    totalItems: 0,
});

const fetchReport = async () => {
  try {
    if (!projectId.value) {
      console.error('❌ Không có projectId, không thể fetch dữ liệu!');
      return;
    }

    const params: ParamsGetReport = {
      page: stateReport.paginationParams.page,
      size: stateReport.paginationParams.size,
      reportTime: stateReport.reportTime !== null ? stateReport.reportTime : null,
    };

    console.log('🚀 Gửi request lấy báo cáo với params:', params);
    const response = await getAllReport(params, projectId.value);

    stateReport.report = response?.data?.data || [];
    stateReport.totalItems = response?.data?.totalElements || 0;

    console.log('✅ Dữ liệu báo cáo nhận được:', stateReport.report);
    console.log('📦 Tổng số báo cáo:', stateReport.totalItems);
  } catch (error) {
    console.error('💥 Lỗi khi tải danh sách báo cáo:', error);
  }
};

const debouncedFetchReport  = debounce(fetchReport, 300);

const updateSearch = (newTime: number) => {
    stateReport.reportTime = newTime;
};

// Theo dõi sự thay đổi của bộ lọc để gọi API
watch(() => stateReport.reportTime, () => {
    stateReport.paginationParams.page = 1;
    debouncedFetchReport ();
});


const fetchTodo = async () => {
    try {
        if (!projectId.value) {
        console.error('❌ Không có projectId, không thể fetch dữ liệu!');
        return;
    }

    console.log('📢 Đang fetch dữ liệu với projectId:', projectId.value);
        const params: ParamsGetTodoProject = {
            page: state.paginationParams.page,
            size: state.paginationParams.size,
            name: state.searchQuery || '',
        };

        const response = await getTodoByProject(params, projectId.value);
        state.todoProjetc = response?.data?.data || [];
        state.totalItems = response?.data?.totalElements || 0;

        console.log('Fetched data:', state.todoProjetc)
    } catch (error) {
        console.error('Lỗi khi tải danh sách task:', error);
    }
};

onMounted(fetchTodo);
onMounted(fetchReport);

const handlePageChange = ({ page, pageSize }: { page: number; pageSize?: number }) => {
    state.paginationParams.page = page;
    if (pageSize) {
        state.paginationParams.size = pageSize;
    }
    fetchTodo();
};

const handlePageChangeReport = ({ page, pageSize }: { page: number; pageSize?: number }) => {
    stateReport.paginationParams.page = page;
    if (pageSize) {
        stateReport.paginationParams.size = pageSize;
    }
    fetchReport();
};
</script>