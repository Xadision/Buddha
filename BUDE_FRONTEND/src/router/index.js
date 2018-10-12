import Vue from 'vue'
import Router from 'vue-router'
import Main from '@/home/Main'
import Head from '@/home/head/HeadMain'
import Bead from '@/home/bead/BeadMain'
import Face from '@/home/face/FaceMain'
import User from '@/home/user/UserMain'
import Body from '@/home/body/BodyMain'
import Arm from '@/home/arm/ArmMain'
import Finger from '@/home/finger/FingerMain'
import store from '@/store'

Vue.use(Router)

const router = new Router({
  base: window.g.SYSTEM_PATH,
  routes: [
    {
      path: '/',
      name: 'Main',
      component: Main,
      redirect: '/head',
      meta: {
        requireAuth: true
      },
      children: [
        {
          path: '/head',
          name: 'Head',
          component: Head
        },
        {
          path: '/bead',
          name: 'Bead',
          component:Bead
        },
        {
          path:'/face',
          name:'Face',
          component:Face
        },
        {
          path:'/finger',
          name:'Finger',
          component:Finger
        },
        {
          path:'/body',
          name:'Body',
          component:Body
        },
        {
          path:'/arm',
          name:'Arm',
          component:Arm
        },
        {
          path:'/user',
          name:'User',
          component:User
        }
      ]
    }
  ]
});

if (localStorage.getItem('token')) {
  store.commit('setLoginToken', localStorage.getItem('token'))
}


/*router.beforeEach((to, from, next) => {
  if (to.matched.some(r => r.meta.requireAuth)) {
    if (store.state.token) {
      next();
    } else {
      next({
        path: '/login',
      })
    }
  } else {
    next();
  }
});*/

export default router;
