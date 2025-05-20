<template>
  <a-modal :open="open" :title="props.title" @cancel="closeModal">
    <template #footer>
      <a-popconfirm
        title="Bạn có chắc chắn muốn lưu thay đổi?"
        @confirm="handleSubmit"
        ok-text="Đồng ý"
        cancel-text="Huỷ"
      >
        <a-button type="primary">Xác nhận</a-button>
      </a-popconfirm>
      <a-button @click="closeModal">Huỷ</a-button>
    </template>
    <FormCustom>
      <a-form
        :model="staffDetail"
        ref="staffForm"
        name="staffForm"
        autocomplete="off"
        class="space-y-4 p-4"
      >
        <div class="flex flex-col">
          <label class="font-medium mb-1">Chọn cơ sở</label>
          <a-select
            v-model:value="idFacilitySelect"
            placeholder="Chọn cơ sở"
            name="facility"
            :rules="rules.facility"
          >
            <a-select-option
              v-for="facility in staffDetailFacility"
              :key="facility.id"
              :value="facility.id"
            >
              {{ facility.nameFacility }}
            </a-select-option>
          </a-select>
        </div>
 
        <!-- Chọn bộ môn -->
        <div class="flex flex-col">
          <label class="font-medium mb-1">Chọn bộ môn</label>
          <a-select
            v-model:value="idDepartmentSelect"
            placeholder="Chọn bộ môn"
            name="department"
            :rules="rules.department"
          >
            <a-select-option
              v-for="item in staffDetailDepartment"
              :key="item.idDepartment"
              :value="item.idDepartment"
            >
              {{ item.nameDepartment }}
            </a-select-option>
          </a-select>
        </div>

        <!-- Chọn chuyên ngành -->
        <div class="flex flex-col">
          <label class="font-medium mb-1">Chọn chuyên ngành</label>
          <a-select
            v-model:value="idMajorSelect"
            placeholder="Chọn chuyên ngành"
            name="major"
            :rules="rules.major"
          >
            <a-select-option
              v-for="dataMajor in staffDetailMajor"
              :key="dataMajor.idMajor"
              :value="dataMajor.idMajor"
            >
              {{ dataMajor.nameMajor }}
            </a-select-option>
          </a-select>
        </div>
      </a-form>
    </FormCustom>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, defineProps, defineEmits, onMounted, reactive } from 'vue'
import { toast } from 'vue3-toastify'
import axios from 'axios'
import {
  createStaffFDM,
  getAllStaffDepartment,
  getAllStaffFacility,
  getAllStaffMajor,
  staffByDepartmentMajor,
  staffDetailIdResponse,
  updateStaffFDm
} from '@/services/api/admin/staff.api'
import FormCustom from '@/components/custom/Form/FormCustom.vue'
import { useRoute } from 'vue-router'

const props = defineProps<{
  open: boolean
  staffId: string | null
  title: string
}>()
const emit = defineEmits(['close', 'success'])

const staffDetailFacility = ref<staffDetailIdResponse[]>([])
const staffDetailDepartment = ref<staffDetailIdResponse[]>()
const staffDetailMajor = ref<staffDetailIdResponse[]>()
const staffDetail = ref<staffDetailIdResponse>()
const staffForm = ref()
const idFacilitySelect = ref<string>('')
const idDepartmentSelect = ref<string>('')
const idMajorSelect = ref<string>('')
const idStaff = ref()
const router = useRoute()
const idFacilitySelectUpdate = ref<string>('')
const idDepartmentSelectUpdate = ref<string>('')
const idMajorSelectUpdate = ref<string>('')
const rules = {
  facility: [
    { required: true, message: 'Mã không được để trống!', trigger: 'blur' },
    { max: 50, message: 'Mã không được vượt quá 50 ký tự!', trigger: 'blur' }
  ],
  major: [
    { required: true, message: 'Tên không được để trống!', trigger: 'blur' },
    { max: 100, message: 'Tên không được vượt quá 100 ký tự!', trigger: 'blur' }
  ],
  department: [
    { required: true, message: 'Tên không được để trống!', trigger: 'blur' },
    { max: 100, message: 'Tên không được vượt quá 100 ký tự!', trigger: 'blur' }
  ]
}

onMounted(() => {
  fetchStaffDetailFacility()
})

watch

const fetchStaffDetailFacility = async () => {
  try {
    const response = await getAllStaffFacility()
    staffDetailFacility.value = response.data as [] // Gán dữ liệu khi đúng định dạng
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    }
  }
}

const fetchStaffDetailDepartment = async (id: string) => {
  idDepartmentSelect.value = ''
  try {
    const response = await getAllStaffDepartment(id)
    staffDetailDepartment.value = response.data as []
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    }
  }
}

const fetchStaffDetailMajor = async (id: string) => {
  idMajorSelect.value = ''
  try {
    const response = await getAllStaffMajor(id)
    staffDetailMajor.value = response.data as []
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    }
  }
}

watch(idFacilitySelect, (newValue, oldValue) => {
  console.log('Giá trị mới của idFacilitySelect:', newValue)
  fetchStaffDetailDepartment(newValue as string) // Đảm bảo kiểu dữ liệu đúng
})

watch(idDepartmentSelect, (newValue, oldValue) => {
  fetchStaffDetailMajor(newValue as any)
  console.log(newValue)
})

watch(
  () => [props.staffId, props.open],
  async ([newId, isOpen]) => {
    if (isOpen) {
      if (staffForm.value) {
        staffForm.value.resetFields()
      }
      if (newId) {
        const data = await staffByDepartmentMajor(newId as string)
        idFacilitySelect.value = data.data.idFacility as string
        idFacilitySelectUpdate.value = data.data.idFacility as string

        await fetchStaffDetailDepartment(data.data.idFacility as string)
        idDepartmentSelect.value = data.data.idDepartment as string
        idDepartmentSelectUpdate.value = data.data.idDepartment as string

        await fetchStaffDetailMajor(data.data.idDepartment as string)
        idMajorSelect.value = data.data.idMajor as string
        idMajorSelectUpdate.value = data.data.idMajor as string
      } else {
        staffDetail.value = { idMajor: '', idDepartment: '', idFacility: '' }
      }
    }
  },
  { immediate: true }
)

const handleSubmit = async () => {
  try {
    const formData = new FormData()
    formData.append('idFacility', idFacilitySelect.value as string)
    formData.append('idDepartment', idDepartmentSelect.value as string)
    formData.append('idMajor', idMajorSelect.value as string)
    formData.append('idStaffDetail', router.params.id as string)

    const formDataUpdate = new FormData()
    formDataUpdate.append('idFacility', idFacilitySelectUpdate.value as string)
    formDataUpdate.append('idDepartment', idDepartmentSelectUpdate.value as string)
    formDataUpdate.append('idMajor', idMajorSelectUpdate.value as string)
    formDataUpdate.append('idStaffDetail', router.params.id as string)
    formDataUpdate.append('idUpdateFacility', idFacilitySelect.value as string)
    formDataUpdate.append('idUpdateDepartment', idDepartmentSelect.value as string)
    formDataUpdate.append('idUpdateMajor', idMajorSelect.value as string)
    
    if (props.staffId == null) {
      const res = await createStaffFDM(formData)
      toast.success(res.message)
    } else {
      console.log(formDataUpdate + 'SSSSSSSS')
      console.log(idFacilitySelectUpdate.value + ' co ưo')
      console.log(idDepartmentSelectUpdate.value + 'bo mon')
      console.log(idMajorSelectUpdate.value + 'chuyen naga ')
      console.log(router.params.id + 'id staff')
      console.log(idFacilitySelect.value + 'co so update')
      console.log(idDepartmentSelect.value + ' bo mon update')
      console.log(idMajorSelect.value + 'chuye chuyenngan ')

      const res = await updateStaffFDm(formDataUpdate)
      toast.success(res.message)
    }
    closeModal()
    emit('success')
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error?.response?.data?.message) {
        toast.error(error?.response?.data?.message)
      }
    }
  }
}

const closeModal = () => emit('close')
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