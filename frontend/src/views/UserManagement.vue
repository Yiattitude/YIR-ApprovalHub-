<template>
  <div>
    <h2>用户管理</h2>
    <el-button type="primary" @click="showCreateDialog">新增用户</el-button>
    
    <el-table :data="users" style="width: 100%; margin-top: 20px" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="username" label="用户名" width="120"></el-table-column>
      <el-table-column prop="fullName" label="姓名" width="120"></el-table-column>
      <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
      <el-table-column prop="phone" label="电话" width="120"></el-table-column>
      <el-table-column prop="department.name" label="部门" width="120">
        <template #default="scope">
          {{ scope.row.department?.name || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="role" label="角色" width="100">
        <template #default="scope">
          {{ getRoleText(scope.row.role) }}
        </template>
      </el-table-column>
      <el-table-column prop="enabled" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.enabled ? 'success' : 'danger'">
            {{ scope.row.enabled ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="fullName">
          <el-input v-model="form.fullName"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-select v-model="form.department" placeholder="请选择部门" value-key="id">
            <el-option 
              v-for="dept in departments" 
              :key="dept.id" 
              :label="dept.name" 
              :value="dept">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="员工" value="EMPLOYEE"></el-option>
            <el-option label="审批人" value="APPROVER"></el-option>
            <el-option label="管理员" value="ADMIN"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="岗位" prop="positions">
          <el-select v-model="form.positions" multiple placeholder="请选择岗位" value-key="id">
            <el-option 
              v-for="pos in positions" 
              :key="pos.id" 
              :label="pos.name" 
              :value="pos">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="enabled">
          <el-switch v-model="form.enabled"></el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import api from '../services/api';

export default {
  name: 'UserManagement',
  setup() {
    const users = ref([]);
    const departments = ref([]);
    const positions = ref([]);
    const loading = ref(false);
    const dialogVisible = ref(false);
    const dialogTitle = ref('');
    const submitting = ref(false);
    const isEdit = ref(false);
    const formRef = ref(null);

    const form = reactive({
      id: null,
      username: '',
      password: '',
      fullName: '',
      email: '',
      phone: '',
      department: null,
      role: 'EMPLOYEE',
      positions: [],
      enabled: true
    });

    const rules = {
      username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
      fullName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
      role: [{ required: true, message: '请选择角色', trigger: 'change' }]
    };

    const loadUsers = async () => {
      loading.value = true;
      try {
        const response = await api.getAllUsers();
        users.value = response.data.data;
      } catch (error) {
        console.error('加载用户列表失败:', error);
        console.error('错误详情:', error.response);
        ElMessage.error('加载用户列表失败: ' + (error.response?.data?.message || error.message));
      } finally {
        loading.value = false;
      }
    };

    const loadDepartments = async () => {
      try {
        const response = await api.getAllDepartments();
        departments.value = response.data.data;
      } catch (error) {
        ElMessage.error('加载部门列表失败');
      }
    };

    const loadPositions = async () => {
      try {
        const response = await api.getAllPositions();
        positions.value = response.data.data;
      } catch (error) {
        ElMessage.error('加载岗位列表失败');
      }
    };

    const showCreateDialog = () => {
      isEdit.value = false;
      dialogTitle.value = '新增用户';
      resetForm();
      dialogVisible.value = true;
    };

    const handleEdit = (user) => {
      isEdit.value = true;
      dialogTitle.value = '编辑用户';
      form.id = user.id;
      form.username = user.username;
      form.fullName = user.fullName;
      form.email = user.email;
      form.phone = user.phone;
      form.department = user.department;
      form.role = user.role;
      form.positions = user.positions || [];
      form.enabled = user.enabled;
      dialogVisible.value = true;
    };

    const handleDelete = async (user) => {
      try {
        await ElMessageBox.confirm(`确认删除用户 ${user.fullName} 吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        await api.deleteUser(user.id);
        ElMessage.success('删除成功');
        loadUsers();
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.response?.data?.message || '删除失败');
        }
      }
    };

    const handleSubmit = async () => {
      if (!formRef.value) return;
      
      await formRef.value.validate(async (valid) => {
        if (valid) {
          submitting.value = true;
          try {
            const data = {
              username: form.username,
              fullName: form.fullName,
              email: form.email,
              phone: form.phone,
              department: form.department,
              role: form.role,
              positions: form.positions,
              enabled: form.enabled
            };

            if (isEdit.value) {
              await api.updateUser(form.id, data);
              ElMessage.success('更新成功');
            } else {
              data.password = form.password;
              await api.createUser(data);
              ElMessage.success('创建成功');
            }
            
            dialogVisible.value = false;
            loadUsers();
          } catch (error) {
            ElMessage.error(error.response?.data?.message || '操作失败');
          } finally {
            submitting.value = false;
          }
        }
      });
    };

    const resetForm = () => {
      form.id = null;
      form.username = '';
      form.password = '';
      form.fullName = '';
      form.email = '';
      form.phone = '';
      form.department = null;
      form.role = 'EMPLOYEE';
      form.positions = [];
      form.enabled = true;
      if (formRef.value) {
        formRef.value.clearValidate();
      }
    };

    const getRoleText = (role) => {
      const roleMap = {
        'ADMIN': '管理员',
        'APPROVER': '审批人',
        'EMPLOYEE': '员工'
      };
      return roleMap[role] || role;
    };

    onMounted(() => {
      loadUsers();
      loadDepartments();
      loadPositions();
    });

    return {
      users,
      departments,
      positions,
      loading,
      dialogVisible,
      dialogTitle,
      submitting,
      isEdit,
      form,
      formRef,
      rules,
      showCreateDialog,
      handleEdit,
      handleDelete,
      handleSubmit,
      getRoleText
    };
  }
};
</script>

<style scoped>
h2 {
  margin-bottom: 20px;
}
</style>
