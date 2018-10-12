<template>
  <div class="headDialog">
    <!--添加-->
    <el-dialog
      title="添加项目"
      :visible.sync="addDialogVisible"
      width="400px">
      <el-form>
        <el-form-item>
          <el-input v-model="head.name" placeholder="项目名" size="large"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="check('add')">确 定</el-button>
      </span>
    </el-dialog>

    <!--修改-->
    <el-dialog
      title="修改项目"
      :visible.sync="editDialogVisible"
      width="400px">
      <el-form>
        <el-form-item label="当前项目名">
          <el-input v-model="oldHeadName" size="large" disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="新项目名">
          <el-input v-model="head.name" size="large"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editCancel">取 消</el-button>
        <el-button type="primary" @click="check('edit')">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  import {axiosPost} from '@/utils/fetchData'
  import {addHeadUrl, updateHeadUrl} from '@/config/globalUrl'
  import Bus from '@/utils/bus'
  import {judgeCodeLen} from "@/utils/handle";

  export default {
    name: "headDialog",
    data() {
      return {
        // 添加模态框
        addDialogVisible: false,
        // 修改模态框
        editDialogVisible: false,
        // 正在操作的项目
        head: {
          id: "",
          name: ""
        },
        // 未修改前的项目名
        oldHeadName: ""
      }
    },
    beforeDestroy() {
      // 取消监听
      Bus.$off('headAdd');
      Bus.$off('headEdit');
    },
    mounted() {
      // 监听添加按钮点击
      Bus.$on('headAdd', () => {
        this.init({});
        this.addDialogVisible = true;
      });
      // 监听修改按钮点击
      Bus.$on('headEdit', (msg) => {
        this.init(msg);
        this.editDialogVisible = true;
      });
    },
    methods: {
      // 初始化数据
      init: function (obj) {
        if (obj.id) {
          this.oldHeadName = obj.name;
          this.head.id = obj.id;
          this.head.name = "";
        } else {
          this.head = {
            id: "",
            name: ""
          }
        }
      },
      // 项目名长度检验
      check: function (fn) {
        if (this.head.name === "") {
          this.$alertWarning("项目名不能为空");
        } else {
          if (judgeCodeLen(this.head.name)) {
            if (fn === "add") {
              this.add();
            } else {
              this.edit();
            }
          } else {
            this.$alertWarning("项目名不能超过64个字符");
          }
        }
      },
      // 添加
      add: function () {
        let options = {
          url: addHeadUrl,
          data: {
            headName: this.head.name
          }
        };
        axiosPost(options).then(response => {
          if (response.data.result === 200) {
            this.$alertSuccess('添加成功');
            this.addDialogVisible = false;
            Bus.$emit('isheadRefresh', "add");
          } else {
            this.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            this.$alertError('请求超时，请刷新重试');
            this.addDialogVisible = false;
          })
      },
      // 修改
      edit: function () {
        let options = {
          url: updateHeadUrl,
          data: {
            headId: this.head.id,
            headName: this.head.name
          }
        };
        axiosPost(options).then(response => {
          if (response.data.result === 200) {
            this.$alertSuccess('编辑成功');
            this.editDialogVisible = false;
            Bus.$emit('isheadRefresh', "edit");
          } else {
            this.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            this.$alertError('请求超时，请刷新重试');
            this.editDialogVisible = false;
          })
      },
      // 取消编辑
      editCancel: function () {
        this.editDialogVisible = false;
      },
    },
  }
</script>

<style scoped>
</style>
