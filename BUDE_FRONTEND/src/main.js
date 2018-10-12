// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import Vuex from 'vuex'
import App from './App'
import router from './router'
import  Icon from 'vue-svg-icon/Icon'
import axios from './config/http'
import store from './store'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import {alertError, alertInfo, alertSuccess,alertWarning} from "./utils/modal";

Vue.prototype.$alertError = alertError
Vue.prototype.$alertInfo = alertInfo
Vue.prototype.$alertSuccess = alertSuccess
Vue.prototype.$alertWarning = alertWarning


Vue.prototype.$axios = axios
Vue.config.productionTip = false
Vue.component('icon', Icon)
Vue.use(ElementUI);
Vue.use(Vuex)
/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>'
})
