<template>
    <div id="explore">
        <section>
            <div class="col2">
              <div v-if="posts.length">
                  <div v-for="post in posts" class="post">
                      <h5>{{ post.title }}</h5>
                      <span>{{ post.createdOn | formatDate }}</span>
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
      performingRequest: false,
    };
  },
  computed: {
    ...mapState(['userProfile', 'currentUser', 'posts']),
  },
  methods: {
      /*
      toggleLike() {
        // Need to grab userID and postID and also update like 
        fb.likesCollection
        .doc(this.editId)
        .update({
            likeCount: this.likeCount
        })
        .then(ref => {
            (this.contact.likeCount = "");
        })
        .catch(err => {
          console.log(err);
        });  
     }
     */
  }
};
</script>
