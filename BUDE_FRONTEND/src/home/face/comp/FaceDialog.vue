<template>
  <div class="faceDialog">
    <!--添加-->
    <el-dialog
      title="添加模块"
      :visible.sync="addDialogVisible"
      width="400px">
      <el-form>
        <el-form-item label="项目名">
          <el-select v-model="headId" placeholder="请选择" style="width:100%">
            <el-option
              v-for="item in headList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="模块名">
          <el-input v-model="faceName" size="large"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="add">确 定</el-button>
      </span>
    </el-dialog>

    <!--修改模块名-->
    <el-dialog
      title="修改模块名"
      :visible.sync="editDialogVisible"
      width="400px">
      <el-form>
        <el-form-item label="项目名">
          <el-input v-model="obj.headName" size="large" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="模块名">
          <el-input v-model="obj.faceName" size="large" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="新模块名">
          <el-input v-model="faceName" size="large"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false;">取 消</el-button>
        <el-button type="primary" @click="edit">确 定</el-button>
      </span>
    </el-dialog>

    <!--发布软件包-->
    <el-dialog
      title="发布软件包"
      :visible.sync="publishDialogVisible"
      width="400px">
      <el-form>
        <el-select v-model="beadId" placeholder="请选择你要发布的软件包" style="width:100%">
          <el-option
            v-for="item in beadList"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="pushDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="publish">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  import {axiosPost} from '@/utils/fetchData'
  import {addFaceUrl, updateFaceUrl} from '@/config/globalUrl'
  import Bus from '@/utils/bus'
  import {mapGetters} from 'vuex';
  import {judgeCodeLen} from "@/utils/handle";

  export default {
    name: "beadDialog",
    data() {
      return {
        // 添加模态框
        addDialogVisible: false,
        // 修改模态框
        editDialogVisible: false,
        // 发布软件包模态框
        publishDialogVisible: false,
        // 软件包
        beadList: [],
        beadId: "",
        obj: {},
        faceName: "",
        headId: "",
        // 发布软件包对象类型
        type: "" //0表示全选，1表示单选
      }
    },
    computed: {
      ...mapGetters(['headList'])
    },
    beforeDestroy() {
      // 取消监听
      Bus.$off('faceAdd');
      Bus.$off('faceEdit');
      Bus.$off('fingerPublish');
    },
    mounted() {
      // 监听添加按钮点击
      Bus.$on('faceAdd', (msg) => {
        this.obj = msg;
        this.faceName = "";
        this.headId = "";
        this.addDialogVisible = true;
      });
      // 监听编辑按钮点击
      Bus.$on('faceEdit', (msg) => {
        this.faceName = "";
        this.obj = msg;
        this.editDialogVisible = true;
      });
      // 监听发布软件包按钮点击
      Bus.$on("fingerPublish", (obj, beadList, type) => {
        this.linkString(obj.headName, obj.faceName, beadList.list);
        this.type = type;
        this.beadId = "";
        this.publishDialogVisible = true;
      });
      // 监听关闭模态框事件
      Bus.$on("closePublishDialog", (msg) => {
        if (msg === true) {
          this.publishDialogVisible = false
        }
      });
    },
    methods: {
      // 添加
      add: function () {
        if (this.headId !== "" && this.faceName !== "") {
          if (judgeCodeLen(this.faceName)) {
            let options = {
              url: addFaceUrl,
              data: {
                headId: this.headId,
                faceName: this.faceName
              }
            };
            axiosPost(options).then(response => {
              if (response.data.result === 200) {
                this.$alertSuccess('添加成功');
                this.addDialogVisible = false;
                Bus.$emit('isCascaderRefresh', "add", this.headId);
              } else {
                this.$alertWarning(response.data.data);
                this.addDialogVisible = false;
              }
            })
              .catch(err => {
                this.$alertError('请求超时，请刷新重试');
              })
          } else {
            this.$alertWarning("模块名不能超过64个字符");
          }
        } else {
          this.$alertWarning("内容不能为空");
        }
      },
      // 修改
      edit: function () {
        if (this.faceName !== "") {
          if (judgeCodeLen(this.faceName)) {
            let options = {
              url: updateFaceUrl,
              data: {
                headId: this.obj.headId,
                faceId: this.obj.faceId,
                faceName: this.faceName
              }
            };
            axiosPost(options).then(response => {
              if (response.data.result === 200) {
                this.$alertSuccess('修改成功');
                this.editDialogVisible = false;
                Bus.$emit('isCascaderRefresh', "edit", this.obj,this.faceName);
              } else {
                this.$alertWarning(response.data.data);
              }
            })
              .catch(err => {
                this.$alertError('请求超时，请刷新重试');
                this.editDialogVisible = false;
              })
          } else {
            this.$alertWarning("模块名不能超过64个字符");
          }
        } else {
          this.$alertWarning("新模块名不能为空");
        }
      },
      // 上传软件包
      publish: function () {
        Bus.$emit("beadPublishFinish", this.beadId, this.type);
      },
      // 拼接软件包名
      linkString: function (headName, faceName, arr) {
        this.beadList = [];
        for (let i = 0; i < arr.length; i++) {
          let obj = arr[i];
          let name = headName + '-' + faceName + '_' + obj.firstCode + '.' + obj.secondCode + '.' +
            obj.debugCode + '_' + obj.suffixTime;
          let beadInfo = {
            id: obj.id,
            name: name
          };
          this.beadList.push(beadInfo);
        }
      },
    },
  }
</script>

<style scoped>
</style>
