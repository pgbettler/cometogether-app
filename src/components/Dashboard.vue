<template>
    <div id="dashboard">
        <section>
            <div class="col1">
              <!-- Depending on User Type,
                 this seciton while show Student or Organization Sideview-->


              <!-- StudentInfo -->
                <div class="profile" v-if="userProfile.accountType == 'Student'">
                    <h5>{{ userProfile.firstName }} {{ userProfile.lastName }}</h5>
                    <p>{{ userProfile.accountType }}</p>
                    <div class="liked-org">
                        <p>This section will show liked organizations</p>
                    </div>
                </div>

              <!-- OrgEditInfo -->
                <div class="profile" v-if="userProfile.accountType == 'Organization'">
                  <OrgEditInfo :toggleForm='toggleForm'></OrgEditInfo>
                </div>
              
              <!--OrgInfo -->
                <div class="profile">
                  <OrgInfo></OrgInfo>
                </div>
            </div>

            <div class="col2">
              <Posts></Posts>
            </div>


            <div v-if="userProfile.accountType == 'Organization'">
              <div class="col2">
                <div class="container">
                  <input type="text" v-model="search" placeholder="Search...">
                  </div>
                <div v-if= "posts.length">
                    <div v-for = "post in filteredPosts" :key = "post.id">
                     <div v-if="post.userId == currentUser.uid" class="post">
                          <Post :post='post' :showUpdateButtons='true' v-on:editPost=""></Post>
                      </div>
                    </div>
                </div> 
                <div v-else>
                    <p class="no-results">There are currently no posts</p>
                </div>
              </div>
            </div>
            
            <div v-else> <!-- Student -->
               <div class="col2">

                 <!-- Goal is to search through the likes collection
                     If the like.userID == currentUser.uid
                     Then show that specific post --> 
                <!-- Still not work !!!!! -->
                <div class= "search"> </div>
                <div v-if="posts.length">
                    <div v-for = "post in filteredPosts" :key = "post.id" class="post">
                        <Post :post='post' :showUpdateButtons='false'></Post>
                    </div>
                </div> 
                <div v-else>
                    <p class="no-results">There are currently no posts</p>
                </div>
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

import StudentInfo from './DashboardInfo/StudentInfo'
import OrgInfo from './DashboardInfo/OrgInfo'
import OrgEditInfo from './DashboardInfo/OrgEditInfo'
import Posts from './Post/Posts'
import Post from './Post/Post'


import { mapState } from "vuex"
import moment from 'moment' //this is used for date formatting

const fb = require("../../firebaseConfig.js");

export default {
  components: {
    StudentInfo,
    OrgInfo,
    OrgEditInfo,
    Posts,
    Post
  },
  data() {
    return {
      post: {
        title: '',
        content: '',
        eventDate: '',
        picture: '',
        likeCount: ''
      },
      showEditForm: false,
      editId: "",
      search: ""
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
   },
    deletePost(id) {
      fb.postsCollection
        .doc(id)
        .delete()
        .catch(err => {
          console.log(err);
        });
    },
  }
};
</script>
