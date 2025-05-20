<script setup lang="ts">
import { ROLES } from '@/constants/roles';
import { useAuthStore } from '@/stores/auth';
import { UserInformation } from '@/types/auth.type';
import { UserOutlined, LogoutOutlined, IdcardOutlined } from '@ant-design/icons-vue';
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { Dropdown, Menu, Avatar, Space } from 'ant-design-vue';
import ModalProfile from './ModalProfile.vue';

const props = defineProps({
  isNoSidebarPage: Boolean
});

const router = useRouter();
const isModalVisible = ref(false); 

const userInfor = reactive<UserInformation>({
  email: '',
  fullName: '',
  pictureUrl: '',
  rolesCodes: '',
  rolesNames: '',
  userCode: '',
  userId: '',
  idFacility: '',
  roleScreen: '',
  roleSwitch: ''
});

const userAuthStore = useAuthStore();

onMounted(() => {
  userInfor.userId = userAuthStore?.user?.userId || '';
  userInfor.email = userAuthStore?.user?.email || '';
  userInfor.pictureUrl = userAuthStore?.user?.pictureUrl || '';
  userInfor.fullName = userAuthStore?.user?.fullName || '';
  userInfor.rolesCodes = userAuthStore?.user?.rolesCodes as string;
  userInfor.roleScreen = userAuthStore?.user?.roleScreen || '';
});

const handleLogout = () => {
  userAuthStore.logout();
  router.push({ name: 'login' });
};

const showProfileModal = () => {
  isModalVisible.value = true;
};

const showRoleUser = computed(() => {
  if (!userInfor || !userInfor.rolesCodes) {
    return 'Không có thông tin vai trò';
  }
  switch (userInfor.roleScreen) {
    case ROLES.ADMIN:
      return 'ADMIN';
    case ROLES.MANAGE:
      return 'Quản lý dự án';
    case ROLES.MEMBER:
      return 'Thành viên dự án';
    default:
      return userInfor.roleScreen;
  }
});
</script>

<template>
  <Dropdown>
    <template #overlay>
      <Menu>
        <Menu.Item>
          <UserOutlined /> Vai trò: {{ showRoleUser }}
        </Menu.Item>
        <Menu.Item @click="showProfileModal">
          <IdcardOutlined /> Thông tin cá nhân
        </Menu.Item>
        <Menu.Item @click="handleLogout">
          <LogoutOutlined /> Đăng xuất
        </Menu.Item>
      </Menu>
    </template>
    <a class="flex items-center gap-3 cursor-pointer">
      <Avatar :src="userInfor.pictureUrl" :size="40" />
      <Space direction="vertical" size="small" class="hidden lg:block">
        <span class="text-sm font-medium">{{ userInfor.fullName }}</span>
      </Space>
    </a>
  </Dropdown>

  <!--ModalProfile -->
  <ModalProfile v-model:visible="isModalVisible" :userInfor="userInfor" />
</template>

<style scoped>
a {
  display: flex;
  align-items: center;
}
</style>
