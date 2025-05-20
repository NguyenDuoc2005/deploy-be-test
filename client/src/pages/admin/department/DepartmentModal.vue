<template>
  <a-modal :open="open" :title="title" @cancel="closeModal" style="top: 20px">
    <template #footer>
      <a-popconfirm title="Bạn có chắc chắn muốn lưu thay đổi?" @confirm="handleSubmit" ok-text="Đồng ý" cancel-text="Huỷ">
        <a-button type="primary">Xác nhận</a-button>
      </a-popconfirm>
      <a-button @click="resetForm(); closeModal();">Huỷ</a-button>
    </template>

    <a-form :model="department" ref="departmentForm" name="departmentForm" autocomplete="off">
      <a-form-item label="Mã bộ môn" name="departmentCode" :rules="rules.departmentCode">
        <a-input v-model:value="department.departmentCode" />
      </a-form-item>
      <a-form-item label="Tên bộ môn" name="departmentName" :rules="rules.departmentName">
        <a-input v-model:value="department.departmentName" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits } from 'vue'
import { getDepartment, addDepartment, updateDepartment } from '@/services/api/department.api'
import { toast } from 'vue3-toastify'
import dayjs from 'dayjs'

const props = defineProps<{ open: boolean; departmentId: string | null; title: string }>()
const emit = defineEmits(['close', 'success'])

const department = ref({
  departmentCode: '',
  departmentName: '',
  departmentStatus: 0
})
const departmentForm = ref()

const rules = {
  departmentCode: [{ required: true, message: 'Mã bộ môn không được để trống!' },  {min: 3, max: 50, message: "Mã bộ môn phải từ 3 - 50 ký tự!"}, { pattern: /^[a-zA-Z0-9]+$/, message: "Mã bộ môn không được chứa ký tự đặc biệt!" }],
  departmentName: [{ required: true, message: 'Tên bộ môn không được để trống!' }, {min: 3, max: 50, message: "Tên bộ môn phải từ 3 - 50 ký tự!"},{ pattern: /^[a-zA-ZÀ-Ỹà-ỹ0-9\s\u00C0-\u1EF9]+$/, message: "Tên bộ môn không được chứa ký tự đặc biệt!" }],
  departmentStatus: [{ required: true, message: 'Trạng thái không được để trống!' }]
}

const fetchDepartmentDetails = async (id: string) => {
  try {
    const response = await getDepartment(id)
    department.value = {
      departmentCode: response.data.departmentCode,
      departmentName: response.data.departmentName,
      departmentStatus: response.data.departmentStatus
    }
  } catch (error) {
    console.error('Lỗi khi lấy thông tin bộ môn:', error)
  }
}

watch(
  () => props.departmentId,
  async (newId) => {
      console.log('🛠 Checking departmentId:', newId);
    if (newId) {
      await fetchDepartmentDetails(newId)
    } else {
      department.value = { departmentCode: '', departmentName: '', departmentStatus: 0 }
    }
  },
  { immediate: true }
)

const closeModal = () => emit('close')

const resetForm = () => {
  department.value = { 
      departmentCode: '', 
      departmentName: '', 
      departmentStatus: 0, 
  }
  departmentForm.value?.resetFields();
}

const handleSubmit = async () => {
  try {
    await departmentForm.value.validate()
    const formData = {
      departmentId: props.departmentId || '',
      departmentCode: department.value.departmentCode,
      departmentName: department.value.departmentName,
      departmentStatus: department.value.departmentStatus,
      createdDate: dayjs().valueOf()
    }

    if (props.departmentId) {
      await updateDepartment(formData, props.departmentId)
      toast.success('Cập nhật thành công!')
    } else {
      await addDepartment(formData)
      toast.success('Thêm thành công!')
    }
    resetForm()
    closeModal()
    emit('success')
    
  } catch (error) {
    if (props.departmentId) {
      toast.error('Cập nhật thất bại')
    } else {
      toast.error('Thêm thất bại')
    }
  }
}
</script>
<style scoped>
.ant-btn {
  background-color: var(--selected-header) !important;
  color: var(--selected-text) !important;
  border-color: var(--selected-header) !important;
}

.ant-btn:hover,
.ant-btn:focus {
  background-color: var(--selected-header-hover) !important;
  border-color: var(--selected-header-hover) !important;
}
</style>