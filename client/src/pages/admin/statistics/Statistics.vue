<template>
  <BreadcrumbDefault label="Thống kê tổng quan dự án">
    <DivCustom label="">
      <div class="kpi-cards-wrapper">
        <KpiCard title="Tổng dự án" :value="overview.totalProjects" type="total" />
        <KpiCard title="Đang thực hiện" :value="overview.inProgress" type="inProgress" />
        <KpiCard title="Hoàn thành" :value="overview.done" type="done" />
        <KpiCard title="Chưa diễn ra" :value="overview.paused" type="paused" />
        <KpiCard title="Trễ hạn" :value="overview.overdue" type="overdue" />
      </div>
    </DivCustom>

    <DivCustom label="">
      <DashboardCharts />
    </DivCustom>
  </BreadcrumbDefault>
</template>

<script setup lang="ts">
import axios from 'axios'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { reactive, onMounted } from 'vue'
import KpiCard from './KpiCard.vue'
import DashboardCharts from './DashboardCharts.vue'
import projectByFacilityData from './DashboardCharts.vue'

import { getTotalProjects, getProjectByFacility } from '@/services/api/admin/project/project.api'
import BreadcrumbDefault from '@/components/custom/Div/BreadcrumbDefault.vue'

const overview = reactive({
  totalProjects: 0,
  inProgress: 0,
  done: 0,
  paused: 0,
  overdue: 0
})

const loadOverview = async () => {
  try {
    const response = await getTotalProjects()
    if (response && response.data) {
      const data = response.data
      overview.totalProjects = data.totalProjects ?? 0
      overview.inProgress = data.inProgress ?? 0
      overview.done = data.done ?? 0
      overview.paused = data.paused ?? 0
      overview.overdue = data.overdue ?? 0
    }
  } catch (error) {
    console.error('Lỗi khi lấy dữ liệu tổng quan dự án', error)
  }
}

onMounted(() => {
  loadOverview()
})
</script>

<style scoped>
.kpi-cards-wrapper {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}
.kpi-cards-wrapper > * {
  flex: 1 1 160px;
  min-width: 160px;
}
</style>
