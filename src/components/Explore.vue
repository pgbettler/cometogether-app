<template>
    <div id="explore">
        <section>
            <div class="col2">
              <div class="container">
                <input type="text" v-model="search" placeholder="Search..">
                <div class="search"></div>
              </div>
              <div v-if="posts.length">
<<<<<<< HEAD
                  <div v-for="post in posts" class="post">
                      <h4>{{ post.title }}</h4>
                      <h5>{{ post.organizationName }}</h5>
                      <span>{{ post.eventDate | moment }}</span>
=======
                  <div v-for="post in filteredPosts" :key="post.id" class="post">
                      <h5>{{ post.title }}</h5>
                      <span>{{ post.eventDate }}</span>
>>>>>>> AlejandroP
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
      performingRequest: false
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
  filters: {
      moment: function(date) {
        return moment(date).format('MMMM Do YYYY, h:mm a');
      },
      trimLength: function(val) {
          if (val.length < 200) {
              return val
          }
          return `${val.substring(0, 200)}...`
      }
  },
  methods: {
<<<<<<< HEAD
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
=======
      
      toggleLike() {
        
        // Need to grab userID and postID and also update like 
        /*fb.likesCollection
        .doc(this.editId)
        .update({
            likeCount: this.likeCount
        })
        .then(ref => {
            (this.contact.likeCount = "");
        })
        .catch(err => {
          console.log(err);
        });  */
     }
     
>>>>>>> AlejandroP
  }
};
</script>
