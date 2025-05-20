<template>
  <a-modal :open="props.open" :title="props.title" @cancel="closeModal" :width="'40%'">
    <div class="max-w-full mx-auto p-6 bg-white shadow-lg rounded-lg">
      <table class="w-full border-collapse border border-gray-300">
        <thead>
          <tr class="bg-gray-100">
            <th class="p-3">STT</th>
            <th class="p-3">Mã chức vụ</th>
            <th class="p-3">Tên chức vụ</th>
            <th class="p-3">Chọn</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in defaultTableRole" :key="item.code" class="hover:bg-gray-50 text-center">
            <td class="p-3">{{ index + 1 }}</td>
            <td class="p-3">{{ item.code }}</td>
            <td class="p-3">{{ item.name }}</td>
            <td class="p-3">
              <label class="custom-checkbox">
                <input type="checkbox" :checked="isCheckedCode(item.code)" @change="toggleCheckbox(item.code)" />
                <span></span>
              </label>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits, watch, onMounted } from 'vue'
import { toast } from 'vue3-toastify'
import {
  getAllStaffRole,
  getAllRoleByStaff,
  roleStaffResponse,
  createStaffRole,
  idRoleAndStaff,
  deleteStaffByRole,
  getStaffRoleByStaff
} from '@/services/api/admin/staff.api'
import { useRoute } from 'vue-router'

const props = defineProps<{ open: boolean; staffId: string | null; title: string }>()
const emit = defineEmits(['close', 'success', 'updateRoles'])
const route = useRoute()
const productId = route.params.id
const staffRole = ref<roleStaffResponse[]>([]) // Danh sách chức vụ từ API
const compareValues = ref<string[]>([]) // Danh sách ID đã được chọn
const selectedItems = ref(new Set()) // Trạng thái tích checkbox
const selectedItemsByCode = ref(new Map()) // Trạng thái tích checkbox

type CodeRole = 'ADMIN' | 'QUAN_LY' | 'THANH_VIEN';
type DefaultTableRoleType = { code: CodeRole, name: String };

const defaultTableRole: DefaultTableRoleType[] = [
  {
    code: 'ADMIN',
    name: 'Admin',
  },
  {
    code: 'QUAN_LY',
    name: 'Quản lý',
  },
  {
    code: 'THANH_VIEN',
    name: 'Thành viên',
  },
]

const loadAssignedRoles = async () => {
  try {
    const res = await getAllRoleByStaff(props.staffId as string)
    compareValues.value = res.data ? res.data.map((r: any) => String(r.id)) : []
  } catch (error) {
    toast.error('Lỗi khi tải quyền đã gán')
  }
}

const loadRoles = async () => {
  try {
    const response = await getAllStaffRole()
    staffRole.value = response.data as roleStaffResponse[]
    console.log(
      'staffRole:',
      staffRole.value.map((item) => item.id)
    )
  } catch (error) {
    toast.error('Lỗi khi tải danh sách chức vụ')
  }
}

const updateCheckedItems = () => {

  if (!staffRole.value.length || !compareValues.value.length) {
    console.log('Dữ liệu chưa sẵn sàng, thoát hàm')
    return
  }

  selectedItems.value.clear()
  selectedItemsByCode.value.clear()

  staffRole.value.forEach((sr) => {
    if (compareValues.value.includes(sr.id as string)) {
      selectedItems.value.add(sr.id);
      selectedItemsByCode.value.set(sr.code, sr.id);
      console.log(sr.code, sr.id);
      console.log('Thêm vào selectedItems:', sr.id)
    }
  })

  console.log('Updated selected items:', Array.from(selectedItemsByCode.value))
}

watch(
  [compareValues, staffRole],
  (newValues) => {
    console.log('🔍 Giá trị mới:', newValues)
    updateCheckedItems()
  },
  { immediate: true }
)

watch(compareValues, () => {
  updateCheckedItems()
})

const isChecked = (id: string) => selectedItems.value.has(String(id));

const isCheckedCode = (code: CodeRole) => selectedItemsByCode.value.has(String(code));

const toggleCheckbox = async (code: CodeRole) => {
  const newMap = new Map(selectedItemsByCode.value)

  if (newMap.has(code)) {
    newMap.delete(code)
    const params: idRoleAndStaff = {
      idRole: selectedItemsByCode.value.get(code),
      idStaff: productId as string
    }
    await deleteStaffByRole(params)
    toast.success('Xóa quyền thành công!')
    fetchDataRole();
  } else {
    try {
      const params: idRoleAndStaff = {
        idRole: selectedItemsByCode.value.get(code),
        idStaff: productId as string,
        codeRole: code
      }

      await createStaffRole(params)
      toast.success('Cập nhật role nhân viên thành công')
      fetchDataRole();
    } catch (error) {
      toast.error('Lỗi khi gán quyền!')
    }
  }

  selectedItemsByCode.value = newMap;

  emit('updateRoles') // 🔥 Emit sự kiện cập nhật bảng
}

onMounted(async () => {
  fetchDataRole();
})

const closeModal = () => {
  emit('close')
}

const handleConfirm = async () => {
  // Gọi API cập nhật role
  await Promise.all(
    Array.from(selectedItems.value).map(async (id) => {
      const params: idRoleAndStaff = { idRole: id as string, idStaff: productId as string }
      await createStaffRole(params)
    })
  )
  emit('success')
  closeModal()
}

const fetchDataRole = async () => {
  try {
    const [rolesRes, assignedRolesRes] = await Promise.all([
      getAllStaffRole(),
      getAllRoleByStaff(productId as string),
    ])

    staffRole.value = rolesRes.data as roleStaffResponse[]
    compareValues.value = assignedRolesRes.data
      ? assignedRolesRes.data.map((r: any) => String(r.idRole))
      : []

    updateCheckedItems()

    emit('success')
  } catch (error) {
    toast.error('Lỗi khi tải dữ liệu ban đầu')
  }
}

</script>

<style scoped>
input[type='checkbox']:checked+div {
  background-color: #facc15;
}

.custom-checkbox {
  display: inline-block;
  position: relative;
  width: 24px;
  height: 24px;
}

.custom-checkbox input {
  opacity: 0;
  position: absolute;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.custom-checkbox span {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #ffcc00;
  /* Màu nền vàng */
  border-radius: 4px;
}

.custom-checkbox input:checked+span::after {
  content: '\2713';
  /* Dấu tích (✔) */
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  color: white;
  /* Dấu tích màu trắng */
  font-weight: bold;
  font-size: 16px;
}
</style>
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