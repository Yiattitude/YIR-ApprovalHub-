<template>
  <div>
    <h2>部门管理</h2>
    <el-button type="primary" @click="showCreateDialog">新增部门</el-button>
    
    <el-table :data="departments" style="width: 100%; margin-top: 20px" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="部门名称" width="150"></el-table-column>
      <el-table-column prop="description" label="描述" width="200"></el-table-column>
      <el-table-column prop="enabled" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.enabled ? 'success' : 'danger'">
            {{ scope.row.enabled ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180">
        <template #default="scope">
          {{ formatDateTime(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3"></el-input>
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
  name: 'DepartmentManagement',
  setup() {
    const departments = ref([]);
    const loading = ref(false);
    const dialogVisible = ref(false);
    const dialogTitle = ref('');
    const submitting = ref(false);
    const isEdit = ref(false);
    const formRef = ref(null);

    const form = reactive({
      id: null,
      name: '',
      description: '',
      enabled: true
    });

    const rules = {
      name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
    };

    const loadDepartments = async () => {
      loading.value = true;
      try {
        const response = await api.getAllDepartments();
        departments.value = response.data.data;
      } catch (error) {
        console.error('加载部门列表失败:', error);
        console.error('错误详情:', error.response);
        ElMessage.error('加载部门列表失败: ' + (error.response?.data?.message || error.message));
      } finally {
        loading.value = false;
      }
    };

    const showCreateDialog = () => {
      isEdit.value = false;
      dialogTitle.value = '新增部门';
      resetForm();
      dialogVisible.value = true;
    };

    const handleEdit = (department) => {
      isEdit.value = true;
      dialogTitle.value = '编辑部门';
      form.id = department.id;
      form.name = department.name;
      form.description = department.description;
      form.enabled = department.enabled;
      dialogVisible.value = true;
    };

    const handleDelete = async (department) => {
      try {
        await ElMessageBox.confirm(`确认删除部门 ${department.name} 吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        await api.deleteDepartment(department.id);
        ElMessage.success('删除成功');
        loadDepartments();
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
              name: form.name,
              description: form.description,
              enabled: form.enabled
            };

            if (isEdit.value) {
              await api.updateDepartment(form.id, data);
              ElMessage.success('更新成功');
            } else {
              await api.createDepartment(data);
              ElMessage.success('创建成功');
            }
            
            dialogVisible.value = false;
            loadDepartments();
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
      form.name = '';
      form.description = '';
      form.enabled = true;
      if (formRef.value) {
        formRef.value.clearValidate();
      }
    };

    const formatDateTime = (dateTime) => {
      if (!dateTime) return '-';
      return new Date(dateTime).toLocaleString('zh-CN');
    };

    onMounted(() => {
      loadDepartments();
    });

    return {
      departments,
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
      formatDateTime
    };
  }
};
</script>

<style scoped>
h2 {
  margin-bottom: 20px;
}
</style>
