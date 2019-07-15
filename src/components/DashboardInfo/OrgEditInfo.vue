<!--

This is strictly what the organizations sees about itself

Allows for creating new posts and editing existing posts -->


<template>
<div id="orgedit-info">
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
</template>
<script>
import { mapState } from "vuex";
import moment from 'moment'; //this is used for date formatting
const fb = require("../../../firebaseConfig.js");

export default {
    data() {
        return {
        showEditForm: false,
        }
    },

    computed: {
        ...mapState(['userProfile', 'currentUser']),
    },

    filters: {

    },

    methods: {
        createPost() {
        // allows organization to create a new post
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

        editPost(post) {
        //make the edit form appear
        // this method is triggered by the "edit" button
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
}
</script>