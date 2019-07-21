<template>
    <div id="dashboard">
      <div id="uploadImageModal">
      <UploadImage v-if='showUploadForm' v-on:childCall='toggleUploadForm(false)' v-on:theImage='loadImage' v-on:uploadClick="uploadImage" class="block"></UploadImage>
      
      </div>
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
                    <div class="create-post" v-if="showEditForm">
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
                            <gmap-autocomplete @place_changed=setPlace></gmap-autocomplete>
                            <slot>
                             <frame><img :src='post.picture' height="100"></frame>
                             </slot>
                            <button raised type="button" class="button" @click='toggleUploadForm(true)'>Upload a Photo</button>
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
                            <gmap-autocomplete  @place_changed=setPlace></gmap-autocomplete>
                            <slot>
                             <frame><img :src='post.picture' height="100"></frame>
                             </slot>

                            <button raised type="button" class="button" @click='toggleUploadForm(true)'>Upload a Photo</button>
                            
                            <button @click="createPost" type="button" class="button">Post</button>
                        </form>
                    </div>
                </div>
            </div>
            <div v-if="userProfile.accountType == 'Organization'">
              <div class="col2">
                <div class="container">
                  <input type="text" v-model="search" placeholder="Search...">
                  </div>
                  <div v-if="posts.length">
                    <div v-for = "post in filteredPosts" :key = "post.id" class="post">
                     <div v-if="post.userId == currentUser.uid" class="post">
                        <div class = "postcontent">
                         <frame><img :src='post.picture' height="100" class="img"></frame>
                          <h4>{{ post.title }}</h4>
                          <h5>{{ post.organizationName }}</h5>
                          <span>{{ post.eventDate | moment }}</span>
                          <!-- used for testing comment out later -->
                          <span>{{ post.locationName }}</span>
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
                        <frame><img :src='post.picture' height="100" class="post-picture" ></frame>
                        <h4> {{ post.title }}</h4>
                        <h5>{{ post.organizationName }}</h5>
                        <span>{{ post.eventDate | moment }}</span>
                        <p>{{ post.content | trimLength }}</p>

                        <!-- Maybe add a unlike button instead of like button since it's already liked -->
                        <!-- Or when it clicks again it unlikes it -->
                        <button @click="toggleLike(post.id, post.likeCount)" class="button">Likes {{ post.likeCount }}</button>
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
import UploadImage from './UploadImage';
import firebase from 'firebase'
const fb = require("../../firebaseConfig.js");
export default {
  components: {
    UploadImage
  },
  data() {
    return {
      post: {
        title: '',
        content: '',
        eventDate: '',
        picture: '',
        likeCount: '',
        locationName: '',
        lat: '',
        lng: ''
      },
      showUploadForm: '',
      showEditForm: false,
      editId: "",
      search: "",
      tempImage: "",
      uploadValue: 0
    };
  },
  computed: {
   filteredPosts() {
      return this.posts.filter((post) => {
        return JSON.stringify(post).toLowerCase().includes(this.search.toLowerCase());
      })
    },
   filteredLikedPosts() {
      return this.likedPosts.filter((post) => {
        return JSON.stringify(post).toLowerCase().includes(this.search.toLowerCase());
      })
    },
    ...mapState(['userProfile', 'currentUser', 'posts', 'likedPosts']),
    
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
    setPlace(place) {
    
      console.log("Old Location: " + this.post.locationName);
      console.log("Place equals: " + JSON.stringify(place.name, undefined, 2));
      this.post.locationName = place.name;
      this.post.lat = place.geometry.location.lat();
      this.post.lng = place.geometry.location.lng();
      console.log("Lat: " + place.geometry.location.lat());
      console.log("New Location: " + this.post.locationName);
      console.log("Event Latitude: " + this.post.lat);
      console.log("Event Longitude: " + this.post.lng);
    },
    createPost() {
      fb.postsCollection.add({
          title: this.post.title,
          createdOn: new Date(),
          eventDate: this.post.eventDate,
          content: this.post.content,
          pictureUrl: this.post.picture,
          userId: this.currentUser.uid,
          organizationName: this.userProfile.organizationName,
          likeCount: 0,
          location: this.post.locationName,
          lat: this.post.lat,
          lng: this.post.lng
        })
        .then(ref => {
          this.post.title = '',
          this.post.content = '',
          this.post.picture = '',
          this.post.eventDate = '',
          this.post.locationName = ''
        })
        .catch(err => {
          console.log(err);
        });
    },
    toggleLike: function(postId, postLikes){
        if(this.liked) {
            this.unlikePost(postId, postLikes)
        } else {
            this.likePost(postId, postLikes)
        }
        this.liked = !this.liked;
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
    unlikePost(postId, postLikes) {
      let docId = `${this.currentUser.uid}_${postId}`
        fb.likesCollection
        .doc(docId)
        .delete()
        .then(() => {
          fb.postsCollection.doc(postId).update({
            likeCount: postLikes - 1
          })
        })
        .catch(err => {
             console.log(err)
          });
   },
    deletePost(id) {
      fb.postsCollection
        .doc(id)
        .delete()
        .catch(err => {
          console.log(err);
        });
    },
    toggleUploadForm(boolValue) {
      this.showUploadForm= boolValue;
      console.log('childCall received');
    },
    loadImage(value) {
    console.log('inside of loadImage')
    this.tempImage = value
    },
    uploadImage() {
    const storageRef = firebase.storage().ref(`/images/${this.tempImage.name}`);
    const task = storageRef.put(this.tempImage);
    task.on('state_changed', snapshot => {
      let percentage = (snapshot.bytesTransferred/snapshot.totalBytes)*100;
      this.uploadValue = percentage;
    }, error =>{console.log(error.message)},
      ()=>{this.uploadValue=100;
        task.snapshot.ref.getDownloadURL().then((url) => {
        this.post.picture = url;
        console.log("insied of uploadImage woo")
        console.log(this.post.picture)
        console.log(this.uploadValue)
        });
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
          pictureUrl: this.post.picture,
          locationName: this.post.locationName
        })
        .then(ref => {
          this.post.title = '',
          this.post.content = '',
          this.post.picture = '',
          this.post.eventDate = '',
          this.post.locationName = ''
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
        (this.post.picture = post.pictureUrl),
        (this.post.locationName = post.locationName),
        (this.editId = post.id);
    }
  }
};
</script>