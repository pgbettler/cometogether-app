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
        let likesArray= []

         querySnapshot.forEach(doc => {
             let post = doc.data()
             post.id = doc.id
             postsArray.push(post)

             fb.likesCollection.where("postId", "==", post.id).onSnapshot(querySnapshot => {
               querySnapshot.forEach(doc => {
                 let likedata = doc.data()
                 likedata.user = doc.userId
                 if (likedata.userId == user.uid) {
                   likesArray.push(post)
                 }
               })
             })
         })
         console.log("Likes Array")
         console.log(likesArray)
         store.commit('setPosts', postsArray)
         store.commit('setLikedPosts', likesArray)
        })
    }
})

export const store = new Vuex.Store({
  state: {
    currentUser: null,
    userProfile: {},
    posts: [],
    likedPosts: []

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
    }
  }
})
