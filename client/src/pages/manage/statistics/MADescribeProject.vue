<template>
  <DivCustom>
    <div class="pl-4">
      <!-- <h1 class="font-medium">Dự án / <a @click=""></a></h1> -->
      <h1 class="font-bold text-xl text-black-2">Bản tóm tắt dự án</h1>
    </div>
    <div class="p-4">
      <a-button type="default" class="mb-4 flex items-center space-x-2">
        <FilterOutlined />
        <span>Filter</span>
      </a-button>

      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4">
        <SummaryCard
          icon="BlockOutlined"
          :title="countTodo + ' Công việc'"
          subtitle="Trong dự án"
          :icon-color="'text-blue-400'"
        />
        <SummaryCard
          icon="EditOutlined"
          :title="dataSprint?.length + ' Giai đoạn dự án'"
          subtitle="Trong dự án"
          :icon-color="'text-blue-400'"
        />
        <SummaryCard
          icon="CheckCircleOutlined"
          :title="todoSuccess + ' công việc đã hoàn thành'"
          subtitle="Trong dự án"
          :icon-color="'text-blue-400'"
        />
        <SummaryCard
          icon="CalendarOutlined"
          :title="todoDoing + ' Công việc đang làm'"
          subtitle="Trong dự án"
          :icon-color="'text-blue-400'"
        />
      </div>
    </div>

    <div>
      <DashboardSection
        :data-todo="dataTodo.data"
        @data-phase="handDatPhase"
        :count-todo-doing="todoDoing"
        :count-todo-no-doing="todoNoDoing?.length ?? 0"
        :count-todo-success="todoSuccess"
      />
    </div>
  </DivCustom>
</template>

<script setup lang="ts">
import { FilterOutlined } from '@ant-design/icons-vue'
import { onMounted, reactive, ref } from 'vue'
import SummaryCard from '../Summary/SummaryCard.vue'
import DivCustom from '@/components/custom/Div/DivCustom.vue'
import DashboardSection from '../Summary/DashboardSection.vue'
import {
  fetchDataTodoByProject,
  ResponseFetchDataToDoProject
} from '@/services/api/manage/todo/todo.api'
import { useRoute } from 'vue-router'
import { phaseResponse } from '@/services/api/manage/phase/phase.api'

const idProject = ref<string>()

const router = useRoute()
const countTodo = ref('')
const dataTodo = reactive({
  data: [] as ResponseFetchDataToDoProject[]
})

const todoSuccess = ref(0) // Khởi tạo với số 0
const todoDoing = ref(0) // Khởi tạo với số 0
const todoNoDoing = ref<ResponseFetchDataToDoProject[]>() // Khởi tạo với số 0
const leghttodoNoDoing = ref(0)
const fetchDataTodo = async (id: string) => {
  try {
    const res = await fetchDataTodoByProject(id)
    dataTodo.data = res.data as unknown as ResponseFetchDataToDoProject[]

    todoNoDoing.value = dataTodo.data.filter((a) => a.idPhaseTodo == null)

    leghttodoNoDoing.value = todoNoDoing.value.length

    countTodo.value = dataTodo.data.length as unknown as string
    dataTodo.data.forEach((a) => {
      checkTodoStatus(a.statusTodo)
    })

    todoDoing.value = dataTodo.data.length - leghttodoNoDoing.value - todoSuccess.value
  } catch (error) {
    console.error('❌ Lỗi khi lấy dữ liệu todo:', error)
  }
}

const checkTodoStatus = (status: string) => {
  switch (status) {
    case 'DA_HOAN_THANH':
      todoSuccess.value++ // Nếu đã hoàn thành, tăng số lượng todo hoàn thành
      break
    case 'HOAN_THANH_SOM':
      todoSuccess.value++ // Nếu hoàn thành sớm, tăng số lượng todo hoàn thành
      break
    case 'QUA_HAN':
      todoSuccess.value++ // Nếu quá hạn, tăng số lượng todo chưa làm
      break
  }
}

onMounted(() => {
  idProject.value = router.params.id as string
  fetchDataTodo(idProject.value)
})

const dataSprint = ref<phaseResponse[]>()
const handDatPhase = (data) => {
  dataSprint.value = data
}
</script>
