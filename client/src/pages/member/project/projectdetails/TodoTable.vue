<template>
  <TodoPhaseProjectTable :searchQuery="statetodo.searchQuery" :todo="filteredBoardTable" :todoId="statetodo.idTodo"
    :paginationParams="statetodo.paginationParams" :totalItems="statetodo.totalItems"
    @page-change="handlePageChangeTodo" @updatePriority="handlePriorityUpdate" />

</template>

<script lang="ts" setup>
import DivCustom from '@/components/custom/Div/DivCustom.vue';
import { onMounted, ref, watch, watchEffect } from 'vue';
import { useRoute } from 'vue-router';
import TodoPhaseProjectTable from './tabletodo/TodoPhaseProjectTable.vue';
import { reactive } from 'vue';
import { debounce, filter } from 'lodash';
import { downloadExcelTodoList, downloadImportLogTodoList, getAllTodoList, getAllTodoPhaseProject, getAllTodoProject, ParamsGetTodoList, TodoListProjectResponse, TodoPhaseProjectResponse, TodoProjectResponse } from '@/services/api/manage/todolist/todolist.api';
import { TodoListResponse } from '@/services/service/member/metodolist.socket';
import { MBMeUpdateTodoRequest, webSocketTodo } from '@/services/service/member/metodo.socket';
import { webSocketMemberProject } from '@/services/service/member/memberproject.socket';
import { webSocketLabelProjectTodo } from '@/services/service/member/melabeltodo.socket';
import { useFilterStore } from '@/stores/filterStore';
import { MBMeBoardResponse } from '@/services/api/member/projectdetail/metodo.api';
import { localStorageAction } from '@/utils/storage';
import { USER_INFO_STORAGE_KEY } from '@/constants/storagekey';

const route = useRoute()
const projectId = ref<string | null>(route.params.id as string || null)

// const reset = () => {
//     state.selectedDepartmentFacilityId = ''; 
// };

//todoProject
const statetodo = reactive({
  searchQuery: '',
  search: null as string | null,
  isModalOpen: false,
  idTodo: null as string | null,
  nameTodo: null as string | null,
  todo: [] as TodoPhaseProjectResponse[],
  paginationParams: { page: 1, size: 10 },
  totalItems: 0,
  isOpenModalImport: false
});

const deadlineTodo = ref(null);

const meAllDetailTodo = ref<TodoPhaseProjectResponse | null>(null);

const userLogin = localStorageAction.get(USER_INFO_STORAGE_KEY)

const filteredBoardTable = ref<TodoPhaseProjectResponse[]>([])

// Lấy filter từ Pinia store
const filterStore = useFilterStore()

watchEffect(() => {
  const filters = {
    memberId: filterStore.memberId,
    noMember: filterStore.noMember,
    myTodo: filterStore.myTodo,
    dueDate: filterStore.dueDate,
    labels: filterStore.labels,
    labelSelected: filterStore.labelsSelected,
    searchText: filterStore.searchText,
    membersSelected: filterStore.membersSelected
  }

  const now = Date.now()

  const result = statetodo.todo.filter(task => {
    const hasMember = (task.students?.length ?? 0) > 0 || (task.staff?.length ?? 0) > 0
    const isDone = task.statusTodo === 'done' || !!task.completionTime

    // Lọc theo từ khóa tìm kiếm
    if (filters.searchText?.trim()) {
      const name = task.todoName?.trim().toLowerCase()
      const keyword = filters.searchText.trim().toLowerCase()
      if (!name.includes(keyword)) return false
    }

    // Lọc theo không có thành viên
    if (filters.noMember && hasMember) return false

    // Lọc theo "việc của tôi"
    if (filters.myTodo) {
      const isMyTask = task.staff?.some(m => m.id === userLogin.userId) || task.students?.some(m => m.id === userLogin.userId)
      if (!isMyTask) return false
    }

    // Lọc theo thành viên được chọn
    if (filters.membersSelected?.length > 0) {
      const matched = task.staff?.some(m => filters.membersSelected.includes(m.id)) ||
        task.students?.some(m => filters.membersSelected.includes(m.id))
      if (!matched) return false
    }

    // Lọc theo nhãn 'no_label'
    if (filters.labels?.includes('no_label') && (task.label?.length ?? 0) > 0) return false

    // Lọc theo các label được chọn
    if (filters.labelSelected?.length > 0) {
      const labelIds = task.label?.map(l => l.id) ?? []
      const hasMatch = filters.labelSelected.some(id => labelIds.includes(id))
      if (!hasMatch) return false
    }

    // Lọc theo hạn
    if (filters.dueDate?.length > 0) {
      const isMatch = filters.dueDate.some(cond => {
        switch (cond) {
          case 'no_due':
            return task.deadlineTodo  == null
          case 'overdue':
          
            return task.deadlineTodo > 0 && task.deadlineTodo < now
          case 'not_done':
            return !task.completionTime
          case 'done_early':
            return task.completionTime !== null && task.completionTime < task.deadlineTodo;
          case 'done_late':
            return  task.completionTime !== null && task.completionTime > task.deadlineTodo;
          default:
            return false
        }
      })

      if (!isMatch) return false
    }

    return true
  })

  filteredBoardTable.value = result
})



const fetchTodoProject = async () => {
  try {
    if (!projectId.value) return;

    const response = await getAllTodoPhaseProject(projectId.value, route.params.idPhase as string);
    statetodo.todo = response?.data?.data || [];
    statetodo.totalItems = response?.data?.totalElements || 0;
    deadlineTodo.value = meAllDetailTodo.value?.deadlineTodo
  } catch (error) {
    console.error('Lỗi khi tải danh sách task:', error);
  }
};

onMounted(fetchTodoProject);

const handlePageChangeTodo = ({ page, pageSize }: { page: number; pageSize?: number }) => {
  statetodo.paginationParams.page = page;
  if (pageSize) {
    statetodo.paginationParams.size = pageSize;
  }
  fetchTodoProject();
};

const debouncedFetchTodoProject = debounce(fetchTodoProject, 300);

watch(() => statetodo.searchQuery, () => {
  statetodo.paginationParams.page = 1;
  debouncedFetchTodoProject();
});

const handlePriorityUpdate = ({ idTodo, priorityLevel }) => {

  const todoItem = statetodo.todo.find(item => item.todoId === idTodo);
  if (todoItem) {
    todoItem.priorityLevel = priorityLevel;
  }

  fetchTodoProject();
};

webSocketTodo.getUpdatePriorityLevel(() => {
  fetchTodoProject()
});

webSocketTodo.getUpdateNameTodo(() => {
  fetchTodoProject()
});

webSocketMemberProject.getJoinTodoVote(() => {
  fetchTodoProject()

});

webSocketMemberProject.getOutTodoVoteMemberProject(() => {
  fetchTodoProject()
});

webSocketLabelProjectTodo.getjoinLabeProjectTodo(fetchTodoProject)
webSocketLabelProjectTodo.getoutLabeProjectTodo(fetchTodoProject)

</script>