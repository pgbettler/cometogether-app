<template>
    <div id="explore">
        <section>
            <div class="col2">
              <div class="container">
                <input type="text" v-model="search" placeholder="Search..">
                <div class="search"></div>
              </div>
              <div v-if="posts.length">
                  <div v-for="post in filteredPosts" :key="post.id" class="post">
                      <h5>{{ post.title }}</h5>
                      <span>{{ post.eventDate }}</span>
                      <p>{{ post.content | trimLength }}</p>
                      <button class="button" @click="toggleLike">  Likes {{ post.likeCount }}</button>
                      <ul>
                         <!-- <li><a>Likes: {{ post.likeCount }}</a></li> -->
                          <li><a>view full post</a></li>
                      </ul>
                  </div>
              </div> 
              <div v-else>
                  <p class="no-results">There are currently no posts</p>
              </div>
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
     
  }
};
</script>
