<template>
  <DivCustomOld label="Quản lí báo cáo dự án">
    <div class="flex justify-between">
      <div>
        <h1 class="font-semibold pl-2">Tên dự án : {{ valueProject?.name }}</h1>

        <div class="flex items-center">
          <h1 class="font-semibold pl-2">Thành viên :</h1>
          <a-avatar-group
            :max-count="3"
            :max-style="{ color: '#f56a00', backgroundColor: '#fde3cf' }"
            class="ml-6"
          >
            <a-tooltip
              v-for="(user, index) in dataStaffProject"
              :key="index"
              :title="
                user.staff == null
                  ? user.student.name + ' - Thành viên'
                  : user.staff.name +
                    ` ${user.role.substring(0, 1) + user.role.substring(1).toLowerCase()}`
              "
              placement="top"
            >
              <a-avatar :src="user.staff == null ? user.student.image : user.staff.picture" />
            </a-tooltip>
          </a-avatar-group>
        </div>
      </div>
      <div class="chart-container max-w-72.5 -mt-20">
        <div v-if="loading" class="flex justify-center items-center h-full">
          <a-spin />
        </div>
        <template v-else>
          <Pie :data="chartData" :options="pieOptions" />
        </template>
      </div>
    </div>
    <a-alert :message="`Ngày : ${selectedDate?.format('YYYY-MM-DD')}`" />
    <a-calendar v-model:value="value" @select="onSelect">
      <template #dateCellRender="{ current }">
        <ul class="events">
          <li v-for="item in getListData(current)" :key="item.content">
            <a-badge :status="item.type" :text="item.content" />
          </li>
        </ul>
      </template>
      <template #monthCellRender="{ current }">
        <div v-if="getMonthData(current)" class="notes-month">
          <section>{{ getMonthData(current) }}</section>
          <span>Backlog number</span>
        </div>
      </template>
    </a-calendar>
  </DivCustomOld>

  <MADetailReport
    :open="dataModalReport.isModalOpen"
    :title="dataModalReport.title"
    :data="dataModalReport.dataReport"
    :page="dataModalReport.page"
    :size="dataModalReport.size"
    :totalItems="dataModalReport.totalItems"
    :time="dataModalReport.time"
    @close="closeModalReport"
  />
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { onMounted, reactive, watch } from 'vue'
import type { Dayjs } from 'dayjs'
import { getAllStaffProject } from '@/services/api/manage/todo/todo.api'
import { useRoute, useRouter } from 'vue-router'
import { detailProject, projectDetailResponse } from '@/services/api/admin/project/project.api'
import { getReport, reportResponse } from '@/services/api/manage/report/report.api'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { Pie } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
} from 'chart.js'
import { toRefs } from 'vue'
import { VCalendar } from 'vuetify/labs/VCalendar'
import dayjs from 'dayjs'
import MADetailReport from './MADetailReport.vue'
import DivCustomOld from '@/components/custom/Div/DivCustomOld.vue'
// Đăng ký các components
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement)
const useReportSuccess = ref<number>(0)
const useReportNotSuccess = ref<number>(0)
const chartData = ref({
  labels: ['Đã báo cáo', 'Chưa báo cáo'],
  datasets: [
    {
      label: 'Thống kê báo cáo',
      data: [useReportSuccess.value, useReportNotSuccess.value],
      backgroundColor: ['rgb(54, 162, 235)', 'rgb(255, 99, 132)'],
      hoverOffset: 4
    }
  ]
})

const pieOptions = {
  responsive: true,
  maintainAspectRatio: false,
  animation: {
    duration: 500
  },
  plugins: {
    legend: {
      position: 'bottom' as const,
      labels: {
        boxWidth: 12,
        padding: 15,
        font: {
          size: 12
        }
      }
    },
    title: {
      display: true,
      text: 'Thống kê báo cáo',
      font: {
        size: 14
      }
    }
  }
}

const dataModalReport = reactive({
  searchQuery: '',
  isModalOpen: false,
  dataReport: [] as reportResponse[],
  title: '',
  page: 1,
  size: 10,
  status: '',
  totalItems: 0,
  time: 0
})

const openModalReport = () => {
  dataModalReport.isModalOpen = true
}

const closeModalReport = () => {
  dataModalReport.isModalOpen = false
}

const getListData = (value: Dayjs) => {
  let listData: { type: string; content: string }[] = []

  const today = dayjs().format('YYYY-MM-DD') // Lấy ngày hôm nay
  const dateKey = value.format('YYYY-MM-DD')

  // Cập nhật dữ liệu cho ngày hôm nay
  if (dateKey === today) {
    const successReports = reportData.value[dateKey]?.success || 0
    const notSuccessReports = reportData.value[dateKey]?.notSuccess || 0

    listData.push({
      type: 'success',
      content: `Đã báo cáo: ${successReports}`
    })

    // Thêm trạng thái cho số người chưa báo cáo
    listData.push({
      type: 'warning',
      content: `Chưa báo cáo: ${notSuccessReports}`
    })
  }

  return listData
}

const loading = ref<boolean>(true)
onMounted(async () => {
  try {
    console.log(chartData.value.datasets[0].data)

    chartData.value = {
      ...chartData.value,
      datasets: [
        {
          ...chartData.value.datasets[0],
          data: [useReportSuccess.value, useReportNotSuccess.value]
        }
      ]
    }
  } catch (error) {
    console.error(' Lỗi khi tải dữ liệu:', error)
  } finally {
    loading.value = false
  }
})
const getMonthData = (value: Dayjs) => {
  // if (value.month() === 8) {
  //   return 1394
  // }
}

const value = ref<Dayjs>(dayjs())
const selectedDate = ref<Dayjs | null>(dayjs())
const emit = defineEmits(['data-report'])
const selectedTime = ref<number>()

const data = reactive({
  searchQuery: '',
  isModalOpen: false,
  selectedProjectId: null as string | null,
  nameDepartmentSearch: '',
  dataReport: [] as reportResponse[],
  page: 1,
  size: 10,
  status: '',
  totalItems: 0
})
const dataStaffProject = ref<[]>([])

const valueProject = ref<projectDetailResponse>()
const param = reactive({})
const dataReport = ref<reportResponse[]>([])
const route = useRoute()

const fetchDataReport = async () => {
  const resData = await getAllStaffProject(idProject.value)
  dataStaffProject.value = resData.data as []
  console.log(dataStaffProject.value)
  const resDetail = await detailProject(idProject.value)
  valueProject.value = resDetail.data

  // Lấy dữ liệu báo cáo cho ngày hôm nay
  const todayReport = dayjs().format('YYYY-MM-DD')
  const resReports = await getReport({
    page: data.page,
    size: data.size,
    idProject: idProject.value,
    time: Number(todayReport)
  })

  // Đếm số lượng cho ngày hôm nay
  resReports.data.content.forEach((item) => {
    const reportDate = todayReport
    if (!reportData.value[reportDate]) {
      reportData.value[reportDate] = { success: 0, notSuccess: 0 }
    }
    if (item.reportTime == null) {
      reportData.value[reportDate].notSuccess++
    } else {
      reportData.value[reportDate].success++
    }
  })

  // Cập nhật dữ liệu cho tất cả các ngày trước ngày hiện tại
  const today = dayjs().format('YYYY-MM-DD')
  for (
    let date = dayjs().subtract(30, 'day');
    date.isBefore(dayjs(today).add(1, 'day'));
    date = date.add(1, 'day')
  ) {
    const dateKey = date.format('YYYY-MM-DD')
    if (!reportData.value[dateKey]) {
      reportData.value[dateKey] = { success: 0, notSuccess: 0 }
    }
  }

  // Cập nhật chart data cho ngày hôm nay
  chartData.value = {
    ...chartData.value,
    datasets: [
      {
        ...chartData.value.datasets[0],
        data: [
          reportData.value[todayReport]?.success || 0,
          reportData.value[todayReport]?.notSuccess || 0
        ]
      }
    ]
  }
}
const idProject = ref('')

onMounted(() => {
  idProject.value = route.params.id as string
  fetchDataReport()
  onSelect(dayjs()) // Cập nhật dữ liệu cho ngày hôm nay
})

const router = useRouter()

const reportData = ref<{ [key: string]: { success: number; notSuccess: number } }>({})

const onSelect = async (date: Dayjs) => {
  openModalReport()

  try {
    loading.value = true
    selectedDate.value = date
    selectedTime.value = date.valueOf()
    dataModalReport.time = selectedTime.value
    // Reset counter cho ngày được chọn
    const dateKey = date.format('YYYY-MM-DD')
    reportData.value[dateKey] = { success: 0, notSuccess: 0 }
    const res = await getReport({
      page: data.page,
      size: data.size,
      time: selectedTime.value,
      idProject: idProject.value
    })

    dataModalReport.dataReport = res.data.content

    // Đếm số lượng
    res.data.content.forEach((item) => {
      if (item.reportTime == null) {
        reportData.value[dateKey].notSuccess++
      } else {
        reportData.value[dateKey].success++
      }
    })

    // Cập nhật chart data
    chartData.value = {
      ...chartData.value,
      datasets: [
        {
          ...chartData.value.datasets[0],
          data: [reportData.value[dateKey].success, reportData.value[dateKey].notSuccess]
        }
      ]
    }

    console.log('Chart data updated:', chartData.value.datasets[0].data)
  } catch (error) {
    console.error('Lỗi:', error)
  } finally {
    loading.value = false
  }
}

watch(
  [useReportSuccess, useReportNotSuccess],
  ([success, notSuccess]) => {
    chartData.value = {
      ...chartData.value,
      datasets: [
        {
          ...chartData.value.datasets[0],
          data: [success, notSuccess]
        }
      ]
    }
  },
  { immediate: true }
)

const paramsTodo = reactive({
  page: 1,
  size: 10,
  totalItem: '',
  idProject: '',
  search: ''
})

// Hàm format ngày để hiển thị
const formatDate = (date: Dayjs) => {
  return date.format('DD/MM/YYYY')
}
</script>

<style scoped>
.events {
  list-style: none;
  margin: 0;
  padding: 0;
}
.events .ant-badge-status {
  overflow: hidden;
  white-space: nowrap;
  width: 100%;
  text-overflow: ellipsis;
  font-size: 12px;
}
.notes-month {
  text-align: center;
  font-size: 28px;
}
.notes-month section {
  font-size: 28px;
}

.chart-container {
  width: 250px;
  height: 250px;
  padding: 10px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* Thêm style cho loading state */
.chart-container .ant-spin-spinning {
  margin: auto;
}
</style>
