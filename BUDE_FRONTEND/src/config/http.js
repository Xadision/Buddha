import axios from 'axios';
import Qs from 'qs';
import {setLoginToken} from "../store/actions";
import store from "../store";
import router from '../router';

axios.interceptors.request.use(
  config => {
    if (config.headers['Content-type'] === "application/x-www-form-urlencoded; charset=UTF-8") {
      if (store.state.token !== '') {
        if (config.data === "") {
          config.data += ("#TOKEN#=" + store.state.token);
        } else {
          config.data += ("&#TOKEN#=" + store.state.token);
        }
      }
    }
    return config;
  },
  error => {
    return Promise.reject(error)
  }
);


/*axios.interceptors.response.use(
  res => {
    if (res.data.result === 401){
      store.commit('setLoginToken', '');
      localStorage.removeItem('token');
      router.replace({
        path: '/login',
      });
    }
    return res
  },
  error => {
    if (error.response) {
      console.log(JSON.stringify(error))
    }
    return Promise.reject(JSON.stringify(error))
  }
);*/

export default axios;
