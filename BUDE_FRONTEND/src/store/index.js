import Vue from 'vue'
import Vuex from 'vuex'
import * as mutations from './mutations'
import * as actions from './actions'
import * as getters from './getters'

Vue.use(Vuex);

const state = {
  /*登录验证标识*/
  token: '',

  /*加载状态 用于控制'@/components/Loading.vue显示'*/
  isLoading:false,

  /*项目对象数组*/
  headList:{}

};

const store = new Vuex.Store({
  state,
  getters,
  actions,
  mutations
});

export default store;