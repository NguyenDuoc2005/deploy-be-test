<template>
  <a-modal :open="open" width="500px" :title="props.title" @cancel="closeModal" @ok="createStage">
    <div class="space-y-4">
      <!-- Chọn giai đoạn -->
      <div>
        <label class="block text-sm font-medium mb-1">Chọn giai đoạn</label>
        <a-select
          placeholder="Chọn giai đoạn"
          class="w-full"
          size="large"
          v-model:value="paramsStage.idPhase"
          :options="
            getAllPhase.map((item) => ({
              label: item.name,
              value: item.id
            }))
          "
        />
      </div>

      <!-- Chọn ngày bắt đầu -->
      <div>
        <label class="block text-sm font-medium mb-1">Ngày bắt đầu</label>
        <a-date-picker
          show-time
          placeholder="Chọn ngày bắt đầu"
          class="w-full"
          size="large"
          v-model:value="paramsStage.startTime"
        />
      </div>

      <!-- Chọn ngày kết thúc cụ thể -->
      <div>
        <label class="block text-sm font-medium mb-1">Ngày kết thúc cụ thể</label>
        <a-date-picker
          show-time
          placeholder="Chọn ngày kết thúc"
          class="w-full"
          size="large"
          v-model:value="paramsStage.endTime"
        />
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted, watch } from 'vue'
import { dataPhase, phaseResponse } from '@/services/api/manage/phase/phase.api'
import { createUpdate, detailStage, stageRequest } from '@/services/api/manage/stage/stage.api'
import { useRoute } from 'vue-router'
import dayjs from 'dayjs'
import { toast } from 'vue3-toastify'
import { h } from 'vue'

// Modal hiển thị
const route = useRoute()

// Props nhận từ cha
const props = defineProps<{
  open: boolean
  title: string
  dataStage: {
    idStage: string
    idPhase: string
  }
}>()

watch(
  () => props.dataStage,
  async (newData) => {
    if (newData?.idStage) {
      await detail()
    }
  },
  { immediate: true, deep: true }
)

const getAllPhase = ref<phaseResponse[]>([])

// Emit sự kiện
const emit = defineEmits(['close', 'success'])

const paramsTodo = reactive({
  page: 1,
  size: 10000,
  totalItem: '',
  idProject: '',
  search: ''
})

const fecthDataStage = async () => {
  paramsTodo.idProject = route.params.id as string
  const res = await dataPhase(paramsTodo)
  getAllPhase.value = res.data.content
}

const detail = async () => {
  try {
    const res = await detailStage(props.dataStage.idStage)
    paramsStage.idPhase = res.data.phaseProject.id
    paramsStage.idStage = res.data.id
    paramsStage.startTime = dayjs(res.data.startTime)
    paramsStage.endTime = dayjs(res.data.endTime)
  } catch (err) {
    console.error('Lỗi khi gọi API detailStage:', err)
  }
}

onMounted(() => {})

detail()
fecthDataStage()
const closeModal = () => {
  paramsStage.endTime = null
  paramsStage.startTime = null
  paramsStage.idPhase = ''
  paramsStage.idStage = ''
  emit('close')
}

const paramsStage = reactive({
  idPhase: '',
  idStage: '',
  startTime: null as any, // ban đầu là null, sau nhận từ date picker
  endTime: null as any
})
const createStage = async () => {
  try {
    const request: stageRequest = {
      idProject: route.params.id as string,
      idStage: paramsStage.idStage,
      idPhase: paramsStage.idPhase,
      startTime: dayjs(paramsStage.startTime).valueOf(),
      endTime: dayjs(paramsStage.endTime).valueOf()
    }
    const res = await createUpdate(request)
    toast.success(res.message)
    emit('success')
    closeModal()
  } catch (error) {
    toast.error(error.response.data.message)
  }
}
</script>

<style scoped>
.ant-modal-content {
  border-radius: 8px;
}
.ant-modal-body {
  padding: 24px;
}
</style>
