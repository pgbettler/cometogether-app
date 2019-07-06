<template>
    <div id="explore">
        <section>
            <div class="col2">
              <div v-if="posts.length">
                  <div v-for = "post in filteredPosts" :key = "post.id" class="post">
                      <h4>{{ post.title }}</h4>
                      <h5>{{ post.organizationName }}</h5>
                      <span>{{ post.eventDate | moment }}</span>
                      <p>{{ post.content | trimLength }}</p>
                      <button @click="likePost(post.id, post.likeCount)" class="button">Likes {{ post.likeCount }}</button>
                  </div>
              </div> 
              <div v-else>
                  <p class="no-results">There are currently no posts</p>
              </div>
            </div>
            <div class="container">
              <input type="text" v-model="search" placeholder="Search..">
              <div class="search"></div>
            </div>
        </section>
    </div>
</template>
<script>
import { mapState } from "vuex";
const fb = require("../../firebaseConfig.js");

export default {
  data() {
    return {
      post: {
        likeCount:''
      },
      search: "",
      contactString: "",
      performingRequest: false,
    };
  },
  computed: {
    filteredPosts() {
      return this.posts.filter((post) => {
        return JSON.stringify(post).toLowerCase().includes(this.search.toLowerCase());
      })
    },
    ...mapState(['userProfile', 'currentUser', 'posts']),
  },
  methods: {
      likePost(postId, postLikes) {
      let docId = `${this.currentUser.uid}_${postId}`
        fb.likesCollection.doc(docId).get().then(doc => {
          // add to users list of liked posts
          if (doc.exists) { return }
          fb.likesCollection.doc(docId).set({
             postId: postId,
             userId: this.currentUser.uid
          }).then(() => {
             // update post likes
             fb.postsCollection.doc(postId).update({
             likeCount: postLikes + 1
            })
          })
          }).catch(err => {
             console.log(err)
          })
   }
  }
};
</script>
