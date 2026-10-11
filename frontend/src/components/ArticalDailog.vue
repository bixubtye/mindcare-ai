<template>
    <el-dialog :title="isEdit ? '文章编辑' : '新增文章'"
    v-model="visible"
    width="50%"
    @close="handleClose"
    >
     <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px">
        <el-form-item label="文章标题" prop="title">
            <el-input v-model="formData.title" placeholder="请输入文章标题" maxlength="200" show-word-limit clearable />
        </el-form-item>
        <el-form-item label="分类"prop="categoryId">
            <el-select v-model="formData.categoryId" placeholder="请选择分类">
                <el-option v-for="item in catagories" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
        </el-form-item>
        <el-form-item label="文章摘要" prop="summary">
            <el-input v-model="formData.summary" type="textarea" placeholder="请输入文章摘要（可选）" maxlength="1000" show-word-limit rows="4" />
        </el-form-item>
        <el-form-item label="文章标签" prop="tags">
            <el-select v-model="formData.tagArray" placeholder="请输入文章标签" multiple filterable allow-create style="width: 100%">
                <el-option v-for="item in commonTags" :key="item" :label="item" :value="item" />
            </el-select>
        </el-form-item>
        <el-form-item label="文章图片">
            <el-upload
                class="avatar-uploader"
                action="#"
                :show-file-list="false"
                :before-upload="beforeUpload"
                :http-request="handleAvatarSuccess"
                accept="image/*"
            >
                <div v-if="!imageUrl" class="cover-placeholder">
                    <p>点击上传图片</p>
                </div>
                <img v-else :src="imageUrl" alt="上传的图片" class="cover-image" />
            </el-upload>
            <div v-if="imageUrl" class="cover-remove">
                <el-button type="danger" size="mini" @click="removeCover">删除图片</el-button>
            </div>
        </el-form-item>
        <el-form-item label="文章正文" prop="content">
            <RichTextEditor v-model="formData.content"
            height="500px"
            placeholder="请输入文章正文"
            maxCharCount="5000"
            @Change="handleContentChange"
            @created="handleCreated"
            />
        </el-form-item>
    </el-form>

    <template #footer>
        <div v-if="showPreview" style="text-align: left">
            <h3>内容预览</h3>
            <div v-html="formData.content"></div>
        </div>
        <el-button type="primary" @click="showPreview = !showPreview">
            {{ showPreview ? '隐藏预览' : '预览效果' }}
        </el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
        <el-button type="primary" @click="handleClose">取消</el-button>
    </template>
    </el-dialog>
</template>
<script setup>
import { computed, reactive, ref, shallowRef, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { uploadFile, createArticle, updateArticle } from '@/api/admin'
import { fileBaseURL } from '@/config/index'

const props = defineProps({
    modelValue: {
        type: Boolean,
        default: false
    },
    catagories: {
        type: Array,
        default: () => []
    },
    isEdit: {
        type: Boolean,
        default: false
    },
    article: {
        type: Object,
        default: null
    }
})
watch(()=>props.article, (newVal) => {
    if (newVal && newVal.id) {
        nextTick(()=>{
             Object.assign(formData, newVal)
        // tags 后端存的是逗号分隔字符串，回填时要拆成数组让 el-select 显示
        formData.tagArray = newVal.tags ? newVal.tags.split(',').filter(Boolean) : []
        businessId.value = newVal.id
        imageUrl.value = newVal.coverImage ? fileBaseURL + newVal.coverImage : ''
        })
    }
}, { immediate: true })
const emit = defineEmits(['update:modelValue', 'submit'])
// computed 中转：props 只读，要改必须发事件通知父组件
const visible = computed({
    get() {
        return props.modelValue
    },
    set(val) {
        emit('update:modelValue', val)
    }
})
const showPreview = ref(false)
const formRef = ref(null)
// 提交按钮的加载状态：请求中禁用按钮并显示 loading，防止重复提交
const loading = ref(false)

const getInitialFormData = () => ({
    "title": "",
    "content": "",
    "coverImage": "",
    "categoryId": 1,
    "summary": "",
    "tags": "",
    "tagArray": [],
    "id": ""
})
const formData = reactive(getInitialFormData())
const commonTags = [
  '情绪管理', '焦虑', '抑郁', '压力', '睡眠',
  '冥想', '正念', '放松', '心理健康', '自我成长',
  '人际关系', '工作压力', '学习方法', '生活技巧'
]
const rules = reactive({
    title: [
        { required: true, message: '请输入文章标题', trigger: 'blur' }
    ],
    categoryId: [
        { required: true, message: '请选择分类', trigger: 'change' }
    ],
    content: [
        { required: true, message: '请输入文章正文', trigger: 'change' }
    ],
})
const imageUrl = ref('')
const handleClose = () => {
    visible.value = false
    Object.assign(formData, getInitialFormData())
    businessId.value = ''
    imageUrl.value = ''
    showPreview.value = false
    formRef.value?.clearValidate()
}
const beforeUpload = (file) => {
    const isJPG = file.type === 'image/jpeg'
    const isPNG = file.type === 'image/png'
    if (!isJPG && !isPNG) {
        ElMessage.error('上传图片只能是 JPG/PNG 格式!')
        return false
    }
    const isLt5M = file.size / 1024 / 1024 < 5
    if (!isLt5M) {
        ElMessage.error('上传图片大小不能超过 5MB!')
        return false
    }
    return true
}
const businessId = ref('')
const handleAvatarSuccess = async ({file}) => {
    // 编辑时沿用文章 id；新增时文章还没有 id，因此生成临时业务 id。
    if (!businessId.value) {
        businessId.value = crypto.randomUUID()
    }
    const fileRes = await uploadFile(file,{
        businessId:businessId.value
    })
    // 响应拦截器返回 { code, data, msg }，文件路径在 data 中
    // 兼容 data 为路径字符串 或 { filePath } / { url } 对象两种情况
    const filePath = typeof fileRes.data === 'string'
        ? fileRes.data
        : (fileRes.data?.filePath || fileRes.data?.url || '')
    imageUrl.value = fileBaseURL + filePath
    formData.coverImage = filePath
}
const removeCover = () => {
    imageUrl.value = ''
    formData.coverImage = ''
}

// WangEditor 实例包含大量内部对象，只需要浅层保存，避免被 Vue 深度代理。
const editorRef = shallowRef(null)

const handleCreated = (editor) => {
    editorRef.value = editor

    // 编辑器创建完成时同步一次初始值，兼容编辑文章时回填已有正文。
    handleContentChange(editor)
}

const handleContentChange = (editor) => {
    // change 事件传入 WangEditor 实例，正文需要保存为带格式的 HTML。
    if (!editor || typeof editor.getHtml !== 'function') return

    formData.content = editor.getText().trim() ? editor.getHtml() : ''
}

const handleSubmit = async () => {
    const valid = await formRef.value.validate().catch(() => false)
    if (!valid) return

    const articleId = formData.id
    const submitData = {
        ...formData,
        tags: formData.tagArray.join(',')
    }
    delete submitData.tagArray
    // 更新接口已经通过 URL 传递 id；创建接口也不应提交空 id。
    delete submitData.id

    if (props.isEdit && !articleId) {
        ElMessage.error('缺少文章 ID，无法更新')
        return
    }

    loading.value = true
    try {
        if (props.isEdit) {
            await updateArticle(articleId, submitData)
        } else {
            await createArticle(submitData)
        }
        ElMessage.success(props.isEdit ? '更新成功' : '发布成功')
        visible.value = false
        emit('submit')
    } catch (err) {
        ElMessage.error(props.isEdit ? '更新失败' : '发布失败')
    } finally {
        loading.value = false
    }
}
</script>
<style scoped>/* 上传触发区：宽度自适应，最大240px，高度按 240:130 比例自动计算 */
.cover-placeholder {
    width: 240px;
    max-width: 240px;
    aspect-ratio: 240 / 130;
    border: 1px dashed #d9d9d9;
    border-radius: 8px;
    background-color: #fafafa;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #909399;
    font-size: 13px;
    cursor: pointer;
    transition: border-color 0.2s, color 0.2s;
    box-sizing: border-box;
}
.cover-placeholder:hover {
    border-color: var(--el-color-primary);
    color: var(--el-color-primary);
}
.cover-placeholder p {
    margin: 0;
    line-height: 1.4;
}
/* 上传后的预览图：与虚线框同比例，等比裁剪不变形 */
.cover-image {
    width: 240px;
    max-width: 240px;
    aspect-ratio: 240 / 130;
    object-fit: cover;
    border-radius: 8px;
    border: 1px solid #ebeef5;
    display: block;
    box-sizing: border-box;
}
</style>
