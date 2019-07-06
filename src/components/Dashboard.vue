<template>
    <div id="dashboard">
        <section>
            <div class="col1">
              <!-- Depending on User Type,
                 this seciton while show Student or Organization Sideview-->


              <!-- Student View:
                  FirstName LastName
                  Student

                  Liked Organizations List
              -->
                <div class="profile" v-if="userProfile.accountType == 'Student'">
                    <h5>{{ userProfile.firstName }} {{ userProfile.lastName }}</h5>
                    <p>{{ userProfile.accountType }}</p>
                    <div class="liked-org">
                        <p>This section will show liked organizations</p>
                    </div>
                </div>
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
                    <div class="edit-post" v-if="showEditForm">
                     <p>Edit Post</p>
                        <form>
                            <textarea v-model.trim="post.title" placeholder="Event Name"></textarea><p>required</p>
                            <datetime 
                              type="datetime" 
                              v-model.trim="post.eventDate" 
                              class = "datepicker"
                              :minute-step="15"
                              use12-hour></datetime>
                            <textarea v-model.trim="post.content" placeholder = "Details" class="details"></textarea>
                            <textarea v-model.trim="post.picture" placeholder = "Add Photo"></textarea>
                            <button @click="saveEditContact" class="button">Save</button>
                        </form>
                    </div>
                    <div v-if="!showEditForm" class="create-post">
                        <p>Create New Event</p>
                        <form @submit.prevent>
                            <textarea v-model.trim="post.title" placeholder="Event Name"></textarea>
                            <datetime 
                              placeholder= 'Click to Add Event Date'
                              type="datetime" 
                              v-model.trim="post.eventDate" 
                              class="datepicker"
                              :minute-step="15"
                              use12-hour></datetime>
                            <textarea v-model.trim="post.content" placeholder = "Details" class="details"></textarea>
                            <textarea v-model.trim="post.picture" placeholder = "Add Photo"></textarea>
                            <button @click="createPost" class="button">Post</button>
                        </form>
                    </div>
                </div>
            </div>
            <div v-if="userProfile.accountType == 'Organization'">
              <div class="col2">
                <div class="container">
                  <input type="text" v-model="search" placeholder="Search...">
                  </div>
                <div v-if= "posts.length">
                    <div v-for = "post in filteredPosts" :key = "post.id" class="post">
                     <div v-if="post.userId == currentUser.uid" class="post">
                        <div class = "postcontent">
                          <h4>{{ post.title }}</h4>
                          <h5>{{ post.organizationName }}</h5>
                          <span>{{ post.eventDate | moment }}</span>
                          <p>{{ post.content | trimLength }}</p>
                          <!-- Maybe add a unlike button instead of like button since it's already liked -->
                          <button class="button">Likes {{ post.likeCount }}</button> <!-- They can only view likes -->
                        </div>
                        <div class="updatebuttons">
                          <ul>
                              <button @click="deletePost(post.id)" class="button">Delete Post</button>
                              <button @click="editPost(post)" class="button">Edit Post</button>
                          </ul>
                        </div>
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
                        <h4>{{ post.title }}</h4>
                        <h5>{{ post.organizationName }}</h5>
                        <span>{{ post.eventDate | moment }}</span>
                        <p>{{ post.content | trimLength }}</p>

                        <!-- Maybe add a unlike button instead of like button since it's already liked -->
                        <!-- Or when it clicks again it unlikes it -->
                        <button @click="likePost(post.id, post.likeCount)" class="button">Likes {{ post.likeCount }}</button>
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
    createPost() {
      fb.postsCollection.add({
          title: this.post.title,
          createdOn: new Date(),
          eventDate: this.post.eventDate,
          content: this.post.content,
          picture: this.post.picture,
          userId: this.currentUser.uid,
          organizationName: this.userProfile.organizationName,
          likeCount: 0
        })
        .then(ref => {
          this.post.title = '',
          this.post.content = '',
          this.post.picture = '',
          this.post.eventDate = ''
        })
        .catch(err => {
          console.log(err);
        });
    },
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
    toggleForm() {
      // hides the appropriate forms at the button clicks
      this.showEditForm = !this.showEditForm;
    },
    saveEditContact() {
      // Calls toggle form at the end of all operations to hide the edit form
      //This method is called by Save to update a contact with info
      fb.postsCollection
        .doc(this.editId)
        .update({
          title: this.post.title,
          content: this.post.content,
          eventDate: this.post.eventDate,
          picture: this.post.picture
        })
        .then(ref => {
          this.post.title = '',
          this.post.content = '',
          this.post.picture = '',
          this.post.eventDate = '';
        })
        .catch(err => {
          console.log(err);
        });
      //hide the form
      this.toggleForm();
    },
    //this method is triggered by the edit button
    //toggles the form and populates the placeholders with contact information
    editPost(post) {
      //make the edit form appear
      this.toggleForm();
      //populate with contact info
      console.log(this.post.title);
      console.log("sanity check");
        (this.post.title = post.title),
        (this.post.content = post.content),
        (this.post.eventDate = post.eventDate),
        (this.post.picture = post.picture),
        (this.editId = post.id);
    }
  }
};
</script>
