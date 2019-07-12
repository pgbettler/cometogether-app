<!--
  OrgView is the component that shows information about an organization. This page should appear for a student
  who is interested in learning more about an organization.

  Left Hand Side: Information about Organization

  Right Hand Side: Posts by Organization
-->

<template>
    <div id="dashboard">
        <section>
            <div class="col1">
              <!-- Organization View:
                  OrganizationName
                  Organization
                  Organization Description

                  Create New Post
              -->
                <div class="profile" v-if="userProfile.accountType == 'Organization'">
                    <h3>{{ userProfile.organizationName }}</h3>
                    <p><i>{{ userProfile.accountType }}</i></p>
                    <h5>{{ userProfile.organizationDetails | trimLength }}</h5>
                    <br>
                </div>
            </div>
            <div v-if="userProfile.accountType == 'Organization'">
              <div class="col2">
                <div class="container">
                  <input type="text" v-model="search" placeholder="Search...">
                  </div>
                <div v-if= "posts.length">
                    <div v-for = "post in filteredPosts" :key = "post.id" class="post">
                     <div v-if="post.userId == this.orgId" class="post">
                        <div class = "postcontent">
                          <h4>{{ post.title }}</h4>
                          <h5>{{ post.organizationName }}</h5>
                          <span>{{ post.eventDate | moment }}</span>
                          <p>{{ post.content | trimLength }}</p>
                          <!-- Maybe add a unlike button instead of like button since it's already liked -->
                          <button class="button">Likes {{ post.likeCount }}</button> <!-- They can only view likes -->
                        </div>
                      </div>
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
import { mapState } from "vuex";
import moment from 'moment'; //this is used for date formatting
const fb = require("../../firebaseConfig.js");


export default {
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
      search: "",
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
   }
  }
};

 console.log('The id is: ' + this.$route.params.id);
</script>
