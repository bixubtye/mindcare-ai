<template>
    <div class="knowledge-page">
        <Pagehead title="知识管理">
            <template #buttons>
                <el-button type="primary" @click="handleAdd">新增</el-button>
            </template>
        </Pagehead>
        <Tablesearch :formItem="formItem" @search="handleSearch" />
        <el-table :data="tableData" style="width: 100%; margin-top: 25px">
            <el-table-column prop="title" label="文章标题" width="300" fixed="left" />
            <el-table-column prop="categoryId" label="分类" width="100" :formatter="formatCategory" />
            <el-table-column prop="authorName" label="作者" width="100" />
            <el-table-column prop="readCount" label="阅读量" width="100" />
            <el-table-column prop="updatedAt" label="发布时间" width="100" />
            <el-table-column label="操作" width="240">
                <template #default="scope">
                    <el-button  text type="primary" @click="handleEdit(scope.row)">编辑</el-button>
                    <el-button   @click="handlePublish(scope.row)" v-if="scope.row.status===0||scope.row.status===2" text type="success">发布</el-button>
                    <el-button   @click="handleUnpublish(scope.row)" v-if="scope.row.status===1" text type="warning">下线</el-button>
                    <el-button text type="danger" @click="handleDelete(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
        layout="prev, pager, next"
        :current-page="pagination.currentPage"
        :page-size="pagination.size"
        :total="pagination.total"
        :hide-on-single-page="false"
        @current-change="handleChange"
        style="margin-top: 20px;"
        />
        <ArticalDailog v-model:modelValue="dialogVisible" :catagories="catagories" :is-edit="isEdit" :article="currentArticle" @submit="handleSubmit" />
    </div>
</template>
<script setup>
import { onMounted, ref, reactive } from 'vue'
import Pagehead from '@/components/Pagehead.vue'
import Tablesearch from '@/components/Tablesearch.vue'
import { catagoryTree,articlePage,getArticleDetail,changeArticleStatus,deleteArticle } from '@/api/admin'
import ArticalDailog from '@/components/ArticalDailog.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
const handlePublish = (row) => {
    ElMessageBox.confirm('确认发布吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        await changeArticleStatus(row.id,{
            status:1
        })
        ElMessage.success('发布成功')
        handleSearch()
    })
}
const handleUnpublish = (row) => {
    ElMessageBox.confirm('确认下线吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        await changeArticleStatus(row.id, {
            status: 2
        })
        ElMessage.success('下线成功')
        handleSearch()
    })
}
const handleDelete = async (row) => {
    if (!row?.id) return

    try {
        await ElMessageBox.confirm(`确认删除文章“${row.title}”吗？删除后无法恢复。`, '删除文章', {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'error'
        })
        await deleteArticle(row.id)
        ElMessage.success('删除成功')

        // 当前页只有一条数据时，删除后回到上一页，避免停留在空白页。
        if (tableData.value.length === 1 && pagination.currentPage > 1) {
            pagination.currentPage -= 1
        }
        await handleSearch()
    } catch (action) {
        // 取消或关闭确认框属于正常操作，不显示错误提示。
        if (action !== 'cancel' && action !== 'close') {
            ElMessage.error('删除失败')
        }
    }
}
const handleSubmit=()=>{
    // 新增成功后回到第一页并重新拉取列表
    pagination.currentPage = 1
    handleSearch()
}
const dialogVisible=ref(false)
const isEdit = ref(false)
const handleAdd = () => {
    isEdit.value = false
    dialogVisible.value = true
}
const formItem = [{
    label: '文章标题',
    prop: 'title',
    type: 'input',
    placeholder: '请输入文章标题'
},
{
    label: '分类',
    prop: 'category',
    type: 'select',
    placeholder: '请选择分类',

},
{
    label: '状态',
    prop: 'status',
    type: 'select',
    placeholder: '请选择状态',
    options: [
        {
            label: '草稿',
            value: 0
        },
        {
            label: '已发布',
            value: 1
        },

        {
            label: '已下线',
            value: 2
        }
    ]
}
 ]
 //分页参数
 const pagination=reactive({
    currentPage:1,
    size:10,
    total:0
 })
const handleSearch = async (formData) => {
    const params={
        ...pagination,
        ...formData
    }
    const res = await articlePage(params)
    // 兼容 { data: { records, total } } 与 { records, total } 两种结构
    const page = res.data || {}
    tableData.value = page.records || []
    pagination.total = page.total || 0
}
//分类映射表
const catagoryMap=reactive({})
//分类列格式化：id → 名称
const formatCategory=(row)=>{
    return catagoryMap[row.categoryId]||row.categoryId
}
//分类列表
const catagories=ref([])
const tableData=ref([])
const currentArticle=ref({})
const handleChange=(page)=>{
    pagination.currentPage=page
    handleSearch()
}
onMounted(async () => {
    const res = await catagoryTree()
    // 后端返回结构：{ code, data: [ { id, categoryName } ] }
    const list = res.data || []
    catagories.value=list.map(item=>{
        catagoryMap[item.id]=item.categoryName
        return{
            label: item.categoryName,
            value: item.id
        }
    })
    formItem[1].options=catagories.value
    handleSearch()
})
const handleEdit = async (row) => {
    if (!row?.id) return

    getArticleDetail(row.id).then((res) => {
        isEdit.value = true
        currentArticle.value = res.data
        dialogVisible.value = true
    })
}
</script>
