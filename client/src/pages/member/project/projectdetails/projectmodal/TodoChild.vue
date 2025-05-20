<template>
  <div class="mb-4">
    <h3 class="font-semibold mb-2 flex items-center space-x-2">
      <FileDoneOutlined class="text-black text-xl" />
      <span>Những việc cần làm</span>
    </h3>
    
    <a-progress :percent="progress" class="mb-3" />

    <!-- Danh sách công việc -->
    <div v-for="(item, index) in todoChild" :key="index"
      class="group flex justify-between items-center px-3 py-2 bg-white rounded-lg border-0 hover:bg-gray-100 transition">

      <!-- Checkbox + tên + trạng thái -->
      <div class="flex items-center space-x-2">
        <a-checkbox :checked="item.statusTodoChild === 1" @change="() => handleStatusChange(item)" />

        <div class="flex flex-col">
          <template v-if="editingId === item.id">
            <div class="flex space-x-2 items-center">
              <a-input v-model:value="editName" size="small" @keyup.enter="saveEdit(item)" />
              <a-button type="primary" size="small" @click="saveEdit(item)">Lưu</a-button>
              <a-button size="small" @click="cancelEdit">Hủy</a-button>
            </div>
          </template>
          <template v-else>
            <span :class="{
              'line-through text-gray-400': item.statusTodoChild === 1,
              'cursor-pointer hover:underline break-words whitespace-normal w-full overflow-hidden': true
            }" style="overflow-wrap: anywhere;" @click="startEdit(item)">
              {{ item.name }}
            </span>

          </template>
        </div>
      </div>

      <!-- Nút xóa -->
      <a-popconfirm :zIndex="10000" title="Bạn có chắc muốn xóa công việc này không?" ok-text="Xóa" cancel-text="Hủy"
        @confirm="() => removeTodoChild(item.id)">
        <a-button type="text" danger size="small"
          class="opacity-0 group-hover:opacity-100 transition-opacity duration-200">
          <DeleteOutlined />
        </a-button>
      </a-popconfirm>

    </div>

    <!-- Thêm việc mới -->
    <div class="mt-4">
      <div v-if="showAddInput" class="flex space-x-2">
        <a-input v-model:value="newTodoChildName" placeholder="Nhập tên công việc..." @keyup.enter="addTodoChild"
          ref="addInputRef" />
        <a-button type="primary" @click="addTodoChild">Lưu</a-button>
        <a-button @click="showAddInput = false">Hủy</a-button>
      </div>
      <div v-else>
        <a-button type="primary" @click="showAddInput = true" class="flex items-center space-x-1">
          <PlusOutlined />
          <span>Thêm</span>
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { TODO_ID_STORAGE_KEY } from '@/constants/key';
import { StatusTodoChild } from '@/constants/statusTodoChild';
import { MBMeTodoChild_Response, ParamseditTodoChecklistTestRequest } from '@/services/api/member/projectdetail/metodo.api';
import { MBMeCreateTodoChildChecklistRequest, MBMeDeleteTodoChecklistRequest, webSocketTodoChild } from '@/services/service/member/todochild.socket';
import { localStorageAction } from '@/utils/storage';
import { DeleteOutlined, EditOutlined, FileDoneOutlined, PlusOutlined, ProfileOutlined, SnippetsOutlined } from '@ant-design/icons-vue';
import { ref, computed, nextTick, } from 'vue';

const props = defineProps<{
  todoChild: MBMeTodoChild_Response[];
  idTodo: string | null
}>();

const emit = defineEmits<{
  (e: 'status-change', item: MBMeTodoChild_Response): void;
  (e: 'add-todoChild', name: string): void;
  (e: 'remove-todoChild', id: string): void;
  (e: 'edit-todoChild', item: MBMeTodoChild_Response): void;
}>();

// Tính phần trăm công việc hoàn thành
const progress = computed(() => {
  if (!props.todoChild.length) return 0;
  const done = props.todoChild.filter(item => item.statusTodoChild === 1).length;
  return Math.round((done / props.todoChild.length) * 100);
});

// Thay đổi trạng thái công việc
const handleStatusChange = (item: MBMeTodoChild_Response) => {
  // Đổi trạng thái local
  item.statusTodoChild = item.statusTodoChild === 1 ? 0 : 1;
  emit('status-change', item);

  // Dùng enum để gửi WebSocket
  const statusEnum = item.statusTodoChild === 1
    ? StatusTodoChild.COMPLETE
    : StatusTodoChild.UNCOMPLETE;

  webSocketTodoChild.sendUpdateStatusTodoChecklist({
    idTodoChild: item.id,
    statusTodoChild: statusEnum,
    idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY),
    sessionId: sessionStorage.getItem('sessionId') || '',
  });

};


// Thêm công việc mới
const newTodoChildName = ref('');
const showAddInput = ref(false);
const addInputRef = ref();

const addTodoChild = async () => {

  if (!newTodoChildName.value.trim() || !props.idTodo) return;

  const payload: MBMeCreateTodoChildChecklistRequest = {
    name: newTodoChildName.value.trim(),
    idTodo: props.idTodo,
  };
  // await createTodoChecklistAPI(payload)
  webSocketTodoChild.sendCreateTodoChildChecklist(payload);

  newTodoChildName.value = '';

  emit('add-todoChild', newTodoChildName.value);

  // Focus lại vào ô input
  nextTick(() => {
    addInputRef.value?.focus();
  });
};

// Xóa công việc
const removeTodoChild = async (idTodoChild: string) => {

  const payload: MBMeDeleteTodoChecklistRequest = {
    idTodoChild: idTodoChild,
    idTodo: localStorageAction.get(TODO_ID_STORAGE_KEY)
  };

  // await deleteTodoChecklistAPI(payload);
  webSocketTodoChild.sendDeleteTodoChecklist(payload);
  emit('remove-todoChild', idTodoChild);

};
const editingId = ref<string | null>(null); // id đang sửa
const editName = ref('');

// Bắt đầu sửa
const startEdit = (item: MBMeTodoChild_Response) => {
  editingId.value = item.id;
  editName.value = item.name;
};

// Hủy sửa
const cancelEdit = () => {
  editingId.value = null;
  editName.value = '';
};

// Lưu sửa
const saveEdit = async (item: MBMeTodoChild_Response) => {
  const trimmedName = editName.value.trim();
  if (!trimmedName || trimmedName === item.name) {
    cancelEdit();
    return;
  }
  // Cập nhật local
  item.name = trimmedName;

  const payload: ParamseditTodoChecklistTestRequest = {
    name: trimmedName,
    idTodoChild: item.id,
  };
  webSocketTodoChild.sendEditTodoChecklist(payload);
  emit('edit-todoChild', item);
  cancelEdit();
};

</script>
