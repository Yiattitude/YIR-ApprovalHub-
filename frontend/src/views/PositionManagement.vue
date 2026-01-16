<template>
  <div>
    <h2>岗位管理</h2>
    <el-button type="primary" @click="showCreateDialog">新增岗位</el-button>
    
    <el-table :data="positions" style="width: 100%; margin-top: 20px" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="岗位名称" width="150"></el-table-column>
      <el-table-column prop="description" label="描述" width="200"></el-table-column>
      <el-table-column prop="level" label="级别" width="80"></el-table-column>
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
        <el-form-item label="岗位名称" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="级别" prop="level">
          <el-input-number v-model="form.level" :min="1" :max="10"></el-input-number>
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
  name: 'PositionManagement',
  setup() {
    const positions = ref([]);
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
      level: 1,
      enabled: true
    });

    const rules = {
      name: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
      level: [{ required: true, message: '请输入级别', trigger: 'blur' }]
    };

    const loadPositions = async () => {
      loading.value = true;
      try {
        const response = await api.getAllPositions();
        positions.value = response.data.data;
      } catch (error) {
        ElMessage.error('加载岗位列表失败');
      } finally {
        loading.value = false;
      }
    };

    const showCreateDialog = () => {
      isEdit.value = false;
      dialogTitle.value = '新增岗位';
      resetForm();
      dialogVisible.value = true;
    };

    const handleEdit = (position) => {
      isEdit.value = true;
      dialogTitle.value = '编辑岗位';
      form.id = position.id;
      form.name = position.name;
      form.description = position.description;
      form.level = position.level;
      form.enabled = position.enabled;
      dialogVisible.value = true;
    };

    const handleDelete = async (position) => {
      try {
        await ElMessageBox.confirm(`确认删除岗位 ${position.name} 吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        await api.deletePosition(position.id);
        ElMessage.success('删除成功');
        loadPositions();
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
              level: form.level,
              enabled: form.enabled
            };

            if (isEdit.value) {
              await api.updatePosition(form.id, data);
              ElMessage.success('更新成功');
            } else {
              await api.createPosition(data);
              ElMessage.success('创建成功');
            }
            
            dialogVisible.value = false;
            loadPositions();
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
      form.level = 1;
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
      loadPositions();
    });

    return {
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
