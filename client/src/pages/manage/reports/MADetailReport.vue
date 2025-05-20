<template>
  <a-modal :open="open" :title="props.title" @cancel="closeModal" :width="'50%'" :footer="null">
    <div class="min-h-[360px]">
      <h1 class="font-semibold">
        Báo cáo công việc ngày : {{ formatDateNVV(Number(props.time)) }}
      </h1>
      <table class="w-full border-collapse mt-4">
        <thead>
          <tr class="bg-gray-100">
            <th class="border p-2 text-left">Người báo cáo</th>

            <th class="border p-2 text-left">Báo cáo</th>
            <th class="border p-2 text-left">Ngày báo cáo</th>
            <th class="border p-2 text-left">Trạng thái</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in props.data" :key="item.id" class="hover:bg-gray-50">
            <td class="border p-2">
              {{ item.nameStaff == null ? item.nameStudent : item.nameStaff }} -
              {{ item.codeStaff == null ? item.codeStudent : item.codeStaff }}
              <!-- <a-avatar-group
                class="mt-2"
                :max-count="3"
                :max-style="{ color: '#f56a00', backgroundColor: '#fde3cf' }"
              >
                <a-tooltip
                  :title="item.imageStaff == null ? item.imageStudent : item.imageStaff"
                  placement="top"
                >
                  <a-avatar :src="item.imageStaff == null ? item.imageStudent : item.imageStaff" />
                </a-tooltip>
              </a-avatar-group> -->
            </td>
            <td class="border p-2 w-2/3">
              <p>
                Nay làm được gì :
                {{ item.workDoneToday == null ? 'Chưa báo cáo' : item.workDoneToday }}
              </p>
              <br />
              <p>
                Mai làm được gì:
                {{ item.workPlanTomorrow == null ? 'Chưa báo cáo' : item.workPlanTomorrow }}
              </p>
              <br />
              <p>Vướng mắc gì : {{ item.obstacles == null ? 'Chưa báo cáo' : item.obstacles }}</p>
            </td>

            <td class="border p-2">
              {{
                formatDate(Number(item.reportTime)) == '01/01/1970 07:00'
                  ? 'Chưa báo cáo'
                  : formatDate(Number(item.reportTime))
              }}
            </td>
            <td class="border p-2">
              {{
                formatDate(Number(item.reportTime)) == '01/01/1970 07:00'
                  ? 'Chưa báo cáo '
                  : 'Đã báo cáo'
              }}
            </td>

            <!-- <td class="border p-2">
              <a-progress
                :percent="item.progressProject"
                :stroke-color="getProgressColor(item.progressProject)"
                size="small"
              />
            </td> -->
          </tr>
        </tbody>
      </table>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, h, onMounted, ref, reactive, watch } from 'vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import { library } from '@fortawesome/fontawesome-svg-core'
library.add(
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight,
  faTrash,
  faTrashAlt,
  faEye
)
import {
  faReceipt,
  faPenToSquare,
  faCircleInfo,
  faFilter,
  faRotateRight,
  faTrash,
  faTrashAlt,
  faEye
} from '@fortawesome/free-solid-svg-icons'
import { getReport, reportResponse } from '@/services/api/manage/report/report.api'
import { formatDateNVV } from '@/utils/commom.helper'
const props = defineProps<{
  open: boolean
  title: string
  time?: number
  data: reportResponse[]
  page: number
  size: number
  totalItems: number
}>()
const emit = defineEmits(['page-change', 'add', 'view', 'delete', 'idFacility', 'close', 'success'])

const closeModal = () => {
  emit('close')
}
const formatDate = (timestamp) => {
  const date = new Date(timestamp)
  const day = String(date.getDate()).padStart(2, '0')
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const year = date.getFullYear()
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${day}/${month}/${year} ${hours}:${minutes}`
}

const dataReport = ref(props.data || [])

// Watch cho props.time

// Watch cho props.data

onMounted(() => {
  console.log('Initial time:', props.time)
  console.log('Initial data:', props.data)
})
</script>
