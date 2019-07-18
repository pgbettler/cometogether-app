import Vue from 'vue'
import Vuex from 'vuex'
const fb = require('../firebaseConfig')

Vue.use(Vuex)

// handle page reload
fb.auth.onAuthStateChanged(user => {
  if (user) {
      store.commit('setCurrentUser', user)
      store.dispatch('fetchUserProfile')

    // realtime updates from our posts collection
       fb.postsCollection.orderBy('eventDate', 'desc').onSnapshot(querySnapshot => {
        let postsArray = []
        let userLikedPosts = []

         querySnapshot.forEach(doc => {
             let post = doc.data()
             post.id = doc.id
             postsArray.push(post)
             fb.likesCollection.where("userId", "==", user.uid).onSnapshot(querySnapshot => {
               querySnapshot.forEach(doc => {
                 let likedpost = doc.data().postId
                 if (likedpost == post.id) {
                   userLikedPosts.push(post)
                 }
               })
             })
         })

         store.commit('setPosts', postsArray)
         store.commit('setLikedPosts', userLikedPosts)
        })
      
      fb.followCollection.where("userId", "==", user.uid).onSnapshot(querySnapshot => {
        let followedArray = []

        querySnapshot.forEach(doc => {
          let orgId = doc.data().orgId
          fb.usersCollection.doc(orgId).get().then(res => {
            followedArray.push(res.data())
          })
        })

        console.log(followedArray)
        store.commit('setFollowedOrgs', followedArray)
      })
    }
})

export const store = new Vuex.Store({
  state: {
    currentUser: null,
    userProfile: {},
    posts: [],
    likedPosts: [],
    followedOrgs: []

  },
  actions: {
    clearData({ commit }) {
      commit('setCurrentUser', null)
      commit('setUserProfile', {})
    },
    fetchUserProfile({ commit, state }) {
      fb.usersCollection.doc(state.currentUser.uid).get().then(res => {
          commit('setUserProfile', res.data())
      }).catch(err => {
          console.log(err)
      })
    },
    
  },
  mutations: {
    setCurrentUser(state, val) {
      state.currentUser = val
    },
     setUserProfile(state, val) {
      state.userProfile = val
    },
    setPosts(state, val) {
      state.posts = val
    },
    setLikedPosts(state, val) {
      state.likedPosts = val
    },
    setFollowedOrgs(state, val) {
      state.followedOrgs = val
    }
  }
})
