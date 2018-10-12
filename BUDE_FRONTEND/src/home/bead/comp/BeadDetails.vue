<template>
  <div class="beadDetails">
    <!--标题-->
    <div class="header">
      <span class="title">软件包</span>
      <el-form :inline="true">
        <el-form-item label="模块" style="margin-bottom:0px">
          <FaceCascader @isFind="find"></FaceCascader>
        </el-form-item>
      </el-form>
    </div>
    <!--表格展示-->
    <div class="details">
      <el-form :inline="true" class="demo-form-inline" :model="selectInfo" style="min-width:1150px;">
        <el-form-item label="软件包">
          <el-input placeholder="如:SMT-display_2.0.0_201808091002" style="width:300px;"
                    v-model="selectInfo.beadName"></el-input>
        </el-form-item>
        <el-form-item label="别名">
          <el-input placeholder="软件包别名"
                    v-model="selectInfo.alias"></el-input>
        </el-form-item>
        <el-form-item label="更新描述">
          <el-input placeholder="更新描述"
                    v-model="selectInfo.updateDescribe"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="btn_find">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button class="button-new-tag" type="primary" @click="btn_upload">上传软件包</el-button>
        </el-form-item>
      </el-form>
      <el-scrollbar style="height:calc(100% - 62px);">
        <el-table
          stripe
          border
          center="true"
          :data="tableData"
          style="width: 100%;">
          <el-table-column
            prop="id"
            label="软件包Id"
            v-if="show"
            align="center"
          >
          </el-table-column>
          <el-table-column
            prop="firstCode"
            label="主版本"
            align="center"
          >
          </el-table-column>
          <el-table-column
            prop="secondCode"
            label="次版本"
            align="center"
          >
          </el-table-column>
          <el-table-column
            prop="debugCode"
            label="修正版本"
            align="center"
          >
          </el-table-column>
          <el-table-column
            prop="suffixTime"
            label="后缀时间"
            align="center"
          >
          </el-table-column>
          <el-table-column
            prop="alias"
            label="软件包别名"
            align="center"
          >
          </el-table-column>
          <el-table-column
            prop="updateDescribe"
            label="更新描述"
            align="center"
          >
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <i class="el-icon-edit" @click="btn_edit(scope.$index, scope.row)"></i>
              <i class="el-icon-delete" @click="btn_delete(scope.$index, scope.row)"></i>
            </template>
          </el-table-column>
        </el-table>
      </el-scrollbar>
    </div>
    <!--分页-->
    <el-pagination
      :current-page="currentPage"
      :page-size="pageSize"
      :page-sizes="[10,20,30,40]"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      ref="pagination"
      layout="total,sizes,prev,pager,next, jumper"
      :total="totallyData">
    </el-pagination>
  </div>
</template>

<script>
  import Bus from '@/utils/bus'
  import FaceCascader from '@/home/face/comp/FaceCascader'
  import {axiosPost} from '@/utils/fetchData'
  import {selectBeadUrl, deleteBeadUrl, downloadBeadUrl} from '@/config/globalUrl'
  import {mapActions} from 'vuex';
  import {refreshHandle} from "@/utils/handle";

  export default {
    name: "beadDetails",
    data() {
      return {
        // 项目Id
        headId: "",
        // 模块Id
        faceId: "",
        // 软件包Id
        beadId: "",
        // 表格数据
        tableData: [],
        // 当前页面
        currentPage: 1,
        // 每页项数
        pageSize: 10,
        // 总项数
        totallyData: 0,
        // 软件包Id不展示
        show: false,
        // 查询信息
        selectInfo:{
          beadName:"",
          alias:"",
          updateDescribe:""
        }
      }
    },
    components: {
      FaceCascader
    },
    beforeDestroy() {
      // 取消监听
      Bus.$off('isbeadRefresh');
    },
    mounted() {
      // 监听刷新事件
      Bus.$on('isbeadRefresh', (type) => {
        if (type === "add" && this.faceId === "") {
        } else {
          this.refresh(type);
        }
      })
    },
    methods: {
      ...mapActions(['setLoading']),
      // 获取项目Id和模块Id
      find: function (obj) {
        this.currentPage = 1;
        this.headId = obj.headId;
        this.faceId = obj.faceId;
        this.selectAll();
      },
      // 点击查询按钮
      btn_find:function(){
        this.$alertInfo("暂未开发");
      },
      // 点击修改按钮
      btn_edit: function (index, row) {
        Bus.$emit('beadEdit', row);
      },
      // 点击删除按钮
      btn_delete: function (index, row) {
        this.beadId = row.id;
        this.$confirm('是否删除该软件包?', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          center: false
        }).then(() => {
          this.deleteItem();
        }).catch(() => {
          this.$alertInfo("已取消删除");
        });
      },
      // 查询软件包信息
      selectAll: function () {
        this.setLoading(true);
        let options = {
          url: selectBeadUrl,
          data: {
            faceId: this.faceId,
            currentPage: this.currentPage,
            pageSize: this.pageSize
          }
        };
        axiosPost(options).then(response => {
          this.setLoading(false);
          if (response.data.result === 200) {
            let data = response.data.data;
            this.totallyData = data.totallyData;
            this.currentPage = data.currentPage;
            this.$refs.pagination.internalCurrentPage = data.currentPage;
            this.tableData = data.list;
          } else {
            this.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            this.setLoading(false);
            this.$alertError('请求超时，请刷新重试');
          })
      },
      // 删除
      deleteItem: function () {
        let options = {
          url: deleteBeadUrl,
          data: {
            beadId: this.beadId
          }
        };
        axiosPost(options).then(response => {
          if (response.data.result === 200) {
            this.$alertSuccess('删除成功');
            this.refresh("delete");
          } else {
            this.$alertWarning(response.data.data);
          }
        })
          .catch(err => {
            this.$alertError('请求超时，请刷新重试');
          })
      },
      // 点击上传软件包按钮
      btn_upload: function () {
        Bus.$emit('uploadBead', true);
      },
      // 刷新
      refresh: function (type) {
        refreshHandle(type, this);
        this.selectAll();
      },
      // currentPage改变
      handleCurrentChange: function (currentPage) {
        this.currentPage = currentPage;
        this.selectAll();
      },
      // pageSize改变
      handleSizeChange: function (pageSize) {
        this.pageSize = pageSize;
        this.selectAll();
      }
    }
  }
</script>

<style scoped>
  .beadDetails {
    width: 100%;
    height: 100%;
  }

  /*标题*/
  .header {
    min-width: 400px;
  }

  .header span.title {
    display: inline-block;
    padding-left: 20px;
    border-left: 3px solid rgb(0, 176, 240);
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 15px;
  }

  /*表格*/
  .details {
    box-sizing: border-box;
    width: 100%;
    height: calc(100% - 125px);
    padding: 20px 20px;
    margin: 10px auto 10px;
    border-width: 1px;
    border-style: solid;
    border-color: rgb(235, 235, 235);
    border-image: initial;
    border-radius: 3px;
    transition: all 0.2s ease 0s;
    background-color: #fff;
  }

  .details:hover {
    box-shadow: rgba(232, 237, 250, 0.6) 0px 0px 8px 0px,
    rgba(232, 237, 250, 0.5) 0px 2px 4px 0px;
  }

  i {
    font-size: 20px;
    cursor: pointer;
  }

  i:not(last-child) {
    margin-right: 12px;
  }
</style>
