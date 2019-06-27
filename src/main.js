import Vue from 'vue'
import App from './App'
import router from './router'
import { store } from './store.js'
const fb = require('../firebaseConfig.js')
import './assets/scss/app.scss'
import Datetime from 'vue-datetime'
import 'vue-datetime/dist/vue-datetime.css'

Vue.config.productionTip = false
Vue.use(Datetime)

// handle page reloads
let app
fb.auth.onAuthStateChanged(user => {
    if (!app) {
        app = new Vue({
            el: '#app',
            router,
            store,
            render: h => h(App)
        })
    }
})
